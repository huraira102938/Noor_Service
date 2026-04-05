package com.danish.noorservice.ui.screens.employee

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
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
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Static data (mirrors ServiceDetailScreen exactly)
// ─────────────────────────────────────────────────────────────────────────────

private val editServiceSkillOptions = mapOf(
    "driver"     to listOf("City Driving", "Highway", "Heavy Vehicle", "Motorcycle", "Car Maintenance"),
    "security"   to listOf("CCTV Operation", "First Aid", "Patrolling", "Firearms Certified"),
    "houseBoy"   to listOf("Cleaning", "Laundry", "Ironing", "Groceries", "Minor Repairs"),
    "officeBoy"  to listOf("Photocopying", "File Management", "Tea/Coffee", "Errands"),
    "cook"       to listOf("Pakistani Cuisine", "Chinese", "Continental", "Baking", "BBQ"),
    "maid"       to listOf("Deep Cleaning", "Laundry", "Dishes", "Childcare", "Cooking"),
    "babysitter" to listOf("Infant Care", "School Drop-off", "Tutoring Assistance", "First Aid"),
    "elderCare"  to listOf("Medication Reminders", "Physical Assistance", "Companionship", "Medical Visits"),
    "gardener"   to listOf("Lawn Mowing", "Pruning", "Planting", "Pest Control"),
    "mechanic"   to listOf("Engine Repair", "Electrical", "Tyre Change", "AC Service"),
)

private val editDaysOfWeek        = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val editTimeSlots         = listOf("Full Day", "Morning", "Evening", "Night", "Flexible")
private val editExperienceOptions = listOf("< 1 year", "1–2 yrs", "3–5 yrs", "6–10 yrs", "10+ yrs")

// ─────────────────────────────────────────────────────────────────────────────
// Edit Profile Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit = {}
) {
    // ── Profile fields ────────────────────────────────────────────────────────
    var fullName by remember { mutableStateOf("Muhammad Ali") }
    var email    by remember { mutableStateOf("m.ali@email.com") }
    var phone    by remember { mutableStateOf("0312-3456789") }
    var cnic     by remember { mutableStateOf("35202-1234567-9") }
    var city     by remember { mutableStateOf("Lahore") }
    var address  by remember { mutableStateOf("House 12, Street 5, DHA Phase 3") }
    var gender   by remember { mutableStateOf("Male") }
    var dob      by remember { mutableStateOf("15/03/1992") }
    var bio      by remember { mutableStateOf("Experienced professional driver with 5+ years in DHA & Gulberg area.") }

    // ── Pricing fields ────────────────────────────────────────────────────────
    var dailyRate   by remember { mutableStateOf("1,200") }
    var hourlyRate  by remember { mutableStateOf("") }
    var monthlyRate by remember { mutableStateOf("") }

    // ── Photo ─────────────────────────────────────────────────────────────────
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // ── Language / service selection ──────────────────────────────────────────
    val languages         = listOf("Urdu", "Punjabi", "English", "Pashto", "Sindhi", "Saraiki")
    val selectedLanguages = remember { mutableStateListOf("Urdu", "Punjabi") }

    val existingServiceIds = remember { mutableStateListOf("driver", "houseBoy") }
    val selectedServices   = remember { mutableStateListOf("driver", "houseBoy") }

    val serviceDetailStates = remember {
        mutableStateMapOf<String, ServiceDetailState>().also { map ->
            existingServiceIds.forEach { id ->
                map[id] = ServiceDetailState(id).applyMockData(id)
            }
        }
    }

    val newlyAddedIds by remember {
        derivedStateOf { selectedServices.filter { it !in existingServiceIds } }
    }

    // ── Gallery picker ─────────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { photoUri = it }
    }

    // ── Validation ────────────────────────────────────────────────────────────
    val newServicesValid by remember {
        derivedStateOf {
            newlyAddedIds.all { id ->
                val s = serviceDetailStates[id]
                s != null && s.experience.isNotEmpty() &&
                        s.selectedDays.isNotEmpty() && s.timeSlot.isNotEmpty()
            }
        }
    }
    val isFormValid = fullName.isNotBlank() && phone.isNotBlank() &&
            city.isNotBlank() && newServicesValid

    var showSavedSnackbar by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")

    // ─────────────────────────────────────────────────────────────────────────
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Avatar row ────────────────────────────────────────────
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
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
                                        model              = photoUri,
                                        contentDescription = "Profile photo",
                                        contentScale       = ContentScale.Crop,
                                        modifier           = Modifier.size(76.dp).clip(CircleShape)
                                    )
                                } else {
                                    Text("MA", fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(NoorOrange)
                                    .border(2.dp, NoorBlueDark, CircleShape)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Change photo",
                                    tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        Column {
                            Text("Edit Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = Color.White, letterSpacing = (-0.3).sp)
                            Text("Tap photo to update", fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.72f))
                        }
                    }
                }
            }

            // ── Scrollable form ───────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Basic Information ─────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("Basic Information")
                    Spacer(Modifier.height(14.dp))
                    NoorTextField(value = fullName, onValueChange = { fullName = it },
                        label = "Full Name *", placeholder = "As on CNIC")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = phone, onValueChange = { phone = it },
                        label = "Phone Number *", placeholder = "03XX-XXXXXXX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = email, onValueChange = { email = it },
                        label = "Email (optional)", placeholder = "you@email.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                    Spacer(Modifier.height(14.dp))
                    Text("Gender", fontSize = 11.sp, color = NoorTextHint,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        genderOptions.forEach { opt ->
                            NoorSelectableChip(label = opt,
                                icon     = if (opt == "Male") "👨" else "👩",
                                selected = gender == opt,
                                onClick  = { gender = opt })
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = dob, onValueChange = { dob = it },
                        label = "Date of Birth", placeholder = "DD/MM/YYYY")
                }

                // ── About You ─────────────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("About You")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = bio, onValueChange = { bio = it },
                        label = "Short Bio (optional)",
                        placeholder = "Tell employers a bit about yourself…",
                        singleLine = false, maxLines = 4)
                    Spacer(Modifier.height(4.dp))
                    Text("${bio.length}/200 characters", fontSize = 10.sp,
                        color    = if (bio.length > 200) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End))
                }

                // ── Location ──────────────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = city, onValueChange = { city = it },
                        label = "City *", placeholder = "e.g. Lahore")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = address, onValueChange = { address = it },
                        label = "Permanent Address", placeholder = "Street, Area, City",
                        singleLine = false, maxLines = 3)
                }

                // ── Identity ──────────────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("Identity")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = cnic, onValueChange = { cnic = it },
                        label = "CNIC Number", placeholder = "XXXXX-XXXXXXX-X",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                // ── Languages ─────────────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("Languages Spoken")
                    Spacer(Modifier.height(12.dp))
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)) {
                        languages.forEach { lang ->
                            val emoji = when (lang) {
                                "Urdu"    -> "🇵🇰"; "Punjabi" -> "🌾"; "English" -> "🇬🇧"
                                "Pashto"  -> "🏔️"; "Sindhi"  -> "🎋"; "Saraiki" -> "🌿"
                                else      -> "💬"
                            }
                            NoorSelectableChip(label = lang, icon = emoji,
                                selected = selectedLanguages.contains(lang),
                                onClick  = {
                                    if (selectedLanguages.contains(lang)) selectedLanguages.remove(lang)
                                    else selectedLanguages.add(lang)
                                })
                        }
                    }
                }

                // ── 💰 Pricing ────────────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("My Rates (PKR)")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Set your pricing so employers know what to expect. Leave blank if not applicable.",
                        fontSize   = 11.sp,
                        color      = NoorTextHint,
                        lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    // Daily rate row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorGreenLight),
                            contentAlignment = Alignment.Center
                        ) { Text("📅", fontSize = 18.sp) }
                        NoorTextField(
                            value           = dailyRate,
                            onValueChange   = { dailyRate = it },
                            label           = "Daily Rate",
                            placeholder     = "e.g. 1,200",
                            modifier        = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(
                            "/ day",
                            fontSize   = 12.sp,
                            color      = NoorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Hourly rate row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorBlueLight),
                            contentAlignment = Alignment.Center
                        ) { Text("⏱️", fontSize = 18.sp) }
                        NoorTextField(
                            value           = hourlyRate,
                            onValueChange   = { hourlyRate = it },
                            label           = "Hourly Rate (optional)",
                            placeholder     = "e.g. 150",
                            modifier        = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(
                            "/ hr",
                            fontSize   = 12.sp,
                            color      = NoorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Monthly rate row
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NoorOrangeLight),
                            contentAlignment = Alignment.Center
                        ) { Text("🗓️", fontSize = 18.sp) }
                        NoorTextField(
                            value           = monthlyRate,
                            onValueChange   = { monthlyRate = it },
                            label           = "Monthly Rate (optional)",
                            placeholder     = "e.g. 25,000",
                            modifier        = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Text(
                            "/ mo",
                            fontSize   = 12.sp,
                            color      = NoorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Info tip
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorBlueLight)
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 13.sp)
                            Text(
                                "Employers see your daily rate on your profile card. Your rates help them decide faster.",
                                fontSize   = 11.sp,
                                color      = NoorBlueDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // ── Services Offered ──────────────────────────────────────────
                NoorSectionCard {
                    ProfileSectionLabel("Services Offered")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tap ✨ to add a new service — you'll need to fill in its details below.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)) {
                        allServiceCategories.forEach { svc ->
                            val isSelected = selectedServices.contains(svc.id)
                            val isNew      = isSelected && svc.id !in existingServiceIds
                            NoorSelectableChip(
                                label    = svc.label,
                                icon     = if (isNew) "✨" else svc.emoji,
                                selected = isSelected,
                                onClick  = {
                                    if (isSelected) {
                                        selectedServices.remove(svc.id)
                                        if (svc.id !in existingServiceIds)
                                            serviceDetailStates.remove(svc.id)
                                    } else {
                                        selectedServices.add(svc.id)
                                        if (svc.id !in existingServiceIds)
                                            serviceDetailStates[svc.id] = ServiceDetailState(svc.id)
                                    }
                                })
                        }
                    }
                }

                // ── Inline detail cards for NEWLY added services ───────────────
                if (newlyAddedIds.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NoorBlueLight)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("ℹ️", fontSize = 14.sp)
                            Text(
                                "Fill in the required details for your newly added service(s) to enable saving.",
                                fontSize   = 12.sp,
                                color      = NoorBlueDark,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    newlyAddedIds.forEach { svcId ->
                        val state    = serviceDetailStates[svcId] ?: return@forEach
                        val category = allServiceCategories.find { it.id == svcId }
                        ProfileServiceDetailCard(
                            category = category?.label ?: svcId,
                            emoji    = category?.emoji  ?: "💼",
                            state    = state,
                            onRemove = {
                                selectedServices.remove(svcId)
                                serviceDetailStates.remove(svcId)
                            }
                        )
                    }
                }

                // ── Save Button ───────────────────────────────────────────────
                NoorPrimaryButton(
                    text    = "Save Changes",
                    enabled = isFormValid,
                    onClick = { showSavedSnackbar = true; onSaved() }
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Success Snackbar ──────────────────────────────────────────────────
        if (showSavedSnackbar) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSavedSnackbar = false
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NoorTextPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(NoorGreen),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Text("Profile updated successfully!", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Inline service detail card — shown for NEWLY added services only
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileServiceDetailCard(
    category: String,
    emoji: String,
    state: ServiceDetailState,
    onRemove: () -> Unit
) {
    NoorSectionCard {
        Row(
            modifier             = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(NoorBlueLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 20.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Text(category, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                Text("New service — fill in required details",
                    fontSize = 10.sp, color = NoorOrange, fontWeight = FontWeight.SemiBold)
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(NoorRedLight)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove",
                    tint = NoorRed, modifier = Modifier.size(15.dp))
            }
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
        Spacer(Modifier.height(16.dp))

        val skillOptions = editServiceSkillOptions[state.serviceId] ?: emptyList()
        if (skillOptions.isNotEmpty()) {
            DetailSubLabel("Skills")
            Spacer(Modifier.height(10.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)) {
                skillOptions.forEach { skill ->
                    NoorSelectableChip(
                        label    = skill,
                        icon     = if (state.selectedSkills.contains(skill)) "✓" else "·",
                        selected = state.selectedSkills.contains(skill),
                        onClick  = {
                            if (state.selectedSkills.contains(skill)) state.selectedSkills.remove(skill)
                            else state.selectedSkills.add(skill)
                        })
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (state.serviceId == "driver") {
            DetailSubLabel("Licence Type")
            Spacer(Modifier.height(10.dp))
            NoorTextField(value = state.licenceType, onValueChange = { state.licenceType = it },
                label = "Licence Type", placeholder = "e.g. LTV, HTV, Motorcycle")
            Spacer(Modifier.height(16.dp))
        }

        DetailSubLabel("Experience *")
        Spacer(Modifier.height(10.dp))
        FlowRow(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)) {
            editExperienceOptions.forEach { opt ->
                NoorSelectableChip(label = opt, icon = "⏱",
                    selected = state.experience == opt,
                    onClick  = { state.experience = opt })
            }
        }
        Spacer(Modifier.height(16.dp))

        DetailSubLabel("Available Days *")
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            editDaysOfWeek.forEach { day ->
                val selected = state.selectedDays.contains(day)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) NoorBlue else NoorBackground)
                        .border(1.dp, if (selected) NoorBlue else NoorBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            if (selected) state.selectedDays.remove(day)
                            else state.selectedDays.add(day)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(day, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = if (selected) Color.White else NoorTextSecondary)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        DetailSubLabel("Preferred Time Slot *")
        Spacer(Modifier.height(10.dp))
        FlowRow(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)) {
            editTimeSlots.forEach { slot ->
                NoorSelectableChip(label = slot, icon = "🕐",
                    selected = state.timeSlot == slot,
                    onClick  = { state.timeSlot = slot })
            }
        }
        Spacer(Modifier.height(16.dp))

        NoorTextField(value = state.note, onValueChange = { state.note = it },
            label = "Additional Notes (optional)",
            placeholder = "Any extra info about this service…",
            singleLine = false, maxLines = 3)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileSectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        color = NoorBlue, letterSpacing = 0.3.sp)
}

@Composable
private fun DetailSubLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        color = NoorTextHint, letterSpacing = 0.5.sp)
}

private fun ServiceDetailState.applyMockData(id: String): ServiceDetailState {
    experience = "3–5 yrs"
    selectedDays.addAll(listOf("Mon", "Tue", "Wed", "Thu", "Fri"))
    timeSlot = "Full Day"
    when (id) {
        "driver"   -> { selectedSkills.addAll(listOf("City Driving", "Highway")); licenceType = "LTV" }
        "houseBoy" -> { selectedSkills.addAll(listOf("Cleaning", "Laundry", "Ironing")) }
    }
    return this
}