package com.elderease.app.ui.main

import android.app.TimePickerDialog
import android.widget.Toast
import android.app.Application
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ui.alarm.cancelJuiceReminder
import com.elderease.app.ui.alarm.scheduleJuiceReminder
import com.elderease.app.ui.viewmodel.JuiceViewModel
import kotlin.math.sin

/* --------------------------------------------------
   COLORS
-------------------------------------------------- */

private val JuiceOrange = Color(0xFFFF9800)
private val LightJuiceOrange = Color(0xFFFFCC80)
private val ScreenBg = Color(0xFFFFFBF0)

/* --------------------------------------------------
   MODEL
-------------------------------------------------- */

data class JuiceItem(
    val day: String,
    val name: String,
    val benefit: String,
    val iconRes: Int,
    val tint: Color
)

/* --------------------------------------------------
   SCREEN
-------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuiceReminderScreen(
    navController: NavHostController,
    juiceViewModel: JuiceViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current

    /* ---------- LOAD TODAY DATA ---------- */
    LaunchedEffect(Unit) {
        juiceViewModel.loadToday()
    }

    val currentIntake by juiceViewModel.currentIntake.collectAsState()
    val dailyLimit by juiceViewModel.dailyLimit.collectAsState()

    val fillPercentage by animateFloatAsState(
        targetValue = (currentIntake.toFloat() / dailyLimit).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "juiceFill"
    )

    var reminderOn by remember { mutableStateOf(true) }

    // ✅ Load saved juice reminder time
    var juiceHour by remember {
        mutableIntStateOf(com.elderease.app.ui.alarm.ReminderPrefs.getJuiceHour(context))
    }
    var juiceMinute by remember {
        mutableIntStateOf(com.elderease.app.ui.alarm.ReminderPrefs.getJuiceMinute(context))
    }


    // 7-Day Juice Plan
    val juices = remember {
        listOf(
            JuiceItem("Mon", "Orange", "Boosts Immunity & Vitamin C", R.drawable.drink, Color(0xFFFF9800)),
            JuiceItem("Tue", "Pomegranate", "Heart Health & Antioxidants", R.drawable.drink, Color(0xFFE91E63)),
            JuiceItem("Wed", "Beetroot", "Lowers Blood Pressure", R.drawable.drink, Color(0xFF9C27B0)),
            JuiceItem("Thu", "Apple", "Improves Digestion & Fiber", R.drawable.drink, Color(0xFF4CAF50)),
            JuiceItem("Fri", "Carrot", "Eye Health & Beta-Carotene", R.drawable.drink, Color(0xFFFF5722)),
            JuiceItem("Sat", "Watermelon", "Hydration & Vitamins", R.drawable.drink, Color(0xFFF44336)),
            JuiceItem("Sun", "Cranberry", "Supports Urinary Health", R.drawable.drink, Color(0xFFB71C1C))
        )
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Juice Reminder", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Track your daily juice intake", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Switch(
                        checked = reminderOn,
                        onCheckedChange = {
                            reminderOn = it
                            if (it) scheduleJuiceReminder(context)
                            else cancelJuiceReminder(context)
                        },
                        modifier = Modifier.scale(0.7f)
                    )
                }
            )
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            /* ---------- BACKGROUND WAVE ---------- */
            JuiceWaveBackground(fillPercentage)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Custom time picker button for Juice reminder
                Button(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                juiceHour = hour
                                juiceMinute = minute

                                // ✅ Save to prefs
                                com.elderease.app.ui.alarm.ReminderPrefs.saveJuiceTime(context, hour, minute)

                                // ✅ Restart alarm
                                cancelJuiceReminder(context)
                                scheduleJuiceReminder(context)

                                Toast.makeText(
                                    context,
                                    "Juice reminder set to %02d:%02d".format(hour, minute),
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            juiceHour,
                            juiceMinute,
                            false
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Set Juice Reminder Time")
                }

                Spacer(modifier = Modifier.height(16.dp))


                /* ---------- TODAY INTAKE CARD ---------- */
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "TODAY INTAKE",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$currentIntake",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = JuiceOrange
                            )
                            Text(
                                text = " / $dailyLimit ml",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        /* ---------- CONTROLS ---------- */
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = ScreenBg,
                            modifier = Modifier.fillMaxWidth().height(64.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                IconButton(onClick = { juiceViewModel.removeJuice(50) }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Remove Juice")
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(JuiceOrange.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalDrink,
                                        tint = JuiceOrange,
                                        contentDescription = null
                                    )
                                }

                                IconButton(onClick = { juiceViewModel.addJuice(50) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Juice")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "7-Day Juice Plan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )

                Spacer(modifier = Modifier.height(12.dp))

                /* ---------- JUICE PLAN LIST ---------- */
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(juices) { item ->
                        JuicePlanItem(item)
                    }
                }
            }
        }
    }
}

/* --------------------------------------------------
   COMPONENTS (UNCHANGED)
-------------------------------------------------- */

@Composable
fun JuicePlanItem(item: JuiceItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.day,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                modifier = Modifier.width(45.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = item.tint)
                Text(item.benefit, fontSize = 12.sp, color = Color.Gray)
            }

            Icon(
                painter = painterResource(id = R.drawable.drink),
                contentDescription = null,
                tint = item.tint,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = item.tint.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun JuiceWaveBackground(fillPercentage: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "juiceWave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val baseFillHeight = size.height * (1f - fillPercentage * 0.4f)

        val path = Path().apply {
            moveTo(0f, size.height)
            lineTo(0f, baseFillHeight)
            for (x in 0..size.width.toInt() step 5) {
                val y = baseFillHeight + 20 * sin((x * 0.01f + waveOffset).toDouble()).toFloat()
                lineTo(x.toFloat(), y)
            }
            lineTo(size.width, size.height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(listOf(LightJuiceOrange, JuiceOrange))
        )
    }
}
