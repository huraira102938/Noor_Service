package com.danish.noorservice.ui.screens.vendor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.vendor.VendorRegistrationEvent
import com.danish.noorservice.viewmodel.vendor.VendorRegistrationViewModel
import com.danish.noorservice.viewmodel.vendor.VendorServiceInput

// ─────────────────────────────────────────────────────────────────────────────
// Vendor service categories offered TO employers (B2B)
// ─────────────────────────────────────────────────────────────────────────────

data class VendorServiceCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val description: String
)

val allVendorServiceCategories = listOf(
    VendorServiceCategory("staffing",     "Staffing Solutions",     "👥", "Bulk workforce supply & temp staffing"),
    VendorServiceCategory("security",     "Security Services",      "🛡️", "Trained guards, CCTV, access control"),
    VendorServiceCategory("cleaning",     "Cleaning & Janitorial",  "🧹", "Office, industrial & residential cleaning"),
    VendorServiceCategory("catering",     "Catering & Mess",        "🍽️", "Corporate meal plans & canteen management"),
    VendorServiceCategory("maintenance",  "Facility Maintenance",   "🔧", "Plumbing, electrical, HVAC & civil works"),
    VendorServiceCategory("it_support",   "IT Support",             "💻", "Network, hardware & helpdesk services"),
    VendorServiceCategory("transport",    "Transport & Logistics",  "🚌", "Staff transport & fleet management"),
    VendorServiceCategory("landscaping",  "Landscaping",            "🌿", "Garden maintenance & outdoor spaces"),
    VendorServiceCategory("pest_control", "Pest Control",           "🐛", "Residential & commercial pest management"),
    VendorServiceCategory("training",     "Training & Development", "📚", "Workforce training & skill development"),
)

private val serviceScaleOptions   = listOf("1–10 staff", "11–50 staff", "51–200 staff", "200+ staff")
private val cityOptions           = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")

// Shared with catalog screen
internal val pricingModelOptions  = listOf("Monthly Contract", "Per Shift", "Project-Based", "Hourly Rate", "Custom Quote")
internal val contractDurationOpts = listOf("< 1 Month", "1 Month", "3 Months", "6 Months", "1 Year", "Flexible")
internal val coverageAreaOptions  = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Registration Screen  (2-step wizard)
//
// FIX: Now accepts VendorRegistrationViewModel so data is saved to Firestore
// and isProfileComplete is set to true on submit.  Previously the "Submit"
// button called onRegistered() directly without persisting anything, causing
// the vendor to land back on this screen every time the app reopened.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorRegistrationScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    // ✅ FIX: Accept (or create) the ViewModel
    viewModel: VendorRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // ✅ FIX: Navigate to success screen only after Firestore write completes
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is VendorRegistrationEvent.Success -> onRegistered()
                is VendorRegistrationEvent.Error   -> { /* shown via uiState.error */ }
            }
        }
    }

    // ── Local UI-only state (expansion, per-service maps, client input) ──────
    // These don't need to survive process death so local state is fine here.
    val selectedServiceIds   = remember { mutableStateListOf<String>() }
    val servicePricingModel  = remember { mutableStateMapOf<String, String>() }
    val servicePriceRange    = remember { mutableStateMapOf<String, String>() }
    val serviceMinContract   = remember { mutableStateMapOf<String, String>() }
    val serviceCoverageAreas = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    val serviceSkills        = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    val expandedServiceIds   = remember { mutableStateListOf<String>() }

    val selectedCities       = remember { mutableStateListOf<String>() }
    var isoRegistered        by remember { mutableStateOf(false) }
    var hasPreviousClients   by remember { mutableStateOf(false) }
    val previousClientsList  = remember { mutableStateListOf<String>() }
    var currentClientInput   by remember { mutableStateOf("") }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.setLogoUri(uri) }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    val vendorCategories = remember(uiState.categories) {
        if (uiState.categories.isNotEmpty()) {
            uiState.categories.map { cat ->
                VendorServiceCategory(
                    id = cat.id,
                    label = cat.label,
                    emoji = cat.emoji,
                    description = cat.label
                )
            }
        } else {
            allVendorServiceCategories
        }
    }

    val step1Valid = uiState.businessName.isNotBlank() && uiState.contactPerson.isNotBlank() &&
            uiState.phone.isNotBlank() && uiState.city.isNotBlank()
    val step2Valid = selectedServiceIds.isNotEmpty() && uiState.serviceScale.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Gradient Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable {
                            if (uiState.currentStep == 1) onBack() else viewModel.goBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Spacer(Modifier.height(16.dp))

                // Step progress bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(2) { index ->
                        val stepNum = index + 1
                        val color = when {
                            stepNum < uiState.currentStep  -> VendorAccent
                            stepNum == uiState.currentStep -> Color.White
                            else                           -> Color.White.copy(alpha = 0.28f)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f).height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                    }
                }

                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.logoUri != null) {
                            AsyncImage(
                                model = uiState.logoUri,
                                contentDescription = "Company Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            Text("🏢", fontSize = 22.sp)
                        }
                    }

                    Column {
                        Text(
                            if (uiState.currentStep == 1) "Business Information" else "Services & Capacity",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.3).sp
                        )
                        Text(
                            if (uiState.currentStep == 1) "Tell us about your business"
                            else "What do you offer and at what scale?",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // ── Form body ─────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.currentStep == 1) {
                // ── STEP 1: Business Info ─────────────────────────────────────

                NoorSectionCard {
                    VendorSectionLabel("Company Logo / Photo")
                    Spacer(Modifier.height(4.dp))
                    Text("Upload your company logo or a photo to display on your profile.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VendorTealLight)
                                .border(
                                    width = 2.dp,
                                    color = if (uiState.logoUri != null) VendorTeal else NoorBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { logoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.logoUri != null) {
                                AsyncImage(
                                    model = uiState.logoUri,
                                    contentDescription = "Logo Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null,
                                        tint = VendorTeal, modifier = Modifier.size(24.dp))
                                    Text("Upload", fontSize = 10.sp,
                                        color = VendorTeal, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                if (uiState.logoUri != null) "✅ Logo selected" else "No logo selected",
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                color = if (uiState.logoUri != null) VendorTeal else NoorTextHint
                            )
                            Text("Recommended: square image, PNG or JPG, at least 200×200 px.",
                                fontSize = 10.sp, color = NoorTextHint, lineHeight = 15.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VendorTealLight)
                                        .clickable { logoPickerLauncher.launch("image/*") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        if (uiState.logoUri != null) "Change Photo" else "Choose File",
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        color = VendorTeal
                                    )
                                }
                                if (uiState.logoUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NoorRedLight)
                                            .clickable { viewModel.setLogoUri(null) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Remove", fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold, color = NoorRed)
                                    }
                                }
                            }
                        }
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("Business Identity")
                    Spacer(Modifier.height(14.dp))
                    NoorTextField(
                        value = uiState.businessName,
                        onValueChange = { viewModel.updateBusinessName(it) },
                        label = "Business / Company Name *",
                        placeholder = "e.g. Al-Noor Facility Services")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.contactPerson,
                        onValueChange = { viewModel.updateContactPerson(it) },
                        label = "Contact Person *",
                        placeholder = "Full name of primary contact")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.updatePhone(it) },
                        label = "Phone Number *", placeholder = "03XX-XXXXXXX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = "Business Email", placeholder = "info@company.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }

                NoorSectionCard {
                    VendorSectionLabel("Legal & Registration")
                    Spacer(Modifier.height(4.dp))
                    Text("Optional but increases trust with employers.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NoorTextField(
                            value = uiState.ntn,
                            onValueChange = { viewModel.updateNtn(it) },
                            label = "NTN Number", placeholder = "XXXXXXX-X",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        NoorTextField(
                            value = uiState.regNumber,
                            onValueChange = { viewModel.updateRegNumber(it) },
                            label = "Company Reg No.", placeholder = "SECP / FBR",
                            modifier = Modifier.weight(1f))
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.city,
                        onValueChange = { viewModel.updateCity(it) },
                        label = "Head Office City *", placeholder = "e.g. Lahore")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.updateAddress(it) },
                        label = "Address", placeholder = "Street, Area, City",
                        singleLine = false, maxLines = 3)
                    Spacer(Modifier.height(14.dp))
                    Text("Cities You Operate In", fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextHint,
                        letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(10.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(10.dp)) {
                        cityOptions.forEach { c ->
                            NoorSelectableChip(
                                label    = c, icon = "📍",
                                selected = selectedCities.contains(c),
                                onClick  = {
                                    if (selectedCities.contains(c)) selectedCities.remove(c)
                                    else selectedCities.add(c)
                                    viewModel.updateOperatingCities(selectedCities.toList())
                                })
                        }
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("About Your Business")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = uiState.bio,
                        onValueChange = { viewModel.updateBio(it) },
                        label = "Business Description (optional)",
                        placeholder = "What makes your business stand out? Mention experience, certifications, notable clients…",
                        singleLine = false, maxLines = 5)
                    Text("${uiState.bio.length}/300",
                        fontSize = 10.sp,
                        color    = if (uiState.bio.length > 280) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                }

                VendorPrimaryButton(
                    text    = "Continue  →",
                    enabled = step1Valid,
                    onClick = { viewModel.goToStep2() }
                )

            } else {
                // ── STEP 2: Services & Capacity ───────────────────────────────

                NoorSectionCard {
                    VendorSectionLabel("Services You Offer *")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Select each service you provide. A detail card will appear below to configure pricing and coverage.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)
                    ) {
                        vendorCategories.forEach { svc ->
                            NoorSelectableChip(
                                label    = svc.label,
                                icon     = svc.emoji,
                                selected = selectedServiceIds.contains(svc.id),
                                onClick  = {
                                    if (selectedServiceIds.contains(svc.id)) {
                                        selectedServiceIds.remove(svc.id)
                                        expandedServiceIds.remove(svc.id)
                                        servicePricingModel.remove(svc.id)
                                        servicePriceRange.remove(svc.id)
                                        serviceMinContract.remove(svc.id)
                                        serviceCoverageAreas.remove(svc.id)
                                    } else {
                                        selectedServiceIds.add(svc.id)
                                        expandedServiceIds.add(svc.id)
                                        serviceCoverageAreas[svc.id] = mutableStateListOf()
                                    }
                                    // Sync selected services to ViewModel
                                    viewModel.updateSelectedServices(selectedServiceIds.toList())
                                }
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = selectedServiceIds.isNotEmpty(),
                    enter   = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        selectedServiceIds.forEach { id ->
                            val svc = vendorCategories.find { it.id == id } ?: return@forEach
                            val isExpanded = expandedServiceIds.contains(id)

                            Card(
                                modifier  = Modifier.fillMaxWidth(),
                                shape     = RoundedCornerShape(16.dp),
                                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isExpanded) expandedServiceIds.remove(id)
                                                else expandedServiceIds.add(id)
                                            },
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(VendorTealLight),
                                            contentAlignment = Alignment.Center
                                        ) { Text(svc.emoji, fontSize = 18.sp) }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(svc.label, fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                            val priceFilled    = servicePriceRange[id]?.isNotBlank() == true
                                            val modelFilled    = servicePricingModel[id]?.isNotBlank() == true
                                            val contractFilled = serviceMinContract[id]?.isNotBlank() == true
                                            val doneCount = listOf(priceFilled, modelFilled, contractFilled).count { it }
                                            Text(
                                                if (doneCount == 3) "✅ Details complete"
                                                else "$doneCount / 3 fields filled",
                                                fontSize = 11.sp,
                                                color = if (doneCount == 3) VendorTeal else NoorTextHint
                                            )
                                        }

                                        Icon(
                                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint     = NoorTextHint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter   = fadeIn() + expandVertically()
                                    ) {
                                        Column {
                                            Spacer(Modifier.height(14.dp))
                                            HorizontalDivider(color = NoorDivider, thickness = 0.7.dp)
                                            Spacer(Modifier.height(14.dp))

                                            Text("Pricing Model *", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement   = Arrangement.spacedBy(8.dp)
                                            ) {
                                                pricingModelOptions.forEach { opt ->
                                                    NoorSelectableChip(
                                                        label    = opt, icon = "💰",
                                                        selected = servicePricingModel[id] == opt,
                                                        onClick  = {
                                                            servicePricingModel[id] = opt
                                                            syncServiceDetail(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceCoverageAreas, viewModel)
                                                        }
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            NoorTextField(
                                                value = servicePriceRange[id] ?: "",
                                                onValueChange = {
                                                    servicePriceRange[id] = it
                                                    syncServiceDetail(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceCoverageAreas, viewModel)
                                                },
                                                label       = "Price Range *",
                                                placeholder = "e.g. PKR 15,000 – 80,000 / month"
                                            )

                                            Spacer(Modifier.height(14.dp))

                                            Text("Minimum Contract Duration *", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement   = Arrangement.spacedBy(8.dp)
                                            ) {
                                                contractDurationOpts.forEach { opt ->
                                                    NoorSelectableChip(
                                                        label    = opt, icon = "📅",
                                                        selected = serviceMinContract[id] == opt,
                                                        onClick  = {
                                                            serviceMinContract[id] = opt
                                                            syncServiceDetail(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceCoverageAreas, viewModel)
                                                        }
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            Text("Coverage Areas", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement   = Arrangement.spacedBy(8.dp)
                                            ) {
                                                coverageAreaOptions.forEach { area ->
                                                    val areas = serviceCoverageAreas[id] ?: mutableStateListOf()
                                                    NoorSelectableChip(
                                                        label    = area, icon = "📍",
                                                        selected = areas.contains(area),
                                                        onClick  = {
                                                            val current = serviceCoverageAreas.getOrPut(id) { mutableStateListOf() }
                                                            if (current.contains(area)) current.remove(area)
                                                            else current.add(area)
                                                            syncServiceDetail(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceCoverageAreas, viewModel)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("Business Capacity")
                    Spacer(Modifier.height(4.dp))
                    Text("Helps employers understand your scale.", fontSize = 11.sp,
                        color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))

                    Text("Workforce Scale *", fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextHint,
                        letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(10.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(10.dp)) {
                        serviceScaleOptions.forEach { opt ->
                            NoorSelectableChip(label = opt, icon = "👥",
                                selected = uiState.serviceScale == opt,
                                onClick  = { viewModel.updateServiceScale(opt) })
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    NoorTextField(
                        value = uiState.yearsInBusiness,
                        onValueChange = { viewModel.updateYearsInBusiness(it) },
                        label = "Years in Business", placeholder = "e.g. 8",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                NoorSectionCard {
                    VendorSectionLabel("Credentials & Trust")
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(36.dp)
                                .clip(RoundedCornerShape(10.dp)).background(VendorTealLight),
                                contentAlignment = Alignment.Center) {
                                Text("🏅", fontSize = 16.sp)
                            }
                            Column {
                                Text("ISO / Quality Certified", fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                                Text("ISO 9001 or equivalent", fontSize = 11.sp, color = NoorTextHint)
                            }
                        }
                        Switch(
                            checked = isoRegistered,
                            onCheckedChange = {
                                isoRegistered = it
                                viewModel.updateIsoCertified(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor   = Color.White, checkedTrackColor   = VendorTeal,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(36.dp)
                                .clip(RoundedCornerShape(10.dp)).background(NoorBlueLight),
                                contentAlignment = Alignment.Center) {
                                Text("🤝", fontSize = 16.sp)
                            }
                            Column {
                                Text("Has Notable Clients", fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                                Text("Add known companies you've served", fontSize = 11.sp,
                                    color = NoorTextHint)
                            }
                        }
                        Switch(
                            checked = hasPreviousClients,
                            onCheckedChange = {
                                hasPreviousClients = it
                                if (!it) {
                                    previousClientsList.clear()
                                    viewModel.updateNotableClients(emptyList())
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor   = Color.White, checkedTrackColor   = VendorTeal,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder)
                        )
                    }

                    AnimatedVisibility(
                        visible = hasPreviousClients,
                        enter   = fadeIn() + expandVertically()
                    ) {
                        Column {
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                NoorTextField(
                                    value = currentClientInput,
                                    onValueChange = { currentClientInput = it },
                                    label = "Add Notable Client",
                                    placeholder = "e.g. DHA, Nishat, Packages Ltd…",
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        if (currentClientInput.isNotBlank() &&
                                            !previousClientsList.contains(currentClientInput.trim())) {
                                            previousClientsList.add(currentClientInput.trim())
                                            viewModel.updateNotableClients(previousClientsList.toList())
                                            currentClientInput = ""
                                        }
                                    },
                                    enabled = currentClientInput.isNotBlank(),
                                    shape  = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor         = VendorTeal,
                                        disabledContainerColor = NoorBorder
                                    ),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add",
                                        modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            if (previousClientsList.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))
                                Text("Notable Clients (${previousClientsList.size})",
                                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                    color = NoorTextPrimary)
                                Spacer(Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    previousClientsList.forEach { client ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape    = RoundedCornerShape(10.dp),
                                            colors   = CardDefaults.cardColors(containerColor = VendorTealLight)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment     = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Text("🏢", fontSize = 16.sp)
                                                    Text(client, fontSize = 13.sp,
                                                        color = NoorTextPrimary,
                                                        fontWeight = FontWeight.Medium)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        previousClientsList.remove(client)
                                                        viewModel.updateNotableClients(previousClientsList.toList())
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = "Remove",
                                                        tint = NoorRed, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Error banner
                uiState.error?.let { errorMsg ->
                    Text("⚠️ $errorMsg", color = NoorRed, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth())
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VendorTealLight)
                        .padding(14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 16.sp)
                        Text(
                            "Your profile will be reviewed by the Noor Services admin team. " +
                                    "Verified vendors are shown to matching employers.",
                            fontSize = 11.sp, color = VendorTealDark, lineHeight = 16.sp
                        )
                    }
                }

                // ✅ FIX: Call viewModel.saveVendorProfile() instead of onRegistered() directly.
                VendorPrimaryButton(
                    text    = if (uiState.isLoading) "Submitting…" else "Submit Registration  ✓",
                    enabled = step2Valid && !uiState.isLoading,
                    onClick = { viewModel.saveVendorProfile() }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper — syncs local per-service UI state into the ViewModel
// Called whenever the user changes any field inside a service detail card.
// ─────────────────────────────────────────────────────────────────────────────

private fun syncServiceDetail(
    id: String,
    pricingModelMap:  Map<String, String>,
    priceRangeMap:    Map<String, String>,
    minContractMap:   Map<String, String>,
    coverageAreasMap: Map<String, SnapshotStateList<String>>,
    viewModel: VendorRegistrationViewModel
) {
    viewModel.updateServiceDetail(
        id,
        VendorServiceInput(
            pricingModel        = pricingModelMap[id] ?: "",
            priceRange          = priceRangeMap[id] ?: "",
            minContractDuration = minContractMap[id] ?: "",
            coverageAreas       = coverageAreasMap[id]?.toList() ?: emptyList()
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorSectionLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        color = VendorTeal, letterSpacing = 0.3.sp)
}

@Composable
fun VendorPrimaryButton(
    text: String, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true
) {
    Button(
        onClick   = onClick, enabled = enabled,
        modifier  = modifier.fillMaxWidth().height(54.dp),
        shape     = RoundedCornerShape(14.dp),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = VendorTeal,
            disabledContainerColor = NoorBorder
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
            color = Color.White, letterSpacing = 0.3.sp)
    }
}