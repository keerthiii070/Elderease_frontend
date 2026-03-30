package com.elderease.app.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.VerticalAlignTop
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.elderease.app.R

@Composable
fun ShoulderReliefScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    var showDisclaimer by remember { mutableStateOf(false) }
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }

    if (showDisclaimer) {
        AlertDialog(
            onDismissRequest = { showDisclaimer = false },
            title = { Text("Medical Disclaimer", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "The information provided here is for educational purposes only and is not a substitute for professional medical advice, diagnosis, or treatment. Always seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition."
                )
            },
            confirmButton = {
                TextButton(onClick = { showDisclaimer = false }) {
                    Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFFA033FF))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    if (selectedImageRes != null) {
        Dialog(onDismissRequest = { selectedImageRes = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = selectedImageRes!!),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { selectedImageRes = null }) {
                        Text("CLOSE", fontWeight = FontWeight.Bold, color = Color(0xFFA033FF))
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = Color(0xFFFBFBFF)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFA033FF), Color(0xFF8E24AA))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Shoulder Relief",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { showDisclaimer = true },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Duration
            ShoulderSectionTitle("Relief by Duration")

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderReliefCard(
                time = "0-15 mins",
                title = "Shoulder Pendulums",
                subtitle = "GENTLE MOTION",
                buttonText = "VIEW EXERCISE",
                imageRes = R.drawable.shou1,
                onButtonClick = { selectedImageRes = R.drawable.pend }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderReliefCard(
                time = "15-30 mins",
                title = "Heat Wrap Therapy",
                subtitle = "DEEP SOOTHING",
                buttonText = "VIEW GUIDE",
                imageRes = R.drawable.shou2,
                onButtonClick = { selectedImageRes = R.drawable.heatwra }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderReliefCard(
                time = "30+ mins",
                title = "Gentle Arm Circles",
                subtitle = "ACTIVE RECOVERY",
                buttonText = "VIEW",
                imageRes = R.drawable.shou3,
                onButtonClick = { selectedImageRes = R.drawable.arm }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Severity
            ShoulderSectionTitle("Relief by Severity")

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderSeverityCard(
                title = "Low Pain: Cross-Body Stretch",
                desc = "FLEXIBILITY FOCUS",
                buttonText = "VIEW STRETCH",
                icon = Icons.Default.SelfImprovement,
                bgColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF00BFA5),
                onButtonClick = { selectedImageRes = R.drawable.cross }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderSeverityCard(
                title = "Moderate Pain: Shoulder Brace",
                desc = "COMPRESSION & SUPPORT",
                buttonText = "VIEW GUIDE",
                icon = Icons.Default.Accessibility,
                bgColor = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF9100),
                onButtonClick = { selectedImageRes = R.drawable.brace }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderSeverityCard(
                title = "High Pain: Immobilize & Call Doctor",
                desc = "RESTRICT MOVEMENT",
                buttonText = null,
                icon = Icons.Default.MedicalServices,
                bgColor = Color(0xFFFFEBEE),
                iconTint = Color(0xFFEF5350)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instant Relief Tools
            ShoulderSectionTitle("Instant Relief Tools")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShoulderToolCard(
                    title = "Support Brace",
                    icon = Icons.Default.AccessibilityNew,
                    modifier = Modifier.weight(1f)
                )
                ShoulderToolCard(
                    title = "Gel Cold Pack",
                    icon = Icons.Default.AcUnit,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Remedies & Medications
            ShoulderSectionTitle("Remedies & Medications")

            Spacer(modifier = Modifier.height(16.dp))

            ShoulderRemedyRow("Topical Gels Like Orthodox", "QUICK RELIEF", icon = Icons.Default.Medication)
            ShoulderRemedyRow("Pain Relievers Like Ibuprofen and Naproxen", "ORAL RELIEF", icon = Icons.Default.HealthAndSafety)

            Spacer(modifier = Modifier.height(24.dp))

            // What To Do Now
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5FF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9C27B0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "WHAT TO DO NOW",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E22CE)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ShoulderDoNowItem(icon = Icons.Default.VerticalAlignTop, text = "Avoid reaching overhead")
                    ShoulderDoNowItem(icon = Icons.Default.Bed, text = "Sleep on your back")
                    ShoulderDoNowItem(icon = Icons.Default.MonitorWeight, text = "Keep shoulders relaxed")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ShoulderSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E2E5D),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
fun ShoulderReliefCard(time: String, title: String, subtitle: String, buttonText: String, imageRes: Int, onButtonClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$time: $title",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA033FF),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ShoulderSeverityCard(title: String, desc: String, buttonText: String?, icon: ImageVector, bgColor: Color, iconTint: Color, onButtonClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(30.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E5D))
                Text(text = desc, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = iconTint, modifier = Modifier.padding(vertical = 2.dp))
                if (buttonText != null) {
                    Button(
                        onClick = onButtonClick,
                        modifier = Modifier.height(32.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = iconTint),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ShoulderToolCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9F5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E5D),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ShoulderRemedyRow(title: String, subtitle: String, icon: ImageVector = Icons.Default.MedicalServices) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFFA033FF), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E5D))
                Text(text = subtitle, fontSize = 10.sp, color = Color(0xFFA033FF), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShoulderDoNowItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFA033FF),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 14.sp, color = Color(0xFF4B5563), fontWeight = FontWeight.Medium)
    }
}
