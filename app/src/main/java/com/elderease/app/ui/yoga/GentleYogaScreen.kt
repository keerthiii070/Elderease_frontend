package com.elderease.app.ui.yoga

import android.annotation.SuppressLint
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.clickable
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ui.main.BottomNavBar
import kotlinx.coroutines.delay

data class YogaPose(
    val id: Int,
    val title: String,
    val description: String,
    val tip: String,
    val duration: String,
    val tag: String? = null,
    val isCompleted: Boolean = false,
    val videoUrl: String
)

@Composable
fun GentleYogaScreen(navController: NavHostController) {
    var timeLeft by remember { mutableLongStateOf(19 * 60L) }
    var isTimerRunning by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isTimerRunning = false
            
            // Vibrate
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(1000)
            }
            
            // Ring
            try {
                val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val r = RingtoneManager.getRingtone(context, notification)
                r.play()
                delay(3000)
                r.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val poses = remember {
        listOf(
            YogaPose(
                id = 1,
                title = "Seated Mountain Pose",
                description = "Keep your spine tall and shoulders relaxed.",
                tip = "Senior Tip: Sit at the edge of a sturdy chair with feet flat on the floor for better balance.",
                duration = "2 min",
                tag = "Beginner",
                isCompleted = true,
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga1.mp4"
            ),
            YogaPose(
                id = 2,
                title = "Gentle Neck Rolls",
                description = "Release tension in the neck and upper shoulders.",
                tip = "Senior Tip: Move slowly and only in a range that feels comfortable. Avoid tilting the head too far back.",
                duration = "1.5 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga2.mp4"
            ),
            YogaPose(
                id = 3,
                title = "Modified Chair Twist",
                description = "Improve spinal flexibility safely.",
                tip = "Senior Tip: Use the chair's backrest for support, but don't force the twist. Keep hips facing forward.",
                duration = "2 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga3.mp4"
            ),
            YogaPose(
                id = 4,
                title = "Seated Forward Fold",
                description = "Stretches the back and hamstrings.",
                tip = "Senior Tip: Rest your hands on your knees or shins. Only fold as far as your body allows without rounding too much.",
                duration = "2 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga4.mp4"
            ),
            YogaPose(
                id = 5,
                title = "Standing Side Reach",
                description = "Gently stretches the sides of the body and improves flexibility.",
                tip = "Senior Tip: Lift one arm overhead and lean gently to the side while breathing comfortably.",
                duration = "1.5 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga5.mp4"
            ),
            YogaPose(
                id = 6,
                title = "Heel Raises",
                description = "Strengthens calves and improves balance.",
                tip = "Senior Tip: Hold onto a chair or wall and slowly lift your heels, then lower with control.",
                duration = "2 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga6.mp4"
            ),
            YogaPose(
                id = 7,
                title = "Knee-to-Chest",
                description = "Good for digestion and hip flexibility.",
                tip = "Senior Tip: Hold behind the thigh instead of the shin if you have sensitive knees.",
                duration = "2 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga7.mp4"
            ),
            YogaPose(
                id = 8,
                title = "Seated Arm Circles",
                description = "Loosens shoulders and improves upper-body mobility.",
                tip = "Senior Tip: Make slow, gentle circles with your arms and stop if you feel strain.",
                duration = "1 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga8.mp4"
            ),
            YogaPose(
                id = 9,
                title = "Standing Weight Shift (Side to Side)",
                description = "Improves balance and stability while gently engaging the legs.",
                tip = "Senior Tip: Shift your weight slowly from one foot to the other and hold onto a chair if needed for support.",
                duration = "1 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga9.mp4"
            ),
            YogaPose(
                id = 10,
                title = "Reclined Butterfly Rest",
                description = "Gently relaxes the hips and lower body while calming the mind.",
                tip = "Senior Tip: Support the knees with pillows or rolled towels for comfort.",
                duration = "3 min",
                videoUrl = "http://192.168.31.194/elder_ease/yoga_videos/yoga10.mp4"
            )
        )
    }


    Scaffold(
        containerColor = Color(0xFFF7F7F9),
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HeaderSection(onBackClick = { navController.popBackStack() })
            }
            
            item {
                TodayFocusCard(
                    timeLeft = timeLeft,
                    isTimerRunning = isTimerRunning,
                    onToggleTimer = { isTimerRunning = !isTimerRunning }
                )
            }
            
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.yoga),
                        contentDescription = null,
                        tint = Color(0xFF916BFF),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Daily Routine",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                }
            }
            
            items(poses) { pose ->
                PoseCard(pose)
            }
        }
    }
}

@Composable
fun HeaderSection(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF916BFF),
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "Gentle Yoga",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF916BFF)
            )
            Text(
                text = "Daily Routine for Vitality",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TodayFocusCard(
    timeLeft: Long,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFBFF)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFF916BFF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "TODAY'S FOCUS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF916BFF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isTimerRunning || timeLeft < 19 * 60L) formatTime(timeLeft) else "19 min total",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF916BFF)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Morning Mobility",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { 0.1f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF916BFF),
                trackColor = Color(0xFFE8EAF6)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1 of 10 poses completed",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                
                Button(
                    onClick = onToggleTimer,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF916BFF)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        if (isTimerRunning) Icons.Default.Pause else Icons.Default.Timer, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isTimerRunning) "Pause Timer" else "Start Timer", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

@Composable
fun PoseCard(pose: YogaPose)
{
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            YogaVideoPlayer(
                videoUrl = pose.videoUrl,
                modifier = Modifier.fillMaxSize()
            )

            // Duration Badge on Image
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = pose.duration,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${pose.id}. ${pose.title}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    
                    if (pose.tag != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Surface(
                            color = Color(0xFFF3E5F5),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = pose.tag,
                                color = Color(0xFF916BFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (pose.isCompleted) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF916BFF),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = pose.description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF3E5F5).copy(alpha = 0.5f))
                .border(1.dp, Color(0xFF916BFF).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF916BFF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = pose.tip,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    lineHeight = 18.sp
                )
            }
        }
    }
}
@SuppressLint("UnsafeOptInUsageError")
@OptIn(UnstableApi::class)
@Composable
fun YogaVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
            volume = 0f
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            }
        )
    }
}
