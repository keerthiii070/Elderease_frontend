package com.elderease.app.ui.healthkit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_HAND_WRIST_RELIEF
import com.elderease.app.ROUTE_HIP_BACK_RELIEF
import com.elderease.app.ROUTE_KNEE_RELIEF
import com.elderease.app.ROUTE_SHOULDER_RELIEF
import kotlinx.coroutines.delay

@Composable
fun PainLogScreen(navController: NavHostController) {

    // ✅ Start with NOTHING selected
    var selectedStiffness by remember { mutableStateOf("") }

    // ✅ Start with 0 pain (not selected)
    var painSeverity by remember { mutableFloatStateOf(0f) }

    var selectedArea by remember { mutableStateOf("Body") }

    val density = LocalDensity.current

    // State for marking pain locations with history for undo/redo
    var markedPoints by remember { mutableStateOf(listOf<Offset>()) }
    var redoPoints by remember { mutableStateOf(listOf<Offset>()) }

    // ✅ Popup message state (center message)
    var popupMessage by remember { mutableStateOf("") }
    var showPopup by remember { mutableStateOf(false) }
    var popupKey by remember { mutableIntStateOf(0) } // to re-trigger popup even if same selection

    fun showMotivationMessage(message: String) {
        popupMessage = message
        popupKey++
        showPopup = true
    }

    LaunchedEffect(popupKey) {
        if (showPopup) {
            delay(1700)
            showPopup = false
        }
    }

    // Helper function to update area label based on points
    fun updateAreaFromPoint(offset: Offset) {
        val xDp = with(density) { offset.x.toDp().value }
        val yDp = with(density) { offset.y.toDp().value }

        selectedArea = when {
            yDp < 60 && xDp in 70f..130f -> "Head"
            // More broad detection for shoulders
            yDp in 60f..110f && (xDp in 40f..95f || xDp in 105f..160f) -> "Shoulder"
            yDp in 120f..180f && (xDp < 80 || xDp > 120) -> "Hand & Wrist"
            yDp in 100f..190f && xDp in 85f..115f -> "Hip & Back"
            yDp in 190f..245f && xDp in 75f..125f -> "Knee"
            else -> "Body"
        }
    }

    // Derived pain level based on severity (allow none selected when painSeverity == 0)
    val selectedPainLevel = when {
        painSeverity == 0f -> -1
        painSeverity <= 3f -> 0
        painSeverity <= 7f -> 1
        else -> 2
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF2E2E5D)
                    )
                }
                Text(
                    text = "Pain Log",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E5D)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "How are you feeling today?",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Body Map Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Pain Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1617)
                    )
                    Text(
                        text = "Tap to mark area",
                        fontSize = 12.sp,
                        color = Color(0xFF6A4CFF).copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Undo/Redo Controls
                Row {
                    IconButton(
                        onClick = {
                            if (markedPoints.isNotEmpty()) {
                                val point = markedPoints.last()
                                markedPoints = markedPoints.dropLast(1)
                                redoPoints = redoPoints + point
                                // Update area to previous or reset
                                if (markedPoints.isNotEmpty()) updateAreaFromPoint(markedPoints.last())
                                else selectedArea = "Body"
                            }
                        },
                        enabled = markedPoints.isNotEmpty(),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = if (markedPoints.isNotEmpty()) Color(0xFF6A4CFF) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (redoPoints.isNotEmpty()) {
                                val point = redoPoints.last()
                                redoPoints = redoPoints.dropLast(1)
                                markedPoints = markedPoints + point
                                updateAreaFromPoint(point)
                            }
                        },
                        enabled = redoPoints.isNotEmpty(),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = if (redoPoints.isNotEmpty()) Color(0xFF6A4CFF) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFFE0E0E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    HumanBodyVisualization(
                        markedPoints = markedPoints,
                        onBodyTap = { offset ->
                            markedPoints = markedPoints + offset
                            redoPoints = emptyList() // Clear redo history on new action
                            updateAreaFromPoint(offset)
                        }
                    )

                    if (selectedArea != "Body") {
                        LocationBadge(
                            label = selectedArea,
                            isSelected = true,
                            onClick = { },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Morning Stiffness
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1EDFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☀️", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Morning Stiffness",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1617)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StiffnessOption("0", "MIN", selectedStiffness == "0", Modifier.weight(1f)) {
                    selectedStiffness = "0"
                    showMotivationMessage("Great! No stiffness today — keep moving 💪")
                }
                StiffnessOption("15", "MIN", selectedStiffness == "15", Modifier.weight(1f)) {
                    selectedStiffness = "15"
                    showMotivationMessage("Nice! A little stretch and you’ll feel even better 🌿")
                }
                // ✅ Keep UI same but no 60+ option (only these 3)
                StiffnessOption("30+", "MIN", selectedStiffness == "30+", Modifier.weight(1f)) {
                    selectedStiffness = "30+"
                    showMotivationMessage("You’re doing well! Gentle movement will help a lot ✨")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pain Severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pain Severity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1617)
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = painSeverity.toInt().toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6A4CFF)
                    )
                    Text(
                        text = "/10",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Slider(
                value = painSeverity,
                onValueChange = { painSeverity = it },
                onValueChangeFinished = {
                    if (painSeverity > 0f) {
                        val msg = when {
                            painSeverity <= 3f -> "You’re doing great 😊 Keep going!"
                            painSeverity <= 7f -> "You’ve got this 💪 Take breaks and breathe."
                            else -> "You are strong ❤️ Take care and don’t push too hard."
                        }
                        showMotivationMessage(msg)
                    }
                },
                valueRange = 0f..10f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF6A4CFF),
                    activeTrackColor = Color(0xFF6A4CFF).copy(alpha = 0.2f),
                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.2f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("NO PAIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                Text("MODERATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                Text("SEVERE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Interactive Face Badges: Selecting a badge updates severity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FaceBadge(
                    "😊",
                    "LOW",
                    Color(0xFFE8F5E9),
                    Color(0xFF4CAF50),
                    selectedPainLevel == 0,
                    Modifier.weight(1f)
                ) {
                    painSeverity = 2f
                    showMotivationMessage("Awesome 😊 Keep staying active and relaxed!")
                }

                FaceBadge(
                    "😐",
                    "MID",
                    Color(0xFFFFF3E0),
                    Color(0xFFFF9800),
                    selectedPainLevel == 1,
                    Modifier.weight(1f)
                ) {
                    painSeverity = 5f
                    showMotivationMessage("You’re managing well 💛 Small steps make big progress.")
                }

                FaceBadge(
                    "☹️",
                    "HIGH",
                    Color(0xFFFFEBEE),
                    Color(0xFFF44336),
                    selectedPainLevel == 2,
                    Modifier.weight(1f)
                ) {
                    painSeverity = 9f
                    showMotivationMessage("Stay strong ❤️ Rest is also healing. You’re not alone.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button -> Navigate to specialized relief screens
            Button(
                onClick = {
                    when (selectedArea) {
                        "Hand & Wrist" -> navController.navigate(ROUTE_HAND_WRIST_RELIEF)
                        "Knee" -> navController.navigate(ROUTE_KNEE_RELIEF)
                        "Hip & Back" -> navController.navigate(ROUTE_HIP_BACK_RELIEF)
                        "Shoulder" -> navController.navigate(ROUTE_SHOULDER_RELIEF)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E6CEF)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "See Solution",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ✅ Popup message in CENTER (only message, with animation)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(100f),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = showPopup,
                enter = scaleIn(animationSpec = tween(200)) + fadeIn(animationSpec = tween(200)),
                exit = scaleOut(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200))
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Text(
                        text = popupMessage,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E5D),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun HumanBodyVisualization(markedPoints: List<Offset>, onBodyTap: (Offset) -> Unit) {
    Canvas(
        modifier = Modifier
            .size(200.dp, 300.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onBodyTap(offset)
                }
            }
    ) {
        val width = size.width
        val bodyColor = Color(0xFFD1D1E9)

        // Head
        drawCircle(bodyColor, radius = 25.dp.toPx(), center = Offset(width / 2, 40.dp.toPx()))

        // Torso
        drawRect(
            bodyColor,
            topLeft = Offset(width / 2 - 10.dp.toPx(), 75.dp.toPx()),
            size = Size(20.dp.toPx(), 80.dp.toPx())
        )

        // Arms
        drawLine(
            bodyColor,
            Offset(width / 2 - 10.dp.toPx(), 80.dp.toPx()),
            Offset(width / 2 - 40.dp.toPx(), 150.dp.toPx()),
            strokeWidth = 15.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            bodyColor,
            Offset(width / 2 + 10.dp.toPx(), 80.dp.toPx()),
            Offset(width / 2 + 40.dp.toPx(), 150.dp.toPx()),
            strokeWidth = 15.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Legs (Shortened to remove ankles/feet)
        drawLine(
            bodyColor,
            Offset(width / 2 - 5.dp.toPx(), 155.dp.toPx()),
            Offset(width / 2 - 15.dp.toPx(), 245.dp.toPx()),
            strokeWidth = 18.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            bodyColor,
            Offset(width / 2 + 5.dp.toPx(), 155.dp.toPx()),
            Offset(width / 2 + 15.dp.toPx(), 245.dp.toPx()),
            strokeWidth = 18.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Marked points
        markedPoints.forEach { point ->
            drawCircle(Color(0xFF6A4CFF), radius = 6.dp.toPx(), center = point)
        }
    }
}

@Composable
fun LocationBadge(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF6A4CFF) else Color.White,
        shadowElevation = 4.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF2E2E5D)
        )
    }
}

@Composable
fun StiffnessOption(value: String, unit: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF8E6CEF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
            Text(
                text = unit,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
            )
        }
    }
}

@Composable
fun FaceBadge(face: String, label: String, bgColor: Color, iconColor: Color, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) bgColor else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, iconColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else bgColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(face, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) iconColor else Color.Gray
            )
        }
    }
}
