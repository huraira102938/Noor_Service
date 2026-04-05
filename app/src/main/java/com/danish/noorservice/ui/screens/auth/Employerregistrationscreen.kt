

package com.danish.noorservice.ui.screens.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Employer Registration Screen
// Shown after user selects "I want to Hire" on RoleSelectionScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerRegistrationScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    var fullName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var city      by remember { mutableStateOf("") }
    var area      by remember { mutableStateOf("") }
    var address   by remember { mutableStateOf("") }
    var about     by remember { mutableStateOf("") }
    var photoUri  by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { photoUri = it } }

    val isFormValid = fullName.isNotBlank() && city.isNotBlank()

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
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Avatar picker + title row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f))
                                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Profile photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    "📷",
                                    fontSize = 28.sp
                                )
                            }
                        }
                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(NoorOrange)
                                .border(2.dp, NoorBlueDark, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            "Create Employer Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tap photo to add a picture",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.72f)
                        )
                    }
                }
            }
        }

        // ── Scrollable Form ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Basic Info card
            NoorSectionCard {
                EmployerRegSectionLabel("Basic Information")
                Spacer(Modifier.height(14.dp))

                NoorTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name *",
                    placeholder = "Your full name"
                )
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "you@email.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            // Location card
            NoorSectionCard {
                EmployerRegSectionLabel("Location")
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City *",
                    placeholder = "e.g. Lahore"
                )
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = "Area / Sector",
                    placeholder = "e.g. DHA Phase 3"
                )
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Full Address",
                    placeholder = "Street, House No…",
                    singleLine = false,
                    maxLines = 3
                )
            }

            // About card
            NoorSectionCard {
                EmployerRegSectionLabel("About")
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value = about,
                    onValueChange = { if (it.length <= 200) about = it },
                    label = "Short Bio (optional)",
                    placeholder = "Tell workers about your household needs…",
                    singleLine = false,
                    maxLines = 4
                )
                Text(
                    "${about.length}/200 characters",
                    fontSize = 10.sp,
                    color = if (about.length > 190) NoorOrange else NoorTextHint,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // CTA
            NoorPrimaryButton(
                text = "Create Account & Continue",
                enabled = isFormValid,
                onClick = onRegistered,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                "* Required fields",
                fontSize = 11.sp,
                color = NoorTextHint,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EmployerRegSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = NoorBlue,
        letterSpacing = 0.3.sp
    )
}