package com.danish.noorservice.ui.screens.employer

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.danish.noorservice.R
import com.danish.noorservice.ui.components.ShimmerBox
import com.danish.noorservice.ui.components.rememberShimmerBrush
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employer.EmployerHomeViewModel
import com.danish.noorservice.viewmodel.employer.EmployerNotificationsViewModel
import com.danish.noorservice.viewmodel.employer.EmployerSettingsViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Employer Home Screen
// Employer browses profiles. To hire, they contact the admin — no direct
// contact with workers.
// ─────────────────────────────────────────────────────────────────────────────

// Sub-screen sealed class (defined at top so it's resolved before use)
sealed class EmployerSubScreen {
    object None          : EmployerSubScreen()
    object Notifications : EmployerSubScreen()
    object EditProfile   : EmployerSubScreen()
}

@Composable
fun EmployerHomeScreen(
    userId: String,
    homeViewModel: EmployerHomeViewModel,
    notificationsViewModel: EmployerNotificationsViewModel,
    settingsViewModel: EmployerSettingsViewModel,
    onBrowse: () -> Unit,
    onSettings: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        homeViewModel.loadProfile(userId)
    }

    var subScreen: EmployerSubScreen by remember { mutableStateOf(EmployerSubScreen.None) }

    BackHandler(enabled = subScreen !is EmployerSubScreen.None) {
        subScreen = EmployerSubScreen.None
    }

    when (subScreen) {
        is EmployerSubScreen.Notifications -> {
            EmployerNotificationsScreen(
                userId = userId,
                viewModel = notificationsViewModel,
                onBack = { subScreen = EmployerSubScreen.None }
            )
            return
        }
        is EmployerSubScreen.EditProfile -> {
            EmployerEditProfileScreen(
                userId = userId,
                profile = uiState.profile,
                viewModel = settingsViewModel,
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
                    Text("Noor Services", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, letterSpacing = (-0.2).sp)
                    Text("Provider (Pvt.) Ltd.", fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                }
            }
        }

        // Full content area
        if (uiState.isLoading || uiState.profile == null) {
            val brush = rememberShimmerBrush()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp)), brush = brush)
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                    ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                }
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)), brush = brush)
            }
        } else {
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
                    .clickable { subScreen = EmployerSubScreen.EditProfile }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = uiState.profile?.fullName?.take(2)?.uppercase() ?: "EM"
                        if (!uiState.profile?.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = uiState.profile!!.photoUrl,
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(58.dp).clip(CircleShape)
                            )
                        } else {
                            Text(
                                initials,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            uiState.profile?.fullName ?: "—",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        val area = uiState.profile?.area ?: ""
                        val city = uiState.profile?.city ?: ""
                        val location = if (area.isNotBlank() && city.isNotBlank()) {
                            "$area, $city"
                        } else if (area.isNotBlank()) {
                            area
                        } else {
                            city
                        }
                        Text(
                            location.ifBlank { "—" }, fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.72f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Employer Account", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                    }
                }

                Text(
                    "✏️ Edit",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // ── How It Works Banner ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NoorBlueLight)
                    .padding(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("ℹ️", fontSize = 16.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            "How it works",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NoorBlueDark
                        )
                        Text(
                            "Browse worker profiles and find your match. To hire, contact the Noor Services admin — " +
                                    "they will connect you with the worker and handle the arrangement.",
                            fontSize = 12.sp,
                            color = NoorBlueDark,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EmployerStatCard(uiState.workerCount.toString(),  "Workers Listed", NoorBlue,   Modifier.weight(1f))
                EmployerStatCard(uiState.serviceCount.toString(), "Services",       NoorGreen,  Modifier.weight(1f))
                EmployerStatCard(uiState.vendorCount.toString(),  "Vendors Listed", NoorOrange, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            if (uiState.workerServiceCategories.isNotEmpty()) {
                Text("Available Workers Services", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = NoorTextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.workerServiceCategories) { category ->
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorBlueLight)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("${category.emoji} ${category.label}", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorBlueDark)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (uiState.vendorCategories.isNotEmpty()) {
                Text("Available Vendor Services", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = NoorTextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.vendorCategories) { category ->
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorOrangeLight)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("${category.emoji} ${category.label}", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorOrange)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // ── Quick Actions ─────────────────────────────────────────────────
            Text("Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = NoorTextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    emoji    = "🔍",
                    label    = "Find Workers",
                    bg       = NoorBlueLight,
                    onClick  = onBrowse,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    emoji    = "🔔",
                    label    = "Notifications",
                    bg       = NoorOrangeLight,
                    onClick  = { subScreen = EmployerSubScreen.Notifications },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Contact Admin Card ────────────────────────────────────────────
            Text("Contact Admin", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = NoorTextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            Card(
                modifier  = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Found a worker you'd like to hire?",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = NoorTextPrimary
                    )
                    Text(
                        "Share the worker's name and your requirements with our admin. " +
                                "We'll verify and connect you with the right candidate.",
                        fontSize   = 12.sp,
                        color      = NoorTextHint,
                        lineHeight = 17.sp
                    )
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    AdminContactRow(emoji = "📞", label = "Phone / WhatsApp", value = "03123339015", onPhoneClick = true)
                    AdminContactRow(emoji = "📧", label = "Email", value = "noorservicesprovider@gmail.com", onEmailClick = true)
                }
            }

            Spacer(Modifier.height(20.dp))

            Spacer(Modifier.height(8.dp))
            }
        }
    }
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
            Text(label, fontSize = 9.sp, color = NoorTextHint,
                fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp)
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
private fun AdminContactRow(emoji: String, label: String, value: String, onPhoneClick: Boolean = false, onEmailClick: Boolean = false) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onPhoneClick) Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923123339015"))
                    context.startActivity(intent)
                }
                else if (onEmailClick) Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${value}"))
                    context.startActivity(intent)
                }
                else Modifier
            ),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 13.sp, color = NoorTextPrimary, fontWeight = FontWeight.SemiBold)
        }
        if (onPhoneClick) Text("💬", fontSize = 18.sp)
        if (onEmailClick) Icon(Icons.Default.Email, contentDescription = "Email", tint = NoorBlue, modifier = Modifier.size(18.dp))
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
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(bg),
                contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 16.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 11.sp, color = NoorTextHint)
            }
            Text(time, fontSize = 10.sp, color = NoorTextHint)
        }
    }
}

