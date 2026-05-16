package com.danish.noorservice.ui.screens.employer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.ui.screens.vendor.VendorServiceListing
import com.danish.noorservice.ui.components.VendorBrowseShimmer
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employer.EmployerProposalViewModel
import com.danish.noorservice.viewmodel.employer.EmployerVendorBrowseViewModel
import java.text.SimpleDateFormat
import java.util.*

private data class PublicVendorProfileWithServices(
    val profile: PublicVendorProfile,
    val services: List<VendorService>
)

private fun Vendor.toPublicVendorProfileWithServices(
    servicesOverride: List<VendorService>? = null,
    categories: List<Category> = emptyList()
): PublicVendorProfileWithServices {
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val joined = dateFormat.format(java.util.Date(createdAt))
    val vendorServices = servicesOverride ?: emptyList()
    val emojis = listOf("🏢", "🛡️", "🧹", "🔧", "🚗", "🌿", "👔", "🧑‍🍳")
    val emojiIndex = uid.hashCode().let { kotlin.math.abs(it) } % emojis.size

    val listings = vendorServices.mapNotNull { service ->
        val category = categories.find { it.id == service.serviceId }
        if (category != null) {
            VendorServiceListing(
                id = service.serviceId,
                categoryId = service.serviceId,
                categoryLabel = category.label,
                emoji = category.emoji,
                isActive = service.isActive,
                description = service.description,
                minContractDuration = service.minContractDuration,
                pricingModel = service.pricingModel,
                priceRange = service.priceRange,
                coverageAreas = service.coverageAreas,
                highlights = service.highlights,
                skills = service.skills
            )
        } else null
    }

    val profile = PublicVendorProfile(
        id = uid,
        businessName = businessName,
        emoji = emojis[emojiIndex],
        logoUrl = logoUrl,
        city = city,
        operatingCities = operatingCities,
        isActive = isActive,
        isISOCertified = isoCertified,
        isVerified = isProfileApproved,
        yearsInBusiness = yearsInBusiness.toString(),
        workforceScale = serviceScale,
        bio = bio,
        notableClients = notableClients,
        listings = listings,
        joinedDate = joined
    )

    return PublicVendorProfileWithServices(profile, vendorServices)
}

data class PublicVendorProfile(
    val id: String,
    val businessName: String,
    val emoji: String,
    val logoUrl: String = "",
    val city: String,
    val operatingCities: List<String>,
    val isActive: Boolean,
    val isISOCertified: Boolean,
    val isVerified: Boolean,
    val yearsInBusiness: String,
    val workforceScale: String,
    val bio: String,
    val notableClients: List<String>,
    val listings: List<VendorServiceListing>,
    val joinedDate: String
)

@Composable
fun EmployerVendorBrowseScreen(
    viewModel: EmployerVendorBrowseViewModel = hiltViewModel(),
    proposalViewModel: EmployerProposalViewModel = hiltViewModel(),
    employerProfile: com.danish.noorservice.data.model.Employer? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    var openVendor by remember { mutableStateOf<PublicVendorProfile?>(null) }
    var query by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadVendors()
    }

    val selectedVendor = openVendor
    if (selectedVendor != null) {
        LaunchedEffect(selectedVendor.id) {
            viewModel.selectVendor(selectedVendor.id)
        }
        EmployerVendorDetailScreen(
            vendor = selectedVendor,
            services = uiState.selectedVendorServices,
            proposalViewModel = proposalViewModel,
            employerProfile = employerProfile,
            onBack = {
                openVendor = null
                viewModel.clearSelectedVendor()
            }
        )
        return
    }

    val realVendors = uiState.vendors.map { vendor ->
        vendor.toPublicVendorProfileWithServices(uiState.vendorServices[vendor.uid], uiState.vendorCategories)
    }

    val filtered = realVendors.filter { v ->
        val matchCat = selectedCatId == null || v.services.any { it.serviceId == selectedCatId }
        val matchQuery = query.isBlank() ||
                v.profile.businessName.contains(query, ignoreCase = true) ||
                v.profile.city.contains(query, ignoreCase = true)
        matchCat && matchQuery && v.profile.isVerified && v.profile.isActive
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Find Vendors", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.3).sp)
                        Text("Browse B2B service providers - Hire via admin", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                    }
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).clickable { viewModel.loadVendors() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search by name, city, service...", fontSize = 13.sp, color = NoorTextHint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NoorTextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NoorSurface, unfocusedContainerColor = NoorSurface,
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, cursorColor = NoorBlue
                    )
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { VendorBrowseChip("All", selectedCatId == null) { selectedCatId = null } }
            items(uiState.vendorCategories) { svc ->
                VendorBrowseChip("${svc.emoji} ${svc.label}", selectedCatId == svc.id) {
                    selectedCatId = if (selectedCatId == svc.id) null else svc.id
                }
            }
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        if (uiState.isLoading || uiState.vendors.isEmpty()) {
            VendorBrowseShimmer()
        } else if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No vendors found", fontSize = 14.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
                    Text("Try adjusting your filters", fontSize = 11.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { Text("${filtered.size} vendor${if (filtered.size != 1) "s" else ""} found", fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold) }
                items(filtered) { vendorWithServices ->
                    VendorBrowseCard(vendor = vendorWithServices.profile) { openVendor = vendorWithServices.profile }
                }
            }
        }
    }
}

@Composable
fun VendorBrowseCard(vendor: PublicVendorProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(14.dp)).background(if (vendor.isActive) NoorBlueLight else NoorBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (vendor.logoUrl.isNotBlank()) {
                        AsyncImage(model = vendor.logoUrl, contentDescription = "Logo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text(vendor.emoji, fontSize = 26.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(vendor.businessName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                        if (vendor.isVerified) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(NoorBlueLight).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("✓ Verified", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NoorBlue)
                            }
                        }
                    }
                    Text("${vendor.city} · ${vendor.yearsInBusiness} yrs experience", fontSize = 11.sp, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (vendor.isActive) NoorGreenLight else NoorBackground)
                            .border(1.dp, if (vendor.isActive) NoorGreen else NoorBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(if (vendor.isActive) "● Open for Business" else "○ Temporarily Closed", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (vendor.isActive) NoorGreen else NoorTextHint)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(vendor.workforceScale, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Text("workforce", fontSize = 9.sp, color = NoorTextHint)
                }
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                vendor.listings.take(3).forEach { listing ->
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(NoorBlueLight).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("${listing.emoji} ${listing.categoryLabel}", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    }
                }
                if (vendor.isISOCertified) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(NoorBackground).border(1.dp, NoorBorder, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("🏅 ISO", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorBrowseChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) NoorBlue else NoorBackground)
            .border(1.dp, if (selected) NoorBlue else NoorBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) Color.White else NoorTextSecondary)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Employer Vendor Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerVendorDetailScreen(
    vendor: PublicVendorProfile,
    services: List<VendorService>,
    proposalViewModel: EmployerProposalViewModel,
    employerProfile: com.danish.noorservice.data.model.Employer?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showProposalSheet by remember { mutableStateOf(false) }
    var hasExistingProposal by remember { mutableStateOf(false) }

    LaunchedEffect(vendor.id) {
        hasExistingProposal = VendorProposalStore.proposals.any {
            it.vendorId == vendor.id && it.status != VendorProposalStatus.DECLINED
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark))).statusBarsPadding().padding(top = 12.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)).clickable { onBack() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Vendor Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.weight(1f))
                    if (vendor.isISOCertified) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("🏅 ISO Certified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NoorSurface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(NoorBlueLight), contentAlignment = Alignment.Center) {
                                if (vendor.logoUrl.isNotBlank()) {
                                    AsyncImage(model = vendor.logoUrl, contentDescription = "Logo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Text(vendor.emoji, fontSize = 32.sp)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(vendor.businessName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                Text("${vendor.city} · Joined ${vendor.joinedDate}", fontSize = 11.sp, color = NoorTextHint)
                                if (vendor.isVerified) {
                                    Box(modifier = Modifier.padding(top = 4.dp).clip(RoundedCornerShape(6.dp)).background(NoorBlueLight).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("✓ Verified", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NoorBlue)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(vendor.bio, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Triple("🏢", "Headquarters", vendor.city),
                        Triple("📅", "Years Active", vendor.yearsInBusiness),
                        Triple("👥", "Workforce", vendor.workforceScale)
                    ).forEach { (emoji, label, value) ->
                        VendorInfoTile(emoji = emoji, label = label, value = value, modifier = Modifier.weight(1f))
                    }
                }
            }

            if (vendor.operatingCities.size > 1) {
                item {
                    VendorDetailSection(title = "Operating Cities") {
                        vendor.operatingCities.forEach { city ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NoorBlue))
                                Text(city, fontSize = 12.sp, color = NoorTextPrimary)
                            }
                        }
                    }
                }
            }

            if (vendor.notableClients.isNotEmpty()) {
                item {
                    VendorDetailSection(title = "Notable Clients") {
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            vendor.notableClients.forEach { client ->
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(NoorBackground).border(1.dp, NoorBorder, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                    Text(client, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = NoorTextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            if (services.isNotEmpty()) {
                item {
                    VendorDetailSection(title = "Services Offered (${services.size})") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            services.filter { it.isActive }.forEach { service ->
                                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = NoorBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(NoorBlueLight), contentAlignment = Alignment.Center) {
                                                Text("🔧", fontSize = 18.sp)
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(service.description.take(60), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                                                Spacer(Modifier.height(2.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    VendorMiniPill("💰 ${service.pricingModel}", NoorGreenLight, NoorGreen)
                                                    VendorMiniPill("📋 ${service.minContractDuration}", NoorBlueLight, NoorBlue)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Text(service.priceRange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NoorBlue)
                                        if (service.coverageAreas.isNotEmpty()) {
                                            Spacer(Modifier.height(4.dp))
                                            Text("Coverage: ${service.coverageAreas.take(3).joinToString(", ")}", fontSize = 10.sp, color = NoorTextHint)
                                        }
                                        if (service.highlights.isNotEmpty()) {
                                            Spacer(Modifier.height(4.dp))
                                            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                service.highlights.take(3).forEach { h ->
                                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(NoorBackground).border(1.dp, NoorBorder, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                        Text(h, fontSize = 9.sp, color = NoorTextSecondary)
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
            }

            item {
                Spacer(Modifier.height(4.dp))
                if (hasExistingProposal) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = NoorGreenLight.copy(alpha = 0.3f)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NoorGreen, modifier = Modifier.size(20.dp))
                            Text("Proposal already sent to admin. Track it in your Vendor Proposals tab.", fontSize = 12.sp, color = NoorGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                } else {
                    Button(
                        onClick = { showProposalSheet = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NoorBlue)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Send Proposal to Admin", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showProposalSheet && employerProfile != null) {
        VendorProposalBottomSheet(
            vendor = vendor,
            employerProfile = employerProfile,
            onDismiss = { showProposalSheet = false },
            onSend = { p ->
                proposalViewModel.sendVendorProposal(
                    vendorId = p.vendorId,
                    vendorBusinessName = p.vendorName,
                    vendorContactPerson = p.vendorContactPerson,
                    vendorPhone = p.vendorPhoneFull,
                    vendorEmail = p.vendorEmail,
                    vendorNtn = p.vendorNtn,
                    vendorRegNumber = p.vendorRegNumber,
                    vendorCity = p.vendorCity,
                    vendorAddress = p.vendorAddress,
                    vendorLogoUrl = p.vendorLogoUrl,
                    vendorBio = p.vendorBio,
                    vendorOperatingCities = p.vendorOperatingCities,
                    vendorServiceScale = p.vendorServiceScale,
                    vendorYearsInBusiness = p.vendorYearsInBusiness,
                    vendorIsoCertified = p.vendorIsoCertified,
                    vendorNotableClients = p.vendorNotableClients,
                    vendorHeadOffice = p.vendorHeadOffice,
                    vendorWorkforceScale = p.vendorWorkforceScale,
                    services = emptyList(),
                    jobTitle = p.jobTitle,
                    serviceLabel = p.serviceLabel,
                    location = p.location,
                    schedule = p.schedule,
                    startDate = p.startDate,
                    offerPrice = p.budget,
                    note = p.note,
                    employerProfile = employerProfile
                )
                hasExistingProposal = true
                showProposalSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorProposalBottomSheet(
    vendor: PublicVendorProfile,
    employerProfile: com.danish.noorservice.data.model.Employer,
    onDismiss: () -> Unit,
    onSend: (VendorProposal) -> Unit
) {
    var jobTitle by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(employerProfile.city) }
    var startDate by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = NoorSurface, dragHandle = { BottomSheetDefaults.DragHandle(color = NoorTextHint) }) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Check, contentDescription = null, tint = NoorBlue, modifier = Modifier.size(22.dp))
                Text("Send Proposal to Admin", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            }
            Text("The admin will review and connect you with ${vendor.businessName}.", fontSize = 12.sp, color = NoorTextHint)

            Spacer(Modifier.height(4.dp))

            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = NoorBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(vendor.emoji, fontSize = 24.sp)
                    Column {
                        Text(vendor.businessName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text(vendor.city, fontSize = 11.sp, color = NoorTextHint)
                    }
                }
            }

            VendorProposalInputField(value = jobTitle, onChange = { jobTitle = it }, label = "Job Title", placeholder = "e.g. AC Installation for Office", icon = "💼")
            VendorProposalInputField(value = location, onChange = { location = it }, label = "Location", placeholder = "City / Area", icon = "📍")
            VendorProposalInputField(value = startDate, onChange = { startDate = it }, label = "Preferred Start Date", placeholder = "e.g. Within 1 week", icon = "📅")
            VendorProposalInputField(value = schedule, onChange = { schedule = it }, label = "Work Schedule", placeholder = "e.g. Full-time, Mon–Sat", icon = "⏰")
            VendorProposalInputField(value = budget, onChange = { budget = it }, label = "Budget / Offer", placeholder = "e.g. PKR 50,000 – 80,000", icon = "💰")
            VendorProposalInputField(value = note, onChange = { note = it }, label = "Note (optional)", placeholder = "Any special requirements…", icon = "📝", singleLine = false)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(46.dp), shape = RoundedCornerShape(10.dp)) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        if (jobTitle.isBlank() || budget.isBlank()) return@Button
                        val proposal = VendorProposal(
                            id = UUID.randomUUID().toString(),
                            vendorName = vendor.businessName,
                            vendorEmoji = vendor.emoji,
                            vendorCity = vendor.city,
                            serviceLabel = vendor.listings.firstOrNull()?.categoryLabel ?: "",
                            jobTitle = jobTitle,
                            location = location,
                            schedule = schedule,
                            startDate = startDate,
                            budget = budget,
                            note = note,
                            sentAt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
                            status = VendorProposalStatus.PENDING,
                            employerName = employerProfile.fullName,
                            employerPhone = employerProfile.phone,
                            employerCity = employerProfile.city,
                            vendorId = vendor.id
                        )
                        onSend(proposal)
                    },
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NoorBlue),
                    enabled = jobTitle.isNotBlank() && budget.isNotBlank()
                ) {
                    Text("Send to Admin", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Proposals Inbox Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorProposalInboxScreen(viewModel: EmployerProposalViewModel = hiltViewModel()) {
    val proposals = VendorProposalStore.proposals
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Pending", "Accepted", "Declined")

    LaunchedEffect(Unit) {
        viewModel.syncStores()
    }

    val filtered = when (selectedTab) {
        1 -> proposals.filter { it.status == VendorProposalStatus.PENDING }
        2 -> proposals.filter { it.status == VendorProposalStatus.ACCEPTED }
        3 -> proposals.filter { it.status == VendorProposalStatus.DECLINED }
        else -> proposals
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark))).statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Vendor Proposals", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (proposals.isNotEmpty()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.25f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                            Text("${proposals.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Track proposals sent to vendors via admin", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NoorSurface,
            contentColor = NoorBlue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        val count = when (index) {
                            1 -> proposals.count { it.status == VendorProposalStatus.PENDING }
                            2 -> proposals.count { it.status == VendorProposalStatus.ACCEPTED }
                            3 -> proposals.count { it.status == VendorProposalStatus.DECLINED }
                            else -> proposals.size
                        }
                        Text("$title${if (count > 0) " ($count)" else ""}", fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                    }
                )
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No proposals yet", fontSize = 14.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
                    Text("Browse vendors and send proposals", fontSize = 11.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    VendorProposalCard(proposal = proposal)
                }
            }
        }
    }
}

@Composable
private fun VendorProposalCard(proposal: VendorProposal) {
    var expanded by remember { mutableStateOf(false) }

    val (accentColor, pillBg, statusLabel, statusEmoji) = when (proposal.status) {
        VendorProposalStatus.PENDING -> listOf(NoorBlue, NoorBlueLight, "Pending", "⏳")
        VendorProposalStatus.ACCEPTED -> listOf(NoorGreen, NoorGreenLight, "Accepted", "✅")
        VendorProposalStatus.DECLINED -> listOf(NoorRed, NoorRedLight, "Declined", "❌")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.width(4.dp).fillMaxHeight().background(accentColor as Color, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                )
                Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(NoorBlueLight), contentAlignment = Alignment.Center) {
                                Text(proposal.vendorEmoji, fontSize = 20.sp)
                            }
                            Column {
                                Text(proposal.vendorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(proposal.vendorCity, fontSize = 11.sp, color = NoorTextHint)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(pillBg as Color).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("$statusEmoji $statusLabel", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(proposal.jobTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                    Text("Budget: ${proposal.budget} · ${proposal.location}", fontSize = 11.sp, color = NoorTextHint)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        VendorProposalTag("📅", proposal.startDate)
                        VendorProposalTag("⏰", proposal.schedule)
                        VendorProposalTag("💰", proposal.budget)
                        VendorProposalTag("🕐", proposal.sentAt)
                    }
                }
            }

            AnimatedVisibility(visible = expanded, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(bottom = 14.dp)) {
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(10.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = NoorBackground), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("PROPOSAL DETAILS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NoorBlue, letterSpacing = 0.5.sp)
                            DetailRow("💼", "Job Title", proposal.jobTitle)
                            DetailRow("📍", "Location", proposal.location)
                            DetailRow("📅", "Preferred Start Date", proposal.startDate)
                            DetailRow("💰", "Budget / Offer", proposal.budget)
                            if (proposal.note.isNotBlank())
                                DetailRow("📝", "Note to Admin", proposal.note, isMultiline = true)
                            Text("VENDOR INFORMATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NoorBlue, letterSpacing = 0.5.sp, modifier = Modifier.padding(top = 4.dp))
                            DetailRow("🏢", "Vendor", proposal.vendorName)
                            DetailRow("🌆", "Headquarters", proposal.vendorCity)
                            DetailRow("📨", "Proposal Sent", proposal.sentAt)
                            Text("ID: ${proposal.id.take(8)}", fontSize = 9.sp, color = NoorTextHint, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    if (proposal.status == VendorProposalStatus.ACCEPTED) {
                        Spacer(Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(NoorGreenLight).padding(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NoorGreen, modifier = Modifier.size(16.dp))
                                Text("Admin has accepted and connected you with this vendor. Expect a call soon.", fontSize = 11.sp, color = NoorGreen, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(bottom = 10.dp), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { expanded = !expanded }, colors = ButtonDefaults.textButtonColors(contentColor = NoorTextHint)) {
                    Text(if (expanded) "Hide details ▲" else "View details ▼", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorInfoTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = NoorSurface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(emoji, fontSize = 18.sp)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            Text(label, fontSize = 9.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun VendorDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = NoorSurface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue, letterSpacing = 0.3.sp)
            content()
        }
    }
}

@Composable
private fun VendorMiniPill(label: String, bg: Color, textColor: Color) {
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
private fun VendorProposalTag(icon: String, label: String) {
    Row(modifier = Modifier.padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(icon, fontSize = 11.sp)
        Text(label, fontSize = 10.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun VendorProposalInputField(value: String, onChange: (String) -> Unit, label: String, placeholder: String, icon: String, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text("$icon  $label", fontSize = 12.sp) },
        placeholder = { Text(placeholder, fontSize = 12.sp, color = NoorTextHint) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine, maxLines = if (singleLine) 1 else 4,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NoorBlue, unfocusedBorderColor = NoorBorder, focusedLabelColor = NoorBlue, cursorColor = NoorBlue)
    )
}

@Composable
private fun DetailRow(icon: String, label: String, value: String, isMultiline: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(icon, fontSize = 14.sp, modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = NoorTextHint, letterSpacing = 0.3.sp)
            if (isMultiline) {
                Text(value, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
            } else {
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
            }
        }
    }
}