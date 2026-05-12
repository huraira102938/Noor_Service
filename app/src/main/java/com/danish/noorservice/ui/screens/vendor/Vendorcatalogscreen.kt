package com.danish.noorservice.ui.screens.vendor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.components.ShimmerBox
import com.danish.noorservice.ui.components.rememberShimmerBrush
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.vendor.VendorCatalogViewModel

data class VendorServiceListing(
    val id: String,
    val categoryId: String,
    val categoryLabel: String,
    val emoji: String,
    var isActive: Boolean,
    var description: String,
    var minContractDuration: String,
    var pricingModel: String,
    var priceRange: String,
    var coverageAreas: List<String>,
    var highlights: List<String>
)

val sampleListings = mutableListOf(
    VendorServiceListing("1", "cleaning",  "Cleaning & Janitorial",  "🧹", true,  "Full-scale office and industrial cleaning.", "1 Month",    "Monthly Contract", "PKR 15,000 – 80,000 / month", listOf("Lahore", "Islamabad"),       listOf("Eco-friendly products", "Trained & uniformed staff")),
    VendorServiceListing("2", "staffing",  "Staffing Solutions",       "👥", true,  "Bulk workforce supply for all requirements.", "3 Months",   "Monthly Contract", "PKR 25,000 – 500,000 / month", listOf("Lahore", "Faisalabad"),        listOf("Background verified staff", "Payroll management")),
    VendorServiceListing("3", "security",  "Security Services",        "🛡️", false, "Guards & CCTV monitoring.",                 "6 Months",   "Per Shift",        "PKR 1,200 – 2,000 / shift",      listOf("Lahore"),                  listOf("Licensed guards", "CCTV monitoring"))
)

fun VendorService.toListing(emoji: String = "💼", label: String = ""): VendorServiceListing = VendorServiceListing(
    id = serviceId, categoryId = serviceId,
    categoryLabel = label.ifBlank { serviceId.replaceFirstChar { it.uppercaseChar() } },
    emoji = emoji, isActive = isActive, description = description,
    minContractDuration = minContractDuration, pricingModel = pricingModel,
    priceRange = priceRange, coverageAreas = coverageAreas, highlights = highlights
)

fun VendorServiceListing.toService(): VendorService = VendorService(
    serviceId         = categoryId,
    pricingModel      = pricingModel,
    priceRange        = priceRange,
    minContractDuration = minContractDuration,
    coverageAreas     = coverageAreas,
    isActive          = isActive,
    description       = description,
    highlights        = highlights
)

// ─────────────────────────────────────────────────────────────────────────────
// Catalog Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorCatalogScreen(
    userId: String,
    viewModel: VendorCatalogViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ Hoisted to composable scope — visible everywhere in this function
    var showAddSheet by remember { mutableStateOf(false) }
    var editingService by remember { mutableStateOf<VendorServiceListing?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ──────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Service Catalog", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = Color.White, letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Manage your B2B service listings", fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.72f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { viewModel.loadServices(userId) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ── Content ────────────────────────────────────────────────────────────
        val services = uiState.services

        when {
            uiState.isLoading -> {
                val brush = rememberShimmerBrush()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(brush)
                        )
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️", fontSize = 36.sp)
                        Text("Failed to load catalog", fontSize = 14.sp, color = NoorTextPrimary)
                        Button(
                            onClick = { viewModel.loadServices(userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = VendorTeal)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }

            services.isEmpty() -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📋", fontSize = 36.sp)
                        Text("No services added yet", fontSize = 14.sp, color = NoorTextHint)
                        Text(
                            "Pull to refresh or add a new service",
                            fontSize = 12.sp,
                            color = NoorTextHint
                        )
                    }
                }
            }

            else -> {
                // ── Stats summary ──────────────────────────────────────────────
                val activeCount = services.count { it.isActive }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CatalogStatChip(
                        value = "${services.size}",
                        label = "Total",
                        valueColor = NoorTextPrimary,
                        bgColor = NoorSurface,
                        modifier = Modifier.weight(1f)
                    )
                    CatalogStatChip(
                        value = "$activeCount",
                        label = "Active",
                        valueColor = VendorTeal,
                        bgColor = VendorTealLight,
                        modifier = Modifier.weight(1f)
                    )
                    CatalogStatChip(
                        value = "${services.size - activeCount}",
                        label = "Paused",
                        valueColor = NoorOrange,
                        bgColor = NoorOrangeLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Listings ───────────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    services.forEach { svc ->
                        val listing = svc.toListing()
                        ServiceListingCard(
                            listing = listing,
                            isLastListing = services.indexOf(svc) == services.lastIndex,
                            onToggle = {
                                viewModel.updateServiceActive(
                                    userId,
                                    svc.serviceId,
                                    !svc.isActive
                                )
                            },
                            onDelete = { viewModel.deleteService(userId, svc.serviceId) },
                            onEdit = { editingService = listing }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // ── Add Service Bottom Sheet ───────────────────────────────────────────────
    if (showAddSheet) {
        AddEditServiceSheet(
            existingListing = null,
            onDismiss = { showAddSheet = false },
            onSave = { listing ->
                viewModel.saveService(userId, listing.toService())
                showAddSheet = false
            }
        )
    }

    // ── Edit Service Bottom Sheet ──────────────────────────────────────────────
    val editSvc = editingService
    if (editSvc != null) {
        AddEditServiceSheet(
            existingListing = editSvc,
            onDismiss = { editingService = null },
            onSave = { listing ->
                viewModel.saveService(userId, listing.toService())
                editingService = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Service service card (expandable)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ServiceListingCard(
    listing: VendorServiceListing,
    isLastListing: Boolean,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row: emoji + info + toggle + expand arrow
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (listing.isActive) VendorTealLight else NoorBackground),
                    contentAlignment = Alignment.Center
                ) { Text(listing.emoji, fontSize = 22.sp) }

                Column(modifier = Modifier.weight(1f)) {
                    Text(listing.categoryLabel, fontSize = 14.sp,
                        fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                    Text(listing.priceRange, fontSize = 11.sp,
                        color = if (listing.isActive) VendorTeal else NoorTextHint,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (listing.isActive) VendorTealLight else NoorBackground)
                            .border(1.dp, if (listing.isActive) VendorTeal else NoorBorder,
                                RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            if (listing.isActive) "● Active" else "○ Paused",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            color = if (listing.isActive) VendorTeal else NoorTextHint
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked         = listing.isActive,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White, checkedTrackColor   = VendorTeal,
                            uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder)
                    )
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint     = NoorTextHint,
                        modifier = Modifier.size(20.dp).clickable { expanded = !expanded }
                    )
                }
            }

            // Expandable details
            AnimatedVisibility(
                visible = expanded,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
                    Spacer(Modifier.height(14.dp))

                    CatalogDetailLabel("Description")
                    Spacer(Modifier.height(6.dp))
                    Text(listing.description, fontSize = 12.sp,
                        color = NoorTextSecondary, lineHeight = 18.sp)

                    Spacer(Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            CatalogDetailLabel("Pricing Model")
                            Spacer(Modifier.height(6.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(VendorTealLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp)) {
                                Text(listing.pricingModel, fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold, color = VendorTeal)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            CatalogDetailLabel("Min. Contract")
                            Spacer(Modifier.height(6.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                .background(NoorBlueLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp)) {
                                Text(listing.minContractDuration, fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold, color = NoorBlue)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    CatalogDetailLabel("Coverage Areas")
                    Spacer(Modifier.height(8.dp))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        listing.coverageAreas.forEach { area ->
                            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(NoorBackground)
                                .padding(horizontal = 10.dp, vertical = 4.dp)) {
                                Text("📍 $area", fontSize = 11.sp, color = NoorTextSecondary)
                            }
                        }
                    }

                    if (listing.highlights.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        CatalogDetailLabel("Key Highlights")
                        Spacer(Modifier.height(8.dp))
                        listing.highlights.forEach { highlight ->
                            Row(modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape)
                                    .background(VendorTeal), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(9.dp))
                                }
                                Text(highlight, fontSize = 12.sp, color = NoorTextSecondary)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
                    Spacer(Modifier.height(12.dp))

                    // Action buttons row: Edit + Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                    ) {
                        // Edit button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(VendorTealLight)
                                .clickable { onEdit() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = null,
                                    tint = VendorTeal, modifier = Modifier.size(14.dp))
                                Text("Edit", fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, color = VendorTeal)
                            }
                        }

                        // Delete button — disabled when this is the only listing
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isLastListing) NoorBackground else NoorRedLight)
                                .then(
                                    if (!isLastListing) Modifier.clickable { showDeleteConfirm = true }
                                    else Modifier
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null,
                                    tint = if (isLastListing) NoorTextHint else NoorRed,
                                    modifier = Modifier.size(14.dp))
                                Text("Remove",
                                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (isLastListing) NoorTextHint else NoorRed)
                            }
                        }
                    }

                    // Hint when delete is disabled
                    if (isLastListing) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "At least one listing must remain in the catalog.",
                            fontSize = 10.sp, color = NoorTextHint,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Remove Listing", fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = NoorRed) },
            text  = { Text(
                "Remove '${listing.categoryLabel}' from your catalog? This cannot be undone.",
                fontSize = 13.sp, color = NoorTextSecondary) },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Remove", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Unified Add / Edit Service Sheet
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AddEditServiceSheet(
    existingListing: VendorServiceListing?,
    onDismiss: () -> Unit,
    onSave: (VendorServiceListing) -> Unit
) {
    val isEditing = existingListing != null

    var selectedCategoryId by remember { mutableStateOf(existingListing?.categoryId ?: "") }
    var description        by remember { mutableStateOf(existingListing?.description ?: "") }
    var pricingModel       by remember { mutableStateOf(existingListing?.pricingModel ?: "") }
    var priceRange         by remember { mutableStateOf(existingListing?.priceRange ?: "") }
    var minContract        by remember { mutableStateOf(existingListing?.minContractDuration ?: "") }
    val coverageAreas      = remember {
        mutableStateListOf<String>().also { list ->
            existingListing?.coverageAreas?.let { list.addAll(it) }
        }
    }
    var highlightsText by remember {
        mutableStateOf(existingListing?.highlights?.joinToString("\n") ?: "")
    }

    val isValid = selectedCategoryId.isNotBlank() && pricingModel.isNotBlank() &&
            priceRange.isNotBlank() && minContract.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = false) {}
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .align(Alignment.BottomCenter),
            shape     = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Sheet header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                if (isEditing) "Edit Service Listing" else "Add New Service",
                                fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                if (isEditing) "Update the details for this service"
                                else "Fill in the details for your new service",
                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.75f))
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close",
                                tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Category picker
                    NoorSectionCard {
                        VendorSectionLabel("Service Category *")
                        Spacer(Modifier.height(10.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement   = Arrangement.spacedBy(10.dp)) {
                            allVendorServiceCategories.forEach { svc ->
                                NoorSelectableChip(
                                    label    = svc.label,
                                    icon     = svc.emoji,
                                    selected = selectedCategoryId == svc.id,
                                    onClick  = { selectedCategoryId = svc.id })
                            }
                        }
                    }

                    // Pricing
                    NoorSectionCard {
                        VendorSectionLabel("Pricing")
                        Spacer(Modifier.height(12.dp))
                        Text("Pricing Model *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            color = NoorTextHint, letterSpacing = 0.4.sp)
                        Spacer(Modifier.height(8.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            pricingModelOptions.forEach { opt ->
                                NoorSelectableChip(label = opt, icon = "💰",
                                    selected = pricingModel == opt,
                                    onClick  = { pricingModel = opt })
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        NoorTextField(value = priceRange, onValueChange = { priceRange = it },
                            label = "Price Range *",
                            placeholder = "e.g. PKR 15,000 – 80,000 / month")
                    }

                    // Contract Duration
                    NoorSectionCard {
                        VendorSectionLabel("Contract Duration")
                        Spacer(Modifier.height(10.dp))
                        Text("Minimum Contract *", fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            color = NoorTextHint, letterSpacing = 0.4.sp)
                        Spacer(Modifier.height(8.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            contractDurationOpts.forEach { opt ->
                                NoorSelectableChip(label = opt, icon = "📅",
                                    selected = minContract == opt,
                                    onClick  = { minContract = opt })
                            }
                        }
                    }

                    // Coverage Areas
                    NoorSectionCard {
                        VendorSectionLabel("Coverage Areas")
                        Spacer(Modifier.height(10.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            coverageAreaOptions.forEach { area ->
                                NoorSelectableChip(label = area, icon = "📍",
                                    selected = coverageAreas.contains(area),
                                    onClick  = {
                                        if (coverageAreas.contains(area)) coverageAreas.remove(area)
                                        else coverageAreas.add(area)
                                    })
                            }
                        }
                    }

                    // Description
                    NoorSectionCard {
                        VendorSectionLabel("Description (optional)")
                        Spacer(Modifier.height(10.dp))
                        NoorTextField(
                            value = description, onValueChange = { description = it },
                            label = "Service Description",
                            placeholder = "What's included, any specialisations, certifications…",
                            singleLine = false, maxLines = 4)
                    }

                    // Key Highlights
                    NoorSectionCard {
                        VendorSectionLabel("Key Highlights (optional)")
                        Spacer(Modifier.height(4.dp))
                        Text("Enter one highlight per line. These appear as bullet points.",
                            fontSize = 11.sp, color = NoorTextHint, lineHeight = 16.sp)
                        Spacer(Modifier.height(10.dp))
                        NoorTextField(
                            value = highlightsText, onValueChange = { highlightsText = it },
                            label = "Highlights",
                            placeholder = "e.g.\nEco-friendly products\nTrained & uniformed staff\n24/7 availability",
                            singleLine = false, maxLines = 6)
                    }

                    VendorPrimaryButton(
                        text    = if (isEditing) "Save Changes  ✓" else "Add to Catalog  ✓",
                        enabled = isValid,
                        onClick = {
                            val svc = allVendorServiceCategories.find { it.id == selectedCategoryId }
                            val highlights = highlightsText
                                .lines()
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                            onSave(
                                VendorServiceListing(
                                    id = existingListing?.id ?: System.currentTimeMillis().toString(),
                                    categoryId = selectedCategoryId,
                                    categoryLabel = svc?.label ?: selectedCategoryId,
                                    emoji = svc?.emoji ?: "💼",
                                    isActive = existingListing != null,
                                    description = description,
                                    minContractDuration = minContract,
                                    pricingModel = pricingModel,
                                    priceRange = priceRange,
                                    coverageAreas = coverageAreas.toList(),
                                    highlights = highlights
                                )
                            )
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CatalogDetailLabel(text: String) {
    Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        color = NoorTextHint, letterSpacing = 0.6.sp)
}

@Composable
private fun CatalogStatChip(
    value: String, label: String,
    valueColor: Color, bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Text(label, fontSize = 10.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
    }
}