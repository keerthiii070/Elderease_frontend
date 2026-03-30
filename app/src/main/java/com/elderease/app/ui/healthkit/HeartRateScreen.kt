package com.elderease.app.ui.healthkit

import android.graphics.Color as AndroidColor
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.elderease.app.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

// ----------------------------------------------------------
// DATA MODEL
// ----------------------------------------------------------

data class HeartRateRecord(
    val timestamp: Long,
    val bpm: Int,
    val status: HeartStatus
)

enum class HeartStatus { LOW, HEALTHY, HIGH }

fun classifyHeartRate(bpm: Int): HeartStatus =
    when {
        bpm < 60 -> HeartStatus.LOW
        bpm <= 100 -> HeartStatus.HEALTHY
        else -> HeartStatus.HIGH
    }

// ----------------------------------------------------------
// STORAGE (SharedPreferences)
// ----------------------------------------------------------

object HeartRateStorage {

    private const val PREF_NAME = "heart_rate_prefs"
    private const val KEY_RECORDS = "records"

    fun addRecord(context: Context, bpm: Int, status: HeartStatus) {
        val ts = System.currentTimeMillis()
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_RECORDS, "") ?: ""

        val newItem = "$ts,$bpm,${status.name}"
        val finalData = if (existing.isBlank()) newItem else "$existing;$newItem"

        prefs.edit().putString(KEY_RECORDS, finalData).apply()
    }

    fun getAllRecords(context: Context): List<HeartRateRecord> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_RECORDS, "") ?: ""

        if (raw.isBlank()) return emptyList()

        return raw.split(";").mapNotNull { row ->
            val p = row.split(",")
            if (p.size != 3) return@mapNotNull null
            val ts = p[0].toLongOrNull() ?: return@mapNotNull null
            val bpm = p[1].toIntOrNull() ?: return@mapNotNull null
            val status = runCatching { HeartStatus.valueOf(p[2]) }.getOrNull() ?: HeartStatus.HEALTHY
            HeartRateRecord(ts, bpm, status)
        }.sortedByDescending { it.timestamp }
    }
}

// ----------------------------------------------------------
// CAMERA STARTER
// ----------------------------------------------------------

fun startHeartRateCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    analyzer: ImageAnalysis.Analyzer
): () -> Unit {

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    var camera: Camera? = null

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            analyzer
        )
        val selector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()

        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            analysis
        )

        // Flash ON while measuring
        camera?.cameraControl?.enableTorch(true)
    }, ContextCompat.getMainExecutor(context))

    // cleanup lambda
    return {
        val cameraProvider = cameraProviderFuture.get()
        camera?.cameraControl?.enableTorch(false)
        cameraProvider.unbindAll()
    }
}

// Simple low-pass filter to smooth PPG signal
class ButterworthFilter {
    private var prev = 0.0
    fun apply(x: Double): Double {
        val alpha = 0.12        // 0..1 (smaller = more smoothing)
        val out = prev + alpha * (x - prev)
        prev = out
        return out
    }
}

class HeartRateAnalyzer(
    private val onSample: (time: Long, value: Double) -> Unit,
    private val onFingerPresent: (Boolean) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastAvg = 0.0
    private var lastFingerCheckTime = 0L

    private val filter = ButterworthFilter()

    // ✅ Keep finger state stable (avoid flickering)
    private var fingerStableCount = 0
    private var lastFingerState = false

    override fun analyze(image: ImageProxy) {
        try {
            val bitmap = image.toBitmap()

            val centerX = bitmap.width / 2
            val centerY = bitmap.height / 2
            val pixel = bitmap.getPixel(centerX, centerY)

            val r = AndroidColor.red(pixel)
            val g = AndroidColor.green(pixel)
            val b = AndroidColor.blue(pixel)

            val brightness = (r + g + b) / 3

            // ✅ Finger is present only when:
            // - RED is high
            // - RED dominates GREEN & BLUE
            // - Brightness is not too high (camera uncovered = bright)
            val fingerDetectedNow =
                (r > 140 && r > g + 20 && r > b + 20 && brightness < 220)

            onFingerPresent(fingerDetectedNow)

            // ✅ If no finger, stop analysis
            if (!fingerDetectedNow) {
                image.close()
                return
            }

            // ---- Continue sample processing ONLY if finger is present ----
            val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            var sum = 0L
            for (bb in data) sum += (bb.toInt() and 0xFF)

            val avgRaw = sum.toDouble() / data.size.toDouble()
            val avg = filter.apply(avgRaw)

            val delta = abs(avg - lastAvg)
            lastAvg = avg

            if (delta > 15) {
                image.close()
                return
            }

            val time = System.currentTimeMillis()
            onSample(time, avg)

            image.close()
        } catch (e: Exception) {
            image.close()
        }
    }


// ----------------------------------------------------------
// BPM ESTIMATION
// ----------------------------------------------------------

    fun estimateBpm(samples: List<Pair<Long, Double>>): Int? {
        if (samples.size < 20) return null

        val values = samples.map { it.second }
        val times = samples.map { it.first }

        val minV = values.minOrNull() ?: return null
        val maxV = values.maxOrNull() ?: return null
        if (maxV - minV < 5.0) return null

        val threshold = minV + (maxV - minV) * 0.6

        var peaks = 0
        for (i in 1 until values.size - 1) {
            val prev = values[i - 1]
            val cur = values[i]
            val next = values[i + 1]

            if (cur > prev && cur > next && cur > threshold) {
                peaks++
            }
        }

        if (peaks < 2) return null

        val durationMs = (times.last() - times.first()).coerceAtLeast(1L)
        val bpm = (peaks * 60_000.0 / durationMs).toInt()

        return bpm.takeIf { it in 40..190 }
    }

// ----------------------------------------------------------
// RESULT HEART (BPM inside 3D heart)
// ----------------------------------------------------------

    @Composable
    fun ResultHeart(bpm: Int) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            Canvas(modifier = Modifier.size(260.dp)) {
                drawCircle(Color(0xFFFFF0F0), radius = size.minDimension / 2)
            }
            Canvas(modifier = Modifier.size(210.dp)) {
                drawCircle(Color(0xFFFFF7F7), radius = size.minDimension / 2)
            }
            Canvas(modifier = Modifier.size(160.dp)) {
                drawCircle(Color.White, radius = size.minDimension / 2)
            }

            Image(
                painter = painterResource(id = R.drawable.heart3d),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = "$bpm bpm",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

// ----------------------------------------------------------
// ECG WAVE WITH GRID + FADE
// ----------------------------------------------------------

    @Composable
    fun ECGWaveCompose(samples: List<Pair<Long, Double>>) {

        val maxPoints = 120
        val visible = samples.takeLast(maxPoints)

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 20.dp)
        ) {
            val w = size.width
            val h = size.height

            val stepX = w / 30f
            val stepY = h / 8f
            for (x in 0..30) {
                drawLine(
                    color = Color(0xFFEDEDED),
                    start = Offset(x * stepX, 0f),
                    end = Offset(x * stepX, h),
                    strokeWidth = 1f
                )
            }
            for (y in 0..8) {
                drawLine(
                    color = Color(0xFFEDEDED),
                    start = Offset(0f, y * stepY),
                    end = Offset(w, y * stepY),
                    strokeWidth = 1f
                )
            }

            if (visible.isEmpty()) {
                val midY = h / 2f
                drawLine(
                    color = Color.Red.copy(alpha = 0.3f),
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    strokeWidth = 3f
                )
                return@Canvas
            }

            val minV = visible.minOf { it.second }
            val maxV = visible.maxOf { it.second }.coerceAtLeast(minV + 1.0)
            val dx = w / (maxPoints - 1)

            val fadePath = Path()

            visible.forEachIndexed { index, (_, value) ->
                val x = index * dx
                val norm = (value - minV) / (maxV - minV)
                val y = h * (1f - norm.toFloat())

                if (index == 0) fadePath.moveTo(x, y) else fadePath.lineTo(x, y)
            }

            drawPath(
                path = fadePath,
                color = Color.Red.copy(alpha = 0.2f),
                style = Stroke(width = 3f)
            )

            val cutoff = (visible.size * 0.7f).toInt()
            val activePath = Path()
            visible.forEachIndexed { index, (_, value) ->
                if (index >= cutoff) {
                    val x = index * dx
                    val norm = (value - minV) / (maxV - minV)
                    val y = h * (1f - norm.toFloat())
                    if (index == cutoff) activePath.moveTo(x, y) else activePath.lineTo(x, y)
                }
            }
            drawPath(
                path = activePath,
                color = Color.Red,
                style = Stroke(width = 4f)
            )
        }
    }

// ----------------------------------------------------------
// HISTORY TAB CONTENT  (Calendar + List + Disclaimer)
// ----------------------------------------------------------

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HistoryTabContent(
        onBackToMeasure: () -> Unit
    ) {
        val context = LocalContext.current

        var records by remember { mutableStateOf(emptyList<HeartRateRecord>()) }
        var currentCalendar by remember { mutableStateOf(Calendar.getInstance()) }
        var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
        var showDisclaimer by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            records = HeartRateStorage.getAllRecords(context)
        }

        val todayCalendar = Calendar.getInstance()
        val monthFormatter = remember { SimpleDateFormat("MMM yyyy", Locale.getDefault()) }
        val cardDateFormatter =
            remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
        val selectedDateFormatter =
            remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

        val dayRecords = remember(records, selectedCalendar) {
            records.filter { rec ->
                val recCal = Calendar.getInstance().apply { timeInMillis = rec.timestamp }
                recCal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                        recCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(12.dp))

            // ---------- TOP SUMMARY CARD ----------
            if (dayRecords.isNotEmpty()) {
                val latest = dayRecords.maxBy { it.timestamp }
                val statusText = when (latest.status) {
                    HeartStatus.LOW -> "Slow"
                    HeartStatus.HEALTHY -> "Healthy"
                    HeartStatus.HIGH -> "High"
                }
                val statusColor = when (latest.status) {
                    HeartStatus.LOW -> Color(0xFFFFA726)
                    HeartStatus.HEALTHY -> Color(0xFF4CAF50)
                    HeartStatus.HIGH -> Color(0xFFF44336)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color(0xFFFFE5E5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.heart3d),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedDateFormatter.format(selectedCalendar.time),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "${latest.bpm} Bpm",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = selectedDateFormatter.format(selectedCalendar.time),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "No record for this day",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onBackToMeasure,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x146A5DF6),
                    contentColor = Color(0xFF6A5DF6)
                )
            ) {
                Text("Test Now", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Bpm Calendar",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthFormatter.format(currentCalendar.time),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "<",
                        modifier = Modifier
                            .clickable {
                                val newCal = currentCalendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, -1)
                                currentCalendar = newCal
                            }
                            .padding(8.dp),
                        fontSize = 18.sp
                    )
                    Text(
                        text = ">",
                        modifier = Modifier
                            .clickable {
                                val newCal = currentCalendar.clone() as Calendar
                                newCal.add(Calendar.MONTH, 1)
                                currentCalendar = newCal
                            }
                            .padding(8.dp),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val firstDayOfMonth =
                (currentCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
            val firstDayIndex = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1
            val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            for (row in 0 until 6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 0 until 7) {
                        val idx = row * 7 + col
                        val dayNum = idx - firstDayIndex + 1

                        if (dayNum < 1 || dayNum > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val dateCal = (currentCalendar.clone() as Calendar).apply {
                                set(
                                    Calendar.DAY_OF_MONTH,
                                    dayNum
                                )
                            }
                            val isSelected =
                                dateCal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                                        dateCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(
                                    Calendar.DAY_OF_YEAR
                                )
                            val hasRecord = records.any { rec ->
                                val recCal =
                                    Calendar.getInstance().apply { timeInMillis = rec.timestamp }
                                recCal.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR) &&
                                        recCal.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR)
                            }
                            val isToday =
                                dateCal.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                                        dateCal.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(
                                    Calendar.DAY_OF_YEAR
                                )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF6A5DF6)
                                            else -> Color(0xFFF5F5F5)
                                        }
                                    )
                                    .clickable {
                                        selectedCalendar = dateCal
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayNum.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                    if (hasRecord) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    if (isSelected) Color.White else Color(
                                                        0xFF6A5DF6
                                                    ), CircleShape
                                                )
                                        )
                                    } else if (isToday) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(
                                                    if (isSelected) Color.White else Color.LightGray,
                                                    CircleShape
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "History",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            if (dayRecords.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Record",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                Column {
                    dayRecords.sortedByDescending { it.timestamp }.forEach { rec ->
                        val statusText = when (rec.status) {
                            HeartStatus.LOW -> "Slow"
                            HeartStatus.HEALTHY -> "Healthy"
                            HeartStatus.HIGH -> "High"
                        }
                        val statusColor = when (rec.status) {
                            HeartStatus.LOW -> Color(0xFFFFA726)
                            HeartStatus.HEALTHY -> Color(0xFF4CAF50)
                            HeartStatus.HIGH -> Color(0xFFF44336)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${rec.bpm}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " bpm",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 3.dp)
                                )

                                Spacer(Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(2.dp)
                                        .background(statusColor)
                                )

                                Spacer(Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = cardDateFormatter.format(Date(rec.timestamp)),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Disclaimer",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { showDisclaimer = true }
                    .padding(vertical = 12.dp),
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (showDisclaimer) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showDisclaimer = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Disclaimer",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Close",
                            color = Color(0xFF6A5DF6),
                            modifier = Modifier
                                .clickable { showDisclaimer = false }
                                .padding(4.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "This heart rate feature uses your phone's camera and flash " +
                                "to estimate your pulse using photoplethysmography (PPG). " +
                                "Readings may be affected by movement, lighting, device hardware, " +
                                "and finger placement.\n\n" +
                                "This app is for wellness and informational purposes only and " +
                                "is not intended for medical diagnosis or emergency use. " +
                                "Always consult a healthcare professional for any health concerns.",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HeartRateScreen(navController: NavHostController) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        var hasPermission by rememberSaveable { mutableStateOf(false) }
        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        var measuring by rememberSaveable { mutableStateOf(false) }
        var finalResultBpm by rememberSaveable { mutableStateOf<Int?>(null) }
        var samples by remember { mutableStateOf(listOf<Pair<Long, Double>>()) }
        var fingerPresent by remember { mutableStateOf(false) }
        var progress by remember { mutableFloatStateOf(0f) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                hasPermission = granted
                if (!granted) {
                    Toast.makeText(context, "Camera permission needed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        LaunchedEffect(Unit) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                hasPermission = true
            } else {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }

        if (measuring && hasPermission) {
            val previewView = remember { PreviewView(context) }

            DisposableEffect(Unit) {
                val analyzer = HeartRateAnalyzer(
                    onSample = { t, v ->

                        // ✅ if finger removed, stop everything
                        if (!fingerPresent) {
                            samples = emptyList()
                            progress = 0f
                            return@HeartRateAnalyzer
                        }

                        samples = (samples + (t to v)).takeLast(200)
                        progress += 0.005f
                    },

                    onFingerPresent = { present ->
                        fingerPresent = present
                        if (!present) {
                            progress = 0f
                            samples = emptyList()
                        }
                    }

                )

                val stopCamera = startHeartRateCamera(
                    context,
                    lifecycleOwner,
                    Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    },
                    analyzer
                )

                onDispose {
                    stopCamera()
                }
            }
            LaunchedEffect(fingerPresent) {
                if (!fingerPresent && measuring) {
                    samples = emptyList()
                    progress = 0f
                }
            }

            LaunchedEffect(progress) {
                if (progress >= 1.0f) {
                    measuring = false
                    val bpm = estimateBpmFFT(samples) ?: estimateBpm(samples)
                    if (bpm != null) {
                        val confidence = calculateConfidence(samples)
                        if (confidence >= 60) {
                            finalResultBpm = bpm
                            val status = classifyHeartRate(bpm)
                            HeartRateStorage.addRecord(context, bpm, status)
                        } else {
                            Toast.makeText(
                                context,
                                "Measurement unstable, try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    progress = 0f
                    samples = emptyList()
                }
            }

            AndroidView(
                factory = { previewView },
                modifier = Modifier.size(1.dp).alpha(0f)
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Heart Rate Test", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FE))
                )
            },
            containerColor = Color(0xFFF8F9FE)
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .background(Color(0xFFEBEBEB), RoundedCornerShape(25.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(if (selectedTab == 0) Color.White else Color.Transparent)
                            .clickable {
                                selectedTab = 0
                                finalResultBpm = null
                                measuring = false
                                progress = 0f
                                samples = emptyList()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Measure",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == 0) Color.Black else Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(if (selectedTab == 1) Color.White else Color.Transparent)
                            .clickable { selectedTab = 1 },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "History",
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == 1) Color.Black else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Recent record",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (selectedTab == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(30.dp))

                        if (finalResultBpm != null && !measuring) {
                            ResultHeart(bpm = finalResultBpm!!)
                            Spacer(Modifier.height(30.dp))
                            Text(
                                text = "Measurement Complete",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(10.dp))
                            val status = classifyHeartRate(finalResultBpm!!)
                            val (msg, color) = when (status) {
                                HeartStatus.LOW -> "Your heart rate is lower than average." to Color(
                                    0xFFFFA726
                                )

                                HeartStatus.HEALTHY -> "Your heart rate is normal." to Color(
                                    0xFF4CAF50
                                )

                                HeartStatus.HIGH -> "Your heart rate is higher than average." to Color(
                                    0xFFF44336
                                )
                            }
                            Text(
                                text = msg,
                                color = color,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(40.dp))
                            Button(
                                onClick = {
                                    finalResultBpm = null
                                    measuring = false
                                    progress = 0f
                                    samples = emptyList()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF6A5DF6))
                            ) {
                                Text(
                                    "Measure Again",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (measuring) {
                            Spacer(Modifier.height(10.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(280.dp)
                            ) {
                                RippleWaves()
                                CircularProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.size(260.dp),
                                    strokeWidth = 8.dp,
                                    color = Color(0xFFFF5252),
                                    trackColor = Color(0xFFFFEBEE)
                                )
                                AnimatedHeart(isBeating = true)
                            }
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (fingerPresent) "Detecting pulse..." else "No Finger Detected",
                                fontSize = 16.sp,
                                color = if (fingerPresent) Color.Gray else Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(20.dp))
                            ECGWaveCompose(samples)
                            Spacer(Modifier.height(20.dp))
                            Text(
                                text = if (fingerPresent) "Detecting pulse..." else "Place your finger on the camera",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            if (!fingerPresent) {
                                Text(
                                    text = "(Cover the back camera and flash)",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Spacer(Modifier.height(40.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(280.dp)
                                    .clickable {
                                        if (hasPermission) {
                                            measuring = true
                                            progress = 0f
                                            samples = emptyList()
                                        } else {
                                            launcher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                            ) {
                                RippleWaves()
                                AnimatedHeart(isBeating = false)
                            }
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = "Tap heart\nstart measure",
                                textAlign = TextAlign.Center,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    HistoryTabContent(
                        onBackToMeasure = { selectedTab = 0 }
                    )
                }
            }
        }
    }

    fun estimateBpmFFT(samples: List<Pair<Long, Double>>): Int? {
        if (samples.size < 64) return null
        val values = samples.map { it.second }
        val n = values.size
        val mean = values.average()
        val signal = values.map { it - mean }
        val magnitudes = DoubleArray(n / 2)
        for (k in magnitudes.indices) {
            var real = 0.0
            var imag = 0.0
            for (i in signal.indices) {
                val angle = 2.0 * PI * k * i / n
                real += signal[i] * cos(angle)
                imag -= signal[i] * sin(angle)
            }
            magnitudes[k] = sqrt(real * real + imag * imag)
        }
        val durationSec = (samples.last().first - samples.first().first) / 1000.0
        val freqResolution = 1.0 / durationSec
        val minIndex = (0.7 / freqResolution).toInt()
        val maxIndex = (3.5 / freqResolution).toInt().coerceAtMost(magnitudes.size - 1)
        val peakIndex = (minIndex..maxIndex).maxByOrNull { magnitudes[it] } ?: return null
        val bpm = (peakIndex * freqResolution * 60).toInt()
        return bpm.takeIf { it in 40..190 }
    }

    fun calculateConfidence(samples: List<Pair<Long, Double>>): Int {
        if (samples.size < 50) return 0
        val values = samples.map { it.second }
        val variance = values.zipWithNext { a, b -> abs(a - b) }.average()
        val duration = (samples.last().first - samples.first().first) / 1000.0
        var score = 100
        if (variance > 12) score -= 30
        if (duration < 10) score -= 30
        return score.coerceIn(0, 100)
    }

    @Composable
    fun AnimatedHeart(isBeating: Boolean) {
        val scale by animateFloatAsState(
            targetValue = if (isBeating) 1.15f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "heartBeat"
        )
        Image(
            painter = painterResource(id = R.drawable.heart3d),
            contentDescription = "Heart",
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
        )
    }

    @Composable
    fun RippleWaves() {
        val infiniteTransition = rememberInfiniteTransition(label = "ripple")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing)
            ),
            label = "scale"
        )
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500)
            ),
            label = "alpha"
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(scale)
                .background(
                    color = Color.Red.copy(alpha = alpha),
                    shape = CircleShape
                )
        )
    }

    fun ImageProxy.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 60, out)
        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}