package com.danish.noorservice.ui.screens.auth

import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.components.NoorTextField



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Gradient Header ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(NoorBlue, NoorBlueDark))
                )
                .padding(top = 56.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Logo placeholder circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "N",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    "Noor Services",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Provider (Pvt.) Ltd.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
            }
        }

        // ── Tab Switcher ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(NoorDivider),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            listOf("Log In", "Sign Up").forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NoorBlue else Color.Transparent)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { selectedTab = index }) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else NoorTextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // ── Form Content ─────────────────────────────────────────────────────
        if (selectedTab == 0) {
            LoginForm(onLoginSuccess = onLoginSuccess)
        } else {
            SignUpForm(onSignUpSuccess = onSignUpSuccess)
        }
    }
}

// ── Login Form ────────────────────────────────────────────────────────────────

@Composable
fun LoginForm(onLoginSuccess: () -> Unit) {
    var phone    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NoorTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "03XX-XXXXXXX",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        NoorTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true
        )

        TextButton(
            onClick = {},
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?", color = NoorBlue, fontSize = 13.sp)
        }

        NoorPrimaryButton(
            text = "Log In",
            onClick = onLoginSuccess,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// ── Sign Up Form ──────────────────────────────────────────────────────────────

@Composable
fun SignUpForm(onSignUpSuccess: () -> Unit) {
    var phone     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NoorTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            placeholder = "03XX-XXXXXXX",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        NoorTextField(
            value = password,
            onValueChange = { password = it },
            label = "Create Password",
            isPassword = true
        )

        NoorTextField(
            value = confirmPw,
            onValueChange = { confirmPw = it },
            label = "Confirm Password",
            isPassword = true,
            isError = confirmPw.isNotEmpty() && confirmPw != password,
            errorMessage = "Passwords do not match"
        )

        NoorPrimaryButton(
            text = "Create Account",
            onClick = onSignUpSuccess,
            enabled = phone.isNotBlank() && password.isNotBlank() && password == confirmPw,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "By signing up you agree to our Terms & Privacy Policy",
            color = NoorTextHint,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}