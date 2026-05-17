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

data class VendorServiceCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val description: String,
    val skills: List<String> = emptyList()
)

private val serviceScaleOptions   = listOf("1–10 staff", "11–50 staff", "51–200 staff", "200+ staff")
private val cityOptions           = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")
internal val pricingModelOptions  = listOf("Monthly Contract", "Per Shift", "Project-Based", "Hourly Rate", "Custom Quote")
internal val contractDurationOpts = listOf("< 1 Month", "1 Month", "3 Months", "6 Months", "1 Year", "Flexible")
internal val coverageAreaOptions  = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Faisalabad", "Multan", "Peshawar", "Other")

@Composable
fun VendorRegistrationScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: VendorRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is VendorRegistrationEvent.Success -> onRegistered()
                is VendorRegistrationEvent.Error   -> { }
            }
        }
    }

    val selectedServiceIds   = remember { mutableStateListOf<String>() }
    val servicePricingModel  = remember { mutableStateMapOf<String, String>() }
    val servicePriceRange    = remember { mutableStateMapOf<String, String>() }
    val serviceMinContract   = remember { mutableStateMapOf<String, String>() }
    val serviceDescription   = remember { mutableStateMapOf<String, String>() }
    val serviceCoverageAreas = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    val serviceHighlights    = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    val serviceSkills        = remember { mutableStateMapOf<String, SnapshotStateList<String>>() }
    val expandedServiceIds   = remember { mutableStateListOf<String>() }
    var highlightInput       = remember { mutableStateMapOf<String, String>() }

    val selectedCities       = remember { mutableStateListOf<String>() }
    var isoRegistered        by remember { mutableStateOf(false) }
    var hasPreviousClients   by remember { mutableStateOf(false) }
    val previousClientsList  = remember { mutableStateListOf<String>() }
    var currentClientInput   by remember { mutableStateOf("") }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.setLogoUri(uri) }

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    val vendorCategories = remember(uiState.categories) {
        if (uiState.categories.isNotEmpty()) {
            uiState.categories.map { cat ->
                VendorServiceCategory(
                    id = cat.id, label = cat.label, emoji = cat.emoji,
                    description = cat.label, skills = cat.skills.map { it.name }
                )
            }
        } else emptyList()
    }

    val step1Valid = uiState.businessName.isNotBlank() && uiState.contactPerson.isNotBlank() &&
            uiState.phone.isNotBlank() && uiState.email.isNotBlank() &&
            uiState.city.isNotBlank() && uiState.address.isNotBlank() &&
            uiState.headOffice.isNotBlank()

    val step2Valid = viewModel.isStep2Valid()

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { if (uiState.currentStep == 1) onBack() else viewModel.goBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(2) { index ->
                        val stepNum = index + 1
                        val color = when {
                            stepNum < uiState.currentStep  -> VendorAccent
                            stepNum == uiState.currentStep -> Color.White
                            else                           -> Color.White.copy(alpha = 0.28f)
                        }
                        Box(modifier = Modifier.weight(1f).height(4.dp)
                            .clip(RoundedCornerShape(2.dp)).background(color))
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.logoUri != null) {
                            AsyncImage(model = uiState.logoUri, contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
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
                            if (uiState.currentStep == 1) "Step 1 of 2 — required fields marked *"
                            else "Step 2 of 2 — configure each service",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.currentStep == 1) {

                // Logo (optional)
                NoorSectionCard {
                    VendorSectionLabel("Company Logo (Optional)")
                    Spacer(Modifier.height(4.dp))
                    Text("You can add or update your logo later from your profile.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp))
                                .background(VendorTealLight)
                                .border(2.dp, if (uiState.logoUri != null) VendorTeal else NoorBorder, RoundedCornerShape(16.dp))
                                .clickable { logoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.logoUri != null) {
                                AsyncImage(model = uiState.logoUri, contentDescription = "Logo Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)))
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null,
                                        tint = VendorTeal, modifier = Modifier.size(24.dp))
                                    Text("Upload", fontSize = 10.sp, color = VendorTeal,
                                        fontWeight = FontWeight.SemiBold)
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
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                    .background(VendorTealLight)
                                    .clickable { logoPickerLauncher.launch("image/*") }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text(if (uiState.logoUri != null) "Change" else "Choose File",
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = VendorTeal)
                                }
                                if (uiState.logoUri != null) {
                                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                        .background(NoorRedLight)
                                        .clickable { viewModel.setLogoUri(null) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)) {
                                        Text("Remove", fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold, color = NoorRed)
                                    }
                                }
                            }
                        }
                    }
                }

                // Business Identity — all required
                NoorSectionCard {
                    VendorSectionLabel("Business Identity")
                    Spacer(Modifier.height(14.dp))
                    NoorTextField(value = uiState.businessName,
                        onValueChange = { viewModel.updateBusinessName(it) },
                        label = "Business / Company Name *", placeholder = "e.g. Al-Noor Facility Services")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.contactPerson,
                        onValueChange = { viewModel.updateContactPerson(it) },
                        label = "Contact Person *", placeholder = "Full name of primary contact")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.phone, onValueChange = { viewModel.updatePhone(it) },
                        label = "Phone Number *", placeholder = "03XX-XXXXXXX",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.email, onValueChange = { viewModel.updateEmail(it) },
                        label = "Business Email *", placeholder = "info@company.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }

                // Location — all required
                NoorSectionCard {
                    VendorSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.headOffice,
                        onValueChange = { viewModel.updateHeadOffice(it) },
                        label = "Head Office Name *", placeholder = "e.g. Al-Noor Head Office, DHA")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.city, onValueChange = { viewModel.updateCity(it) },
                        label = "Head Office City *", placeholder = "e.g. Lahore")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.address, onValueChange = { viewModel.updateAddress(it) },
                        label = "Full Address *", placeholder = "Street, Area, City",
                        singleLine = false, maxLines = 3)
                    Spacer(Modifier.height(14.dp))
                    Text("Cities You Operate In (optional)", fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextHint, letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(10.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        cityOptions.forEach { c ->
                            NoorSelectableChip(label = c, icon = "📍",
                                selected = selectedCities.contains(c),
                                onClick = {
                                    if (selectedCities.contains(c)) selectedCities.remove(c)
                                    else selectedCities.add(c)
                                    viewModel.updateOperatingCities(selectedCities.toList())
                                })
                        }
                    }
                }

                // Legal — optional
                NoorSectionCard {
                    VendorSectionLabel("Legal & Registration (Optional)")
                    Spacer(Modifier.height(4.dp))
                    Text("Increases trust with employers but not required.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NoorTextField(value = uiState.ntn, onValueChange = { viewModel.updateNtn(it) },
                            label = "NTN Number", placeholder = "XXXXXXX-X",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        NoorTextField(value = uiState.regNumber,
                            onValueChange = { viewModel.updateRegNumber(it) },
                            label = "Company Reg No.", placeholder = "SECP / FBR",
                            modifier = Modifier.weight(1f))
                    }
                }

                // Bio — optional
                NoorSectionCard {
                    VendorSectionLabel("About Your Business (Optional)")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(value = uiState.bio, onValueChange = { viewModel.updateBio(it) },
                        label = "Business Description",
                        placeholder = "What makes your business stand out?",
                        singleLine = false, maxLines = 5)
                    Text("${uiState.bio.length}/300", fontSize = 10.sp,
                        color = if (uiState.bio.length > 280) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                }

                // Validation hint
                if (!step1Valid) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(NoorOrangeLight).padding(12.dp)) {
                        Text("⚠️ Please fill all required (*) fields to continue.",
                            fontSize = 12.sp, color = NoorOrange)
                    }
                }

                VendorPrimaryButton(text = "Continue  →", enabled = step1Valid,
                    onClick = { viewModel.goToStep2() })

            } else {
                // ── STEP 2 ────────────────────────────────────────────────────

                NoorSectionCard {
                    VendorSectionLabel("Services You Offer *")
                    Spacer(Modifier.height(6.dp))
                    Text("Select each service. Fill all required fields in each service card.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        vendorCategories.forEach { svc ->
                            NoorSelectableChip(label = svc.label, icon = svc.emoji,
                                selected = selectedServiceIds.contains(svc.id),
                                onClick = {
                                    if (selectedServiceIds.contains(svc.id)) {
                                        selectedServiceIds.remove(svc.id)
                                        expandedServiceIds.remove(svc.id)
                                        servicePricingModel.remove(svc.id)
                                        servicePriceRange.remove(svc.id)
                                        serviceMinContract.remove(svc.id)
                                        serviceDescription.remove(svc.id)
                                        serviceCoverageAreas.remove(svc.id)
                                        serviceHighlights.remove(svc.id)
                                    } else {
                                        selectedServiceIds.add(svc.id)
                                        expandedServiceIds.add(svc.id)
                                        serviceCoverageAreas[svc.id] = mutableStateListOf()
                                        serviceHighlights[svc.id] = mutableStateListOf()
                                    }
                                    viewModel.updateSelectedServices(selectedServiceIds.toList())
                                })
                        }
                    }
                }

                AnimatedVisibility(visible = selectedServiceIds.isNotEmpty(),
                    enter = fadeIn() + expandVertically()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        selectedServiceIds.forEach { id ->
                            val svc = vendorCategories.find { it.id == id } ?: return@forEach
                            val isExpanded = expandedServiceIds.contains(id)
                            val detail = uiState.serviceDetails[id]
                            val isComplete = detail != null
                                    && detail.pricingModel.isNotBlank()
                                    && detail.priceRange.isNotBlank()
                                    && detail.minContractDuration.isNotBlank()
                                    && detail.description.isNotBlank()
                                    && detail.coverageAreas.isNotEmpty()

                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            if (isExpanded) expandedServiceIds.remove(id)
                                            else expandedServiceIds.add(id)
                                        },
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.size(40.dp)
                                            .clip(RoundedCornerShape(10.dp)).background(VendorTealLight),
                                            contentAlignment = Alignment.Center) {
                                            Text(svc.emoji, fontSize = 18.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(svc.label, fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                            Text(
                                                if (isComplete) "✅ All required fields filled"
                                                else "⚠️ Required fields missing",
                                                fontSize = 11.sp,
                                                color = if (isComplete) VendorTeal else NoorOrange
                                            )
                                        }
                                        Icon(
                                            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null, tint = NoorTextHint,
                                            modifier = Modifier.size(20.dp))
                                    }

                                    AnimatedVisibility(visible = isExpanded,
                                        enter = fadeIn() + expandVertically()) {
                                        Column {
                                            Spacer(Modifier.height(14.dp))
                                            HorizontalDivider(color = NoorDivider, thickness = 0.7.dp)
                                            Spacer(Modifier.height(14.dp))

                                            // Description — required
                                            NoorTextField(
                                                value = serviceDescription[id] ?: "",
                                                onValueChange = {
                                                    serviceDescription[id] = it
                                                    syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                },
                                                label = "Service Description *",
                                                placeholder = "Describe what you offer for this service…",
                                                singleLine = false, maxLines = 4
                                            )

                                            Spacer(Modifier.height(14.dp))

                                            // Pricing Model — required
                                            Text("Pricing Model *", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                pricingModelOptions.forEach { opt ->
                                                    NoorSelectableChip(label = opt, icon = "💰",
                                                        selected = servicePricingModel[id] == opt,
                                                        onClick = {
                                                            servicePricingModel[id] = opt
                                                            syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                        })
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Price Range — required
                                            NoorTextField(value = servicePriceRange[id] ?: "",
                                                onValueChange = {
                                                    servicePriceRange[id] = it
                                                    syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                },
                                                label = "Price Range *",
                                                placeholder = "e.g. PKR 15,000 – 80,000 / month")

                                            Spacer(Modifier.height(14.dp))

                                            // Min Contract — required
                                            Text("Minimum Contract Duration *", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                contractDurationOpts.forEach { opt ->
                                                    NoorSelectableChip(label = opt, icon = "📅",
                                                        selected = serviceMinContract[id] == opt,
                                                        onClick = {
                                                            serviceMinContract[id] = opt
                                                            syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                        })
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Coverage Areas — required (at least one)
                                            Text("Coverage Areas * (select at least one)", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(8.dp))
                                            @OptIn(ExperimentalLayoutApi::class)
                                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                coverageAreaOptions.forEach { area ->
                                                    val areas = serviceCoverageAreas[id] ?: mutableStateListOf()
                                                    NoorSelectableChip(label = area, icon = "📍",
                                                        selected = areas.contains(area),
                                                        onClick = {
                                                            val current = serviceCoverageAreas.getOrPut(id) { mutableStateListOf() }
                                                            if (current.contains(area)) current.remove(area)
                                                            else current.add(area)
                                                            syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                        })
                                                }
                                            }

                                            Spacer(Modifier.height(14.dp))

                                            // Highlights — optional
                                            Text("Highlights (Optional)", fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NoorTextHint, letterSpacing = 0.4.sp)
                                            Spacer(Modifier.height(4.dp))
                                            Text("e.g. 24/7 availability, trained staff, GPS tracking",
                                                fontSize = 10.sp, color = NoorTextHint)
                                            Spacer(Modifier.height(8.dp))
                                            Row(modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.Bottom) {
                                                NoorTextField(
                                                    value = highlightInput[id] ?: "",
                                                    onValueChange = { highlightInput[id] = it },
                                                    label = "Add Highlight",
                                                    placeholder = "e.g. 24/7 Support",
                                                    modifier = Modifier.weight(1f), singleLine = true)
                                                Button(
                                                    onClick = {
                                                        val txt = (highlightInput[id] ?: "").trim()
                                                        if (txt.isNotBlank()) {
                                                            val list = serviceHighlights.getOrPut(id) { mutableStateListOf() }
                                                            if (!list.contains(txt)) list.add(txt)
                                                            highlightInput[id] = ""
                                                            syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                        }
                                                    },
                                                    enabled = (highlightInput[id] ?: "").isNotBlank(),
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = VendorTeal,
                                                        disabledContainerColor = NoorBorder),
                                                    modifier = Modifier.height(56.dp)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = "Add",
                                                        modifier = Modifier.size(18.dp))
                                                }
                                            }
                                            val highlights = serviceHighlights[id]
                                            if (!highlights.isNullOrEmpty()) {
                                                Spacer(Modifier.height(8.dp))
                                                @OptIn(ExperimentalLayoutApi::class)
                                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    highlights.forEach { h ->
                                                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                                            .background(VendorTealLight)
                                                            .padding(horizontal = 10.dp, vertical = 5.dp)) {
                                                            Row(verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                Text(h, fontSize = 11.sp, color = VendorTeal,
                                                                    fontWeight = FontWeight.Medium)
                                                                Icon(Icons.Default.Close,
                                                                    contentDescription = "Remove",
                                                                    tint = VendorTeal,
                                                                    modifier = Modifier.size(12.dp).clickable {
                                                                        highlights.remove(h)
                                                                        syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                                    })
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            // Skills — optional
                                            if (svc.skills.isNotEmpty()) {
                                                Spacer(Modifier.height(14.dp))
                                                Text("Skills Offered (Optional)", fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = NoorTextHint, letterSpacing = 0.4.sp)
                                                Spacer(Modifier.height(8.dp))
                                                @OptIn(ExperimentalLayoutApi::class)
                                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    svc.skills.forEach { skill ->
                                                        val selectedSkills = serviceSkills.getOrPut(id) { mutableStateListOf() }
                                                        NoorSelectableChip(label = skill, icon = "⚡",
                                                            selected = selectedSkills.contains(skill),
                                                            onClick = {
                                                                val skills = serviceSkills.getOrPut(id) { mutableStateListOf() }
                                                                if (skills.contains(skill)) skills.remove(skill)
                                                                else skills.add(skill)
                                                                syncAll(id, servicePricingModel, servicePriceRange, serviceMinContract, serviceDescription, serviceCoverageAreas, serviceHighlights, serviceSkills, viewModel)
                                                            })
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Workforce Scale — required
                NoorSectionCard {
                    VendorSectionLabel("Business Capacity")
                    Spacer(Modifier.height(4.dp))
                    Text("Workforce scale is required. Years in business is optional.",
                        fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                    Spacer(Modifier.height(14.dp))
                    Text("Workforce Scale *", fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextHint, letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(10.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        serviceScaleOptions.forEach { opt ->
                            NoorSelectableChip(label = opt, icon = "👥",
                                selected = uiState.serviceScale == opt,
                                onClick = { viewModel.updateServiceScale(opt) })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    NoorTextField(value = uiState.yearsInBusiness,
                        onValueChange = { viewModel.updateYearsInBusiness(it) },
                        label = "Years in Business (Optional)", placeholder = "e.g. 8",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }

                // Credentials — optional
                NoorSectionCard {
                    VendorSectionLabel("Credentials & Trust (Optional)")
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(VendorTealLight), contentAlignment = Alignment.Center) {
                                Text("🏅", fontSize = 16.sp)
                            }
                            Column {
                                Text("ISO / Quality Certified", fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                                Text("ISO 9001 or equivalent", fontSize = 11.sp, color = NoorTextHint)
                            }
                        }
                        Switch(checked = isoRegistered,
                            onCheckedChange = { isoRegistered = it; viewModel.updateIsoCertified(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White, checkedTrackColor = VendorTeal,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder))
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                .background(NoorBlueLight), contentAlignment = Alignment.Center) {
                                Text("🤝", fontSize = 16.sp)
                            }
                            Column {
                                Text("Has Notable Clients", fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                                Text("Add known companies you've served", fontSize = 11.sp, color = NoorTextHint)
                            }
                        }
                        Switch(checked = hasPreviousClients,
                            onCheckedChange = {
                                hasPreviousClients = it
                                if (!it) { previousClientsList.clear(); viewModel.updateNotableClients(emptyList()) }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White, checkedTrackColor = VendorTeal,
                                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder))
                    }

                    AnimatedVisibility(visible = hasPreviousClients, enter = fadeIn() + expandVertically()) {
                        Column {
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom) {
                                NoorTextField(value = currentClientInput,
                                    onValueChange = { currentClientInput = it },
                                    label = "Add Notable Client",
                                    placeholder = "e.g. DHA, Nishat, Packages Ltd…",
                                    modifier = Modifier.weight(1f), singleLine = true)
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
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VendorTeal,
                                        disabledContainerColor = NoorBorder),
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
                                Text("Notable Clients (${previousClientsList.size})", fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                                Spacer(Modifier.height(8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    previousClientsList.forEach { client ->
                                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = VendorTealLight)) {
                                            Row(modifier = Modifier.fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically) {
                                                Row(verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                    Text("🏢", fontSize = 16.sp)
                                                    Text(client, fontSize = 13.sp, color = NoorTextPrimary,
                                                        fontWeight = FontWeight.Medium)
                                                }
                                                IconButton(onClick = {
                                                    previousClientsList.remove(client)
                                                    viewModel.updateNotableClients(previousClientsList.toList())
                                                }, modifier = Modifier.size(32.dp)) {
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

                uiState.error?.let { errorMsg ->
                    Text("⚠️ $errorMsg", color = NoorRed, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth())
                }

                if (!step2Valid) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(NoorOrangeLight).padding(12.dp)) {
                        Text("⚠️ Each service needs description, pricing model, price range, minimum contract duration, and at least one coverage area.",
                            fontSize = 12.sp, color = NoorOrange, lineHeight = 17.sp)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(VendorTealLight).padding(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 16.sp)
                        Text("Your profile will be reviewed by the Noor Services admin team. Verified vendors are shown to matching employers.",
                            fontSize = 11.sp, color = VendorTealDark, lineHeight = 16.sp)
                    }
                }

                VendorPrimaryButton(
                    text = if (uiState.isLoading) "Submitting…" else "Submit Registration  ✓",
                    enabled = step2Valid && !uiState.isLoading,
                    onClick = { viewModel.saveVendorProfile() }
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sync helper — now includes description and highlights
// ─────────────────────────────────────────────────────────────────────────────

private fun syncAll(
    id: String,
    pricingModelMap:  Map<String, String>,
    priceRangeMap:    Map<String, String>,
    minContractMap:   Map<String, String>,
    descriptionMap:   Map<String, String>,
    coverageAreasMap: Map<String, SnapshotStateList<String>>,
    highlightsMap:    Map<String, SnapshotStateList<String>>,
    skillsMap:        Map<String, SnapshotStateList<String>>,
    viewModel: VendorRegistrationViewModel
) {
    viewModel.updateServiceDetail(
        id,
        VendorServiceInput(
            pricingModel        = pricingModelMap[id] ?: "",
            priceRange          = priceRangeMap[id] ?: "",
            minContractDuration = minContractMap[id] ?: "",
            description         = descriptionMap[id] ?: "",
            coverageAreas       = coverageAreasMap[id]?.toList() ?: emptyList(),
            highlights          = highlightsMap[id]?.toList() ?: emptyList(),
            skills              = skillsMap[id]?.toList() ?: emptyList()
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable composables
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
        onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VendorTeal, disabledContainerColor = NoorBorder),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
            color = Color.White, letterSpacing = 0.3.sp)
    }
}