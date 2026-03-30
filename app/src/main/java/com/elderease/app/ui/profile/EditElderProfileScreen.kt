package com.elderease.app.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.elderease.app.R
import com.elderease.app.data.DrinkWaterViewModel
import com.elderease.app.ui.network.ApiClient
import com.elderease.app.ui.viewmodel.EditProfileViewModel
import com.elderease.app.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditElderProfileScreen(
    navController: NavController,
    editViewModel: EditProfileViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    drinkWaterViewModel: DrinkWaterViewModel = viewModel(
        factory = DrinkWaterViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val context = LocalContext.current

    val email = remember {
        context.getSharedPreferences("elder_ease_prefs", Context.MODE_PRIVATE)
            .getString("user_email", "") ?: ""
    }

    /* ---------- API STATE ---------- */
    val profile by profileViewModel.profile.collectAsState()
    val emergency by profileViewModel.emergency.collectAsState()
    val loading by profileViewModel.loading.collectAsState()

    /* ---------- WATER GOAL ---------- */
    val dailyTarget by drinkWaterViewModel.dailyTarget.collectAsState()

    /* ---------- PROFILE FORM STATE ---------- */
    var fullName by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var medicalConditions by rememberSaveable { mutableStateOf("") }

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var existingImage by rememberSaveable { mutableStateOf<String?>(null) }

    // ✅ Prevent overwriting form values after save/recomposition
    var profileLoadedOnce by rememberSaveable { mutableStateOf(false) }

    // ✅ Cache busting key (force reload image)
    var imageCacheKey by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    /* ---------- EMERGENCY CONTACT STATE ---------- */
    var contactName by rememberSaveable { mutableStateOf("") }
    var relationship by rememberSaveable { mutableStateOf("") }
    var contactPhone by rememberSaveable { mutableStateOf("") }
    var contactEmail by rememberSaveable { mutableStateOf("") }
    var contactAge by rememberSaveable { mutableStateOf("") }

    /* ---------- LOAD DATA ---------- */
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile(email)
        profileViewModel.loadEmergencyContact(email)
        drinkWaterViewModel.loadToday()
    }

    /* ---------- POPULATE PROFILE (ONLY ONCE) ---------- */
    LaunchedEffect(profile) {
        if (profileLoadedOnce) return@LaunchedEffect

        profile?.let {
            fullName = it.fullName.orEmpty()
            age = it.age?.toString().orEmpty()
            phone = it.phone.orEmpty()
            weight = it.weightKg?.toString().orEmpty()
            bloodGroup = it.bloodGroup.orEmpty()
            medicalConditions = it.healthConditions.orEmpty()
            existingImage = it.profileImage

            profileLoadedOnce = true
        }
    }

    /* ---------- POPULATE EMERGENCY ---------- */
    LaunchedEffect(emergency) {
        emergency?.let {
            contactName = it.contactName.orEmpty()
            relationship = it.relationship.orEmpty()
            contactPhone = it.contactPhone.orEmpty()
            contactEmail = it.contactEmail.orEmpty()
            contactAge = it.contactAge?.toString().orEmpty()
        }
    }

    /* ---------- IMAGE PICKER ---------- */
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageUri = it

            // ✅ when new image selected → remove old image + refresh cache
            if (it != null) {
                existingImage = null
                imageCacheKey = System.currentTimeMillis()
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Elder Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            /* ---------- PROFILE IMAGE ---------- */
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(contentAlignment = Alignment.BottomEnd) {

                    // ✅ FINAL IMAGE URL (Emulator + Phone supported)
                    val finalImageModel: Any? = when {
                        imageUri != null -> imageUri
                        !existingImage.isNullOrEmpty() ->
                            "${ApiClient.profileImageBaseUrl}${existingImage}?t=$imageCacheKey"
                        else -> null
                    }

                    AsyncImage(
                        model = finalImageModel,
                        contentDescription = null,
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(R.drawable.profile),
                        error = painterResource(R.drawable.profile),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .offset((-6).dp, (-6).dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.PhotoCamera, null)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            /* ---------- PERSONAL INFO ---------- */
            EditSectionTitle("Personal Information")
            Spacer(Modifier.height(16.dp))

            InfoCard {
                EditTextField(fullName, { fullName = it }, "Full Name")
                Spacer(Modifier.height(12.dp))

                Row {
                    EditTextField(age, { age = it }, "Age", Modifier.weight(1f), "years")
                    Spacer(Modifier.width(12.dp))
                    EditTextField(weight, { weight = it }, "Weight", Modifier.weight(1f), "kg")
                }

                Spacer(Modifier.height(12.dp))
                EditTextField(phone, { phone = it }, "Phone Number")

                Spacer(Modifier.height(12.dp))
                BloodGroupDropdown(bloodGroup) { bloodGroup = it }
            }

            Spacer(Modifier.height(28.dp))

            /* ---------- MEDICAL CONDITIONS ---------- */
            EditSectionTitle("Medical Conditions")
            Spacer(Modifier.height(16.dp))

            InfoCard {
                OutlinedTextField(
                    value = medicalConditions,
                    onValueChange = { medicalConditions = it },
                    label = { Text("Medical Conditions (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            Spacer(Modifier.height(28.dp))

            /* ---------- EMERGENCY CONTACT ---------- */
            EditSectionTitle("Emergency Contact")
            Spacer(Modifier.height(16.dp))

            InfoCard {
                EditTextField(contactName, { contactName = it }, "Contact Name")
                Spacer(Modifier.height(12.dp))
                EditTextField(relationship, { relationship = it }, "Relationship")
                Spacer(Modifier.height(12.dp))
                EditTextField(contactPhone, { contactPhone = it }, "Phone Number")
                Spacer(Modifier.height(12.dp))
                EditTextField(contactEmail, { contactEmail = it }, "Email")
                Spacer(Modifier.height(12.dp))
                EditTextField(contactAge, { contactAge = it }, "Age")
            }

            Spacer(Modifier.height(28.dp))

            /* ---------- DAILY WATER GOAL ---------- */
            EditSectionTitle("Daily Water Goal")
            Spacer(Modifier.height(16.dp))

            InfoCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            if (dailyTarget > 500)
                                drinkWaterViewModel.setDailyTarget(dailyTarget - 100)
                        }
                    ) {
                        Icon(Icons.Default.RemoveCircleOutline, null)
                    }

                    Text(
                        text = "$dailyTarget ml",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    IconButton(
                        onClick = {
                            if (dailyTarget < 5000)
                                drinkWaterViewModel.setDailyTarget(dailyTarget + 100)
                        }
                    ) {
                        Icon(Icons.Default.AddCircleOutline, null)
                    }
                }

                Slider(
                    value = dailyTarget.toFloat(),
                    onValueChange = { drinkWaterViewModel.setDailyTarget(it.toInt()) },
                    valueRange = 500f..5000f
                )

                Text(
                    "Maximum limit: 5000 ml",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(32.dp))

            /* ---------- SAVE ---------- */
            Button(
                onClick = {

                    Log.d(
                        "PROFILE_SAVE",
                        "fullName=$fullName age=$age weight=$weight phone=$phone conditions=$medicalConditions"
                    )

                    editViewModel.saveProfileAndEmergency(
                        context = context,
                        email = email,
                        userPhone = phone.trim(),
                        fullName = fullName.trim(),
                        age = age.toIntOrNull() ?: 0,
                        weight = weight.toIntOrNull() ?: 0,
                        bloodGroup = bloodGroup,
                        medicalConditions = medicalConditions,
                        imageUri = imageUri,
                        existingImage = existingImage,
                        contactName = contactName,
                        relationship = relationship,
                        contactPhone = contactPhone,
                        contactEmail = contactEmail,
                        contactAge = contactAge.toIntOrNull() ?: 0
                    ) {
                        // ✅ force reload image after save
                        imageCacheKey = System.currentTimeMillis()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ---------- REUSABLE ---------- */

@Composable
private fun EditSectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    trailingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = trailingText?.let { { Text(it, color = Color.Gray) } },
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BloodGroupDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val items = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Blood Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded, { expanded = false }) {
            items.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
