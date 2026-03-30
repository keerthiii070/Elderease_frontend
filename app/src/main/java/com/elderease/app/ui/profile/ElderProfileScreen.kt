package com.elderease.app.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.elderease.app.R
import com.elderease.app.ui.network.ApiClient
import com.elderease.app.ui.viewmodel.ProfileViewModel

private const val ROUTE_EDIT_PROFILE = "edit_elder_profile"
private const val ROUTE_LOGIN = "login"
private const val ROUTE_SETTINGS = "settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElderProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    /* ---------- SESSION ---------- */
    val email = remember {
        context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
            .getString("user_email", null)
    }

    /* ---------- STATE ---------- */
    val profile by profileViewModel.profile.collectAsState()
    val emergency by profileViewModel.emergency.collectAsState()
    val loading by profileViewModel.loading.collectAsState()
    val error by profileViewModel.error.collectAsState()

    /* ---------- LOAD DATA ---------- */
    LaunchedEffect(email) {
        email?.let {
            profileViewModel.loadProfile(it)
            profileViewModel.loadEmergencyContact(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Elder Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(ROUTE_SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        containerColor = Color(0xFFF3F6F9)
    ) { padding ->

        when {
            loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error ?: "Something went wrong", color = Color.Red)
                }
            }

            profile == null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No profile data found", color = Color.Gray)
                }
            }

            else -> {
                val p = profile!!

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(24.dp))

                    /* ---------- PROFILE IMAGE ---------- */
                    Box(contentAlignment = Alignment.BottomEnd) {

                        // ✅ Auto works in Emulator + Real Phone
                        val imageUrl = p.profileImage?.let {
                            ApiClient.profileImageBaseUrl + it
                        }

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .width(170.dp)
                                .height(120.dp)
                                .shadow(12.dp, RoundedCornerShape(60.dp))
                                .clip(RoundedCornerShape(60.dp)),
                            placeholder = painterResource(R.drawable.profile),
                            error = painterResource(R.drawable.profile),
                            onError = {
                                Log.e("IMAGE", "Failed URL: $imageUrl")
                            }
                        )

                        Box(
                            modifier = Modifier
                                .offset((-8).dp, (-8).dp)
                                .size(42.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    navController.navigate(ROUTE_EDIT_PROFILE)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, null)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        p.fullName ?: "Unknown",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "${p.age ?: 0} Years Old",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(28.dp))

                    /* ---------- STATS ---------- */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            Icons.Default.Bloodtype,
                            "Blood Group",
                            p.bloodGroup ?: "N/A",
                            Color.Red,
                            Modifier.weight(1f)
                        )
                        StatCard(
                            Icons.Default.Scale,
                            "Weight",
                            "${p.weightKg ?: 0} kg",
                            Color(0xFF3F51B5),
                            Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    /* ---------- MEDICAL CONDITIONS ---------- */
                    SectionTitle("Medical Conditions")
                    Spacer(Modifier.height(16.dp))

                    val conditions =
                        p.healthConditions?.split(",")?.map { it.trim() } ?: emptyList()

                    if (conditions.isEmpty()) {
                        Text("No medical conditions recorded", color = Color.Gray)
                    } else {
                        FlowRow(horizontalArrangement = Arrangement.Center) {
                            conditions.forEach {
                                ConditionChip(it, R.drawable.heart)
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    /* ---------- EMERGENCY CONTACT ---------- */
                    SectionTitle("Emergency Contact")
                    Spacer(Modifier.height(16.dp))

                    emergency?.let {
                        EmergencyContactCard(
                            name = it.contactName ?: "N/A",
                            relationship = it.relationship ?: "N/A",
                            phoneNumber = it.contactPhone ?: "N/A"
                        )
                    } ?: Text("No emergency contact added", color = Color.Gray)

                    Spacer(Modifier.height(36.dp))

                    Button(
                        onClick = { navController.navigate(ROUTE_EDIT_PROFILE) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profile", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    /* ---------- LOGOUT ---------- */
                    TextButton(
                        onClick = {
                            val prefs = context.getSharedPreferences(
                                "elder_ease_prefs",
                                Context.MODE_PRIVATE
                            )
                            prefs.edit().clear().apply()

                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red)
                        Spacer(Modifier.width(6.dp))
                        Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.shadow(10.dp, RoundedCornerShape(24.dp))) {
        Column(Modifier.padding(20.dp)) {
            Icon(icon, null, tint = tint)
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ConditionChip(text: String, iconRes: Int) {
    Card(shape = RoundedCornerShape(50)) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(iconRes), null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text)
        }
    }
}

@Composable
private fun EmergencyContactCard(
    name: String,
    relationship: String,
    phoneNumber: String
) {
    val context = LocalContext.current

    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(relationship, color = Color.Gray)
                Text(phoneNumber, color = Color.Gray)
            }
            IconButton(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                    )
                }
            ) {
                Icon(Icons.Default.Call, null)
            }
        }
    }
}
