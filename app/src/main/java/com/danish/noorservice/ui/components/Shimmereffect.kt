package com.danish.noorservice.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
// Core shimmer brush builder — reuse across all skeleton composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun rememberShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFFE8E8E8),
        Color(0xFFF5F5F5),
        Color(0xFFE8E8E8),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue  = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start  = Offset(translateAnim - 200f, 0f),
        end    = Offset(translateAnim, 0f)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Generic shimmer box — building block
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    brush: Brush = rememberShimmerBrush(),
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Home screen skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeScreenShimmer() {
    val brush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(brush)
        )

        // Status banner skeleton
        ShimmerBox(
            modifier     = Modifier.fillMaxWidth().height(64.dp),
            brush        = brush,
            cornerRadius = 12.dp
        )

        // Languages skeleton
        ShimmerBox(
            modifier     = Modifier.fillMaxWidth().height(80.dp),
            brush        = brush,
            cornerRadius = 16.dp
        )

        // Services skeleton
        ShimmerBox(
            modifier     = Modifier.fillMaxWidth().height(80.dp),
            brush        = brush,
            cornerRadius = 16.dp
        )

        // Info card skeleton
        ShimmerBox(
            modifier     = Modifier.fillMaxWidth().height(100.dp),
            brush        = brush,
            cornerRadius = 16.dp
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Settings screen skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SettingsScreenShimmer() {
    val brush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Profile summary card skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(brush)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerBox(modifier = Modifier.width(140.dp).height(16.dp), brush = brush)
                    ShimmerBox(modifier = Modifier.width(100.dp).height(12.dp), brush = brush)
                    ShimmerBox(modifier = Modifier.width(80.dp).height(20.dp), brush = brush, cornerRadius = 20.dp)
                }
            }
        }

        // Section group skeletons
        repeat(3) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ShimmerBox(modifier = Modifier.width(80.dp).height(10.dp), brush = brush)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Edit profile screen skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EditProfileShimmer() {
    val brush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (it == 0) 160.dp else 120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Notifications screen skeleton
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NotificationsShimmer() {
    val brush = rememberShimmerBrush()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        repeat(6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerBox(
                    modifier     = Modifier.size(44.dp),
                    brush        = brush,
                    cornerRadius = 12.dp
                )
                Column(
                    modifier              = Modifier.weight(1f),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(13.dp), brush = brush)
                    ShimmerBox(modifier = Modifier.fillMaxWidth().height(11.dp),     brush = brush)
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f).height(11.dp), brush = brush)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp, end = 16.dp)
                    .height(0.6.dp)
                    .background(Color(0xFFEEEEEE))
            )
        }
    }
}