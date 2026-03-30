package com.elderease.app.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.elderease.app.R
import com.elderease.app.ROUTE_HOME
import com.elderease.app.ROUTE_LOGIN
import com.elderease.app.ROUTE_SPLASH
import com.elderease.app.isUserLoggedIn
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showImage by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    // Animation for the splash image
    val imageAlpha by animateFloatAsState(
        targetValue = if (showImage) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "imageAlpha"
    )

    val imageScale by animateFloatAsState(
        targetValue = if (showImage) 1f else 0.6f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "imageScale"
    )

    LaunchedEffect(Unit) {
        // Start image animation immediately
        showImage = true
        
        // Wait for 2 seconds as requested before showing text
        delay(2000)
        showText = true
        
        // Total duration before navigation
        delay(3000)
        
        val destination = if (isUserLoggedIn(context)) ROUTE_HOME else ROUTE_LOGIN
        navController.navigate(destination) {
            popUpTo(ROUTE_SPLASH) { 
                inclusive = true 
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Splash Image Section
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer(
                        alpha = imageAlpha,
                        scaleX = imageScale,
                        scaleY = imageScale
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Text Animations appear after 2 seconds
            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(1200)) + expandVertically(
                    animationSpec = tween(1200),
                    expandFrom = Alignment.Top
                ),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "ElderEase",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D47A1),
                        letterSpacing = (-1).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(1.dp)
                            .background(Color(0xFFB0BEC5))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Care • Connect • Comfort",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF546E7A),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
