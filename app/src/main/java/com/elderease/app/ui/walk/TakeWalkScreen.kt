package com.elderease.app.ui.walk

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.core.content.ContextCompat
import java.util.Calendar
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeWalkScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    /* ---------- PERMISSION ---------- */

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
            if (!granted) permanentlyDenied = true
        }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    /* ---------- SENSOR ---------- */

    val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var steps by remember { mutableStateOf(0) }
    var baseSteps by remember { mutableStateOf(0f) }

    // ✅ API 24 SAFE DATE
    var lastResetDay by remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
    }

    val goal = 6000
    val progress by animateFloatAsState(
        targetValue = steps.toFloat() / goal,
        label = ""
    )

    val distance = steps * 0.0007
    val calories = steps * 0.04
    val minutes = steps / 100

    if (hasPermission) {
        DisposableEffect(stepSensor) {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val calendar = Calendar.getInstance()
                    val today = calendar.get(Calendar.DAY_OF_YEAR)
                    val totalSteps = event.values[0]

                    // 🔄 DAILY RESET
                    if (today != lastResetDay) {
                        baseSteps = totalSteps
                        steps = 0
                        lastResetDay = today
                        return
                    }

                    if (baseSteps == 0f) baseSteps = totalSteps
                    steps = min((totalSteps - baseSteps).toInt(), goal)
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            stepSensor?.let {
                sensorManager.registerListener(
                    listener,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    /* ---------- PERMISSION BLOCKED ---------- */

    if (!hasPermission && permanentlyDenied) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Permission required to track walking")
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }) {
                Text("Open Settings")
            }
        }
        return
    }

    /* ---------- UI ---------- */

    Scaffold(
        containerColor = Color(0xFFF7F1FF),
        topBar = {
            TopAppBar(
                title = { Text("Take a Walk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            Box(contentAlignment = Alignment.Center) {

                Canvas(modifier = Modifier.size(280.dp)) {
                    drawArc(
                        color = Color(0xFFE0E0FF),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(28f)
                    )

                    drawArc(
                        color = Color(0xFF8D86FF),
                        startAngle = 135f,
                        sweepAngle = 270f * progress,
                        useCenter = false,
                        style = Stroke(28f, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        null,
                        tint = Color(0xFF8D86FF),
                        modifier = Modifier.size(48.dp)
                    )
                    Text("$steps", fontSize = 38.sp, fontWeight = FontWeight.Bold)
                    Text("steps today", color = Color.Gray)
                    Text("Goal $goal", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                WalkMetric("Distance", "%.2f km".format(distance), Icons.Default.Route)
                WalkMetric("Calories", "%.0f".format(calories), Icons.Default.LocalFireDepartment)
                WalkMetric("Time", "${minutes} min", Icons.Default.Timer)
            }
        }
    }
}

@Composable
fun WalkMetric(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color(0xFF6C63FF))
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}
