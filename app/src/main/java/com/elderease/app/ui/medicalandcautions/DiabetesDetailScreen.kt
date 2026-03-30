package com.elderease.app.ui.medicalandcautions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiabetesDetailScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)),
                color = Color(0xFF8E6CEF)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Abstract circles
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
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MedicalServices, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chronic Condition", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Text(
                            "Diabetes",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // What is it? Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFFF3E5F5)
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF9C27B0), modifier = Modifier.padding(10.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("What is it?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "A condition where your body struggles to regulate sugar levels. It's like a traffic jam for energy in your blood, requiring careful management of diet and activity.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Common Symptoms
            Text("Common Symptoms", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SymptomItem("Excess Thirst", Icons.Default.Opacity, Color(0xFFFFF3E0), Color(0xFFFF9800), Modifier.weight(1f))
                SymptomItem("Frequent Urination", Icons.Default.WaterDrop, Color(0xFFE3F2FD), Color(0xFF2196F3), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SymptomItem("Blurry Vision", Icons.Default.VisibilityOff, Color(0xFFFFEBEE), Color(0xFFEF5350), Modifier.weight(1f))
                SymptomItem("Fatigue", Icons.Default.BatteryAlert, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // What to Avoid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("What to Avoid", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E), modifier = Modifier.weight(1f))
                        Icon(Icons.Default.Block, null, tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    DiabetesAvoidItem("Sugary drinks and sodas")
                    DiabetesAvoidItem("Processed white bread & pasta")
                    DiabetesAvoidItem("Skipping meals frequently")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Daily Care Tips
            Text("Daily Care Tips", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CareTipItem("Balanced Diet", "Focus on fiber and leafy greens.", Icons.Default.Restaurant, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f))
                CareTipItem("Active Living", "Walk for 20 mins daily.", Icons.Default.DirectionsRun, Color(0xFFE3F2FD), Color(0xFF2196F3), Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // When to see a doctor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddBox, null, tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("When to see a doctor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "If you experience extreme thirst, sudden weight loss, or cuts that won't heal, please schedule a visit immediately.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SymptomItem(title: String, icon: ImageVector, iconBg: Color, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1C1E))
        }
    }
}

@Composable
fun DiabetesAvoidItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFEF5350)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun CareTipItem(title: String, desc: String, icon: ImageVector, iconBg: Color, iconTint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconBg
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1A1C1E))
            Text(desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
        }
    }
}
