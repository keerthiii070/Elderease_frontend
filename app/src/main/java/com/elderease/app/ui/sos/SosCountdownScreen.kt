package com.elderease.app.ui.sos

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosCountdownScreen(
    navController: NavHostController,
    emailArg: String
) {
    val context = LocalContext.current
    val activity = context as Activity

    var countDown by remember { mutableIntStateOf(5) }
    var cancelled by remember { mutableStateOf(false) }
    var sosTriggered by remember { mutableStateOf(false) } // ✅ prevent double trigger

    // ✅ Countdown timer
    LaunchedEffect(Unit) {
        while (countDown > 0 && !cancelled) {
            delay(1000)
            countDown--
        }
    }

    // ✅ When countdown hits 0 -> CALL immediately -> wait 3 sec -> return back
    LaunchedEffect(countDown) {
        if (countDown == 0 && !cancelled && !sosTriggered) {
            sosTriggered = true

            try {
                Toast.makeText(context, "Countdown finished! Calling now...", Toast.LENGTH_LONG).show()

                // ✅ DIRECT CALL
                SosManager(context).triggerSOSCallOnly()

                // ✅ wait 3 seconds then go back to EmergencyAssistScreen
                delay(3000)

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFF2F2),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calling Emergency", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = countDown.toString(),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Calling emergency in $countDown seconds...",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    cancelled = true
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}
