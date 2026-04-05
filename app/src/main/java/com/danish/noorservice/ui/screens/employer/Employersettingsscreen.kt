package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.screens.employee.ChangePasswordScreen
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Sub-screen enum
// ─────────────────────────────────────────────────────────────────────────────

private enum class EmployerSettingsSubScreen {
    NONE, EDIT_PROFILE, CHANGE_PASSWORD, NOTIFICATIONS
}

// ─────────────────────────────────────────────────────────────────────────────
// Settings Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerSettingsScreen(onLogout: () -> Unit = {}) {
    var subScreen by remember { mutableStateOf(EmployerSettingsSubScreen.NONE) }

    when (subScreen) {
        EmployerSettingsSubScreen.EDIT_PROFILE -> {
            EmployerEditProfileScreen(
                onBack  = { subScreen = EmployerSettingsSubScreen.NONE },
                onSaved = { subScreen = EmployerSettingsSubScreen.NONE }
            )
            return
        }
        EmployerSettingsSubScreen.CHANGE_PASSWORD -> {
            ChangePasswordScreen(
                onBack            = { subScreen = EmployerSettingsSubScreen.NONE },
                onPasswordChanged = { subScreen = EmployerSettingsSubScreen.NONE }
            )
            return
        }
        EmployerSettingsSubScreen.NOTIFICATIONS -> {
            EmployerNotificationsScreen(onBack = { subScreen = EmployerSettingsSubScreen.NONE })
            return
        }
        EmployerSettingsSubScreen.NONE -> {}
    }

    var pushNotifications by remember { mutableStateOf(true)  }

    var showLogoutDialog  by remember { mutableStateOf(false) }
    var showDeleteDialog  by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text("Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text("Manage your preferences", fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.72f))
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Profile summary card ──────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth()
                    .clickable { subScreen = EmployerSettingsSubScreen.EDIT_PROFILE },
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier             = Modifier.padding(16.dp),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(NoorOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("DA", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Danish Awan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Spacer(Modifier.height(2.dp))
                        Text("newservicesprovided@gmail.com", fontSize = 11.sp, color = NoorTextHint)
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(NoorOrangeLight).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) { Text("🏠 Employer Account", fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorOrange) }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = NoorTextHint, modifier = Modifier.size(20.dp))
                }
            }

            // ── Notifications ─────────────────────────────────────────────────
            EmployerSettingsGroup("Notifications") {
                EmployerSettingsNavItem("🔔", NoorBlueLight, "View Notifications") {
                    subScreen = EmployerSettingsSubScreen.NOTIFICATIONS
                }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                EmployerSettingsToggle("🔔", NoorBlueLight, "Push Notifications",
                    pushNotifications) { pushNotifications = it }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

            }

            // ── Account ───────────────────────────────────────────────────────
            EmployerSettingsGroup("Account") {
                EmployerSettingsNavItem("👤", NoorOrangeLight, "Edit Profile") {
                    subScreen = EmployerSettingsSubScreen.EDIT_PROFILE
                }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                EmployerSettingsNavItem("🔒", Color(0xFFF3EEF9), "Change Password") {
                    subScreen = EmployerSettingsSubScreen.CHANGE_PASSWORD
                }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                EmployerSettingsNavItem("📄", NoorBackground, "Terms & Conditions") {}
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                EmployerSettingsNavItem("🛡️", NoorBackground, "Privacy Policy") {}
            }

            // ── Session ───────────────────────────────────────────────────────
            EmployerSettingsGroup("Session") {
                EmployerSettingsNavItem("🚪", NoorBlueLight, "Log Out") { showLogoutDialog = true }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                EmployerSettingsNavItem("🗑️", NoorRedLight, "Delete Account",
                    titleColor = NoorRed) { showDeleteDialog = true }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title            = { Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text             = { Text("Are you sure you want to log out?", fontSize = 13.sp, color = NoorTextSecondary) },
            confirmButton    = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Log Out", color = NoorBlue, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton    = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title            = { Text("Delete Account", fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = NoorRed) },
            text             = {
                Text("This will permanently delete your account and all data. This cannot be undone.",
                    fontSize = 13.sp, color = NoorTextSecondary)
            },
            confirmButton    = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Delete", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton    = {
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
private fun EmployerSettingsGroup(
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
private fun EmployerSettingsToggle(
    emoji: String, emojiBg: Color, title: String,
    checked: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(
        modifier             = Modifier.fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
            contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = NoorTextPrimary, modifier = Modifier.weight(1f))
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
private fun EmployerSettingsNavItem(
    emoji: String, emojiBg: Color, title: String,
    titleColor: Color = NoorTextPrimary, onClick: () -> Unit
) {
    Row(
        modifier             = Modifier.fillMaxWidth().clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
            contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = titleColor, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint     = if (titleColor == NoorRed) NoorRed else NoorTextHint,
            modifier = Modifier.size(18.dp))
    }
}