package com.elderease.app.ui.medicalandcautions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BPGuideScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Understanding BP", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            // Header Illustration
            Image(
                painter = painterResource(id = R.drawable.pb),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            // Definition
            Column {
                Text(
                    "What is Blood Pressure?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Blood pressure is the force of your blood moving through your vessels. It is measured with two numbers.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            // Systolic Card
            BPNumericCard(
                title = "Systolic",
                tag = "TOP NUMBER",
                description = "Pressure when your heart beats and pushes blood out.",
                icon = Icons.Default.Timeline,
                borderColor = Color(0xFF673AB7)
            )

            // Diastolic Card
            BPNumericCard(
                title = "Diastolic",
                tag = "BOTTOM NUMBER",
                description = "Pressure when your heart rests between beats.",
                icon = Icons.Default.MonitorHeart,
                borderColor = Color(0xFF9575CD)
            )

            // Reference Ranges
            Text("Reference Ranges", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BPRangeRow("Normal", "Healthy", "< 120", "< 80", Color(0xFF4CAF50))
                    BPRangeRow("Elevated", "Warning", "120-129", "< 80", Color(0xFFFFB300))
                    BPRangeRow("High (S1)", "Hypertension", "130-139", "80-89", Color(0xFFFF9800))
                    BPRangeRow("High (S2)", "Critical", "140+", "90+", Color(0xFFF44336))
                }
            }

            // Quick Tips
            Text("Quick Tips", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1C1E))
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExpandableTipCard(
                    title = "Maintain a healthy diet",
                    icon = Icons.Default.Restaurant,
                    content = "Eat more fruits, vegetables, and whole grains. Reduce sodium intake."
                )
                ExpandableTipCard(
                    title = "Stay active",
                    icon = Icons.Default.DirectionsRun,
                    content = "Aim for at least 30 minutes of moderate activity most days of the week."
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun BPNumericCard(title: String, tag: String, description: String, icon: ImageVector, borderColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, brush = Brush.verticalGradient(listOf(borderColor, borderColor.copy(alpha = 0.1f))), shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(borderColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = borderColor, modifier = Modifier.size(20.dp))
                }
                Box(modifier = Modifier.background(borderColor.copy(alpha = 0.05f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = borderColor)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(description, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BPRangeRow(label: String, subLabel: String, sys: String, dia: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subLabel, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(sys, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Gray)
        Text(dia, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}

@Composable
fun ExpandableTipCard(title: String, icon: ImageVector, content: String) {
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
                Icon(icon, null, tint = Color(0xFF673AB7), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(content, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            }
        }
    }
}
