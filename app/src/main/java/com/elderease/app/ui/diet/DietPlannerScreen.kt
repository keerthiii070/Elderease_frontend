package com.elderease.app.ui.diet

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.elderease.app.R

@Composable
fun DietPlannerScreen(
    navController: NavHostController,
    viewModel: DietPlannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
    val email = prefs.getString("user_email", "") ?: ""

    var prompt by remember { mutableStateOf("") }

    val reply by viewModel.reply.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3DFFF), Color(0xFFF3E7FF))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            /* ---------- HEADER ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }

                Text(
                    text = "AI Diet Planner",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1617)
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {

                /* ---------- DIET CARD ---------- */

                /* ---------- TITLE ROW ---------- */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommended for Today",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEBE8FF)
                    ) {
                        Text(
                            text = "Personalized",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6A4CFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /* ---------- MEALS ---------- */
                MealCard(
                    "BREAKFAST",
                    "Steel Cut Oats",
                    "High in fiber to help manage blood sugar levels and keep you full longer.",
                    "https://images.unsplash.com/photo-1517673132405-a56a62b18caf?q=80&w=500",
                    Icons.Filled.WbSunny
                )

                MealCard(
                    "LUNCH",
                    "Salmon & Spinach",
                    "Omega-3 fatty acids for heart health, combined with iron-rich leafy greens.",
                    "https://images.unsplash.com/photo-1467003909585-2f8a72700288?q=80&w=500",
                    Icons.Filled.WbCloudy
                )

                MealCard(
                    "DINNER",
                    "Hearty Lentil Soup",
                    "A low-glycemic, protein-rich meal that supports easy digestion before sleep.",
                    "https://images.unsplash.com/photo-1547592166-23ac45744acd?q=80&w=500",
                    Icons.Filled.Nightlight
                )

                /* ---------- AI INSIGHT ---------- */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFC7BFFF).copy(alpha = 0.5f))
                        .border(
                            1.dp,
                            Color(0xFF8E76FF).copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF6A4CFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lightbulb, null, tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "AI Health Insight",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "\"Consistent meal times are key for managing diabetes. Try to have lunch within the same 60-minute window daily.\"",
                                fontSize = 14.sp,
                                color = Color(0xFF4B4B4B),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                /* ---------- AI RESPONSE ---------- */
                when {
                    loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    error != null -> {
                        Text(error!!, color = Color.Red)
                    }

                    reply != null -> {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Text(
                                text = reply!!,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        /* ---------- INPUT BAR ---------- */
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (prompt.isEmpty()) {
                            Text("Ask AI for diet advice...", color = Color.Gray)
                        }
                        inner()
                    }
                )

                IconButton(
                    onClick = {
                        if (prompt.isNotBlank()) {
                            viewModel.sendPrompt(email, prompt)
                            prompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6A4CFF))
                ) {
                    Icon(Icons.Filled.Send, null, tint = Color.White)
                }
            }
        }

        /* ---------- FAB ---------- */
    }
}

/* ---------- MEAL CARD ---------- */
@Composable
fun MealCard(
    time: String,
    title: String,
    description: String,
    imageUrl: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.foodd)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = Color(0xFF6A4CFF), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(time, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(description, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
