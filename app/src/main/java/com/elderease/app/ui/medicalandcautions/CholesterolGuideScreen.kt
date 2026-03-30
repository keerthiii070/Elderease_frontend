package com.elderease.app.ui.medicalandcautions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CholesterolGuideScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Understanding Cholesterol", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Definition Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFEDE7F6), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.high), // Reusing existing icon
                                contentDescription = null,
                                tint = Color(0xFF673AB7),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("DEFINITION", color = Color(0xFF673AB7), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        "What is Cholesterol?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1C1E)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Cholesterol is a waxy substance found in your blood. While your body needs it to build healthy cells, high levels can increase your risk of heart disease.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Image(
                        painter = painterResource(id = R.drawable.high), // Reusing heartcg image
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Cholesterol Types Section
            Text("Cholesterol Types", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeExpandableCard(
                    title = "LDL (Bad Cholesterol)",
                    subtitle = "Low-Density Lipoprotein",
                    description = "LDL is called \"bad\" cholesterol because it takes cholesterol to your arteries, where it can collect in the artery walls. Too much LDL leads to a buildup of plaque.",
                    icon = Icons.Default.TrendingDown,
                    iconBg = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFEF5350)
                )
                
                TypeExpandableCard(
                    title = "HDL (Good Cholesterol)",
                    subtitle = "High-Density Lipoprotein",
                    description = "Known as \"good\" cholesterol because it helps remove other forms of cholesterol from your bloodstream. Higher levels are generally better.",
                    icon = Icons.Default.TrendingUp,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF4CAF50)
                )
                
                TypeExpandableCard(
                    title = "Triglycerides",
                    subtitle = "Type of fat (lipid)",
                    description = "A type of fat found in your blood. When you eat, your body converts any calories it doesn't need to use right away into triglycerides.",
                    icon = Icons.Default.WaterDrop,
                    iconBg = Color(0xFFFFF9C4),
                    iconTint = Color(0xFFFBC02D)
                )
            }

            // Healthy Ranges Section
            Text("Healthy Ranges", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                RangeVisualizerCard(
                    title = "LDL Levels",
                    optimalText = "Optimal: < 100 mg/dL",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFF4CAF50), Color(0xFFFFEBEE), Color(0xFFF44336))),
                    labels = listOf("GOOD", "BORDERLINE", "HIGH")
                )
                
                RangeVisualizerCard(
                    title = "HDL Levels",
                    optimalText = "Optimal: > 60 mg/dL",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFF44336), Color(0xFFFFEBEE), Color(0xFF4CAF50))),
                    labels = listOf("LOW (RISK)", "MEDIUM", "HIGH (GOOD)")
                )
            }

            // Did you know Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF673AB7))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Did you know?", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                        Text(
                            "Your body produces all the cholesterol it needs. The rest comes from food derived from animals, such as meat, poultry, and dairy products.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Text(
                "This information is for educational purposes only and does not constitute medical advice. Please consult your doctor for diagnosis and treatment.",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun TypeExpandableCard(title: String, subtitle: String, description: String, icon: ImageVector, iconBg: Color, iconTint: Color) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(40.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(description, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun RangeVisualizerCard(title: String, optimalText: String, gradient: Brush, labels: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Box(modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(optimalText, color = Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).background(gradient))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.forEach { label ->
                    Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
