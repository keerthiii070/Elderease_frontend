package com.elderease.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.elderease.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {

    var input by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        // 🌊 Background image
        Image(
            painter = painterResource(id = R.drawable.bg_waves),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔙 Back button
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .size(28.dp)
                .clickable {
                    navController.popBackStack()
                }
        )

        // 📦 Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.forgot_password_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Forgot Password?",
                fontSize = 26.sp,
                color = Color(0xFF3A3A3A),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Please enter your email or phone to\nreset your password.",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Email / Phone") },
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7F73),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF7F73)
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ✅ FIXED NAVIGATION
            Button(
                onClick = {
                    navController.navigate("verify_email")
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7F73)
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
