package com.elderease.app.ui.login

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ROUTE_PERSONALIZE
import com.elderease.app.ROUTE_EMERGENCY_CONTACT
import com.elderease.app.ui.model.PersonalizeRequest
import com.elderease.app.ui.viewmodel.PersonalizeViewModel

@Composable
fun PersonalizeScreen(
    navController: NavHostController,
    emailArg: String,
    fullNameArg: String,
    ageArg: String
) {

    val viewModel: PersonalizeViewModel = viewModel()

    val loading by viewModel.loading.collectAsState()
    val message by viewModel.message.collectAsState()

    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf(fullNameArg) }
    var age by remember { mutableStateOf(ageArg) }
    var conditions by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    Scaffold(containerColor = Color(0xFFF2F6FF)) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.personalize_header),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Let's get to know you", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Help us personalize your ElderEase experience", fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Step 1 / 3",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    LargeField("Full Name", fullName, "Full name", { fullName = it }, Icons.Default.Person)
                    Spacer(Modifier.height(12.dp))

                    LargeField("Age", age, "Age", { age = it }, Icons.Default.CalendarMonth)
                    Spacer(Modifier.height(12.dp))

                    LargeField("Health Conditions (optional)", conditions, "Conditions", { conditions = it }, Icons.Default.Favorite)
                    Spacer(Modifier.height(12.dp))

                    LargeField("Blood Group", bloodGroup, "Blood group", { bloodGroup = it }, Icons.Default.LocalHospital)

                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MiniField(Modifier.weight(1f), "Weight (kg)", weight, "Weight", { weight = it }, Icons.Default.Scale)
                        MiniField(Modifier.weight(1f), "Height (cm)", height, "Height", { height = it }, Icons.Default.Height)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (loading) CircularProgressIndicator()

            if (message.isNotEmpty()) {
                Text(message, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val request = PersonalizeRequest(
                        email = emailArg,
                        full_name = fullName,
                        age = age.toIntOrNull() ?: 0,
                        conditions = conditions,
                        blood_group = bloodGroup,
                        weight_kg = weight.toDoubleOrNull() ?: 0.0,
                        height_cm = height.toDoubleOrNull() ?: 0.0
                    )

                    viewModel.saveProfile(request) {
                        val encodedEmail = Uri.encode(emailArg)

                        navController.navigate("$ROUTE_EMERGENCY_CONTACT/$encodedEmail") {
                            popUpTo(ROUTE_PERSONALIZE) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFF))
            ) {
                Text("Continue →", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

/* ---------- FIELDS ---------- */

@Composable
private fun LargeField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    Text(label, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, null) },
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun MiniField(
    modifier: Modifier,
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    Column(modifier = modifier) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(icon, null) },
            shape = RoundedCornerShape(18.dp)
        )
    }
}
