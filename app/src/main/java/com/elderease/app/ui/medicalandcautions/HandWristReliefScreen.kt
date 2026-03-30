package com.elderease.app.ui.medicalandcautions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.ui.main.BottomNavBar

@Composable
fun HandWristReliefScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

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
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2))
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
                        text = "Full Hand & Wrist\nRelief",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 24.sp
                    )
                    IconButton(
                        onClick = { },
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

                Text(
                    text = "COMPREHENSIVE CARE GUIDE",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                )

                // Tabs
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.Black.copy(alpha = 0.1f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("TIPS BY STIFFN..", color = Color(0xFF7B1FA2), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("TIPS BY SEVER..", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Duration
            HandWristSectionTitle("Relief by Duration")

            Spacer(modifier = Modifier.height(16.dp))

            DurationReliefCard(
                time = "0-15 mins",
                title = "Gentle Flexing",
                desc = "Light movement to reduce morning stiffness.",
                buttonText = "START EXERCISES",
                icon = { Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(32.dp)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationReliefCard(
                time = "15-30 mins",
                title = "Warm Soak",
                desc = "Soak for 10 minutes to relax deep joint tissue.",
                buttonText = "VIEW GUIDE",
                icon = { Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(32.dp)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationReliefCard(
                time = "30+ mins",
                title = "Finger Glides",
                desc = "Therapeutic glides for persistent stiffness.",
                buttonText = "WATCH VIDEO",
                icon = { Icon(Icons.Default.Waves, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(32.dp)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Relief by Severity Level
            HandWristSectionTitle("Relief by Severity Level")

            Spacer(modifier = Modifier.height(16.dp))

            SeverityReliefCard(
                title = "Low Pain (1-3): Gentle Flexing",
                desc = "Maintain mobility with non-strenuous movement.",
                icon = Icons.Default.FitnessCenter,
                iconBg = Color(0xFFE8F5E9),
                iconTint = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeverityReliefCard(
                title = "Moderate Pain (4-6): Compression",
                desc = "Apply light pressure to manage swelling and pain.",
                icon = Icons.Default.AccessibilityNew,
                iconBg = Color(0xFFFFFDE7),
                iconTint = Color(0xFFFBC02D),
                buttonText = "VIEW GUIDE"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SeverityReliefCard(
                title = "High Pain (7-10): Maximum Rest",
                desc = "Immediately cease activity and seek professional advice.",
                icon = Icons.Default.Close,
                iconBg = Color(0xFFFFEBEE),
                iconTint = Color(0xFFE53935),
                buttonText = "CONTACT DOCTOR",
                isAlert = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Instant Relief Tools
            HandWristSectionTitle("Instant Relief Tools")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolCard(
                    title = "Paraffin Wax Treatment",
                    icon = Icons.Default.MedicalServices,
                    modifier = Modifier.weight(1f)
                )
                ToolCard(
                    title = "Cold Compression",
                    icon = Icons.Default.MedicalServices,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Remedies & Medications
            HandWristSectionTitle("Remedies & Medications")

            Spacer(modifier = Modifier.height(16.dp))

            RemedyItem("OTC Gels (Voltaren/Arnica)", "Topical relief for localized pain.")
            RemedyItem("Turmeric & Ginger", "Natural anti-inflammatory supplements.")

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
                                .background(Color(0xFFE9D5FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF9333EA), modifier = Modifier.size(20.dp))
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

                    DoNowItem("Avoid heavy lifting or gripping today.")
                    DoNowItem("Wear your wrist brace during sleep.")
                    DoNowItem("Set a timer for 20-min rest intervals.")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HandWristSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2E2E5D),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
fun DurationReliefCard(time: String, title: String, desc: String, buttonText: String, icon: @Composable () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$time: $title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Button(
                    onClick = { },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF9F5FF)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}

@Composable
fun SeverityReliefCard(
    title: String, 
    desc: String, 
    icon: ImageVector, 
    iconBg: Color, 
    iconTint: Color,
    buttonText: String? = null,
    isAlert: Boolean = false
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                if (buttonText != null) {
                    Button(
                        onClick = { },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isAlert) Color(0xFFEF5350) else Color(0xFFFF4081)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                    ) {
                        Text(buttonText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun ToolCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
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
fun RemedyItem(title: String, desc: String) {
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
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF9C27B0), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2E5D))
                Text(text = desc, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun DoNowItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF9333EA))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 13.sp, color = Color(0xFF4B5563))
    }
}
