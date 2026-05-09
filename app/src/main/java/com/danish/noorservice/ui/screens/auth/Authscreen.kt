package com.danish.noorservice.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.R
import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.auth.AuthEvent
import com.danish.noorservice.viewmodel.auth.AuthViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onSignUpSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Gradient Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .padding(top = 56.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.noor_services_app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(scaleX = 2.1f, scaleY = 2.1f, translationY = 20f),
                        contentScale = ContentScale.Crop
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

        // ── Tab Switcher ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(NoorDivider),
        ) {
            listOf("Log In", "Sign Up").forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NoorBlue else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else NoorTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // ── Form ──────────────────────────────────────────────────────────────
        if (selectedTab == 0) {
            LoginForm(viewModel = viewModel, onLoginSuccess = onLoginSuccess)
        } else {
            SignUpForm(viewModel = viewModel, onSignUpSuccess = onSignUpSuccess)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Login Form
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LoginForm(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState  by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.LoginSuccess) onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NoorTextField(
            value           = email,
            onValueChange   = { email = it },
            label           = "Email",
            placeholder     = "you@example.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        NoorTextField(
            value         = password,
            onValueChange = { password = it },
            label         = "Password",
            isPassword    = true
        )
        TextButton(
            onClick  = {},
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?", color = NoorBlue, fontSize = 13.sp)
        }
        NoorPrimaryButton(
            text    = if (uiState.isLoading) "Logging in..." else "Log In",
            onClick = { viewModel.login(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading,
            modifier = Modifier.padding(top = 4.dp)
        )
        uiState.error?.let {
            Text(it, color = NoorRed, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sign Up Form — role picker is built in so we NEVER hardcode a role
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SignUpForm(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit
) {
    var email        by remember { mutableStateOf("") }
    var password     by remember { mutableStateOf("") }
    var confirmPw    by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("") }
    val uiState      by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.SignupSuccess) onSignUpSuccess()
        }
    }

    // Role options shown inside sign-up (admin excluded — admin access is separate)
    val roles = listOf(
        Triple("employee", "💼", "I'm looking for a Job"),
        Triple("employer", "🏠", "I want to Hire"),
        Triple("vendor",   "🏢", "I'm a Service Vendor"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Role picker ───────────────────────────────────────────────────────
        Text(
            "I am registering as *",
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = NoorTextHint,
            letterSpacing = 0.4.sp
        )

        roles.forEach { (roleKey, emoji, label) ->
            val isSelected = selectedRole == roleKey
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) NoorBlue.copy(alpha = 0.08f) else NoorSurface
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) NoorBlue else NoorBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { selectedRole = roleKey }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(emoji, fontSize = 22.sp)
                Text(
                    label,
                    fontSize   = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color      = if (isSelected) NoorBlue else NoorTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                // Radio dot
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(if (isSelected) NoorBlue else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) NoorBlue else NoorBorder,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)

        // ── Credentials ───────────────────────────────────────────────────────
        NoorTextField(
            value           = email,
            onValueChange   = { email = it },
            label           = "Email",
            placeholder     = "you@example.com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        NoorTextField(
            value         = password,
            onValueChange = { password = it },
            label         = "Create Password",
            isPassword    = true
        )
        NoorTextField(
            value        = confirmPw,
            onValueChange = { confirmPw = it },
            label         = "Confirm Password",
            isPassword    = true,
            isError       = confirmPw.isNotEmpty() && confirmPw != password,
            errorMessage  = "Passwords do not match"
        )

        NoorPrimaryButton(
            text    = if (uiState.isLoading) "Creating account..." else "Create Account",
            onClick = { viewModel.signup(email, password, selectedRole) },
            enabled = email.isNotBlank()
                    && password.isNotBlank()
                    && password == confirmPw
                    && selectedRole.isNotEmpty()
                    && !uiState.isLoading,
            modifier = Modifier.padding(top = 4.dp)
        )

        if (selectedRole.isEmpty()) {
            Text(
                "⚠️ Please select your role above before creating an account",
                color    = NoorOrange,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        uiState.error?.let {
            Text(it, color = NoorRed, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
        }

        Text(
            "By signing up you agree to our Terms & Privacy Policy",
            color     = NoorTextHint,
            fontSize  = 11.sp,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}