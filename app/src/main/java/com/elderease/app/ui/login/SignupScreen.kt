package com.elderease.app.ui.login

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ROUTE_PERSONALIZE
import com.elderease.app.ROUTE_SIGNUP
import com.elderease.app.ui.network.ApiClient
import com.elderease.app.ui.viewmodel.SignupViewModel
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavHostController) {

    val viewModel: SignupViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val loading by viewModel.loading.collectAsState()
    val signupSuccess by viewModel.signupSuccess.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    /* ---------- EMAIL OTP STATE ---------- */
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var emailVerified by remember { mutableStateOf(false) }
    var otpLoading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    /* =========================================================
       ✅ CORRECT NAVIGATION TRIGGER
       ========================================================= */
    LaunchedEffect(signupSuccess) {
        if (signupSuccess) {

            val prefs = context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_email", email.trim())
                .putBoolean("is_logged_in", true)
                .apply()

            navController.navigate(
                "$ROUTE_PERSONALIZE/${Uri.encode(email)}/${Uri.encode(fullName)}/${Uri.encode(age)}"
            ) {
                popUpTo(ROUTE_SIGNUP) { inclusive = true }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F8))
    ) {

        Image(
            painter = painterResource(R.drawable.signup_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier
                .padding(16.dp)
                .clickable { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Register", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text("Create your new account", color = Color.DarkGray)

            Image(
                painter = painterResource(R.drawable.signup_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(240.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(35.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(modifier = Modifier.padding(24.dp)) {

                    SignupTextField("Full Name", fullName, { fullName = it }, "Enter Full Name")
                    Spacer(Modifier.height(16.dp))

                    SignupTextField(
                        "Phone Number",
                        phoneNumber,
                        { phoneNumber = it },
                        "Enter Phone Number"
                    )
                    Spacer(Modifier.height(16.dp))

                    SignupTextField("Age", age, { age = it }, "Enter Age")
                    Spacer(Modifier.height(16.dp))

                    SignupTextField("Email", email, { email = it }, "Enter Email")
                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(context, "Enter email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            otpLoading = true
                            scope.launch {
                                try {
                                    val preRes = ApiClient.api.preSignup(
                                        mapOf("email" to email.trim())
                                    )

                                    if (preRes["status"] != true) {
                                        Toast.makeText(
                                            context,
                                            preRes["message"].toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }

                                    val otpRes = ApiClient.api.send_Email_Otp(
                                        mapOf("email" to email.trim())
                                    )

                                    if (otpRes["status"] == true) {
                                        otpSent = true
                                        Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            otpRes["message"].toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } finally {
                                    otpLoading = false
                                }
                            }
                        },
                        enabled = !emailVerified
                    ) {
                        if (otpLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text(if (emailVerified) "Email Verified ✅" else "Send OTP")
                        }
                    }

                    if (otpSent && !emailVerified) {
                        Spacer(Modifier.height(12.dp))
                        SignupTextField("OTP", otp, { otp = it }, "Enter OTP")
                        Spacer(Modifier.height(8.dp))

                        Button(onClick = {
                            scope.launch {
                                val res = ApiClient.api.verifyEmailOtp(
                                    mapOf("email" to email.trim(), "otp" to otp.trim())
                                )

                                if (res["status"] == true) {
                                    emailVerified = true
                                    Toast.makeText(context, "Email verified", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        res["message"].toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }) {
                            Text("Verify OTP")
                        }
                    }

                    Divider(Modifier.padding(vertical = 16.dp))

                    SignupTextField(
                        "Password",
                        password,
                        { password = it },
                        "Enter Password",
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    SignupTextField(
                        "Confirm Password",
                        confirmPassword,
                        { confirmPassword = it },
                        "Confirm Password",
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                            ) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(isChecked, onCheckedChange = { isChecked = it })
                        Spacer(Modifier.width(6.dp))
                        Text("I agree to the terms")
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (!emailVerified || !isChecked) {
                                Toast.makeText(context, "Complete all steps", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            Toast.makeText(context, "Signup request started...", Toast.LENGTH_SHORT).show()
                            viewModel.signup(
                                context,
                                fullName.trim(),
                                email.trim(),
                                phoneNumber.trim(),
                                age.trim(),
                                password.trim(),
                                confirmPassword.trim()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp))
                        } else {
                            Text("Create Account", fontSize = 18.sp)
                        }
                    }
                    val message by viewModel.message.collectAsState()

                    if (message.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = message,
                            color = if (message.contains("success", true)) Color(0xFF2E7D32) else Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(label, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp)
        )
    }
}
