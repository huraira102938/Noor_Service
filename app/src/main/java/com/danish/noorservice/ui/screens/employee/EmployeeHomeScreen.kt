package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.Image
import com.danish.noorservice.R
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Home sub-screen routing
// ─────────────────────────────────────────────────────────────────────────────

private enum class HomeSubScreen { NONE, NOTIFICATIONS, EDIT_PROFILE }

// ─────────────────────────────────────────────────────────────────────────────
// Employee Home Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployeeHomeScreen(
    onNavigateToMessages: () -> Unit,
    onNavigateToProposals: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var subScreen by remember { mutableStateOf(HomeSubScreen.NONE) }

    // ── Sub-screen routing ────────────────────────────────────────────────────
    when (subScreen) {
        HomeSubScreen.NOTIFICATIONS -> {
            NotificationsScreen(onBack = { subScreen = HomeSubScreen.NONE })
            return
        }
        HomeSubScreen.EDIT_PROFILE -> {
            EditProfileScreen(
                onBack  = { subScreen = HomeSubScreen.NONE },
                onSaved = { subScreen = HomeSubScreen.NONE }
            )
            return
        }
        HomeSubScreen.NONE -> { /* fall through */ }
    }

    // ── Main home content ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo mark
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter     = painterResource(id = R.drawable.noor_services_app_logo),
                        contentDescription = "Logo",
                        modifier    = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .graphicsLayer {
                                scaleX       = 3f
                                scaleY       = 3f
                                translationY = 39f
                                translationX = 4f
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "Noor Services",
                        fontSize      = 15.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        "Provider (Pvt.) Ltd.",
                        fontSize      = 10.sp,
                        color         = Color.White.copy(alpha = 0.65f),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(Modifier.weight(1f))

                // ── Settings icon ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint               = Color.White,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // ── Profile Card ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "MA",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Muhammad Ali",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            "Lahore · Joined Mar 2025",
                            fontSize = 11.sp,
                            color    = Color.White.copy(alpha = 0.72f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ServiceTag("🚗 Driver")
                            ServiceTag("🧹 House Boy")
                        }
                    }
                }

                // ── Edit button (top-right) → EditProfileScreen ───────────────
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { subScreen = HomeSubScreen.EDIT_PROFILE }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint               = Color.White,
                        modifier           = Modifier.size(14.dp)
                    )
                }

                // ── Available pill (bottom-right) ─────────────────────────────
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NoorGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF90EE90))
                        )
                        Text(
                            "Available",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }
            }

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(number = "12", label = "Proposals", modifier = Modifier.weight(1f))
                StatCard(number = "8",  label = "Accepted",  modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick Access ──────────────────────────────────────────────────
            Text(
                "Quick Access",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = NoorTextPrimary,
                modifier   = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMenuItem(
                    emoji    = "👤",
                    iconBg   = NoorBlueLight,
                    title    = "How Your Profile Appears",
                    subtitle = "View your public profile as employers see it",
                    badge    = null,
                    onClick  = { /* TODO: Navigate to profile preview */ }
                )
                QuickMenuItem(
                    emoji    = "🔔",
                    iconBg   = NoorOrangeLight,
                    title    = "Notifications",
                    subtitle = "View all updates",
                    badge    = "3",
                    onClick  = { subScreen = HomeSubScreen.NOTIFICATIONS }
                )
                QuickMenuItem(
                    emoji    = "⚙️",
                    iconBg   = Color(0xFFF3EEF9),
                    title    = "Settings",
                    subtitle = "Notifications, privacy & more",
                    badge    = null,
                    onClick  = onNavigateToSettings
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Recent Activity ───────────────────────────────────────────────
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Activity",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color      = NoorTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                Text(
                    "See all",
                    fontSize   = 12.sp,
                    color      = NoorBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.clickable { onNavigateToProposals() }
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier            = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityItem(
                    emoji    = "✅",
                    bg       = NoorGreenLight,
                    title    = "Proposal Accepted",
                    subtitle = "Farhan Ahmed — Driver · DHA Phase 5",
                    time     = "Today"
                )
                ActivityItem(
                    emoji    = "📩",
                    bg       = NoorOrangeLight,
                    title    = "New Proposal Received",
                    subtitle = "Sara Khan — Driver · Model Town",
                    time     = "Yesterday"
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ServiceTag(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(number, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = NoorBlue)
            Text(label,  fontSize = 10.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
        }
    }
}

@Composable
private fun QuickMenuItem(
    emoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: String?,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier             = Modifier.padding(14.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 20.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(title,   fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = NoorTextHint)
            }

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NoorOrange)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = NoorTextHint,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ActivityItem(
    emoji: String,
    bg: Color,
    title: String,
    subtitle: String,
    time: String
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier             = Modifier.padding(12.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 16.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(title,    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = NoorTextHint)
            }

            Text(time, fontSize = 10.sp, color = NoorTextHint)
        }
    }
}