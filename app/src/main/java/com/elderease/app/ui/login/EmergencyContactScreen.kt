package com.elderease.app.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ROUTE_EMERGENCY_ASSIST
import com.elderease.app.ROUTE_EMERGENCY_CONTACT
import com.elderease.app.ui.model.EmergencyContactRequest
import com.elderease.app.ui.viewmodel.EmergencyContactViewModel

@Composable
fun EmergencyContactScreen(
    navController: NavHostController,
    emailArg: String
) {

    val viewModel: EmergencyContactViewModel = viewModel()

    val loading by viewModel.loading.collectAsState()
    val message by viewModel.message.collectAsState()

    val scrollState = rememberScrollState()

    var contactName by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactAge by remember { mutableStateOf("") }

    Scaffold(containerColor = Color(0xFFF4EEFF)) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {

            Spacer(Modifier.height(16.dp))

            EmergencyTopMessage()

            Text(
                text = "Step 2 of 3 • Emergency Contact",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4A3F8C)
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- STEP INDICATOR ---------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepCircle("1", false)
                ProgressLine(true, Modifier.weight(1f))
                StepCircle("2", true)
                ProgressLine(false, Modifier.weight(1f))
                StepCircle("3", false)
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Image(
                        painter = painterResource(id = R.drawable.grandpa),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(Modifier.height(16.dp))

                    LabeledEditField("Contact Name*", contactName, "Sarah") {
                        contactName = it
                    }

                    Spacer(Modifier.height(12.dp))

                    LabeledEditField("Relationship*", relationship, "Daughter") {
                        relationship = it
                    }

                    Spacer(Modifier.height(12.dp))

                    LabeledEditField("Phone*", contactPhone, "9999999999") {
                        contactPhone = it
                    }

                    Spacer(Modifier.height(12.dp))

                    LabeledEditField("Email*", contactEmail, "sarah@gmail.com") {
                        contactEmail = it
                    }

                    Spacer(Modifier.height(12.dp))

                    LabeledEditField("Age*", contactAge, "32") {
                        contactAge = it
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red)
                        Spacer(Modifier.width(8.dp))
                        Text("This contact will be notified during SOS.")
                    }

                    Spacer(Modifier.height(20.dp))

                    if (loading) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                    }

                    if (message.isNotEmpty()) {
                        Text(message, color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFBDA7FF))
                        ) {
                            Text("← Back", color = Color.White)
                        }

                        Button(
                            enabled = !loading, // ✅ FIX 2 (disable multiple clicks)
                            onClick = {

                                // ✅ FIX 1 (validation)
                                if (contactName.isBlank() ||
                                    relationship.isBlank() ||
                                    contactPhone.isBlank() ||
                                    contactEmail.isBlank() ||
                                    contactAge.isBlank()
                                ) {
                                    viewModel.setMessage("Please fill all required fields")
                                    return@Button
                                }

                                viewModel.saveEmergencyContact(
                                    EmergencyContactRequest(
                                        user_email = emailArg,
                                        contact_name = contactName.trim(),
                                        relationship = relationship.trim(),
                                        contact_phone = contactPhone.trim(),
                                        contact_email = contactEmail.trim(),
                                        contact_age = contactAge.toIntOrNull() ?: 0
                                    )
                                ) {
                                    navController.navigate(ROUTE_EMERGENCY_ASSIST) {
                                        popUpTo(ROUTE_EMERGENCY_CONTACT) { inclusive = true }
                                    }

                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF6A4CFF))
                        ) {
                            Text("Continue →", color = Color.White)
                        }

                    }
                }
            }
                        Spacer(Modifier.height(40.dp))
        }
    }
}

/* --------------------------------------------------
   SUPPORT COMPOSABLES
-------------------------------------------------- */

@Composable
fun ProgressLine(active: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(5.dp)
            .background(
                if (active) Color(0xFFDEB7FF) else Color(0xFFE6DDF8),
                RoundedCornerShape(5.dp)
            )
    )
}

@Composable
fun StepCircle(number: String, active: Boolean) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                if (active) Color(0xFFB47CFF) else Color(0xFFE5D6FF),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            number,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else Color(0xFF7A69A3)
        )
    }
}

@Composable
fun EmergencyTopMessage() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.grandpaicon),
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(CircleShape)
        )

        Spacer(Modifier.width(10.dp))

        Box {
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Great! You’ve completed 66% (Step 2/3).", fontWeight = FontWeight.Bold)
                    Text("Just a few more details!", fontSize = 14.sp)
                }
            }

            Canvas(
                modifier = Modifier.size(20.dp).offset(x = 4.dp, y = 28.dp)
            ) {
                val path = Path().apply {
                    moveTo(0f, size.height / 2)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(path, Color.White)
            }
        }
    }
}

@Composable
fun LabeledEditField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
