package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.R
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Employer Home Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerHomeScreen(
    onBrowse: () -> Unit,
    onBookings: () -> Unit,
    onMessages: () -> Unit,
    onSettings: () -> Unit
) {
    var subScreen by remember { mutableStateOf<EmployerSubScreen>(EmployerSubScreen.None) }

    when (subScreen) {
        is EmployerSubScreen.Notifications -> {
            EmployerNotificationsScreen(onBack = { subScreen = EmployerSubScreen.None })
            return
        }
        is EmployerSubScreen.EditProfile -> {
            EmployerEditProfileScreen(
                onBack  = { subScreen = EmployerSubScreen.None },
                onSaved = { subScreen = EmployerSubScreen.None }
            )
            return
        }
        else -> {}
    }

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
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter            = painterResource(id = R.drawable.noor_services_app_logo),
                        contentDescription = "Logo",
                        modifier           = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .graphicsLayer { scaleX = 3f; scaleY = 3f; translationY = 39f; translationX = 4f },
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Noor Services", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.2).sp)
                    Text("Provider (Pvt.) Ltd.", fontSize = 10.sp, color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.weight(1f))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // ── Employer Profile Card ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(NoorOrange, Color(0xFFB84800))))
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
                        Text("DA", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Danish Awan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("DHA Phase 3, Lahore", fontSize = 11.sp, color = Color.White.copy(alpha = 0.72f))
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Employer Account", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Notification badge removed from here
            }

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmployerStatCard("5",  "Total Workers",   NoorBlue,   Modifier.weight(1f))
                EmployerStatCard("12", "Proposal Sent",   NoorGreen,  Modifier.weight(1f))
                EmployerStatCard("2",  "Proposal Accepted",       NoorOrange, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // ── Free Access Card ───────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable { /* Show coming soon info */ },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - Icon and text
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Full Access - No Charges Yet!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "You're enjoying completely FREE access to all features. Enjoy Noor services — your trusted partner for every task.!",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 14.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        // Decorative line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NoorOrange, Color.White.copy(alpha = 0.3f))
                                    )
                                )
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 10.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Premium features coming soon!",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Right side - Badge
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NoorOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎉", fontSize = 24.sp)
                            Text(
                                "BETA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick Actions ─────────────────────────────────────────────────
            Text("Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(emoji = "🔍", label = "Find Workers", bg = NoorBlueLight, onClick = onBrowse, modifier = Modifier.weight(1f))
                QuickActionCard(emoji = "🔔", label = "Notifications",  bg = NoorGreenLight, onClick = onBookings, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Recent Proposals ───────────────────────────────────────────────
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Recent", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary, modifier = Modifier.weight(1f))
                Text("See all", fontSize = 12.sp, color = NoorBlue, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onBookings() })
            }
            Spacer(Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EmployerActivityItem("✅", NoorGreenLight, "Proposal Accepted", "Muhammad Ali — Driver · DHA Phase 5", "Today")
                EmployerActivityItem("⏳", NoorOrangeLight, "Proposal Pending", "Sara Khan — Maid · Model Town", "Yesterday")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-screen sealed class
// ─────────────────────────────────────────────────────────────────────────────

sealed class EmployerSubScreen {
    object None          : EmployerSubScreen()
    object Notifications : EmployerSubScreen()
    object EditProfile   : EmployerSubScreen()
}

// ─────────────────────────────────────────────────────────────────────────────
// Small composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerStatCard(number: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(number, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accent)
            Text(label, fontSize = 9.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
        }
    }
}

@Composable
private fun QuickActionCard(
    emoji: String,
    label: String,
    bg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(bg),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 22.sp) }
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorTextSecondary)
        }
    }
}

@Composable
fun EmployerActivityItem(emoji: String, bg: Color, title: String, subtitle: String, time: String) {
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
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title,    fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = NoorTextHint)
            }
            Text(time, fontSize = 10.sp, color = NoorTextHint)
        }
    }
}