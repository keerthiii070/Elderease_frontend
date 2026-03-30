package com.elderease.app.ui.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.elderease.app.ROUTE_EMERGENCY_ASSIST
import com.elderease.app.ROUTE_HOME
import com.elderease.app.ROUTE_SOS_COUNTDOWN
import com.elderease.app.ui.repository.EmergencyRepository
import com.elderease.app.ui.sos.GpsEnableUtil
import com.elderease.app.ui.sos.LocationUtil
import com.elderease.app.ui.sos.ShakeDetector
import com.elderease.app.ui.sos.SosManager
import com.elderease.app.ui.sos.WhatsAppUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyAssistScreen(
    navController: NavHostController,
    emailArg: String,
    goHomeAfterPermission: Boolean = false // ✅ NEW FLAG
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionsGranted by remember { mutableStateOf(false) }
    var whatsappLoading by remember { mutableStateOf(false) }
    var gpsPopupAsked by remember { mutableStateOf(false) }

    // ✅ Prevent navigating multiple times
    var navigatedHome by remember { mutableStateOf(false) }

    val requiredPermissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /* ---------- PERMISSION LAUNCHER ---------- */
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        permissionsGranted = requiredPermissions.all { result[it] == true }

        if (permissionsGranted) {
            Toast.makeText(context, "All SOS permissions enabled ✅", Toast.LENGTH_LONG).show()

            // ✅ Navigate Home ONLY when coming from Signup flow
            if (goHomeAfterPermission && !navigatedHome) {
                navigatedHome = true
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_EMERGENCY_ASSIST) { inclusive = true }
                }
            }

        } else {
            Toast.makeText(context, "Some permissions missing ❌", Toast.LENGTH_LONG).show()
        }
    }

    /* ---------- AUTO CHECK + REQUEST PERMISSIONS ON SCREEN OPEN ---------- */
    LaunchedEffect(Unit) {
        permissionsGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (!permissionsGranted) {
            permissionLauncher.launch(requiredPermissions)
        } else {
            // ✅ If already granted, go home ONLY for signup flow
            if (goHomeAfterPermission && !navigatedHome) {
                navigatedHome = true
                navController.navigate(ROUTE_HOME) {
                    popUpTo(ROUTE_EMERGENCY_ASSIST) { inclusive = true }
                }
            }
        }
    }

    /* ---------- AUTO ASK GPS ENABLE POPUP AFTER PERMISSIONS GRANTED ---------- */
    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted && !gpsPopupAsked) {
            gpsPopupAsked = true
            GpsEnableUtil.requestEnableGps(activity) { enabled ->
                if (!enabled) {
                    Toast.makeText(
                        context,
                        "GPS is OFF. Location may not work.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /* =========================================================
       ✅ SHAKE DETECTOR (LIFECYCLE SAFE)
       ========================================================= */

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val shakeDetector = remember {
        ShakeDetector {
            if (!permissionsGranted) {
                Toast.makeText(context, "Enable SOS permissions first", Toast.LENGTH_SHORT).show()
                return@ShakeDetector
            }

            scope.launch {
                try {
                    SosManager(context).triggerSOSCallOnly()
                    Toast.makeText(context, "SOS Call Started by Shake!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message ?: "SOS Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, accelerometer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (accelerometer != null) {
                        sensorManager.registerListener(
                            shakeDetector,
                            accelerometer,
                            SensorManager.SENSOR_DELAY_UI
                        )
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    sensorManager.unregisterListener(shakeDetector)
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    /* ---------------- UI ---------------- */

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Enable SOS Permissions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEDED)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SOS",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Red
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "SOS needs permission to:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = """
• Open WhatsApp to alert emergency contact
• Make emergency calls
• Share your location link
                """.trimIndent(),
                textAlign = TextAlign.Start,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { permissionLauncher.launch(requiredPermissions) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(
                    "Enable SOS Permissions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!permissionsGranted) {
                        Toast.makeText(context, "Enable SOS permissions first", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    navController.navigate("$ROUTE_SOS_COUNTDOWN/$emailArg")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A4CFF))
            ) {
                Text("🚨 EMERGENCY SOS", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (!permissionsGranted) {
                        Toast.makeText(context, "Enable SOS permissions first", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    if (whatsappLoading) return@Button
                    whatsappLoading = true

                    scope.launch {
                        try {
                            val response = EmergencyRepository().getEmergencyContact(emailArg)

                            if (!response.status || response.contact == null) {
                                Toast.makeText(context, "No emergency contact found", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val phone = response.contact.contactPhone
                            if (phone.isNullOrEmpty()) {
                                Toast.makeText(context, "Contact phone missing", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val name = response.contact.contactName ?: "Emergency Contact"
                            val locationLink = LocationUtil.getGoogleMapsLink(context)

                            val message = """
🚨 SOS ALERT 🚨
Hello $name,
$emailArg needs help immediately!

📍 Location:
$locationLink

Please contact them ASAP.
""".trimIndent()

                            WhatsAppUtil.openWhatsApp(context, phone, message)

                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp failed: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            whatsappLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                if (whatsappLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Preparing WhatsApp...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text("📩 Send WhatsApp Message", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (!permissionsGranted) {
                Text(
                    "Permissions are required to use SOS",
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    "Tip: Shake phone 3 times to start SOS call",
                    color = Color(0xFF4A3F8C),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
