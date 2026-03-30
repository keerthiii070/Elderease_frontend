package com.elderease.app.ui.main

import com.elderease.app.ui.network.ApiClient
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ArrowForwardIos
import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.content.Context
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import androidx.navigation.compose.currentBackStackEntryAsState
import com.elderease.app.ui.viewmodel.ProfileViewModel
import com.elderease.app.data.DrinkWaterViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import com.elderease.app.*
import com.elderease.app.data.BloodPressureViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/* --------------------------------------------------
   ROUTES (LOCAL – NO SEPARATE FILE)
-------------------------------------------------- */


@Composable
fun HomeScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel(),
    bloodPressureViewModel: BloodPressureViewModel = viewModel(factory = BloodPressureViewModel.Factory),
    drinkWaterViewModel: DrinkWaterViewModel = viewModel(
        factory = DrinkWaterViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { TopSection(navController, profileViewModel) }
            item { DateSelectorSection() }
            item { DailyDrinkTargetSection(navController, drinkWaterViewModel) }
            item { HealthKitSection(navController, bloodPressureViewModel) }
            item { GuidesSection(navController) }
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}


@Composable
fun TopSection(
    navController: NavHostController,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current

    /* ---------- SESSION ---------- */
    val email = remember {
        context.getSharedPreferences(
            "elder_ease_prefs",
            Context.MODE_PRIVATE
        ).getString("user_email", null)
    }

    /* ---------- PROFILE STATEu ---------- */
    val profile by profileViewModel.profile.collectAsState()

    /* ---------- LOAD PROFILE ONCE ---------- */
    LaunchedEffect(email) {
        email?.let { profileViewModel.loadProfile(it) }
    }

    /* ---------- USER NAME ---------- */
    val userName = profile?.fullName?.takeIf { it.isNotBlank() } ?: "User"

    /* ---------- PROFILE IMAGE URL ---------- */
    val profileImageUrl = profile?.profileImage?.let {
        ApiClient.profileImageBaseUrl + it
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Color(0xFFA084F4))
            .padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                /* ---------- GREETING ---------- */
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello $userName 👋",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "You're closer to your goals today!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                /* ---------- PROFILE IMAGE ---------- */
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable {
                            navController.navigate(ROUTE_ELDER_PROFILE)
                        },
                    placeholder = painterResource(R.drawable.profile),
                    error = painterResource(R.drawable.profile),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun DateSelectorSection() {
    val today = remember { Calendar.getInstance() }

    // 🔥 This controls which month is currently shown
    var currentMonth by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                timeInMillis = today.timeInMillis
                set(Calendar.DAY_OF_MONTH, 1) // start of month
            }
        )
    }

    // 🔥 Dates are generated based on currentMonth (whole month dates)
    val dates = remember(currentMonth) {
        val temp = currentMonth.clone() as Calendar
        val maxDays = temp.getActualMaximum(Calendar.DAY_OF_MONTH)

        (1..maxDays).map { day ->
            Calendar.getInstance().apply {
                timeInMillis = temp.timeInMillis
                set(Calendar.DAY_OF_MONTH, day)
            }
        }
    }

    var selectedDate by remember {
        mutableLongStateOf(today.timeInMillis)
    }

    // 🔥 scroll to selected date (or first date) when month changes
    val listState = rememberLazyListState()

    LaunchedEffect(currentMonth) {
        val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }

        val indexToScroll = if (
            selectedCal.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
            selectedCal.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
        ) {
            selectedCal.get(Calendar.DAY_OF_MONTH) - 1
        } else {
            0
        }

        listState.scrollToItem(indexToScroll)
    }

    val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayNameFormatter = remember { SimpleDateFormat("EEE", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp)
    ) {
        // ---------- HEADER (Month & Arrows) ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color(0xFF6B4EA1),
                modifier = Modifier.clickable {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = monthFormatter.format(currentMonth.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF6B4EA1),
                modifier = Modifier.clickable {
                    currentMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- DATE ROW ----------
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(dates) { calendar ->
                val isSelected = isSameDay(calendar.timeInMillis, selectedDate)
                DateItem(
                    calendar = calendar,
                    isSelected = isSelected,
                    dayNameFormatter = dayNameFormatter
                ) {
                    selectedDate = calendar.timeInMillis
                }
            }
        }
    }
}

@Composable
fun DateItem(
    calendar: Calendar,
    isSelected: Boolean,
    dayNameFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    // Colors based on the provided image
    val selectedPurple = Color(0xFF6B4EA1)
    val unselectedBg = Color(0xFFF3E9FF) // Light lavender background
    val unselectedText = Color(0xFF4A4A4A)

    Box(
        modifier = Modifier
            .width(65.dp) // Adjusted width for the circular overlap
            .height(110.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // The Background Card
        Surface(
            modifier = Modifier
                .width(55.dp)
                .height(90.dp),
            shape = RoundedCornerShape(30.dp),
            color = unselectedBg
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dayNameFormatter.format(calendar.time),
                    fontSize = 14.sp,
                    color = if (isSelected) Color.Transparent else unselectedText
                )
                Text(
                    text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Transparent else unselectedText
                )
            }
        }

        // The Selection Overlay (Circle and label)
        if (isSelected) {
            Surface(
                modifier = Modifier
                    .width(64.dp)
                    .height(95.dp),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .padding(2.dp)
                            .background(selectedPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // The small dot under the number
                            Box(modifier = Modifier.size(3.dp).background(Color.White, CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = dayNameFormatter.format(calendar.time),
                        color = unselectedText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Helper function to compare dates without time
private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun DailyDrinkTargetSection(
    navController: NavHostController,
    drinkWaterViewModel: DrinkWaterViewModel
) {

    /* ---------- LOAD TODAY DATA ---------- */
    LaunchedEffect(Unit) {
        drinkWaterViewModel.loadToday()
    }

    /* ---------- DATA FROM API ---------- */
    val dailyTarget by drinkWaterViewModel.dailyTarget.collectAsState()
    val currentIntake by drinkWaterViewModel.currentIntake.collectAsState()

    /* ---------- PROGRESS ---------- */
    val progress = remember(currentIntake, dailyTarget) {
        if (dailyTarget > 0)
            (currentIntake.toFloat() / dailyTarget).coerceIn(0f, 1f)
        else 0f
    }

    /* ---------- GLASSES ---------- */
    val glasses = currentIntake / 200

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navController.navigate(ROUTE_DRINK_WATER) },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Image(
                painter = painterResource(id = R.drawable.drinktargetwave),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .align(Alignment.BottomCenter),
                alpha = 0.6f
            )

            Column(modifier = Modifier.padding(15.dp)) {

                /* ---------- TOP ROW ---------- */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hydration Hub",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF2E2E5D)
                        )
                        Text(
                            text = "$currentIntake ml water ($glasses Glasses)",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    /* ---------- MOVING CIRCLE ---------- */
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(100.dp),
                            strokeWidth = 10.dp,
                            color = Color(0xFF8E6CEF),
                            trackColor = Color(0xFFE0E0E0).copy(alpha = 0.5f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$dailyTarget ml",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                            Text(
                                text = "$currentIntake ml",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                /* ---------- ACTION BUTTONS (KEEP BOTH) ---------- */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    DrinkActionCard(
                        title = "Stay hydrated!!",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(ROUTE_DRINK_WATER)
                            },
                        gradientColors = listOf(
                            Color(0xFFB18CFE),
                            Color(0xFF8E6CEF)
                        )
                    )

                    DrinkActionCard(
                        title = "Grab Your Juice",
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate(ROUTE_JUICE_REMINDER)
                            },
                        gradientColors = listOf(
                            Color(0xFFB18CFE),
                            Color(0xFF8E6CEF)
                        ),
                        isJuice = true
                    )
                }
            }
        }
    }
}

@Composable
fun DrinkActionCard(
    title: String,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    isJuice: Boolean = false
) {
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
    ) {
        // Subtle wave pattern overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.75f)
                quadraticTo(
                    size.width * 0.25f, size.height * 0.65f,
                    size.width * 0.5f, size.height * 0.75f
                )
                quadraticTo(
                    size.width * 0.75f, size.height * 0.85f,
                    size.width, size.height * 0.75f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, Color.White.copy(alpha = 0.15f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isJuice) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    if (isJuice) {
                        Text(
                            text = "200-400ml",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "Tap Here",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                
                if (isJuice) {
                    Image(
                        painter = painterResource(id = R.drawable.juice),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HealthKitSection(navController: NavHostController, bloodPressureViewModel: BloodPressureViewModel) {
    val bpRecords by bloodPressureViewModel.allRecords.collectAsState()
    val latestBP = bpRecords.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Wellness Hub",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
        }

        /* ---------- SCROLLABLE CARDS ---------- */
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            item {
                BloodPressureCard(
                    reading = if (latestBP != null) "${latestBP.systolic}/${latestBP.diastolic}" else "120/80",
                    lastUpdated = if (latestBP != null) {
                        val diff = System.currentTimeMillis() - latestBP.timestamp
                        val minutes = (diff / 60000).toInt()
                        if (minutes < 60) "last update ${minutes}m"
                        else if (minutes < 1440) "last update ${minutes / 60}h"
                        else "last update ${minutes / 1440}d"
                    } else "no data",
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        navController.navigate(ROUTE_BLOOD_PRESSURE)
                    }
                )
            }
            item {
                HeartRateCard(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        navController.navigate(ROUTE_HEART_RATE)
                    }
                )
            }

            item {
                BMICalculatorCard(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        navController.navigate(ROUTE_BMI_CALCULATOR)
                    }
                )
            }
            item {
                JointArthritisTrackerCard(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        navController.navigate(ROUTE_PAIN_LOG)
                    }
                )
            }
            item {
                YogaCard(
                    modifier = Modifier.width(150.dp),
                    onClick = {
                        navController.navigate(ROUTE_YOGA)
                    }
                )
            }

        }
    }
}

@Composable
fun YogaCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFFF4F2FF))
                        )
                    )
            )

            Image(
                painter = painterResource(id = R.drawable.drinktargetwave),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .align(Alignment.BottomCenter),
                alpha = 0.3f
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFE8E4FF), Color(0xFFD6CFFF).copy(alpha = 0.6f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.yoga),
                        contentDescription = "Yoga",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Yoga",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.height(35.dp).padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val heights = listOf(0.25f, 0.45f, 0.65f, 0.85f, 1.0f)
                    heights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(h)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFBB86FC), Color(0xFF7E57C2))
                                    )
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Mindful Minutes",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7E57C2)
                )
            }
        }
    }
}

@Composable
fun JointArthritisTrackerCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF9C4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arthritis),
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.6.dp))
                Text("Joint & Arthritis\nTracker", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF455A64), lineHeight = 11.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9).copy(alpha = 0.5f))
                    .padding(6.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text("Avg ", fontSize = 8.sp, color = Color.Gray)
                        Text("5.3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF455A64))
                    }
                    
                    Canvas(modifier = Modifier.fillMaxWidth().height(30.dp)) {
                        val points = listOf(0.6f, 0.4f, 0.55f, 0.7f, 0.5f, 0.35f, 0.25f)
                        val width = size.width
                        val height = size.height
                        val path = Path().apply {
                            moveTo(0f, height * points[0])
                            for (i in 1 until points.size) {
                                lineTo(width * (i.toFloat() / (points.size - 1)), height * points[i])
                            }
                        }
                        
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(
                            fillPath,
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFAED581).copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                        
                        drawPath(
                            path,
                            color = Color(0xFFAED581),
                            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        for (i in points.indices) {
                            drawCircle(
                                color = Color.White,
                                radius = 2.dp.toPx(),
                                center = Offset(width * (i.toFloat() / (points.size - 1)), height * points[i])
                            )
                            drawCircle(
                                color = Color(0xFFAED581),
                                radius = 2.dp.toPx(),
                                center = Offset(width * (i.toFloat() / (points.size - 1)), height * points[i]),
                                style = Stroke(width = 0.5.dp.toPx())
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFC8E6C9)), contentAlignment = Alignment.Center) {
                            Text("😊", fontSize = 6.sp)
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("1", "3", "6").forEach {
                                Text(it, fontSize = 8.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFFF9C4)), contentAlignment = Alignment.Center) {
                            Text("☹️", fontSize = 6.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BMICalculatorCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Balance,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("BMI Calculator", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Row(verticalAlignment = Alignment.Bottom) {
                Text("24.8", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Normal", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 2.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // Speedometer-like visualization
            Box(modifier = Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.BottomCenter) {
                Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    val strokeWidth = 6.dp.toPx()
                    val arcSize = Size(size.width, size.height * 2)
                    
                    val gradient = Brush.sweepGradient(
                        0.0f to Color(0xFFAED581),
                        0.5f to Color(0xFF4FC3F7),
                        1.0f to Color(0xFFFF8A65)
                    )
                    
                    drawArc(
                        brush = gradient,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = arcSize,
                        topLeft = Offset(0f, 0f)
                    )
                    
                    val center = Offset(size.width / 2, size.height)
                    val angleInDegrees = 180f + (180f * 0.45f)
                    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
                    val needleLength = size.height * 0.7f
                    val needleEnd = Offset(
                        (center.x + needleLength * cos(angleInRadians)).toFloat(),
                        (center.y + needleLength * sin(angleInRadians)).toFloat()
                    )
                    
                    drawLine(
                        color = Color(0xFF37474F),
                        start = center,
                        end = needleEnd,
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF37474F)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HeartRateCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = null,
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.6.dp))
                Text("Heart Rate", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("98", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                Spacer(modifier = Modifier.width(2.dp))
                Text("bpm", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                val heights = listOf(15.dp, 25.dp, 18.dp, 35.dp, 22.dp, 28.dp, 18.dp)
                heights.forEachIndexed { index, h ->
                    Box(modifier = Modifier
                        .width(7.dp)
                        .height(h)
                        .background(if (index == 3) Color(0xFFEF5350) else Color(0xFFFFCDD2), RoundedCornerShape(3.dp)))
                }
            }
        }
    }
}

@Composable
fun BloodPressureCard(reading: String, lastUpdated: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text("BLOOD PRESSURE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), contentAlignment = Alignment.Center) {
                Image(painterResource(id = R.drawable.heartcg), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(reading, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(lastUpdated, color = Color.Gray, fontSize = 9.sp)
        }
    }
}

@Composable
fun GuidesSection(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {

        /* ----------- HEADER ROW ----------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Medical & Cautions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* ----------- BIG CARD 1 ----------- */
        HealthServiceBigCard(
            title = "Medical Reports",
            subtitle = "Understand your medical reports\nterms",
            iconBg = Color(0xFF2563EB),
            icon = Icons.Default.BarChart,
            showArrow = true,
            onClick = { navController.navigate(ROUTE_MEDICAL_AWARENESS) }
        )

        Spacer(modifier = Modifier.height(14.dp))

        /* ----------- MIDDLE 2 SMALL CARDS ----------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            HealthServiceSmallCard(
                modifier = Modifier.weight(1f),
                title = "Health Conditions",
                subtitle = "Learn about health\nissues",
                iconBg = Color(0xFF16A34A),
                icon = Icons.Default.ShowChart,
                onClick = { navController.navigate(ROUTE_MEDICAL_CONDITIONS) }
            )

            HealthServiceSmallCard(
                modifier = Modifier.weight(1f),
                title = "Safe Meds",
                subtitle = "Tips on safe medication use",
                iconBg = Color(0xFFF59E0B),
                icon = Icons.Default.Medication,
                onClick = { navController.navigate(ROUTE_MEDICINE_SAFETY) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        /* ----------- BIG CARD 2 ----------- */
        HealthServiceBigCard(
            title = "MedHub Discovery",
            subtitle = "Find hospitals & check medicine\nprices",
            iconBg = Color(0xFF6366F1),
            icon = Icons.Default.LocalHospital,
            showArrow = true,
            onClick = { navController.navigate(ROUTE_NAVIGATION_HUB) }
        )
    }
}


/* =========================================================
   BIG CARD UI (Top + Bottom)
========================================================= */
@Composable
fun HealthServiceBigCard(
    title: String,
    subtitle: String,
    iconBg: Color,
    icon: ImageVector,
    showArrow: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                    lineHeight = 14.sp
                )
            }

            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Go",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


/* =========================================================
   SMALL CARD UI (Middle Two Cards)
========================================================= */
@Composable
fun HealthServiceSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconBg: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f) // makes it square like image
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF6B7280),
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun GuideCard(
    title: String,
    subtitle: String,
    titleColor: Color,
    modifier: Modifier = Modifier,
    backgroundImageRes: Int
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {

            /* 🔥 FORCE IMAGE TO LOOK BIGGER */
            Image(
                painter = painterResource(backgroundImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .graphicsLayer {
                        scaleX = 1.25f   // ⭐ INCREASE THIS
                        scaleY = 1.25f   // ⭐ INCREASE THIS
                    }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3
                )
            }
        }
    }
}


@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        /* ---------- HOME ---------- */
        NavigationBarItem(
            selected = currentRoute == ROUTE_HOME,
            onClick = {
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_HOME) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.homeicon),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Home") }
        )

        /* ---------- DIET PLANNER ---------- */
        NavigationBarItem(
            selected = currentRoute == ROUTE_DIET_PLANNER,
            onClick = {
                navController.navigate(ROUTE_DIET_PLANNER) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.dietrem),
                    contentDescription = "Diet Planner",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Diet Planner") }
        )

        /* ---------- MEDSCAN ---------- */
        NavigationBarItem(
            selected = currentRoute == ROUTE_MEDSCAN,
            onClick = {
                navController.navigate(ROUTE_MEDSCAN) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.navmed),
                    contentDescription = "MedScan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("MedScan") }
        )

        /* ---------- SOS ---------- */
        NavigationBarItem(
            selected = currentRoute == ROUTE_EMERGENCY_ASSIST,
            onClick = {
                navController.navigate(ROUTE_EMERGENCY_ASSIST) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.sosicon),
                    contentDescription = "SOS",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("SOS") }
        )
    }
}
