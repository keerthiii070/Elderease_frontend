package com.elderease.app.ui.medicalandcautions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonMistakesScreen(navController: NavHostController) {
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
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "SAFETY GUIDE",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF673AB7),
                                    letterSpacing = 1.sp
                                )
                            }
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

                // Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .offset(x = 200.dp, y = (-20).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        )

                        Column(modifier = Modifier.padding(24.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Key Insights",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                "Common Medicine\nMistakes",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 34.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                "Many seniors accidentally misuse medication. Here are the most frequent errors and how to avoid them.",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Key Principles",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Key Principles Section
                PrincipleCard("Stay Organized", "Use a daily pill organizer to keep track of doses.", Icons.Default.Inventory)
                PrincipleCard("Stay Informed", "Ask your pharmacist about every new prescription.", Icons.Default.Info)
                PrincipleCard("Stay Consistent", "Take medications at the same time each day.", Icons.Default.Update)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    "Detailed Guide",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mistakes List
                mistakesList.forEach { mistake ->
                    MistakeItemExpanded(mistake)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PrincipleCard(title: String, desc: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun MistakeItemExpanded(mistake: MistakeData) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = mistake.iconBg
                ) {
                    Icon(
                        mistake.icon,
                        contentDescription = null,
                        tint = mistake.iconTint,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        mistake.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4A148C)
                    )
                    Text(
                        mistake.shortDesc,
                        fontSize = 13.sp,
                        color = mistake.iconTint.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    mistake.fullDescription,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3E5F5).copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF7C4DFF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "ADVICE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF7C4DFF),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                mistake.advice,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MistakeData(
    val title: String,
    val shortDesc: String,
    val fullDescription: String,
    val advice: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

val mistakesList = listOf(
    MistakeData(
        "Splitting Pills Incorrectly",
        "Not all pills are meant to be split",
        "Splitting a pill that isn't designed for it can cause the medication to be released too quickly or unevenly, potentially leading to an overdose or reduced effectiveness.",
        "Only split pills that are scored (have a pre-cut line). Never split capsules, enteric-coated tablets, or extended-release medications.",
        Icons.Default.ContentCut,
        Color(0xFFFFEBEE),
        Color(0xFFEF5350)
    ),
    MistakeData(
        "Mixing Up Timing",
        "Consistency is key",
        "Taking medication at random times can lead to fluctuations in blood levels, making the treatment less effective or increasing side effects.",
        "Use a daily pill organizer and set phone alarms. Try to associate taking your medicine with a daily routine like brushing your teeth or eating breakfast.",
        Icons.Default.Schedule,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    MistakeData(
        "Stopping Meds Early",
        "Especially common with antibiotics",
        "Many people stop taking their medicine as soon as they feel better. This can allow the condition to return or contribute to antibiotic resistance.",
        "Always complete the full course of your medication as prescribed by your doctor, even if your symptoms have disappeared.",
        Icons.Default.Cancel,
        Color(0xFFE8F5E9),
        Color(0xFF4CAF50)
    ),
    MistakeData(
        "Storing in the Bathroom",
        "Heat and moisture damage meds",
        "The moisture and temperature changes in a bathroom can cause medications to break down and lose potency before their expiration date.",
        "Store your medications in a cool, dry place away from direct sunlight, such as a dedicated cabinet in a hallway or bedroom dresser.",
        Icons.Default.Bathtub,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    ),
    MistakeData(
        "Using Kitchen Spoons",
        "Inaccurate liquid measurements",
        "Standard household spoons vary widely in size and do not provide an accurate dose for liquid medications, which is risky for seniors.",
        "Always use the measuring device (cup, syringe, or spoon) provided by the manufacturer or your pharmacist for liquid doses.",
        Icons.Default.Restaurant,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    MistakeData(
        "Taking Double Doses",
        "Trying to make up for a missed one",
        "If you miss a dose, taking two at once to 'catch up' can cause toxic levels of the drug in your system and severe side effects.",
        "If you miss a dose, take it as soon as you remember unless it's almost time for your next one. Never double up without consulting your doctor.",
        Icons.Default.AddCircle,
        Color(0xFFFFEBEE),
        Color(0xFFEF5350)
    ),
    MistakeData(
        "Using Expired Meds",
        "Potency and safety issues",
        "Medications can become less effective or even chemically unstable after their expiration date, which may be dangerous for chronic conditions.",
        "Check your medicine cabinet every six months. Safely dispose of any expired medications at a local pharmacy or drug take-back location.",
        Icons.Default.Warning,
        Color(0xFFFFF3E0),
        Color(0xFFFF9800)
    ),
    MistakeData(
        "Crushing Time-Release Pills",
        "Can lead to dangerous 'dose dumping'",
        "Extended-release (ER/XR) pills are meant to release slowly. Crushing them causes the entire dose to enter the blood at once.",
        "Check with your pharmacist if you have trouble swallowing. Some meds have liquid versions or shouldn't be crushed under any circumstances.",
        Icons.Default.Handyman,
        Color(0xFFE8F5E9),
        Color(0xFF4CAF50)
    ),
    MistakeData(
        "Ignoring OTC Brand Names",
        "Hidden duplicate ingredients",
        "Many over-the-counter brands contain the same active ingredients (like acetaminophen). Taking multiple can lead to accidental overdose.",
        "Always read the 'Active Ingredients' list on every bottle. Be careful not to take two different products that contain the same drug.",
        Icons.Default.Inventory,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    ),
    MistakeData(
        "Not Refilling on Time",
        "Gaps in chronic care",
        "Running out of essential medications for heart disease or diabetes can cause sudden health crises or hospitalizations.",
        "Set a reminder to request a refill when you have 7 days of medication left. Use pharmacy apps for auto-refill if available.",
        Icons.Default.Refresh,
        Color(0xFFF3E5F5),
        Color(0xFF9C27B0)
    ),
    MistakeData(
        "Mixing Multiple Medications in One Container",
        "Identification risk",
        "Storing different pills in the same bottle can lead to taking the wrong medication, especially if they look similar.",
        "Keep each medication in its original container with the label intact. Only move them to a pill organizer when ready for use.",
        Icons.Default.Diversity3,
        Color(0xFFE1F5FE),
        Color(0xFF03A9F4)
    ),
    MistakeData(
        "Ignoring Side Effects",
        "Delayed response to reactions",
        "Many seniors dismiss side effects as 'just getting old', which can delay treatment for serious adverse reactions.",
        "Report any new or unusual symptoms to your doctor immediately. Keep a symptoms diary to track when they occur.",
        Icons.Default.HealthAndSafety,
        Color(0xFFFFEBEE),
        Color(0xFFF44336)
    )
)
