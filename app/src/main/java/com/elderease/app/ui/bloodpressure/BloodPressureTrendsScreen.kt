package com.elderease.app.ui.bloodpressure

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.data.BloodPressureRecord
import com.elderease.app.data.BloodPressureViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BloodPressureTrendsScreen(
    navController: NavHostController,
    viewModel: BloodPressureViewModel = viewModel(factory = BloodPressureViewModel.Factory)
) {
    val allRecords by viewModel.allRecords.collectAsState()
    var selectedTab by remember { mutableIntStateOf(1) } // 0: Daily, 1: Weekly, 2: Monthly

    // Filter and process records based on selected tab
    val filteredRecords = remember(allRecords, selectedTab) {
        val calendar = Calendar.getInstance()
        
        when (selectedTab) {
            0 -> { // Daily - entries from today
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                val todayStart = calendar.timeInMillis
                allRecords.filter { it.timestamp >= todayStart }.sortedBy { it.timestamp }
            }
            1 -> { // Weekly - last 7 days
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekAgo = calendar.timeInMillis
                allRecords.filter { it.timestamp >= weekAgo }.sortedBy { it.timestamp }
            }
            2 -> { // Monthly - last 30 days
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                val monthAgo = calendar.timeInMillis
                allRecords.filter { it.timestamp >= monthAgo }.sortedBy { it.timestamp }
            }
            else -> allRecords.sortedBy { it.timestamp }
        }
    }

    val averageSystolic = if (filteredRecords.isNotEmpty()) filteredRecords.map { it.systolic }.average().toInt() else 0
    val averageDiastolic = if (filteredRecords.isNotEmpty()) filteredRecords.map { it.diastolic }.average().toInt() else 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3DFFF), Color(0xFFF3E7FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2E2E5D),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Blood Pressure\nTrends",
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1617),
                    lineHeight = 26.sp
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color(0xFF6A4CFF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Tab Selector
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabItem("Daily", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                    TabItem("Weekly", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                    TabItem("Monthly", selectedTab == 2, Modifier.weight(1f)) { selectedTab = 2 }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val title = when(selectedTab) {
                                0 -> "Daily Outlook"
                                1 -> "Weekly Outlook"
                                else -> "Monthly Outlook"
                            }
                            Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1617)
                            )
                            val dateText = remember(selectedTab) {
                                val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                                val cal = Calendar.getInstance()
                                val end = sdf.format(cal.time)
                                when(selectedTab) {
                                    0 -> end
                                    1 -> {
                                        cal.add(Calendar.DAY_OF_YEAR, -7)
                                        "${sdf.format(cal.time)} - $end"
                                    }
                                    else -> {
                                        cal.add(Calendar.DAY_OF_YEAR, -30)
                                        "${sdf.format(cal.time)} - $end"
                                    }
                                }
                            }
                            Text(
                                text = dateText,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LegendItem("SYS", Color(0xFF8E76FF))
                            Spacer(modifier = Modifier.width(12.dp))
                            LegendItem("DIA", Color(0xFFC7BFFF))
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Dynamic Chart Visualization
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        if (filteredRecords.isNotEmpty()) {
                            DynamicLineChart(filteredRecords)
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No data for this period", color = Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic Labels based on selected tab
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val labels = when(selectedTab) {
                            0 -> listOf("6AM", "9AM", "12PM", "3PM", "6PM", "9PM", "12AM")
                            1 -> listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                            else -> listOf("W1", "W2", "W3", "W4")
                        }
                        labels.forEach { label ->
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard("AVG\nSYSTOLIC", "$averageSystolic", "mmHg", Color(0xFF8E76FF), Modifier.weight(1f))
                        StatCard("AVG\nDIASTOLIC", "$averageDiastolic", "mmHg", Color(0xFFC7BFFF), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TabItem(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF1D1617) else Color(0xFF4B4B4B)
        )
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D1617))
    }
}

@Composable
fun StatCard(label: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF7F8F8))
            .padding(16.dp)
    ) {
        Column {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, lineHeight = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = unit, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 6.dp))
            }
        }
    }
}

@Composable
fun DynamicLineChart(records: List<BloodPressureRecord>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val sysMax = 200f
        val sysMin = 60f
        val diaMax = 140f
        val diaMin = 40f

        fun normalizeSys(value: Int): Float = 
            1f - ((value.toFloat() - sysMin) / (sysMax - sysMin)).coerceIn(0f, 1f)
            
        fun normalizeDia(value: Int): Float = 
            1f - ((value.toFloat() - diaMin) / (diaMax - diaMin)).coerceIn(0f, 1f)

        // Draw grid lines
        drawLine(Color.LightGray.copy(alpha = 0.2f), Offset(0f, height * 0.25f), Offset(width, height * 0.25f))
        drawLine(Color.LightGray.copy(alpha = 0.2f), Offset(0f, height * 0.5f), Offset(width, height * 0.5f))
        drawLine(Color.LightGray.copy(alpha = 0.2f), Offset(0f, height * 0.75f), Offset(width, height * 0.75f))

        if (records.size > 1) {
            // Draw SYS Path
            val sysPath = Path().apply {
                val firstY = height * normalizeSys(records[0].systolic)
                moveTo(0f, firstY)
                records.forEachIndexed { index, record ->
                    val x = width * (index.toFloat() / (records.size - 1))
                    val y = height * normalizeSys(record.systolic)
                    lineTo(x, y)
                }
            }
            drawPath(sysPath, Color(0xFF8E76FF), style = Stroke(width = 3.dp.toPx()))

            // Draw DIA Path
            val diaPath = Path().apply {
                val firstY = height * normalizeDia(records[0].diastolic)
                moveTo(0f, firstY)
                records.forEachIndexed { index, record ->
                    val x = width * (index.toFloat() / (records.size - 1))
                    val y = height * normalizeDia(record.diastolic)
                    lineTo(x, y)
                }
            }
            drawPath(diaPath, Color(0xFFC7BFFF), style = Stroke(width = 3.dp.toPx()))
            
            // Draw points for the last entry
            val lastX = width
            val lastSysY = height * normalizeSys(records.last().systolic)
            val lastDiaY = height * normalizeDia(records.last().diastolic)
            
            drawCircle(Color(0xFF8E76FF), radius = 6.dp.toPx(), center = Offset(lastX, lastSysY))
            drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(lastX, lastSysY))
            
            drawCircle(Color(0xFFC7BFFF), radius = 6.dp.toPx(), center = Offset(lastX, lastDiaY))
            drawCircle(Color.White, radius = 3.dp.toPx(), center = Offset(lastX, lastDiaY))
        } else if (records.size == 1) {
            val x = width / 2
            val sysY = height * normalizeSys(records[0].systolic)
            val diaY = height * normalizeDia(records[0].diastolic)
            
            drawCircle(Color(0xFF8E76FF), radius = 6.dp.toPx(), center = Offset(x, sysY))
            drawCircle(Color(0xFFC7BFFF), radius = 6.dp.toPx(), center = Offset(x, diaY))
        }
    }
}
