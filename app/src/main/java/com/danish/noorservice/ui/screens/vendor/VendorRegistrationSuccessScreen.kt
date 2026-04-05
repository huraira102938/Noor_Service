package com.danish.noorservice.ui.screens.vendor


import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

@Composable
fun VendorRegistrationSuccessScreen(onGoToHome: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier         = Modifier.fillMaxSize().background(NoorBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp).scale(scale).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(VendorTeal, VendorTealDark))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text("Vendor Registered! 🎉", fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, color = NoorTextPrimary,
                textAlign = TextAlign.Center)

            Text(
                "Your business profile has been submitted for review.\n" +
                        "The admin will verify your details and activate your vendor account.",
                fontSize = 14.sp, color = NoorTextSecondary,
                textAlign = TextAlign.Center, lineHeight = 21.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                VendorSuccessBadge("✅", "Profile\nSubmitted")
                VendorSuccessBadge("🔍", "Under\nReview")
                VendorSuccessBadge("📩", "Awaiting\nActivation")
            }

            Spacer(Modifier.height(16.dp))

            VendorPrimaryButton(text = "Go to Dashboard", onClick = onGoToHome)
        }
    }
}

@Composable
private fun VendorSuccessBadge(emoji: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(VendorTealLight),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 20.sp) }
        Text(label, fontSize = 10.sp, color = NoorTextSecondary,
            fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
            lineHeight = 14.sp)
    }
}