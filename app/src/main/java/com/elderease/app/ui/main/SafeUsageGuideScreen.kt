package com.elderease.app.ui.main

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeUsageGuideScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFC49BFB), Color(0xFFF8FAFC))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "Safety Guide",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            color = Color(0xFFF3E5F5)
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF9C27B0),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            "How To Take Medicine Safely",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1E),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            "Follow these key guidelines to ensure your medication works effectively and keeps you healthy.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            lineHeight = 20.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Guidelines List
                GuidelineItem(
                    title = "Follow the Schedule",
                    description = "Take medicine at the same time every day as prescribed. Set alarms or use a pill organizer to help you remember.",
                    icon = Icons.Default.AccessTime,
                    iconBg = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF2196F3)
                )

                GuidelineItem(
                    title = "Check Food Requirements",
                    description = "Some medicines must be taken with food, while others on an empty stomach. Read the label carefully.",
                    icon = Icons.Default.Restaurant,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = Color(0xFF4CAF50)
                )

                GuidelineItem(
                    title = "Don't Skip Doses",
                    description = "Consistency is key for medication effectiveness. If you miss a dose, consult your pharmacist on what to do.",
                    icon = Icons.Default.EventBusy,
                    iconBg = Color(0xFFFFF3E0),
                    iconTint = Color(0xFFFF9800)
                )

                GuidelineItem(
                    title = "Store Correctly",
                    description = "Keep medications in a cool, dry place away from direct sunlight and out of reach of children.",
                    icon = Icons.Default.WbSunny,
                    iconBg = Color(0xFFFFEBEE),
                    iconTint = Color(0xFFF44336)
                )

                GuidelineItem(
                    title = "Check Expiry Dates",
                    description = "Never take expired medicine. They may lose effectiveness or become harmful over time.",
                    icon = Icons.Default.History,
                    iconBg = Color(0xFFF3E5F5),
                    iconTint = Color(0xFF9C27B0)
                )

                GuidelineItem(
                    title = "Use a Pill Organizer",
                    description = "A weekly pill box can help you stay organized and prevent missing or doubling doses.",
                    icon = Icons.Default.Apps,
                    iconBg = Color(0xFFE0F2F1),
                    iconTint = Color(0xFF009688)
                )

                GuidelineItem(
                    title = "Don't Share Meds",
                    description = "Medication is prescribed specifically for you. Sharing can be dangerous and ineffective.",
                    icon = Icons.Default.Group,
                    iconBg = Color(0xFFEFEBE9),
                    iconTint = Color(0xFF795548)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GuidelineItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconBg
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1C1E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
