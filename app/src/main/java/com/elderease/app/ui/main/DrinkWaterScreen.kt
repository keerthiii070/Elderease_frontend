package com.elderease.app.ui.main

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.app.Application
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.data.DrinkWaterViewModel
import com.elderease.app.ui.alarm.cancelWaterReminder
import com.elderease.app.ui.alarm.scheduleWaterReminder
import kotlin.math.sin

/* --------------------------------------------------
   COLORS
-------------------------------------------------- */
private val WaterBlue = Color(0xFF2196F3)
private val LightWaterBlue = Color(0xFFBBDEFB)
private val ScreenBg = Color(0xFFF0F8FF)

/* --------------------------------------------------
   SCREEN
-------------------------------------------------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkWaterScreen(
    navController: NavHostController,
    drinkWaterViewModel: DrinkWaterViewModel = viewModel(
        factory = DrinkWaterViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    /* ---------- STATE FROM BACKEND ---------- */
    val dailyTarget by drinkWaterViewModel.dailyTarget.collectAsState()
    val currentIntake by drinkWaterViewModel.currentIntake.collectAsState()
    val message by drinkWaterViewModel.uiMessage.collectAsState()

    /* ---------- UI STATE ---------- */
    var selectedCup by remember { mutableIntStateOf(200) }
    var reminderOn by remember { mutableStateOf(true) }

    var waterTimes by remember {
        mutableStateOf(com.elderease.app.ui.alarm.WaterTimePrefs.getWaterTimes(context))
    }

    var waterIntervalHours by remember {
        mutableIntStateOf(com.elderease.app.ui.alarm.ReminderPrefs.getWaterInterval(context))
    }

    var intervalMenuExpanded by remember { mutableStateOf(false) }

    // ✅ NEW: Dialog open/close state
    var showReminderDialog by remember { mutableStateOf(false) }

    /* ---------- LOAD TODAY DATA ---------- */
    LaunchedEffect(Unit) {
        drinkWaterViewModel.loadToday()
    }

    /* ---------- SNACKBAR ---------- */
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            drinkWaterViewModel.clearMessage()
        }
    }

    val fillPercentage by animateFloatAsState(
        targetValue = (currentIntake.toFloat() / dailyTarget).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "fill"
    )

    Scaffold(
        containerColor = ScreenBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Water Intake", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                actions = {

                    // ✅ Bell icon added
                    IconButton(onClick = { showReminderDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder Settings"
                        )
                    }

                    // Reminder Switch (same logic)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reminder", fontSize = 12.sp)
                        Switch(
                            checked = reminderOn,
                            onCheckedChange = {
                                reminderOn = it
                                if (it) {
                                    // schedule all saved times
                                    com.elderease.app.ui.alarm.cancelAllWaterReminders(context)

                                    waterTimes.forEachIndexed { index, t ->
                                        val parts = t.split(":")
                                        val h = parts[0].toInt()
                                        val m = parts[1].toInt()
                                        com.elderease.app.ui.alarm.scheduleWaterReminderAtTime(
                                            context,
                                            h,
                                            m,
                                            index
                                        )
                                    }

                                } else {
                                    com.elderease.app.ui.alarm.cancelAllWaterReminders(context)
                                }
                            },
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            )
        }
    ) { padding ->

        // ✅ Reminder Settings Dialog (Popup)
        if (showReminderDialog) {
            AlertDialog(
                onDismissRequest = { showReminderDialog = false },
                title = { Text("Water Reminder Settings") },
                text = {
                    Column {

                        // Add Reminder Time Button
                        Button(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val timeStr = "%02d:%02d".format(hour, minute)

                                        val updated = (waterTimes + timeStr).distinct().sorted()
                                        waterTimes = updated

                                        com.elderease.app.ui.alarm.WaterTimePrefs.saveWaterTimes(
                                            context,
                                            updated
                                        )

                                        com.elderease.app.ui.alarm.cancelAllWaterReminders(context)

                                        updated.forEachIndexed { index, t ->
                                            val parts = t.split(":")
                                            val h = parts[0].toInt()
                                            val m = parts[1].toInt()
                                            com.elderease.app.ui.alarm.scheduleWaterReminderAtTime(
                                                context,
                                                h,
                                                m,
                                                index
                                            )
                                        }

                                        Toast.makeText(
                                            context,
                                            "Water reminder added at $timeStr",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    14,
                                    0,
                                    false
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("➕ Add Reminder Time")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Times list + Delete icon
                        if (waterTimes.isNotEmpty()) {
                            Text("Reminder Times", fontWeight = FontWeight.SemiBold)

                            Spacer(modifier = Modifier.height(6.dp))

                            waterTimes.forEachIndexed { index, t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• $t", fontSize = 14.sp, color = Color.DarkGray)

                                    IconButton(
                                        onClick = {
                                            val updated = waterTimes.toMutableList().apply {
                                                removeAt(index)
                                            }
                                            waterTimes = updated

                                            com.elderease.app.ui.alarm.WaterTimePrefs.saveWaterTimes(
                                                context,
                                                updated
                                            )

                                            com.elderease.app.ui.alarm.cancelAllWaterReminders(context)

                                            updated.forEachIndexed { i, time ->
                                                val parts = time.split(":")
                                                val h = parts[0].toInt()
                                                val m = parts[1].toInt()
                                                com.elderease.app.ui.alarm.scheduleWaterReminderAtTime(
                                                    context,
                                                    h,
                                                    m,
                                                    i
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Time")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interval Dropdown
                        Text("Interval", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(6.dp))

                        Box {
                            OutlinedButton(
                                onClick = { intervalMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Every $waterIntervalHours hour(s)")
                            }

                            DropdownMenu(
                                expanded = intervalMenuExpanded,
                                onDismissRequest = { intervalMenuExpanded = false }
                            ) {
                                listOf(1, 2, 3, 4, 5, 6).forEach { hour ->
                                    DropdownMenuItem(
                                        text = { Text("Every $hour hour(s)") },
                                        onClick = {
                                            waterIntervalHours = hour

                                            com.elderease.app.ui.alarm.ReminderPrefs.saveWaterInterval(
                                                context,
                                                hour
                                            )

                                            cancelWaterReminder(context)
                                            scheduleWaterReminder(context)

                                            intervalMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showReminderDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /* ---------- BACKGROUND ---------- */
            WaterWaveBackground(fillPercentage)
            VerticalScale(dailyTarget)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(40.dp))

                Text(
                    text = "$currentIntake",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = WaterBlue
                )

                Text(
                    text = "/$dailyTarget ml (Daily Limit)",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(32.dp))

                /* ---------- CONTROL PANEL ---------- */
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, WaterBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {

                        IconButton(
                            onClick = {
                                if (currentIntake >= selectedCup) {
                                    drinkWaterViewModel.removeIntake(selectedCup)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Remove, null, tint = WaterBlue)
                        }

                        VerticalDivider(Modifier.fillMaxHeight())

                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.drink),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp)
                            )
                            Text("${selectedCup}ml", fontSize = 11.sp)
                        }

                        VerticalDivider(Modifier.fillMaxHeight())

                        IconButton(
                            onClick = {
                                drinkWaterViewModel.addIntake(selectedCup)
                            }
                        ) {
                            Icon(Icons.Default.Add, null, tint = WaterBlue)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Daily limit is set in Profile",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/* --------------------------------------------------
   HELPERS
-------------------------------------------------- */

@Composable
fun VerticalScale(target: Int) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(10) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(2.dp)
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }
        Text("$target ml", fontSize = 10.sp)
    }
}

@Composable
fun WaterWaveBackground(fillPercentage: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val baseFillHeight = height * (1f - fillPercentage)

        val path = Path().apply {
            moveTo(0f, height)
            lineTo(0f, baseFillHeight)
            for (x in 0..width.toInt() step 5) {
                val y =
                    baseFillHeight + 15 * sin((x * 0.015f + waveOffset).toDouble()).toFloat()
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                listOf(LightWaterBlue, WaterBlue)
            )
        )
    }
}

fun Modifier.scale(scale: Float) =
    this.then(Modifier.graphicsLayer(scaleX = scale, scaleY = scale))
