package com.elderease.app.ui.healthkit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun BMIResultScreen(
    navController: NavHostController,
    bmi: Float,
    category: String,
    age: Int
) {
    val resultText = getSeniorBMIMessage(bmi)
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
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
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2E2E5D)
                    )
                }
                Text(
                    text = "BMI Results",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Health Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR HEALTH PROFILE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E5D),
                            letterSpacing = 1.sp
                        )
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF2E2E5D),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Gauge
                    Box(
                        modifier = Modifier
                            .size(240.dp, 120.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 24.dp.toPx()
                            val arcSize = Size(size.width - strokeWidth, (size.height * 2) - strokeWidth)
                            
                            // Background Arc
                            drawArc(
                                color = Color(0xFFF1F3FF),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                size = arcSize,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                            )
                            
                            // Progress Arc
                            val sweepAngle = ((bmi - 10) / (40 - 10) * 180f).coerceIn(0f, 180f)
                            drawArc(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFC7BFFF), Color(0xFF6A4CFF))
                                ),
                                startAngle = 180f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                size = arcSize,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f", bmi),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E5D)
                            )
                            Text(
                                text = category.uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A4CFF),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Description
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF8F9FF))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = if (bmi in 22f..27f) "Great News!" else "Health Tip",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E5D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resultText,
                                fontSize = 14.sp,
                                color = Color(0xFF4B4B4B),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "BMI CATEGORIES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E5D),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories List
            CategoryItem("Underweight", "Below 18.5", category == "Underweight")
            Spacer(modifier = Modifier.height(12.dp))
            CategoryItem("Normal Range", "18.5 to 24.9", category == "Normal")
            Spacer(modifier = Modifier.height(12.dp))
            CategoryItem("Overweight", "25 to 29.9", category == "Overweight")
            Spacer(modifier = Modifier.height(12.dp))
            CategoryItem("Obese", "30 or higher", category == "Obese")

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CategoryItem(label: String, range: String, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color.White else Color(0xFFF8F9FF)
        ),
        border = if (isActive) BorderStroke(2.dp, Color(0xFF6A4CFF)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp, 40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) Color(0xFF6A4CFF) else Color(0xFFC7BFFF))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Text(
                    text = range,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (isActive) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF6A4CFF)
                )
            }
        }
    }
}

fun getSeniorBMIMessage(bmi: Float): String {
    return when {
        bmi < 18.5f -> "Your BMI is low. For seniors, maintaining a bit more weight can help protect against fractures and provide energy during illness. Consult with your doctor about nutrition."
        bmi in 18.5f..21.9f -> "Your BMI is within the standard normal range. However, for adults over 65, a BMI between 22 and 27 is often considered optimal for long-term health and vitality."
        bmi in 22.0f..27.0f -> "Your BMI is ${String.format("%.1f", bmi)}. This is a very healthy range for seniors, supporting mobility, muscle mass, and long-term vitality. Keep up the good work!"
        bmi in 27.1f..29.9f -> "Your BMI is slightly elevated. While some extra weight can be protective for seniors, ensure you're staying active to support joint health and cardiovascular fitness."
        else -> "Your BMI is in the obese range. This can put extra strain on your joints and heart. Focus on gentle movement and nutrient-dense meals to support your mobility."
    }
}
