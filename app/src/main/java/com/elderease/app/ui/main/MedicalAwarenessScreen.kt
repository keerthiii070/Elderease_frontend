package com.elderease.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_BP_GUIDE
import com.elderease.app.ROUTE_CHOLESTEROL_GUIDE
import com.elderease.app.ROUTE_GLUCOSE_GUIDE
import com.elderease.app.ROUTE_LAB_TERMS_GUIDE

@Composable
fun MedicalAwarenessScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0C3FC), Color(0xFFF8FAFC))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(34.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF8E6CEF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Medical\nAwareness",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E),
                    lineHeight = 40.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val categories = listOf(
                AwarenessCategory(
                    "Blood Sugar",
                    "Glucose monitoring\n& trends",
                    Icons.Filled.WaterDrop,
                    Color(0xFFFFEBEE),
                    Color(0xFFEF5350),
                    Color(0xFFFFF5F5),
                    onClick = { navController.navigate(ROUTE_GLUCOSE_GUIDE) }
                ),
                AwarenessCategory(
                    "BP Readings",
                    "Systolic & Diastolic\nhistory",
                    Icons.Filled.Favorite,
                    Color(0xFFE3F2FD),
                    Color(0xFF42A5F5),
                    Color(0xFFF5F9FF),
                    onClick = { navController.navigate(ROUTE_BP_GUIDE) }
                ),
                AwarenessCategory(
                    "Cholesterol\nLevels",
                    "LDL, HDL &\nTriglycerides",
                    Icons.Filled.MonitorHeart,
                    Color(0xFFFFF9C4),
                    Color(0xFFFBC02D),
                    Color(0xFFFFFFF0),
                    onClick = { navController.navigate(ROUTE_CHOLESTEROL_GUIDE) }
                ),
                AwarenessCategory(
                    "Common Lab\nTests",
                    "CBC, BMP &\nExplanations",
                    Icons.Filled.Science,
                    Color(0xFFE0F2F1),
                    Color(0xFF26A69A),
                    Color(0xFFF0FFFE),
                    onClick = { navController.navigate(ROUTE_LAB_TERMS_GUIDE) }
                )
            )
            
            // Grid replacement using Rows and Columns for better scroll control
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                for (i in categories.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CategoryCard(categories[i], modifier = Modifier.weight(1f))
                        if (i + 1 < categories.size) {
                            CategoryCard(categories[i + 1], modifier = Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class AwarenessCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val shapeColor: Color,
    val onClick: () -> Unit
)

@Composable
fun CategoryCard(category: AwarenessCategory, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { category.onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background abstract shape
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 40.dp, y = (-20).dp)
                    .clip(CircleShape)
                    .background(category.shapeColor)
                    .align(Alignment.TopEnd)
            )
            
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(category.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = category.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Column {
                    Text(
                        text = category.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1C1E),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category.subtitle,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
