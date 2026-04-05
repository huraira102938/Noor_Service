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
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*

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
    VendorServiceCategory("staffing",    "Staffing Solutions",     "👥", "Bulk workforce supply & temp staffing"),
    VendorServiceCategory("security",    "Security Services",      "🛡️", "Trained guards, CCTV, access control"),
    VendorServiceCategory("cleaning",    "Cleaning & Janitorial",  "🧹", "Office, industrial & residential cleaning"),
    VendorServiceCategory("catering",    "Catering & Mess",        "🍽️", "Corporate meal plans & canteen management"),
    VendorServiceCategory("maintenance", "Facility Maintenance",   "🔧", "Plumbing, electrical, HVAC & civil works"),
    VendorServiceCategory("it_support",  "IT Support",             "💻", "Network, hardware & helpdesk services"),
    VendorServiceCategory("transport",   "Transport & Logistics",  "🚌", "Staff transport & fleet management"),
    VendorServiceCategory("landscaping", "Landscaping",            "🌿", "Garden maintenance & outdoor spaces"),
    VendorServiceCategory("pest_control","Pest Control",           "🐛", "Residential & commercial pest management"),
    VendorServiceCategory("training",    "Training & Development", "📚", "Workforce training & skill development"),
)

private val serviceScaleOptions = listOf("1–10 staff", "11–50 staff", "51–200 staff", "200+ staff")
private val cityOptions = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")

// Shared with catalog screen
internal val pricingModelOptions  = listOf("Monthly Contract", "Per Shift", "Project-Based", "Hourly Rate", "Custom Quote")
internal val contractDurationOpts = listOf("< 1 Month", "1 Month", "3 Months", "6 Months", "1 Year", "Flexible")
internal val coverageAreaOptions  = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Registration Screen  (2-step wizard)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorRegistrationScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    // ── Step 1 fields ─────────────────────────────────────────────────────────
    var businessName    by remember { mutableStateOf("") }
    var contactPerson   by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var ntn             by remember { mutableStateOf("") }
    var regNumber       by remember { mutableStateOf("") }
    var city            by remember { mutableStateOf("") }
    var address         by remember { mutableStateOf("") }
    var bio             by remember { mutableStateOf("") }
    val selectedCities  = remember { mutableStateListOf<String>() }
    var logoUri         by remember { mutableStateOf<Uri?>(null) }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> logoUri = uri }

    // ── Step 2 fields ─────────────────────────────────────────────────────────
    val selectedServiceIds   = remember { mutableStateListOf<String>() }
    // Per-service detail maps (keyed by serviceId)
    val servicePricingModel  = remember { mutableStateMapOf<String, String>() }
    val servicePriceRange    = remember { mutableStateMapOf<String, String>() }
    val serviceMinContract   = remember { mutableStateMapOf<String, String>() }
    val serviceCoverageAreas = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    // Which service cards are expanded
    val expandedServiceIds   = remember { mutableStateListOf<String>() }

    var serviceScale         by remember { mutableStateOf("") }
    var yearsInBusiness      by remember { mutableStateOf("") }
    var isoRegistered        by remember { mutableStateOf(false) }
    var hasPreviousClients   by remember { mutableStateOf(false) }
    val previousClientsList  = remember { mutableStateListOf<String>() }
    var currentClientInput   by remember { mutableStateOf("") }

    val step1Valid = businessName.isNotBlank() && contactPerson.isNotBlank() &&
            phone.isNotBlank() && city.isNotBlank()
    val step2Valid = selectedServiceIds.isNotEmpty() && serviceScale.isNotBlank()

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
                        .clickable { if (currentStep == 1) onBack() else currentStep = 1 },
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
                            stepNum < currentStep  -> VendorAccent
                            stepNum == currentStep -> Color.White
                            else                   -> Color.White.copy(alpha = 0.28f)
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
                    // Show logo preview in header if uploaded, else placeholder
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (logoUri != null) {
                            AsyncImage(
                                model = logoUri,
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
                            if (currentStep == 1) "Business Information" else "Services & Capacity",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.3).sp
                        )
                        Text(
                            if (currentStep == 1) "Tell us about your business"
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
            if (currentStep == 1) {
                // ── STEP 1: Business Info ─────────────────────────────────────

                // ── Logo / Company Photo ──────────────────────────────────────
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
                        // Preview box
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VendorTealLight)
                                .border(
                                    width = 2.dp,
                                    color = if (logoUri != null) VendorTeal else NoorBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { logoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoUri != null) {
                                AsyncImage(
                                    model = logoUri,
                                    contentDescription = "Logo Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = VendorTeal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text("Upload", fontSize = 10.sp,
                                        color = VendorTeal, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                if (logoUri != null) "✅ Logo selected" else "No logo selected",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (logoUri != null) VendorTeal else NoorTextHint
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
                                        if (logoUri != null) "Change Photo" else "Choose File",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = VendorTeal
                                    )
                                }
                                if (logoUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NoorRedLight)
                                            .clickable { logoUri = null }
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
                    NoorTextField(value = businessName, onValueChange = { businessName = it },
                        label = "Business / Company Name *",
                        placeholder = "e.g. Al-Noor Facility Services")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = contactPerson, onValueChange = { contactPerson = it },
                        label = "Contact Person *",
                        placeholder = "Full name of primary contact")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = phone, onValueChange = { phone = it },
                        label = "Phone Number *", placeholder = "03XX-XXXXXXX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = email, onValueChange = { email = it },
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
                        NoorTextField(value = ntn, onValueChange = { ntn = it },
                            label = "NTN Number", placeholder = "XXXXXXX-X",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        NoorTextField(value = regNumber, onValueChange = { regNumber = it },
                            label = "Company Reg No.", placeholder = "SECP / FBR",
                            modifier = Modifier.weight(1f))
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = city, onValueChange = { city = it },
                        label = "Head Office City *", placeholder = "e.g. Lahore")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = address, onValueChange = { address = it },
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
                                })
                        }
                    }
                }

                NoorSectionCard {
                    VendorSectionLabel("About Your Business")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value = bio,
                        onValueChange = { if (it.length <= 300) bio = it },
                        label = "Business Description (optional)",
                        placeholder = "What makes your business stand out? Mention experience, certifications, notable clients…",
                        singleLine = false, maxLines = 5)
                    Text("${bio.length}/300",
                        fontSize = 10.sp,
                        color    = if (bio.length > 280) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                }

                VendorPrimaryButton(
                    text    = "Continue  →",
                    enabled = step1Valid,
                    onClick = { currentStep = 2 }
                )

            } else {
                // ── STEP 2: Services & Capacity ───────────────────────────────

                // ── Service category chips ────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Services You Offer *")
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Select each service you provide. A detail card will appear below to configure pricing and coverage.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)
                    ) {
                        allVendorServiceCategories.forEach { svc ->
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
                                        expandedServiceIds.add(svc.id) // auto-expand on select
                                        serviceCoverageAreas[svc.id] = mutableStateListOf()
                                    }
                                }
                            )
                        }
                    }
                }

                // ── Per-service detail cards ───────────────────────────────────
                AnimatedVisibility(
                    visible = selectedServiceIds.isNotEmpty(),
                    enter   = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        selectedServiceIds.forEach { id ->
                            val svc = allVendorServiceCategories.find { it.id == id } ?: return@forEach
                            val isExpanded = expandedServiceIds.contains(id)

                            Card(
                                modifier  = Modifier.fillMaxWidth(),
                                shape     = RoundedCornerShape(16.dp),
                                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Card header row — tap to expand/collapse
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
                                            val priceFilled  = servicePriceRange[id]?.isNotBlank() == true
                                            val modelFilled  = servicePricingModel[id]?.isNotBlank() == true
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

                                    // Expanded detail form
                                    AnimatedVisibility(
                                        visible = isExpanded,
                                        enter   = fadeIn() + expandVertically()
                                    ) {
                                        Column {
                                            Spacer(Modifier.height(14.dp))
                                            HorizontalDivider(color = NoorDivider, thickness = 0.7.dp)
                                            Spacer(Modifier.height(14.dp))

                                            // ── Pricing Model ─────────────────
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
                                                        label    = opt,
                                                        icon     = "💰",
                                                        selected = servicePricingModel[id] == opt,
                                                        onClick  = { servicePricingModel[id] = opt }
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // ── Price Range ───────────────────
                                            NoorTextField(
                                                value = servicePriceRange[id] ?: "",
                                                onValueChange = { servicePriceRange[id] = it },
                                                label       = "Price Range *",
                                                placeholder = "e.g. PKR 15,000 – 80,000 / month"
                                            )

                                            Spacer(Modifier.height(14.dp))

                                            // ── Minimum Contract Duration ─────
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
                                                        label    = opt,
                                                        icon     = "📅",
                                                        selected = serviceMinContract[id] == opt,
                                                        onClick  = { serviceMinContract[id] = opt }
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // ── Coverage Areas ────────────────
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
                                                        label    = area,
                                                        icon     = "📍",
                                                        selected = areas.contains(area),
                                                        onClick  = {
                                                            val current = serviceCoverageAreas.getOrPut(id) { mutableStateListOf() }
                                                            if (current.contains(area)) current.remove(area)
                                                            else current.add(area)
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
                                selected = serviceScale == opt,
                                onClick  = { serviceScale = opt })
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    NoorTextField(value = yearsInBusiness, onValueChange = { yearsInBusiness = it },
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
                            onCheckedChange = { isoRegistered = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor   = Color.White, checkedTrackColor   = VendorTeal,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder)
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(12.dp))

                    // Updated Notable Clients Section with Add Button
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
                                if (!it) previousClientsList.clear()
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

                            // Input row for adding new client
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
                                        if (currentClientInput.isNotBlank() && !previousClientsList.contains(currentClientInput.trim())) {
                                            previousClientsList.add(currentClientInput.trim())
                                            currentClientInput = ""
                                        }
                                    },
                                    enabled = currentClientInput.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VendorTeal,
                                        disabledContainerColor = NoorBorder
                                    ),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            // Display list of added clients
                            if (previousClientsList.isNotEmpty()) {
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    "Notable Clients (${previousClientsList.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NoorTextPrimary
                                )

                                Spacer(Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    previousClientsList.forEach { client ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = VendorTealLight)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    Text("🏢", fontSize = 16.sp)
                                                    Text(
                                                        client,
                                                        fontSize = 13.sp,
                                                        color = NoorTextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }

                                                IconButton(
                                                    onClick = { previousClientsList.remove(client) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Remove",
                                                        tint = NoorRed,
                                                        modifier = Modifier.size(18.dp)
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

                VendorPrimaryButton(
                    text    = "Submit Registration  ✓",
                    enabled = step2Valid,
                    onClick = onRegistered
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
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