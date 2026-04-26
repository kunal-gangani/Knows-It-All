package com.example.know_it_all.presentation.ui.screen.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.know_it_all.presentation.ui.navigation.Screen
import com.example.know_it_all.ui.theme.KnowItAllColors
import kotlinx.coroutines.delay

/**
 * Fixes applied:
 *  1. No longer reads isLoggedIn synchronously — receives it as a parameter
 *     from NavGraph which observes AuthViewModel.uiState reactively.
 *  2. LoadingDots replaced with a pulsing acid-green dot matching the
 *     design reference's online presence indicator.
 *  3. Typography and colors aligned to KnowItAllTheme.
 *  4. Logo uses the editorial heavy/thin weight contrast from the reference.
 */
@Composable
fun SplashScreen(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.7f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "logo_alpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "text_alpha"
    )

    LaunchedEffect(Unit) {
        delay(100)
        logoVisible = true
        delay(400)
        textVisible = true
        delay(1800)
        if (isLoggedIn) {
            navController.navigate(Screen.Radar.route) {
                popUpTo(Screen.Splash.route) { isInclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { isInclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KnowItAllColors.Cream),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            // Logo mark — black rounded square with acid green dot
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .size(100.dp)
                    .background(
                        color = KnowItAllColors.NearBlack.copy(alpha = logoAlpha),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = KnowItAllColors.AcidGreen
                )
                // Online dot — top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(12.dp)
                        .background(KnowItAllColors.AcidGreen, CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // App name — editorial weight contrast
            Text(
                text = "KnowItAll",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = KnowItAllColors.NearBlack.copy(alpha = textAlpha),
                letterSpacing = (-2).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bridging the Knowledge Gap,\nOne Trade at a Time.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = KnowItAllColors.CharcoalGray.copy(alpha = textAlpha),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Pulsing acid green loading indicator
            PulsingDot()
        }
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(10.dp)
                .background(
                    KnowItAllColors.AcidGreen.copy(alpha = alpha),
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Loading",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = KnowItAllColors.WarmGray,
            letterSpacing = 1.sp
        )
    }
}