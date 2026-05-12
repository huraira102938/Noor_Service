package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.components.SettingsScreenShimmer
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeNotificationsViewModel
import com.danish.noorservice.viewmodel.employee.EmployeeSettingsViewModel

private enum class SettingsSubScreen {
    NONE, EDIT_PROFILE, CHANGE_PASSWORD, NOTIFICATIONS
}

@Composable
fun EmployeeSettingsScreen(
    userId: String,
    onLogout: () -> Unit = {},
    onProfileSaved: () -> Unit = {},
    notificationsViewModel: EmployeeNotificationsViewModel? = null,
    settingsViewModel: EmployeeSettingsViewModel
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var subScreen by remember { mutableStateOf(SettingsSubScreen.NONE) }

    when (subScreen) {
        SettingsSubScreen.EDIT_PROFILE -> {
            // ✅ FIX: Pass the SAME viewModel instance so EditProfileScreen
            // reads the already-loaded profile without firing another fetch.
            EditProfileScreen(
                userId    = userId,
                onBack    = { subScreen = SettingsSubScreen.NONE },
                onSaved   = {
                    subScreen = SettingsSubScreen.NONE
                    onProfileSaved()
                },
                viewModel = settingsViewModel
            )
            return
        }
        SettingsSubScreen.CHANGE_PASSWORD -> {
            ChangePasswordScreen(
                onBack            = { subScreen = SettingsSubScreen.NONE },
                onPasswordChanged = { subScreen = SettingsSubScreen.NONE }
            )
            return
        }
        SettingsSubScreen.NOTIFICATIONS -> {
            val notifVm = notificationsViewModel
            if (notifVm != null) {
                NotificationsScreen(
                    userId = userId,
                    onBack = { subScreen = SettingsSubScreen.NONE },
                    viewModel = notifVm
                )
                return
            }
            subScreen = SettingsSubScreen.NONE
        }
        SettingsSubScreen.NONE -> { /* fall through */ }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val profile = uiState.profile
    val initials = profile?.fullName
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercaseChar().toString() }
        ?: "?"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Settings",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Manage your preferences",
                        fontSize = 12.sp,
                        color    = Color.White.copy(alpha = 0.72f)
                    )
                }
                Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { settingsViewModel.loadProfile(userId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NoorBlue)
            }
            return@Column
        }

        if (uiState.profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pull to refresh", color = NoorTextHint, fontSize = 14.sp)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Profile summary card ──────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .clickable { subScreen = SettingsSubScreen.EDIT_PROFILE },
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier  = Modifier.padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ✅ FIX: Show actual profile photo when available.
                    // Falls back to initials avatar when no photo is set.
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(NoorBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profile?.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model              = profile!!.photoUrl,
                                contentDescription = "Profile photo",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Text(
                                initials,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            profile?.fullName ?: "—",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = NoorTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        if (!profile?.email.isNullOrBlank()) {
                            Text(profile!!.email, fontSize = 12.sp, color = NoorTextHint)
                        }
                        if (!profile?.city.isNullOrBlank()) {
                            Text("📍 ${profile!!.city}", fontSize = 11.sp, color = NoorTextHint)
                        }
                        Spacer(Modifier.height(4.dp))
                        val isApproved = profile?.isProfileApproved ?: false
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isApproved) NoorGreenLight else NoorOrangeLight
                        ) {
                            Text(
                                if (isApproved) "✅ Approved" else "⏳ Pending Review",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (isApproved) NoorGreen else NoorOrange,
                                modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint     = NoorTextHint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ── Availability ──────────────────────────────────────────────────
            SettingsGroup(title = "Availability") {
                SettingsToggleItem(
                    emoji   = "🟢",
                    emojiBg = NoorGreenLight,
                    title   = "Available for Work",
                    checked = uiState.isActive,
                    onToggle = { settingsViewModel.updateAvailability(userId, it) }
                )
            }

            // ── Notifications ─────────────────────────────────────────────────
            SettingsGroup(title = "Notifications") {
                SettingsNavItem(
                    emoji   = "🔔",
                    emojiBg = NoorBlueLight,
                    title   = "View Notifications",
                    onClick = { subScreen = SettingsSubScreen.NOTIFICATIONS }
                )
            }

            // ── Account ───────────────────────────────────────────────────────
            SettingsGroup(title = "Account") {
                SettingsNavItem(
                    emoji   = "👤",
                    emojiBg = NoorBlueLight,
                    title   = "Edit Profile",
                    onClick = { subScreen = SettingsSubScreen.EDIT_PROFILE }
                )
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                SettingsNavItem(
                    emoji   = "🔒",
                    emojiBg = Color(0xFFF3EEF9),
                    title   = "Change Password",
                    onClick = { subScreen = SettingsSubScreen.CHANGE_PASSWORD }
                )
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                SettingsNavItem(emoji = "📄", emojiBg = NoorBackground, title = "Terms & Conditions", onClick = {})
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                SettingsNavItem(emoji = "🛡️", emojiBg = NoorBackground, title = "Privacy Policy", onClick = {})
            }

            // ── Session ───────────────────────────────────────────────────────
            SettingsGroup(title = "Session") {
                SettingsNavItem(
                    emoji   = "🚪",
                    emojiBg = NoorBlueLight,
                    title   = "Log Out",
                    onClick = { showLogoutDialog = true }
                )
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                SettingsNavItem(
                    emoji      = "🗑️",
                    emojiBg    = NoorRedLight,
                    title      = "Delete Account",
                    titleColor = NoorRed,
                    onClick    = { showDeleteDialog = true }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text  = { Text("Are you sure you want to log out?", fontSize = 13.sp, color = NoorTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text("Log Out", color = NoorBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Delete Account",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = NoorRed
                )
            },
            text = {
                Text(
                    "This will permanently delete your account and all data. This action cannot be undone.",
                    fontSize = 13.sp,
                    color    = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Delete", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable settings components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text          = title.uppercase(),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = NoorTextHint,
            letterSpacing = 0.8.sp,
            modifier      = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content   = { Column(content = content) }
        )
    }
}

@Composable
private fun SettingsToggleItem(
    emoji: String,
    emojiBg: Color,
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(emojiBg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }

        Text(
            title,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = NoorTextPrimary,
            modifier   = Modifier.weight(1f)
        )

        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = NoorBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = NoorBorder
            )
        )
    }
}

@Composable
private fun SettingsNavItem(
    emoji: String,
    emojiBg: Color,
    title: String,
    titleColor: Color = NoorTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(emojiBg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }

        Text(
            title,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = titleColor,
            modifier   = Modifier.weight(1f)
        )

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint     = if (titleColor == NoorRed) NoorRed else NoorTextHint,
            modifier = Modifier.size(18.dp)
        )
    }
}