package com.elderease.app.ui.bloodpressure

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_BLOOD_PRESSURE
import com.elderease.app.ROUTE_BLOOD_PRESSURE_HISTORY
import com.elderease.app.data.BloodPressureRecord
import com.elderease.app.data.BloodPressureViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BloodPressureScreen(
    navController: NavHostController,
    bloodPressureViewModel: BloodPressureViewModel = viewModel(factory = BloodPressureViewModel.Factory)
) {
    var systolic by remember { mutableStateOf("120") }
    var diastolic by remember { mutableStateOf("80") }
    var pulse by remember { mutableStateOf("72") }
    
    var showDialog by remember { mutableStateOf(false) }
    var editingField by remember { mutableStateOf("") }
    var tempValue by remember { mutableStateOf("") }

    val currentDate = remember { SimpleDateFormat("MMM d", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }

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
            
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back", 
                        tint = Color(0xFF4527A0),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Add Reading",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Date Time Selection Row
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF6A4CFF), modifier = Modifier.size(20.dp))
                    Text(text = " Today, $currentDate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E5D))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF6A4CFF), modifier = Modifier.size(20.dp))
                    Text(text = " $currentTime", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E5D))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Systolic Card
            ReadingInputCard(
                label = "Systolic (Top)",
                value = systolic,
                unit = "mmHg",
                icon = Icons.Default.KeyboardArrowUp,
                onClick = {
                    editingField = "Systolic"
                    tempValue = systolic
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Diastolic Card
            ReadingInputCard(
                label = "Diastolic (Bottom)",
                value = diastolic,
                unit = "mmHg",
                icon = Icons.Default.KeyboardArrowDown,
                onClick = {
                    editingField = "Diastolic"
                    tempValue = diastolic
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Heart Rate Card
            ReadingInputCard(
                label = "Heart Rate",
                value = pulse,
                unit = "BPM",
                icon = Icons.Default.Favorite,
                iconColor = Color(0xFFFF8A80),
                onClick = {
                    editingField = "Heart Rate"
                    tempValue = pulse
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Tap numbers to edit. Ensure you are sitting comfortably before measuring.",
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = Color(0xFF6E7191),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val sys = systolic.toIntOrNull() ?: 120
                    val dia = diastolic.toIntOrNull() ?: 80
                    val p = pulse.toIntOrNull() ?: 72

                    bloodPressureViewModel.insert(
                        BloodPressureRecord(
                            systolic = sys,
                            diastolic = dia,
                            pulse = p
                        )
                    )
                    navController.navigate(ROUTE_BLOOD_PRESSURE_HISTORY) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFF))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Reading", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }


            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Edit $editingField") },
                text = {
                    TextField(
                        value = tempValue,
                        onValueChange = { tempValue = it },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        when (editingField) {
                            "Systolic" -> systolic = tempValue
                            "Diastolic" -> diastolic = tempValue
                            "Heart Rate" -> pulse = tempValue
                        }
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ReadingInputCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    iconColor: Color = Color(0xFFD1C4E9),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E5D)
                    )
                    Text(
                        text = unit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E8EFF)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = value,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0D7FF)
                    )
                }
            }
        }
    }
}
