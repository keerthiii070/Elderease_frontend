package com.elderease.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlucoseGuideScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Glucose Guide",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )
                        Text(
                            "Comprehensive Reference",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF6A1B9A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE0C3FC).copy(alpha = 0.3f))
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0C3FC).copy(alpha = 0.3f), Color(0xFFF8FAFC))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Blood Sugar 101
            GuideInfoCard(
                title = "Blood Sugar 101",
                icon = Icons.Default.Bloodtype,
                iconTint = Color(0xFF8E6CEF)
            ) {
                Text(
                    "Blood sugar, or glucose, is the main sugar found in your blood. It comes from the food you eat and is your body's main source of energy. Understanding your levels is the first step to better health.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            // Fasting Glucose
            GuideInfoCard(
                title = "Fasting Glucose",
                tag = "mg/dL"
            ) {
                Text(
                    "This test measures your blood sugar after an overnight fast (not eating for at least 8 hours). It is usually done first thing in the morning.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                RangeItem(label = "Normal", range = "70 - 99", description = "Your body is processing sugar effectively.", color = Color(0xFF4CAF50))
                RangeItem(label = "Prediabetes", range = "100 - 125", description = "Levels are higher than normal. Lifestyle changes can often prevent progression.", color = Color(0xFFFFB300))
                RangeItem(label = "Diabetes", range = "126+", description = "A result of 126 mg/dL or higher on two separate tests usually indicates diabetes.", color = Color(0xFFF44336))
            }

            // Post-Meal (2 Hours)
            GuideInfoCard(
                title = "Post-Meal (2 Hours)",
                icon = Icons.Default.Restaurant,
                iconTint = Color(0xFF8E6CEF)
            ) {
                Text(
                    "Postprandial glucose is measured exactly 2 hours after the start of a meal. This shows how well your body manages the influx of sugar from food.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Visualization Bar Placeholder
                Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight().background(Color(0xFF4CAF50), RoundedCornerShape(4.dp)))
                }
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("0", fontSize = 10.sp, color = Color.Gray)
                    Text("140 mg/dL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("300+", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TargetCard(label = "Target", value = "< 140", color = Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                    TargetCard(label = "High", value = "> 140", color = Color(0xFFF44336), modifier = Modifier.weight(1f))
                }
            }

            // Hemoglobin A1c
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Hemoglobin A1c", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("The 3-Month Average", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Unlike daily finger-prick tests which give a snapshot, HbA1c measures the percentage of blood sugar attached to hemoglobin over the past 2-3 months.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    A1cItem(label = "Normal", value = "Below 5.7%", color = Color(0xFF4CAF50))
                    A1cItem(label = "Prediabetes", value = "5.7% to 6.4%", color = Color(0xFFFFB300))
                    A1cItem(label = "Diabetes", value = "6.5% or higher", color = Color(0xFFF44336))
                }
            }

            // Hypoglycemia (Low)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hypoglycemia (Low)", color = Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Blood sugar below 70 mg/dL is considered low. This can be dangerous if not treated immediately.",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Symptom Placeholder
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Opacity, null, tint = Color(0xFFF44336))
                            Text("Sweating", fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Timeline, null, tint = Color(0xFFF44336))
                            Text("Shaking", fontSize = 12.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF44336).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Rule of 15: Eat 15g of fast-acting carbs (like juice or candy), wait 15 minutes, and check again.",
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Tips for Management
            GuideInfoCard(title = "Tips for Management") {
                ManagementTipItem(icon = Icons.Default.Opacity, title = "Stay Hydrated", desc = "Drinking water helps your kidneys flush out excess blood sugar through urine.")
                ManagementTipItem(icon = Icons.Default.DirectionsRun, title = "Move After Meals", desc = "A 10-15 minute walk after eating can significantly lower post-meal spikes.")
                ManagementTipItem(icon = Icons.Default.Info, title = "Monitor Regularly", desc = "Keeping track helps you identify patterns and triggers in your diet.")
            }

            Text(
                "This guide is for informational purposes only and does not constitute medical advice. Ranges may vary based on age, condition and other factors. Always consult your healthcare provider.",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )
        }
    }
}

@Composable
fun GuideInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    tag: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
                }
                if (tag != null) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0C3FC).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(tag, fontSize = 10.sp, color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun RangeItem(label: String, range: String, description: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
        Text(range, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
    }
}

@Composable
fun TargetCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text("Recommended for most adults", fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
fun A1cItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ManagementTipItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFE0C3FC).copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
