package com.elderease.app.ui.medscan

import kotlinx.coroutines.withContext
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.elderease.app.R
import com.elderease.app.data.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedScanScreen() {

    var showCamera by remember { mutableStateOf(false) }


    // ✅ System back dispatcher (no NavController needed)
    val backDispatcher =
        androidx.activity.compose.LocalOnBackPressedDispatcherOwner
            .current?.onBackPressedDispatcher

    when {
        showCamera -> {
            CameraScreen(onDismiss = { showCamera = false })
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    backDispatcher?.onBackPressed()
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.medic),
                                    contentDescription = "MedScan Logo",
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "MedScan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text(
                                        "Identify medicines instantly",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                bottomBar = {
                    Button(
                        onClick = { showCamera = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Scan Now")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Now", fontSize = 16.sp)
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCamera = true },
                    ) {
                        Row(
                            modifier = Modifier.padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Scan Medicine",
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Scan Medicine",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    "Take a photo of your medicine to get detailed information",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "What you'll get",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    WhatYouWillGet()

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "How it works",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HowItWorks()
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CameraScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var scannedText by remember { mutableStateOf<String?>(null) }
    var lastScannedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        hasPermission = isGranted
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(context, "Image selected", Toast.LENGTH_SHORT).show()
            capturedImageUri = uri   // ✅ show preview screen
        }
    }

    var medicine by remember { mutableStateOf<Medicine?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val client = remember { OkHttpClient() }
    var showAnalyzingScreen by remember { mutableStateOf(false) }
    fun sendMedicineToBackend(ocrText: String) {

        Log.d("API_DEBUG", "SENDING OCR TO API = $ocrText")

        val json = JSONObject()
        json.put("ocr_text", ocrText)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://192.168.31.194:8000/api/predict-medicine/")
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_RESULT", "Failed: ${e.message}")

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "API Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    showAnalyzingScreen = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                Log.d("API_RESULT", result ?: "Empty")

                Handler(Looper.getMainLooper()).post {
                    try {
                        val jsonObject = JSONObject(result ?: "")
                        val prediction = jsonObject.getJSONObject("prediction")

                        medicine = Medicine(
                            medicineName = prediction.getString("medicine_name"),
                            composition = prediction.getString("composition"),
                            uses = prediction.getString("uses"),
                            sideEffects = prediction.getString("side_effects"),
                            imageUrl = "",
                            manufacturer = prediction.getString("manufacturer"),
                            excellentReview = prediction.getInt("excellent_review"),
                            averageReview = prediction.getInt("average_review"),
                            poorReview = prediction.getInt("poor_review"),
                            prescriptionRequired = prediction.getString("prescription_required"),
                            precautions = prediction.getString("precautions"),
                            storage = prediction.getString("storage"),
                            warnings = prediction.getString("warnings"),
                            category = prediction.getString("category")
                        )

                    } catch (e: Exception) {
                        Toast.makeText(context, "Parse Error: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("API_PARSE", e.message ?: "Parse error")
                    }

                    showAnalyzingScreen = false
                }
            }
        })
    }


    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var camera: androidx.camera.core.Camera? by remember { mutableStateOf(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    fun searchMedicine(medicineName: String) {
        isLoading = true
        error = null

        val json = JSONObject()
        json.put("medicine_name", medicineName)

        val body = json.toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.68.104.999/elderease_api/search_medicine.php")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    error = "Failed to connect to server"
                    showAnalyzingScreen = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()

                try {
                    val jsonObject = JSONObject(result ?: "")
                    if (jsonObject.getBoolean("status")) {

                        val data = jsonObject.getJSONObject("data")

                        val med = Medicine(
                            medicineName = data.getString("medicine_name"),
                            composition = data.getString("composition"),
                            uses = data.getString("uses"),
                            sideEffects = data.getString("side_effects"),
                            imageUrl = data.getString("image_url"),
                            manufacturer = data.getString("manufacturer"),
                            excellentReview = data.getInt("excellent_review"),
                            averageReview = data.getInt("average_review"),
                            poorReview = data.getInt("poor_review"),
                            prescriptionRequired = data.optString("prescription_required", "N/A"),
                            precautions = data.optString("precautions", "N/A"),
                            storage = data.optString("storage", "N/A"),
                            warnings = data.optString("warnings", "N/A"),
                            category = data.optString("category", "N/A")
                        )

                        Handler(Looper.getMainLooper()).post {
                            medicine = med
                            showAnalyzingScreen = false
                        }

                    } else {
                        Handler(Looper.getMainLooper()).post {
                            error = "Medicine not found"
                            showAnalyzingScreen = false
                        }
                    }

                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).post {
                        error = "Parsing error"
                        showAnalyzingScreen = false

                    }
                }
            }
        })
    }


    when {

        // 🔵 1️⃣ OCR RESULT SCREEN — HIGHEST PRIORITY
        scannedText != null -> {
            OCRResultScreen(
                text = scannedText!!,
                onClose = {
                    scannedText = null
                }
            )
        }

        // 🟢 2️⃣ FINAL MEDICINE RESULT SCREEN
        medicine != null -> {
            ScanResultScreen(
                medicine = medicine!!,
                imageUri = lastScannedImageUri,
                onDismiss = onDismiss,
                onScanAnother = {
                    capturedImageUri = null
                    medicine = null
                    lastScannedImageUri = null
                },

            )
        }

        // 🟡 3️⃣ ANALYZING / LOADING
        showAnalyzingScreen -> {
            AnalyzingMedicineScreen()
        }

        // 🟠 4️⃣ IMAGE PREVIEW
        capturedImageUri != null -> {
            ImagePreview(
                uri = capturedImageUri!!,
                onRetake = {
                    capturedImageUri = null
                },
                onConfirm = {
                    showAnalyzingScreen = true

                    lastScannedImageUri = capturedImageUri

                    val image = InputImage.fromFilePath(
                        context,
                        capturedImageUri!!
                    )

                    val recognizer =
                        TextRecognition.getClient(
                            TextRecognizerOptions.DEFAULT_OPTIONS
                        )

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->

                            val fullText = visionText.text
                            Log.d("OCR_RESULT", "FULL OCR TEXT: $fullText")

                            sendMedicineToBackend(fullText)

// ❌ remove scannedText screen
                            scannedText = null

                            capturedImageUri = null

                        }
                        .addOnFailureListener {
                            showAnalyzingScreen = false
                        }
                }
            )
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                if (hasPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = androidx.camera.core.Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build()
                                cameraProvider.unbindAll()
                                camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner, cameraSelector, preview, imageCapture
                                )
                                camera?.cameraControl?.enableTorch(isFlashOn)
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Text(
                    "Position medicine in frame",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(300.dp)
                        .border(4.dp, Color.Blue, RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.Center)
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp, start = 32.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                takePicture(context, imageCapture, cameraExecutor) { uri ->
                                    capturedImageUri = uri
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.Blue)
                        )
                    }

                    IconButton(onClick = {
                        isFlashOn = !isFlashOn
                        camera?.cameraControl?.enableTorch(isFlashOn)
                    }) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            if (medicine == null) {
                capturedImageUri = null // Go back to camera
            }
            error = null // Reset error
        }
    }
}

@Composable
fun ImagePreview(
    uri: Uri,
    onRetake: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        bitmap?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onRetake) {
                Text("Retake")
            }
            Button(onClick = onConfirm) {
                Text("Confirm")
            }
        }
    }
}


fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    cameraExecutor: ExecutorService,
    onImageCaptured: (Uri) -> Unit
) {
    val photoFile = File(
        context.filesDir,
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(photoFile)
        .build()

    imageCapture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(
                outputFileResults: ImageCapture.OutputFileResults
            ) {
                val savedUri = Uri.fromFile(photoFile)

                // ✅ MAIN THREAD FIX
                Handler(Looper.getMainLooper()).post {
                    onImageCaptured(savedUri)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        "Photo capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )
}

@Composable
fun WhatYouWillGet() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FeatureItem(Icons.AutoMirrored.Filled.Article, "Medicine Name & Composition", "Get the full name and chemical composition of the medicine.")
        FeatureItem(Icons.Default.Info, "Side Effects", "Understand the potential side effects and precautions.")
        FeatureItem(Icons.Default.Warning, "Uses", "Know what this medicine is used to treat.")
    }
}

@Composable
fun FeatureItem(icon: ImageVector, title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun HowItWorks() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        HowItWorksItem(Icons.Default.Filter1, "Take a Photo", "Ensure the medicine name is clear in the photo.")
        HowItWorksItem(Icons.Default.Filter2, "Review Details", "Get a summary of the medicine's information.")
        HowItWorksItem(Icons.Default.Filter3, "Save for Later", "Keep a history of your scanned medicines.")
    }
}

@Composable
fun HowItWorksItem(icon: ImageVector, title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun AnalyzingMedicineScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Analyzing Medicine...", color = Color.White, fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    medicine: Medicine,
    imageUri: Uri?,
    onDismiss: () -> Unit,
    onScanAnother: () -> Unit,

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Result") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },

            )
        },
        bottomBar = {
            Button(
                onClick = onScanAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Scan Another")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ✅ Medicine Name Title
            Text(
                text = medicine.medicineName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ ADD IMAGE HERE (PASTE THIS BLOCK)
            if (imageUri != null) {
                val context = LocalContext.current
                var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

                LaunchedEffect(imageUri) {
                    bitmap = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(imageUri)?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    }
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Scanned medicine image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ✅ Your old cards start from here
            InfoCard(
                title = "Manufacturer",
                text = medicine.manufacturer
            )

            InfoCard(
                title = "Composition",
                text = medicine.composition
            )

            InfoCard(
                title = "Uses",
                text = medicine.uses
            )

            InfoCard(
                title = "Side Effects",
                text = medicine.sideEffects
            )

            InfoCard(
                title = "Reviews",
                text = "Excellent: ${medicine.excellentReview}, " +
                        "Average: ${medicine.averageReview}, " +
                        "Poor: ${medicine.poorReview}"
            )

            InfoCard(
                title = "Prescription Required",
                text = medicine.prescriptionRequired
            )

            InfoCard(
                title = "Precautions",
                text = medicine.precautions
            )

            InfoCard(
                title = "Storage",
                text = medicine.storage
            )

            InfoCard(
                title = "Warnings",
                text = medicine.warnings
            )

            InfoCard(
                title = "Category",
                text = medicine.category
            )
    }
    }
}

@Composable
fun InfoCard(title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRResultScreen(
    text: String,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scanned Text") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 16.sp
            )
        }
    }
}
