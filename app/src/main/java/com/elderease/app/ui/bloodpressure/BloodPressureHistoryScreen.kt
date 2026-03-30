package com.elderease.app.ui.bloodpressure

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_BLOOD_PRESSURE_TRENDS
import com.elderease.app.data.BloodPressureRecord
import com.elderease.app.data.BloodPressureViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BloodPressureHistoryScreen(
    navController: NavHostController,
    viewModel: BloodPressureViewModel = viewModel(factory = BloodPressureViewModel.Factory)
) {
    val records by viewModel.allRecords.collectAsState()

    val averageSystolic = if (records.isNotEmpty()) records.map { it.systolic }.average().toInt() else 0
    val averageDiastolic = if (records.isNotEmpty()) records.map { it.diastolic }.average().toInt() else 0
    val averageStatus = getBPStatus(averageSystolic, averageDiastolic)

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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6A4CFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "History",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(
                    onClick = { navController.navigate(ROUTE_BLOOD_PRESSURE_TRENDS) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color(0xFF6A4CFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Average Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8E76FF), Color(0xFF6A4CFF))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "AVERAGE THIS WEEK",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$averageSystolic/$averageDiastolic",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "mmHg",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = averageStatus,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // History List
            val groupedRecords = remember(records) {
                records.sortedByDescending { it.timestamp }.groupBy { record ->
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    val recordDate = sdf.format(Date(record.timestamp))
                    val today = sdf.format(Date())
                    if (recordDate == today) "TODAY" else recordDate.uppercase()
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                groupedRecords.forEach { (date, recordsInDate) ->
                    item {
                        Text(
                            text = date,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6E7191),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(recordsInDate) { record ->
                        HistoryItem(record)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(record: BloodPressureRecord) {
    val status = getBPStatus(record.systolic, record.diastolic)
    val statusColor = getBPStatusColor(status)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val timeStr = timeFormat.format(Date(record.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (status == "NORMAL" || status == "OPTIMAL") Icons.Default.CheckCircle else Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${record.systolic}/${record.diastolic}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = status,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeStr,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFFF5A79),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${record.pulse} bpm",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

fun getBPStatus(systolic: Int, diastolic: Int): String {
    return when {
        systolic >= 180 || diastolic >= 120 -> "CRISIS"
        systolic >= 140 || diastolic >= 90 -> "STAGE 2"
        systolic >= 130 || diastolic >= 80 -> "STAGE 1"
        systolic >= 120 && diastolic < 80 -> "ELEVATED"
        systolic < 120 && diastolic < 80 -> "OPTIMAL"
        else -> "NORMAL"
    }
}

fun getBPStatusColor(status: String): Color {
    return when (status) {
        "NORMAL", "OPTIMAL" -> Color(0xFF4CAF50)
        "ELEVATED" -> Color(0xFFFFC107)
        "STAGE 1" -> Color(0xFFFF9800)
        "STAGE 2" -> Color(0xFFF44336)
        "CRISIS" -> Color(0xFFB71C1C)
        else -> Color(0xFF4CAF50)
    }
}
