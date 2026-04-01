package com.danish.noorservice.ui.screens.employee


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Change Password Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onPasswordChanged: () -> Unit = {}
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showSuccess     by remember { mutableStateOf(false) }

    // ── Password strength rules ───────────────────────────────────────────────
    val hasMinLength   = newPassword.length >= 8
    val hasUppercase   = newPassword.any { it.isUpperCase() }
    val hasDigit       = newPassword.any { it.isDigit() }
    val hasSpecialChar = newPassword.any { "!@#\$%^&*()_+-=[]|,.?/:;<>".contains(it) }

    val passwordStrength = listOf(hasMinLength, hasUppercase, hasDigit, hasSpecialChar).count { it }

    val strengthLabel = when (passwordStrength) {
        0, 1 -> "Weak"
        2    -> "Fair"
        3    -> "Good"
        4    -> "Strong"
        else -> ""
    }
    val strengthColor = when (passwordStrength) {
        0, 1 -> NoorRed
        2    -> NoorOrange
        3    -> Color(0xFFF9A825)   // amber
        4    -> NoorGreen
        else -> NoorBorder
    }

    val passwordsMatch  = confirmPassword.isNotEmpty() && newPassword == confirmPassword
    val passwordMismatch = confirmPassword.isNotEmpty() && newPassword != confirmPassword

    val isFormValid = currentPassword.isNotBlank() &&
            newPassword.isNotBlank() &&
            passwordStrength >= 2 &&
            passwordsMatch

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NoorBackground)
        ) {
            // ── Gradient Header ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
            ) {
                Column {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint     = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Lock icon
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔒", fontSize = 26.sp)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Change Password",
                        fontSize      = 20.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.3).sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Create a strong, unique password to keep your account secure",
                        fontSize   = 12.sp,
                        color      = Color.White.copy(alpha = 0.72f),
                        lineHeight = 18.sp
                    )
                }
            }

            // ── Form ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Current password ──────────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PasswordSectionLabel("Current Password")
                        Spacer(Modifier.height(12.dp))
                        NoorTextField(
                            value         = currentPassword,
                            onValueChange = { currentPassword = it },
                            label         = "Current Password",
                            isPassword    = true
                        )
                    }
                }

                // ── New password ──────────────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PasswordSectionLabel("New Password")
                        Spacer(Modifier.height(12.dp))

                        NoorTextField(
                            value         = newPassword,
                            onValueChange = { newPassword = it },
                            label         = "New Password",
                            isPassword    = true
                        )

                        // ── Strength indicator ────────────────────────────────
                        AnimatedVisibility(
                            visible = newPassword.isNotEmpty(),
                            enter   = fadeIn() + expandVertically()
                        ) {
                            Column {
                                Spacer(Modifier.height(12.dp))

                                // Strength bar
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    repeat(4) { index ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    if (index < passwordStrength) strengthColor
                                                    else NoorBorder
                                                )
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        strengthLabel,
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color      = strengthColor
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                // Requirements checklist
                                Text(
                                    text          = "PASSWORD REQUIREMENTS",
                                    fontSize      = 9.sp,
                                    fontWeight    = FontWeight.Bold,
                                    color         = NoorTextHint,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    PasswordRule("At least 8 characters",   hasMinLength)
                                    PasswordRule("One uppercase letter",     hasUppercase)
                                    PasswordRule("One number",               hasDigit)
                                    PasswordRule("One special character",    hasSpecialChar)
                                }
                            }
                        }
                    }
                }

                // ── Confirm password ──────────────────────────────────────────
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PasswordSectionLabel("Confirm New Password")
                        Spacer(Modifier.height(12.dp))
                        NoorTextField(
                            value        = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label        = "Confirm Password",
                            isPassword   = true,
                            isError      = passwordMismatch,
                            errorMessage = "Passwords do not match"
                        )

                        // Match success indicator
                        AnimatedVisibility(
                            visible = passwordsMatch,
                            enter   = fadeIn() + expandVertically()
                        ) {
                            Row(
                                modifier             = Modifier.padding(top = 8.dp),
                                verticalAlignment    = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(NoorGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint     = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Text(
                                    "Passwords match",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = NoorGreen
                                )
                            }
                        }
                    }
                }

                // ── Security tip ──────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NoorOrangeLight)
                        .padding(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.Top
                    ) {
                        Text("💡", fontSize = 16.sp)
                        Text(
                            "Tip: Never share your password with anyone, including Noor Services staff. " +
                                    "We will never ask you for your password.",
                            fontSize   = 11.sp,
                            color      = NoorOrange,
                            lineHeight = 16.sp
                        )
                    }
                }

                // ── Submit button ─────────────────────────────────────────────
                NoorPrimaryButton(
                    text    = "Update Password",
                    enabled = isFormValid,
                    onClick = {
                        showSuccess = true
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Success overlay ───────────────────────────────────────────────────
        if (showSuccess) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2200)
                showSuccess = false
                onPasswordChanged()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier  = Modifier
                        .padding(40.dp)
                        .fillMaxWidth(),
                    shape     = RoundedCornerShape(24.dp),
                    colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(listOf(NoorGreen, NoorGreenDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint     = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            "Password Updated!",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = NoorTextPrimary
                        )
                        Text(
                            "Your password has been changed successfully.",
                            fontSize   = 13.sp,
                            color      = NoorTextSecondary,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PasswordSectionLabel(text: String) {
    Text(
        text          = text,
        fontSize      = 13.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = NoorBlue,
        letterSpacing = 0.3.sp
    )
}

@Composable
private fun PasswordRule(label: String, satisfied: Boolean) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (satisfied) NoorGreen else NoorBorder),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = if (satisfied) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(9.dp)
            )
        }
        Text(
            text       = label,
            fontSize   = 11.sp,
            color      = if (satisfied) NoorTextPrimary else NoorTextHint,
            fontWeight = if (satisfied) FontWeight.Medium else FontWeight.Normal
        )
    }
}