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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.HealthAndSafety
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
fun HipBackReliefScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    var selectedImageRes by remember { mutableStateOf<Int?>(null) }

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
                        text = "Hip & Back Relief",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                }

                Text(
                    text = "TARGETED RELIEF HUB",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Duration
            SectionTitle("Relief by Duration")

            Spacer(modifier = Modifier.height(16.dp))

            HipBackReliefCard(
                time = "0-15 mins",
                title = "Pelvic Tilts",
                subtitle = "GENTLE STRETCH",
                buttonText = "VIEW EXERCISE",
                imageRes = R.drawable.hip1,
                onButtonClick = { selectedImageRes = R.drawable.pelvic }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HipBackReliefCard(
                time = "15-30 mins",
                title = "Lumbar Heat Wrap",
                subtitle = "SOOTHING WARMTH",
                buttonText = "VIEW GUIDE",
                imageRes = R.drawable.hip2,
                onButtonClick = { selectedImageRes = R.drawable.heat }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HipBackReliefCard(
                time = "30+ mins",
                title = "Water Aerobics",
                subtitle = "LOW IMPACT ACTIVITY",
                buttonText = "VIEW",
                imageRes = R.drawable.hip3,
                onButtonClick = { selectedImageRes = R.drawable.wateraero }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Severity
            SectionTitle("Relief by Severity")

            Spacer(modifier = Modifier.height(16.dp))

            HipBackSeverityCard(
                title = "Low Pain (1-3): Cat-Cow Stretch",
                desc = "GENTLE MOBILITY",
                buttonText = "VIEW STRETCHES",
                icon = Icons.Default.AccessibilityNew,
                bgColor = Color(0xFFE0F2F1),
                iconTint = Color(0xFF00BFA5),
                imageRes = R.drawable.hip1,
                onButtonClick = { selectedImageRes = R.drawable.cat }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HipBackSeverityCard(
                title = "Moderate Pain (4-6): Support Belt",
                desc = "STABILIZE LUMBAR",
                buttonText = "VIEW GUIDE",
                icon = Icons.Default.Elderly,
                bgColor = Color(0xFFFFF3E0),
                iconTint = Color(0xFFFF9100),
                imageRes = R.drawable.hip2,
                onButtonClick = { selectedImageRes = R.drawable.belt }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HipBackSeverityCard(
                title = "High Pain (7-10): Rest & Call Doctor",
                desc = "IMMEDIATE ATTENTION",
                buttonText = null,
                icon = Icons.Default.MedicalServices,
                bgColor = Color(0xFFFFEBEE),
                iconTint = Color(0xFFEF5350)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instant Relief Tools
            SectionTitle("Instant Relief Tools")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HipBackToolCard(
                    title = "Lumbar Support Pillow",
                    imageRes = R.drawable.pillow,
                    modifier = Modifier.weight(1f)
                )
                HipBackToolCard(
                    title = "Hip Compression Wrap",
                    imageRes = R.drawable.wrap,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Remedies & Medications
            SectionTitle("Remedies & Medications")

            Spacer(modifier = Modifier.height(16.dp))

            HipBackRemedyRow("Topical Gels", "FAST ABSORPTION", icon = Icons.Default.Medication)
            HipBackRemedyRow("Anti-inflammatories", "PAIN REDUCTION", icon = Icons.Default.HealthAndSafety)

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

                    HipBackDoNowItem(icon = Icons.Default.AccessibilityNew, text = "Avoid heavy lifting")
                    HipBackDoNowItem(icon = Icons.Default.Chair, text = "Use ergonomic chairs")
                    HipBackDoNowItem(icon = Icons.Default.Straighten, text = "Keep a straight posture")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E2E5D),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
fun HipBackReliefCard(time: String, title: String, subtitle: String, buttonText: String, imageRes: Int, onButtonClick: () -> Unit = {}) {
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
fun HipBackSeverityCard(title: String, desc: String, buttonText: String?, icon: ImageVector, bgColor: Color, iconTint: Color, imageRes: Int? = null, onButtonClick: () -> Unit = {}) {
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
fun HipBackToolCard(title: String, imageRes: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E5D),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun HipBackRemedyRow(title: String, subtitle: String, icon: ImageVector = Icons.Default.MedicalServices) {
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
fun HipBackDoNowItem(icon: ImageVector, text: String) {
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
