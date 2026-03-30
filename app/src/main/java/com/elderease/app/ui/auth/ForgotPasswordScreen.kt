package com.elderease.app.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ui.viewmodel.ForgotPasswordViewModel
import com.elderease.app.ui.viewmodel.VerifyOtpViewModel

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {

    val context = LocalContext.current
    val forgotVM: ForgotPasswordViewModel = viewModel()
    val verifyVM: VerifyOtpViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var showOtp by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // OTP digits
    var d1 by remember { mutableStateOf("") }
    var d2 by remember { mutableStateOf("") }
    var d3 by remember { mutableStateOf("") }
    var d4 by remember { mutableStateOf("") }
    var d5 by remember { mutableStateOf("") }
    var d6 by remember { mutableStateOf("") }

    val otp = d1 + d2 + d3 + d4 + d5 + d6

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        Image(
            painter = painterResource(R.drawable.lavender_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { navController.popBackStack() }
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Forgot Password",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Enter your registered email",
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Enter email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    loading = true
                    forgotVM.sendOtp(
                        email.trim(),
                        onSuccess = {
                            loading = false
                            showOtp = true
                        },
                        onError = {
                            loading = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = !loading,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7F73))
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Continue", fontSize = 18.sp, color = Color.White)
                }
            }

            /* ---------- OTP SECTION ---------- */
            if (showOtp) {

                Spacer(Modifier.height(40.dp))

                Text(
                    text = "Verify OTP",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Code sent to $email",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // ✅ PERFECT OTP BOXES (6) - NO OVERLAP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OtpDigitBox(d1, Modifier.weight(1f)) { d1 = it }
                    OtpDigitBox(d2, Modifier.weight(1f)) { d2 = it }
                    OtpDigitBox(d3, Modifier.weight(1f)) { d3 = it }
                    OtpDigitBox(d4, Modifier.weight(1f)) { d4 = it }
                    OtpDigitBox(d5, Modifier.weight(1f)) { d5 = it }
                    OtpDigitBox(d6, Modifier.weight(1f)) { d6 = it }
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (otp.length < 6) {
                            Toast.makeText(context, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        verifyVM.verifyOtp(
                            email = email.trim(),
                            otp = otp,
                            onSuccess = {
                                loading = false
                                navController.navigate("reset/${email.trim()}")
                            },
                            onError = {
                                loading = false
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    enabled = !loading,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7F73))
                ) {
                    Text("Verify & Continue", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

/* --------------------------------------------------
   OTP DIGIT BOX (ALWAYS FITS - NO OVERLAP)
-------------------------------------------------- */
@Composable
fun OtpDigitBox(
    value: String,
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f) // ✅ always square
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(14.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (it.length <= 1 && it.all(Char::isDigit)) {
                    onChange(it)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    innerTextField()
                }
            }
        )
    }
}
