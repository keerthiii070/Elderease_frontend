package com.elderease.app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_ALZHEIMER_DETAIL
import com.elderease.app.ROUTE_ARTHRITIS_DETAIL
import com.elderease.app.ROUTE_DIABETES_DETAIL
import com.elderease.app.ROUTE_HEART_DISEASE_DETAIL
import com.elderease.app.ROUTE_HYPERTENSION_DETAIL
import com.elderease.app.ROUTE_OSTEOPOROSIS_DETAIL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalConditionsScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)),
                color = Color(0xFF8E6CEF)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Abstract circles for design matching the image
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .offset(x = 220.dp, y = (-30).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxSize()
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Text(
                            "Healthcare Library",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Text(
                            "Health\nConditions",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 38.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "MOST COMMON CONDITIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(medicalConditions) { condition ->
                    ConditionItem(condition) {
                        if (condition.route != null) {
                            navController.navigate(condition.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConditionItem(condition: ConditionData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = condition.iconBg
            ) {
                Icon(
                    condition.icon,
                    contentDescription = null,
                    tint = condition.iconTint,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    condition.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A1C1E)
                )
                Text(
                    condition.subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

data class ConditionData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val route: String? = null
)

val medicalConditions = listOf(
    ConditionData("Arthritis", "Joint pain & stiffness", Icons.Default.Accessibility, Color(0xFFF3E5F5), Color(0xFF9C27B0), route = ROUTE_ARTHRITIS_DETAIL),
    ConditionData("Hypertension", "High blood pressure management", Icons.Default.Favorite, Color(0xFFFFEBEE), Color(0xFFEF5350), route = ROUTE_HYPERTENSION_DETAIL),
    ConditionData("Diabetes Type 2", "Blood sugar monitoring", Icons.Default.Opacity, Color(0xFFE3F2FD), Color(0xFF2196F3), route = ROUTE_DIABETES_DETAIL),
    ConditionData("Heart Disease", "Cardiovascular health", Icons.Default.MonitorHeart, Color(0xFFE8F5E9), Color(0xFF4CAF50), route = ROUTE_HEART_DISEASE_DETAIL),
    ConditionData("Osteoporosis", "Bone density and strength", Icons.Default.Person, Color(0xFFFFF3E0), Color(0xFFFF9800), route = ROUTE_OSTEOPOROSIS_DETAIL),
    ConditionData("Alzheimer's", "Memory and cognitive care", Icons.Default.Psychology, Color(0xFFEDE7F6), Color(0xFF673AB7), route = ROUTE_ALZHEIMER_DETAIL)
)
