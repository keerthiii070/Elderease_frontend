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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.NordicWalking
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
fun KneeReliefScreen(navController: NavHostController) {
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
                        text = "Knee Pain Relief",
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
            KneeSectionTitle("Relief by Duration")

            Spacer(modifier = Modifier.height(16.dp))

            KneeReliefCard(
                time = "0-15 mins",
                title = "Seated Leg Extensions",
                subtitle = "MILD RELIEF",
                buttonText = "VIEW EXERCISE",
                imageRes = R.drawable.leg,
                onButtonClick = { selectedImageRes = R.drawable.legex }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KneeReliefCard(
                time = "15-30 mins",
                title = "Heat/Cold Therapy",
                subtitle = "DEEP RELAXATION",
                buttonText = "VIEW GUIDE",
                imageRes = R.drawable.cold,
                onButtonClick = { selectedImageRes = R.drawable.heco }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KneeReliefCard(
                time = "30+ mins",
                title = "Gentle Yoga/Tai Chi",
                subtitle = "MINDFUL RECOVERY",
                buttonText = "VIEW ",
                imageRes = R.drawable.tai,
                onButtonClick = { selectedImageRes = R.drawable.yogi }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Severity
            KneeSectionTitle("Relief by Severity")

            Spacer(modifier = Modifier.height(16.dp))

            KneeSeverityCard(
                title = "Low Pain (1-3): Gentle Flexing",
                desc = "RECOMMENDED FOR STIFFNESS",
                buttonText = "VIEW STRETCHES",
                icon = Icons.Default.AccessibilityNew,
                bgColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF00BFA5),
                onButtonClick = { selectedImageRes = R.drawable.flex }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KneeSeverityCard(
                title = "Moderate Pain (4-6): Compression",
                desc = "REDUCE SWELLING NOW",
                buttonText = "VIEW GUIDE",
                icon = Icons.Default.Timer,
                bgColor = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF9100),
                onButtonClick = { selectedImageRes = R.drawable.com }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KneeSeverityCard(
                title = "High Pain (7-10): Maximum Rest",
                desc = "IMMEDIATE ATTENTION",
                buttonText = null,
                icon = Icons.Default.HealthAndSafety,
                bgColor = Color(0xFFFFEBEE),
                iconTint = Color(0xFFEF5350)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instant Relief Tools
            KneeSectionTitle("Instant Relief Tools")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KneeToolCard(
                    title = "Knee Support Brace",
                    icon = Icons.Default.AccessibilityNew,
                    modifier = Modifier.weight(1f)
                )
                KneeToolCard(
                    title = "Reusable Ice Pack",
                    icon = Icons.Default.AcUnit,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Remedies & Medications
            KneeSectionTitle("Remedies & Medications")

            Spacer(modifier = Modifier.height(16.dp))

            KneeRemedyRow("Topical NSAID Gels", "FAST ABSORPTION", icon = Icons.Default.Medication)
            KneeRemedyRow("Acetaminophen", "ORAL RELIEF", icon = Icons.Default.HealthAndSafety)

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

                    KneeDoNowItem(Icons.Default.VerticalAlignTop, "Elevate your leg")
                    KneeDoNowItem(Icons.Default.MonitorWeight, "Maintain healthy weight")
                    KneeDoNowItem(Icons.Default.NordicWalking, "Use a cane for stability")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun KneeSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E2E5D),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
fun KneeReliefCard(time: String, title: String, subtitle: String, buttonText: String, imageRes: Int, onButtonClick: () -> Unit = {}) {
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
fun KneeSeverityCard(title: String, desc: String, buttonText: String?, icon: androidx.compose.ui.graphics.vector.ImageVector, bgColor: Color, iconTint: Color, onButtonClick: () -> Unit = {}) {
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
fun KneeToolCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
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
fun KneeRemedyRow(title: String, subtitle: String, icon: ImageVector = Icons.Default.MedicalServices) {
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
fun KneeDoNowItem(icon: ImageVector, text: String) {
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
