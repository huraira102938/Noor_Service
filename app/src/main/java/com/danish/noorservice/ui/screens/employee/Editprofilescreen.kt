package com.danish.noorservice.ui.screens.employee

import android.content.Intent
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.imageLoader
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.ui.components.EditProfileShimmer
import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeSettingsViewModel

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit = {},
    // ✅ FIX: Receives the already-loaded EmployeeSettingsViewModel from
    // EmployeeSettingsScreen so we NEVER trigger a second Firestore fetch.
    // hiltViewModel() is kept as a fallback for standalone preview/testing only.
    viewModel: EmployeeSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ FIX: REMOVED the LaunchedEffect { viewModel.loadProfile(userId) } that
    // was here before. The profile is already loaded by EmployeeSettingsScreen
    // before it navigates here, so calling loadProfile again caused:
    //   1. A redundant Firestore read
    //   2. A brief isLoading = true flash that wiped the form fields
    // The hasLoaded guard in the VM would have caught most cases, but removing
    // this call entirely is the cleanest and most reliable fix.

    // Navigate away after a successful save
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            context.imageLoader.memoryCache?.clear()
            viewModel.clearSaveSuccess()
            onSaved()
        }
    }

    val profile  = uiState.profile
    val services = uiState.services

    val categoriesMap = remember(uiState.categories) {
        uiState.categories.associate { it.id to it }
    }

    val editServiceCategories = remember(categoriesMap) {
        if (categoriesMap.isNotEmpty()) {
            categoriesMap.values.map { cat ->
                ServiceCategory(
                    id = cat.id,
                    label = cat.label,
                    emoji = cat.emoji
                )
            }
        } else {
            allServiceCategories
        }
    }

    // ✅ FIX: Show shimmer on initial load instead of a blocking spinner.
    // This can only happen if this screen is ever opened without going through
    // EmployeeSettingsScreen (e.g. deep-link or direct navigation in future).
    if (uiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NoorBackground)
        ) {
            // Keep the gradient header visible during shimmer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
            ) {
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
            }
            EditProfileShimmer()
        }
        return
    }

    var fullName    by remember(profile) { mutableStateOf(profile?.fullName    ?: "") }
    var email       by remember(profile) { mutableStateOf(profile?.email       ?: "") }
    var phone       by remember(profile) { mutableStateOf(profile?.phone       ?: "") }
    var cnic        by remember(profile) { mutableStateOf(profile?.cnic        ?: "") }
    var city        by remember(profile) { mutableStateOf(profile?.city        ?: "") }
    var address     by remember(profile) { mutableStateOf(profile?.address     ?: "") }
    var gender      by remember(profile) { mutableStateOf(profile?.gender      ?: "") }
    var dob         by remember(profile) { mutableStateOf(profile?.dob         ?: "") }
    var bio         by remember(profile) { mutableStateOf(profile?.bio         ?: "") }
    var dailyRate   by remember(profile) { mutableStateOf(profile?.dailyRate   ?: "") }
    var hourlyRate  by remember(profile) { mutableStateOf(profile?.hourlyRate  ?: "") }
    var monthlyRate by remember(profile) { mutableStateOf(profile?.monthlyRate ?: "") }

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val languages         = listOf("Urdu", "Punjabi", "English", "Pashto", "Sindhi", "Saraiki")
    val selectedLanguages = remember(profile) {
        mutableStateListOf<String>().apply {
            addAll(profile?.languages ?: listOf("Urdu"))
        }
    }

    val existingServiceIds = remember(services) {
        mutableStateListOf<String>().apply { addAll(services.map { it.serviceId }) }
    }
    val selectedServices = remember(services) {
        mutableStateListOf<String>().apply { addAll(services.map { it.serviceId }) }
    }
    val serviceDetailStates = remember(services) {
        mutableStateMapOf<String, ServiceDetailState>().also { map ->
            services.forEach { svc ->
                map[svc.serviceId] = ServiceDetailState(svc.serviceId).apply {
                    experience = svc.experience
                    timeSlot   = svc.availabilityTime
                    note       = svc.additionalNote
                    selectedDays.addAll(svc.availabilityDays)
                    selectedSkills.addAll(svc.skills)
                    if (svc.serviceId == "driver") licenceType = svc.additionalNote
                }
            }
        }
    }

    val newlyAddedIds by remember {
        derivedStateOf { selectedServices.filter { it !in existingServiceIds } }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            photoUri = it
        }
    }

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
            city.isNotBlank() && newServicesValid && !uiState.isSaving

    val genderOptions = listOf("Male", "Female")

    val initials = fullName
        .split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "?" }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(NoorBackground)
        ) {
            // Gradient Header
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
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.clickable {
                            galleryLauncher.launch(arrayOf("image/*"))
                        }) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    photoUri != null ->
                                        AsyncImage(
                                            model = photoUri,
                                            contentDescription = "Profile photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(76.dp).clip(CircleShape)
                                        )
                                    !profile?.photoUrl.isNullOrBlank() ->
                                        AsyncImage(
                                            model = profile!!.photoUrl,
                                            contentDescription = "Profile photo",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(76.dp).clip(CircleShape)
                                        )
                                    else ->
                                        Text(initials, fontSize = 28.sp,
                                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(26.dp).clip(CircleShape)
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

            // Scrollable form
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                NoorSectionCard {
                    ProfileSectionLabel("Identity")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = cnic, onValueChange = { cnic = it },
                        label = "CNIC Number", placeholder = "XXXXX-XXXXXXX-X",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

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

                NoorSectionCard {
                    ProfileSectionLabel("My Rates (PKR)")
                    Spacer(Modifier.height(4.dp))
                    Text("Set your pricing so employers know what to expect.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(NoorGreenLight), contentAlignment = Alignment.Center) {
                            Text("📅", fontSize = 18.sp) }
                        NoorTextField(value = dailyRate, onValueChange = { dailyRate = it },
                            label = "Daily Rate", placeholder = "e.g. 1,200",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Text("/ day", fontSize = 12.sp, color = NoorTextHint)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(NoorBlueLight), contentAlignment = Alignment.Center) {
                            Text("⏱️", fontSize = 18.sp) }
                        NoorTextField(value = hourlyRate, onValueChange = { hourlyRate = it },
                            label = "Hourly Rate (optional)", placeholder = "e.g. 150",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Text("/ hr", fontSize = 12.sp, color = NoorTextHint)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
                            .background(NoorOrangeLight), contentAlignment = Alignment.Center) {
                            Text("🗓️", fontSize = 18.sp) }
                        NoorTextField(value = monthlyRate, onValueChange = { monthlyRate = it },
                            label = "Monthly Rate (optional)", placeholder = "e.g. 25,000",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        Text("/ mo", fontSize = 12.sp, color = NoorTextHint)
                    }
                }

                NoorSectionCard {
                    ProfileSectionLabel("Services Offered")
                    Spacer(Modifier.height(6.dp))
                    Text("Your current services are shown selected. Tap to add or remove.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)) {
                        editServiceCategories.forEach { svc ->
                            val isSelected = selectedServices.contains(svc.id)
                            val isNew      = isSelected && svc.id !in existingServiceIds
                            NoorSelectableChip(
                                label    = svc.label,
                                icon     = if (isNew) "✨" else svc.emoji,
                                selected = isSelected,
                                onClick  = {
                                    if (isSelected) {
                                        selectedServices.remove(svc.id)
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

                if (existingServiceIds.isNotEmpty()) {
                    Text("YOUR SERVICES", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = NoorTextHint, letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 4.dp))
                    existingServiceIds
                        .filter { selectedServices.contains(it) }
                        .forEach { svcId ->
                            val state    = serviceDetailStates[svcId] ?: return@forEach
                            val category = categoriesMap[svcId] ?: allServiceCategories.find { it.id == svcId }
                            val categorySkills = viewModel.getCategorySkills(svcId)
                            EditableServiceCard(
                                category   = viewModel.getServiceName(svcId),
                                emoji      = viewModel.getServiceEmoji(svcId),
                                state      = state,
                                categorySkills = categorySkills,
                                isExisting = true
                            )
                        }
                }

                if (newlyAddedIds.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NoorBlueLight)
                        .padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text("ℹ️ Fill in the required details for your new service(s) to enable saving.",
                            fontSize = 12.sp, color = NoorBlueDark,
                            fontWeight = FontWeight.Medium, lineHeight = 17.sp)
                    }
                    newlyAddedIds.forEach { svcId ->
                        val state    = serviceDetailStates[svcId] ?: return@forEach
                        val categorySkills = viewModel.getCategorySkills(svcId)
                        EditableServiceCard(
                            category   = viewModel.getServiceName(svcId),
                            emoji      = viewModel.getServiceEmoji(svcId),
                            state      = state,
                            categorySkills = categorySkills,
                            isExisting = false,
                            onRemove   = {
                                selectedServices.remove(svcId)
                                serviceDetailStates.remove(svcId)
                            }
                        )
                    }
                }

                if (uiState.error != null) {
                    Box(modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorRedLight)
                        .padding(horizontal = 14.dp, vertical = 10.dp)) {
                        Text("⚠️ ${uiState.error}", fontSize = 12.sp, color = NoorRed)
                    }
                }

                NoorPrimaryButton(
                    text    = if (uiState.isSaving) "Saving…" else "Save Changes",
                    enabled = isFormValid,
                    onClick = {
                        val updatedServices = selectedServices.mapNotNull { svcId ->
                            val s = serviceDetailStates[svcId] ?: return@mapNotNull null
                            EmployeeService(
                                serviceId        = svcId,
                                skills           = s.selectedSkills.toList(),
                                experience       = s.experience,
                                availabilityDays = s.selectedDays.toList(),
                                availabilityTime = s.timeSlot,
                                additionalNote   = s.note,
                                dailyRate        = dailyRate
                            )
                        }
                        viewModel.saveProfile(
                            userId          = userId,
                            fullName        = fullName,
                            email           = email,
                            phone           = phone,
                            cnic            = cnic,
                            city            = city,
                            address         = address,
                            gender          = gender,
                            dob             = dob,
                            bio             = bio,
                            languages       = selectedLanguages.toList(),
                            dailyRate       = dailyRate,
                            hourlyRate      = hourlyRate,
                            monthlyRate     = monthlyRate,
                            serviceIds      = selectedServices.toList(),
                            updatedServices = updatedServices,
                            photoUri        = photoUri
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        // Saving overlay
        if (uiState.isSaving) {
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .navigationBarsPadding().padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NoorTextPrimary)
                    .padding(horizontal = 18.dp, vertical = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Saving profile…", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ── Editable service card ─────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableServiceCard(
    category: String,
    emoji: String,
    state: ServiceDetailState,
    categorySkills: List<String> = emptyList(),
    isExisting: Boolean,
    onRemove: (() -> Unit)? = null
) {
    NoorSectionCard {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(40.dp)
                .background(NoorBlueLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(category, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                Text(
                    if (isExisting) "Tap to edit your details" else "New — fill in required details",
                    fontSize   = 10.sp,
                    color      = if (isExisting) NoorTextHint else NoorOrange,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (!isExisting && onRemove != null) {
                Box(modifier = Modifier.size(30.dp).clip(CircleShape)
                    .background(NoorRedLight).clickable { onRemove() },
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Close, contentDescription = "Remove",
                        tint = NoorRed, modifier = Modifier.size(15.dp))
                }
            }
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
        Spacer(Modifier.height(16.dp))

        val skillOptions = if (categorySkills.isNotEmpty()) categorySkills else (editServiceSkillOptions[state.serviceId] ?: emptyList())
        if (skillOptions.isNotEmpty()) {
            EditSubLabel("Skills")
            Spacer(Modifier.height(10.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)) {
                skillOptions.forEach { skill ->
                    NoorSelectableChip(label = skill,
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
            EditSubLabel("Licence Type")
            Spacer(Modifier.height(10.dp))
            NoorTextField(value = state.licenceType, onValueChange = { state.licenceType = it },
                label = "Licence Type", placeholder = "e.g. LTV, HTV, Motorcycle")
            Spacer(Modifier.height(16.dp))
        }

        EditSubLabel("Experience *")
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

        EditSubLabel("Available Days *")
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            editDaysOfWeek.forEach { day ->
                val selected = state.selectedDays.contains(day)
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                    .background(if (selected) NoorBlue else NoorBackground)
                    .border(1.dp, if (selected) NoorBlue else NoorBorder, RoundedCornerShape(8.dp))
                    .clickable {
                        if (selected) state.selectedDays.remove(day)
                        else state.selectedDays.add(day)
                    }.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center) {
                    Text(day, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        color = if (selected) Color.White else NoorTextSecondary)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        EditSubLabel("Preferred Time Slot *")
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

@Composable
private fun ProfileSectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        color = NoorBlue, letterSpacing = 0.3.sp)
}

@Composable
private fun EditSubLabel(text: String) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        color = NoorTextHint, letterSpacing = 0.5.sp)
}