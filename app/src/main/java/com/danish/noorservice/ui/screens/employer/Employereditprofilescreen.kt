package com.danish.noorservice.ui.screens.employer


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
import androidx.compose.material.icons.filled.Check
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

@Composable
fun EmployerEditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("Danish Awan") }
    var email    by remember { mutableStateOf("newservicesprovided@gmail.com") }
    var phone    by remember { mutableStateOf("0300-9254605") }
    var city     by remember { mutableStateOf("Lahore") }
    var area     by remember { mutableStateOf("DHA Phase 3") }
    var address  by remember { mutableStateOf("House 7, Street 12, DHA Phase 3") }
    var bio      by remember { mutableStateOf("Looking for reliable and verified domestic service workers in Lahore.") }

    var photoUri         by remember { mutableStateOf<Uri?>(null) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUri = it }
    }

    val isFormValid = fullName.isNotBlank() && phone.isNotBlank() && city.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(20.dp))

                    // Avatar row
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
                            Box(
                                modifier = Modifier.size(76.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)).border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photoUri != null) {
                                    AsyncImage(model = photoUri, contentDescription = "Profile photo",
                                        contentScale = ContentScale.Crop, modifier = Modifier.size(76.dp).clip(CircleShape))
                                } else {
                                    Text("DA", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                            Box(
                                modifier = Modifier.size(26.dp).clip(CircleShape).background(NoorOrange).border(2.dp, NoorBlueDark, CircleShape).align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                        Column {
                            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.3).sp)
                            Text("Tap photo to update", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                        }
                    }
                }
            }

            // ── Form ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Basic Info
                NoorSectionCard {
                    EmployerSectionLabel("Basic Information")
                    Spacer(Modifier.height(14.dp))
                    NoorTextField(fullName, { fullName = it }, "Full Name *", placeholder = "Your full name")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(phone, { phone = it }, "Phone Number *", placeholder = "03XX-XXXXXXX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(email, { email = it }, "Email", placeholder = "you@email.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }

                // Location
                NoorSectionCard {
                    EmployerSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(city, { city = it }, "City *", placeholder = "e.g. Lahore")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(area, { area = it }, "Area / Sector", placeholder = "e.g. DHA Phase 3")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(address, { address = it }, "Full Address", placeholder = "Street, House No…",
                        singleLine = false, maxLines = 3)
                }

                // Bio
                NoorSectionCard {
                    EmployerSectionLabel("About")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(bio, { bio = it }, "Short Bio (optional)",
                        placeholder = "Tell workers about your household needs…",
                        singleLine = false, maxLines = 4)
                    Text("${bio.length}/200 characters", fontSize = 10.sp,
                        color    = if (bio.length > 200) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End))
                }

                NoorPrimaryButton("Save Changes", enabled = isFormValid, onClick = { showSavedSnackbar = true; onSaved() })
                Spacer(Modifier.height(16.dp))
            }
        }

        // Snackbar
        if (showSavedSnackbar) {
            LaunchedEffect(Unit) { kotlinx.coroutines.delay(2000); showSavedSnackbar = false }
            Box(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding().padding(16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(NoorTextPrimary).padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(NoorGreen), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Text("Profile updated successfully!", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployerSectionLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue, letterSpacing = 0.3.sp)
}