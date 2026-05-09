package com.danish.noorservice.ui.screens.employer

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danish.noorservice.ui.screens.vendor.VendorServiceListing
import com.danish.noorservice.ui.screens.vendor.allVendorServiceCategories
import com.danish.noorservice.ui.screens.vendor.sampleListings
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employer.EmployerVendorBrowseViewModel
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// Vendor public profile model (what employers see)
// ─────────────────────────────────────────────────────────────────────────────

data class PublicVendorProfile(
    val id: String,
    val businessName: String,
    val emoji: String,
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

// ─────────────────────────────────────────────────────────────────────────────
// Sample vendor data
// ─────────────────────────────────────────────────────────────────────────────

val sampleVendors = listOf(
    PublicVendorProfile(
        id = "v1",
        businessName = "Al-Noor Facility Services",
        emoji = "🏢",
        city = "Lahore",
        operatingCities = listOf("Lahore", "Islamabad", "Faisalabad"),
        isActive = true,
        isISOCertified = true,
        isVerified = true,
        yearsInBusiness = "8",
        workforceScale = "51–200 staff",
        bio = "Integrated facility management for commercial & residential clients since 2017.",
        notableClients = listOf("DHA Lahore", "Nishat Group", "Packages Ltd", "Engro Corp"),
        listings = sampleListings.toList(),
        joinedDate = "Jan 2025"
    ),
    PublicVendorProfile(
        id = "v2",
        businessName = "ShieldPro Security",
        emoji = "🛡️",
        city = "Karachi",
        operatingCities = listOf("Karachi", "Islamabad"),
        isActive = true,
        isISOCertified = false,
        isVerified = true,
        yearsInBusiness = "12",
        workforceScale = "51–200 staff",
        bio = "Pakistan's trusted security solutions provider. Armed, unarmed & CCTV specialists.",
        notableClients = listOf("Port Qasim Authority", "Habib Bank", "K-Electric"),
        listings = listOf(
            VendorServiceListing(
                id = "s1", categoryId = "security",
                categoryLabel = "Security Services", emoji = "🛡️",
                isActive = true,
                description = "Trained armed/unarmed guards and CCTV monitoring for commercial premises.",
                minContractDuration = "6 Months",
                pricingModel = "Per Shift",
                priceRange = "PKR 1,200 – 2,500 / shift",
                coverageAreas = listOf("Karachi", "Islamabad"),
                highlights = listOf("Licensed armed guards", "24/7 monitoring centre", "Emergency protocol")
            )
        ),
        joinedDate = "Oct 2024"
    ),
    PublicVendorProfile(
        id = "v3",
        businessName = "GreenLeaf Landscaping",
        emoji = "🌿",
        city = "Islamabad",
        operatingCities = listOf("Islamabad", "Rawalpindi"),
        isActive = true,
        isISOCertified = false,
        isVerified = false,
        yearsInBusiness = "5",
        workforceScale = "11–50 staff",
        bio = "Complete outdoor solutions — lawn care, seasonal planting and commercial landscaping.",
        notableClients = listOf("NUST University", "Serena Hotel"),
        listings = listOf(
            VendorServiceListing(
                id = "s2", categoryId = "landscaping",
                categoryLabel = "Landscaping", emoji = "🌿",
                isActive = true,
                description = "Lawn maintenance, seasonal planting and irrigation systems.",
                minContractDuration = "3 Months",
                pricingModel = "Monthly Contract",
                priceRange = "PKR 20,000 – 120,000 / month",
                coverageAreas = listOf("Islamabad", "Rawalpindi"),
                highlights = listOf("Certified horticulturists", "Eco-friendly approach", "Free site assessment")
            )
        ),
        joinedDate = "Mar 2025"
    ),
    PublicVendorProfile(
        id = "v4",
        businessName = "SwiftCatering Co.",
        emoji = "🍽️",
        city = "Lahore",
        operatingCities = listOf("Lahore", "Multan", "Faisalabad"),
        isActive = false,
        isISOCertified = true,
        isVerified = true,
        yearsInBusiness = "10",
        workforceScale = "11–50 staff",
        bio = "Corporate canteen management and event catering since 2014. ISO 22000 certified kitchen.",
        notableClients = listOf("Sapphire Textile", "Ali Gohar & Co", "Fauji Fertilizer"),
        listings = listOf(
            VendorServiceListing(
                id = "s3", categoryId = "catering",
                categoryLabel = "Catering & Mess", emoji = "🍽️",
                isActive = false,
                description = "Daily meal plans, canteen operations and large-scale event catering.",
                minContractDuration = "1 Month",
                pricingModel = "Monthly Contract",
                priceRange = "PKR 150 – 400 / meal",
                coverageAreas = listOf("Lahore", "Multan"),
                highlights = listOf("ISO 22000 kitchen", "Customisable menu", "Up to 1,000 covers/day")
            )
        ),
        joinedDate = "Nov 2024"
    ),
    PublicVendorProfile(
        id = "v5",
        businessName = "TechSupport Hub",
        emoji = "💻",
        city = "Lahore",
        operatingCities = listOf("Lahore", "Islamabad", "Karachi"),
        isActive = true,
        isISOCertified = false,
        isVerified = true,
        yearsInBusiness = "3",
        workforceScale = "1–10 staff",
        bio = "On-site & remote IT helpdesk, network setup and hardware repair for SMEs.",
        notableClients = listOf("Daraz.pk", "Rozee.pk"),
        listings = listOf(
            VendorServiceListing(
                id = "s4", categoryId = "it_support",
                categoryLabel = "IT Support", emoji = "💻",
                isActive = true,
                description = "Network setup, server maintenance, hardware repair and remote helpdesk.",
                minContractDuration = "< 1 Month",
                pricingModel = "Hourly Rate",
                priceRange = "PKR 2,000 – 5,000 / hr",
                coverageAreas = listOf("Lahore", "Islamabad", "Karachi"),
                highlights = listOf("Certified engineers", "SLA-backed response", "Remote & on-site")
            )
        ),
        joinedDate = "Feb 2025"
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Employer Vendor Browse Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerVendorBrowseScreen(
    viewModel: EmployerVendorBrowseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var openVendor     by remember { mutableStateOf<PublicVendorProfile?>(null) }
    var query          by remember { mutableStateOf("") }
    var selectedCatId  by remember { mutableStateOf<String?>(null) }
    var showActiveOnly by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadVendors()
    }

    if (openVendor != null) {
        EmployerVendorDetailScreen(
            vendor = openVendor!!,
            onBack = { openVendor = null }
        )
        return
    }

    val filtered = sampleVendors.filter { v ->
        val matchCat    = selectedCatId == null || v.listings.any { it.categoryId == selectedCatId }
        val matchActive = !showActiveOnly || v.isActive
        val matchQuery  = query.isBlank() ||
                v.businessName.contains(query, ignoreCase = true) ||
                v.city.contains(query, ignoreCase = true) ||
                v.listings.any { it.categoryLabel.contains(query, ignoreCase = true) }
        matchCat && matchActive && matchQuery
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header + search
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Text(
                    "Find Vendors", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp
                )
                Text(
                    "Browse B2B service providers · Hire via admin",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("Search by name, city, service…", fontSize = 13.sp, color = NoorTextHint)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = NoorTextHint)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = NoorSurface,
                        unfocusedContainerColor = NoorSurface,
                        focusedBorderColor      = Color.Transparent,
                        unfocusedBorderColor    = Color.Transparent,
                        cursorColor             = NoorBlue
                    )
                )
            }
        }

        // Service category filter chips
        LazyRow(
            modifier              = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { VendorBrowseChip("All", selectedCatId == null) { selectedCatId = null } }
            items(allVendorServiceCategories) { svc ->
                VendorBrowseChip(
                    label    = "${svc.emoji} ${svc.label}",
                    selected = selectedCatId == svc.id,
                    onClick  = { selectedCatId = if (selectedCatId == svc.id) null else svc.id }
                )
            }
        }

        // Active-only toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NoorSurface)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Open for business only", fontSize = 12.sp,
                color = NoorTextSecondary, fontWeight = FontWeight.Medium
            )
            Switch(
                checked         = showActiveOnly,
                onCheckedChange = { showActiveOnly = it },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White, checkedTrackColor   = NoorBlue,
                    uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder
                )
            )
        }
        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // Results
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No vendors found", fontSize = 14.sp,
                        color = NoorTextHint, fontWeight = FontWeight.Medium)
                    Text("Try adjusting your filters", fontSize = 11.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "${filtered.size} vendor${if (filtered.size != 1) "s" else ""} found",
                        fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold
                    )
                }
                items(filtered) { vendor ->
                    VendorBrowseCard(vendor = vendor, onClick = { openVendor = vendor })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Browse Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorBrowseCard(vendor: PublicVendorProfile, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (vendor.isActive) NoorBlueLight else NoorBackground),
                    contentAlignment = Alignment.Center
                ) { Text(vendor.emoji, fontSize = 26.sp) }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            vendor.businessName, fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (vendor.isVerified) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorBlueLight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("✓ Verified", fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold, color = NoorBlue)
                            }
                        }
                    }
                    Text(
                        "${vendor.city} · ${vendor.yearsInBusiness} yrs experience",
                        fontSize = 11.sp, color = NoorTextHint
                    )
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (vendor.isActive) NoorGreenLight else NoorBackground)
                            .border(
                                1.dp,
                                if (vendor.isActive) NoorGreen else NoorBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            if (vendor.isActive) "● Open for Business" else "○ Temporarily Closed",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            color = if (vendor.isActive) NoorGreen else NoorTextHint
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(vendor.workforceScale, fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Text("workforce", fontSize = 9.sp, color = NoorTextHint)
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
            Spacer(Modifier.height(10.dp))

            // Service tags
            Row(
                modifier              = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                vendor.listings.take(3).forEach { listing ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NoorBlueLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${listing.emoji} ${listing.categoryLabel}",
                            fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue
                        )
                    }
                }
                if (vendor.isISOCertified) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NoorBackground)
                            .border(1.dp, NoorBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("🏅 ISO", fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorTextSecondary)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployerVendorDetailScreen(
    vendor: PublicVendorProfile,
    onBack: () -> Unit
) {
    var showProposalDialog by remember { mutableStateOf(false) }
    var proposalSent by remember {
        mutableStateOf(VendorProposalStore.proposals.any { it.vendorName == vendor.businessName })
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header
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
                        .size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(18.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp).clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) { Text(vendor.emoji, fontSize = 30.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(vendor.businessName, fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            if (vendor.isActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(NoorGreen)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("Open", fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                        Text(
                            "${vendor.city} · Joined ${vendor.joinedDate}",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (vendor.isVerified) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.16f))
                                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text("✅ Verified", fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            if (vendor.isISOCertified) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.16f))
                                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text("🏅 ISO Certified", fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Body
        Column(
            modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VendorInfoTile("👥", "Workforce",  vendor.workforceScale, Modifier.weight(1f))
                VendorInfoTile("⏱",  "Experience", "${vendor.yearsInBusiness} yrs", Modifier.weight(1f))
                VendorInfoTile("📍", "Head City",  vendor.city, Modifier.weight(1f))
            }

            VendorDetailSection("About") {
                Text(vendor.bio, fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp)
            }

            VendorDetailSection("Services Offered (${vendor.listings.size})") {
                vendor.listings.forEach { listing ->
                    VendorListingRow(listing = listing)
                    if (listing != vendor.listings.last()) {
                        HorizontalDivider(
                            color     = NoorDivider,
                            thickness = 0.6.dp,
                            modifier  = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }

            if (vendor.notableClients.isNotEmpty()) {
                VendorDetailSection("Notable Clients") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)
                    ) {
                        vendor.notableClients.forEach { client ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorBackground)
                                    .border(1.dp, NoorBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("🤝 $client", fontSize = 11.sp,
                                    color = NoorTextSecondary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            VendorDetailSection("Cities They Operate In") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    vendor.operatingCities.forEach { city ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NoorBlueLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("📍 $city", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorBlue)
                        }
                    }
                }
            }

            // Info banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NoorBlueLight)
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("ℹ️", fontSize = 16.sp)
                    Text(
                        "All vendor engagements go through the Noor Services admin. " +
                                "Send a proposal and we'll connect you.",
                        fontSize = 12.sp, color = NoorBlueDark, lineHeight = 17.sp
                    )
                }
            }

            // CTA
            if (proposalSent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NoorGreenLight)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(NoorGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text("Proposal Sent to Admin!", fontSize = 14.sp,
                                fontWeight = FontWeight.Bold, color = NoorGreen)
                            Text(
                                "Track status in 'Proposals' → Vendor tab.",
                                fontSize = 11.sp, color = NoorTextSecondary, lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick   = { showProposalDialog = true },
                    modifier  = Modifier.fillMaxWidth().height(54.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = NoorBlue),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("📩  Send Proposal to Admin", fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showProposalDialog) {
        SendVendorProposalDialog(
            vendor    = vendor,
            onDismiss = { showProposalDialog = false },
            onSent    = { showProposalDialog = false; proposalSent = true }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Expandable listing row inside vendor detail
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorListingRow(listing: VendorServiceListing) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth().clickable { expanded = !expanded },
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(NoorBlueLight),
                contentAlignment = Alignment.Center
            ) { Text(listing.emoji, fontSize = 18.sp) }
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.categoryLabel, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Text(listing.priceRange, fontSize = 11.sp,
                    color = NoorBlue, fontWeight = FontWeight.Medium)
            }
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (listing.isActive) NoorGreenLight else NoorBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (listing.isActive) "Active" else "Paused",
                        fontSize = 9.sp, fontWeight = FontWeight.Bold,
                        color = if (listing.isActive) NoorGreen else NoorTextHint
                    )
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null, tint = NoorTextHint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        AnimatedVisibility(visible = expanded, enter = fadeIn() + expandVertically()) {
            Column(modifier = Modifier.padding(top = 10.dp, start = 48.dp)) {
                if (listing.description.isNotBlank()) {
                    Text(listing.description, fontSize = 12.sp,
                        color = NoorTextSecondary, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    VendorMiniPill("💰 ${listing.pricingModel}", NoorBlueLight, NoorBlue)
                    VendorMiniPill("📅 Min: ${listing.minContractDuration}", NoorBackground, NoorTextSecondary)
                }
                if (listing.highlights.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    listing.highlights.forEach { h ->
                        Row(
                            modifier              = Modifier.padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(14.dp).clip(CircleShape).background(NoorBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null,
                                    tint = Color.White, modifier = Modifier.size(8.dp))
                            }
                            Text(h, fontSize = 11.sp, color = NoorTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Send Vendor Proposal Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SendVendorProposalDialog(
    vendor: PublicVendorProfile,
    onDismiss: () -> Unit,
    onSent: () -> Unit
) {
    var serviceRequired by remember { mutableStateOf("") }
    var location        by remember { mutableStateOf("") }
    var duration        by remember { mutableStateOf("") }
    var startDate       by remember { mutableStateOf("") }
    var budget          by remember { mutableStateOf("") }
    var note            by remember { mutableStateOf("") }

    val isValid = serviceRequired.isNotBlank() && location.isNotBlank() && budget.isNotBlank()

    val firstServiceLabel = vendor.listings.firstOrNull()?.categoryLabel ?: "Service"

    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(20.dp),
        containerColor   = NoorSurface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📩 Send Proposal to Admin",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NoorTextPrimary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorBlueLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(NoorBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) { Text(vendor.emoji, fontSize = 18.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(vendor.businessName, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text("${vendor.city} · $firstServiceLabel",
                            fontSize = 11.sp, color = NoorTextHint)
                    }
                    if (vendor.isVerified) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NoorBlueDark.copy(alpha = 0.1f))
                                .border(1.dp, NoorBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text("✓ Verified", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = NoorBlue)
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("REQUIREMENT DETAILS", fontSize = 9.sp, fontWeight = FontWeight.Bold,
                    color = NoorTextHint, letterSpacing = 0.8.sp)

                VendorProposalInputField(value = serviceRequired, onChange = { serviceRequired = it },
                    label = "Service Required *", placeholder = "e.g. Office Cleaning – 3 floors", icon = "🔧")
                VendorProposalInputField(value = location, onChange = { location = it },
                    label = "Location *", placeholder = "e.g. DHA Phase 4, Lahore", icon = "📍")
                VendorProposalInputField(value = duration, onChange = { duration = it },
                    label = "Contract Duration", placeholder = "e.g. 6 months, ongoing", icon = "📅")
                VendorProposalInputField(value = startDate, onChange = { startDate = it },
                    label = "Preferred Start Date", placeholder = "e.g. 1 May 2026", icon = "🗓")
                VendorProposalInputField(value = budget, onChange = { budget = it },
                    label = "Budget / Offer *", placeholder = "e.g. PKR 50,000 / month", icon = "💰")
                VendorProposalInputField(
                    value = note, onChange = { if (it.length <= 200) note = it },
                    label = "Note to Admin (optional)",
                    placeholder = "Additional requirements or questions…",
                    icon = "📝", singleLine = false
                )
                if (note.isNotEmpty()) {
                    Text(
                        "${note.length}/200", fontSize = 10.sp,
                        color    = if (note.length > 190) NoorOrange else NoorTextHint,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(NoorOrangeLight).padding(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.Top
                    ) {
                        Text("💡", fontSize = 13.sp)
                        Text(
                            "The admin will review and connect you with ${vendor.businessName}. " +
                                    "Check 'Proposals' for status updates.",
                            fontSize = 11.sp, color = NoorOrange, lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val now = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                    VendorProposalStore.proposals.add(
                        0,
                        VendorProposal(
                            id           = UUID.randomUUID().toString(),
                            vendorName   = vendor.businessName,
                            vendorEmoji  = vendor.emoji,
                            vendorCity   = vendor.city,
                            serviceLabel = serviceRequired.trim(),
                            jobTitle     = serviceRequired.trim(),
                            location     = location.trim(),
                            schedule     = duration.trim().ifBlank { "TBD" },
                            startDate    = startDate.trim().ifBlank { "TBD" },
                            budget       = budget.trim(),
                            note         = note.trim(),
                            sentAt       = now,
                            status       = VendorProposalStatus.PENDING   // ← updated
                        )
                    )
                    onSent()
                },
                enabled  = isValid,
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NoorBlue),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Send to Admin", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick  = onDismiss,
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Cancel", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Proposals Inbox Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorProposalInboxScreen() {
    val proposals = VendorProposalStore.proposals
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Pending", "Accepted", "Declined")   // ← updated

    val filtered = when (selectedTab) {
        1    -> proposals.filter { it.status == VendorProposalStatus.PENDING }
        2    -> proposals.filter { it.status == VendorProposalStatus.ACCEPTED }
        3    -> proposals.filter { it.status == VendorProposalStatus.DECLINED }
        else -> proposals.toList()
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 0.dp)
        ) {
            Column {
                LazyRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding        = PaddingValues(end = 8.dp)
                ) {
                    items(tabs.size) { index ->
                        val label = tabs[index]
                        val count = when (index) {
                            0    -> proposals.size
                            1    -> proposals.count { it.status == VendorProposalStatus.PENDING }
                            2    -> proposals.count { it.status == VendorProposalStatus.ACCEPTED }
                            3    -> proposals.count { it.status == VendorProposalStatus.DECLINED }
                            else -> 0
                        }
                        val isSelected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.clickable { selectedTab = index }
                        ) {
                            Text(
                                if (count > 0) "$label ($count)" else label,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f),
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(if (isSelected) 40.dp else 0.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🏢", fontSize = 48.sp)
                    Text("No vendor proposals yet", fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                    Text("Browse vendors and send a proposal to admin.",
                        fontSize = 12.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    VendorProposalCard(
                        proposal = proposal,
                        onCancel = {
                            val idx = VendorProposalStore.proposals.indexOfFirst { p -> p.id == it.id }
                            if (idx != -1) VendorProposalStore.proposals.removeAt(idx)
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Proposal Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorProposalCard(
    proposal: VendorProposal,
    onCancel: (VendorProposal) -> Unit
) {
    var showCancelDlg by remember { mutableStateOf(false) }
    var expanded      by remember { mutableStateOf(false) }

    val (accentColor, pillBg, statusLabel, statusEmoji) = when (proposal.status) {
        VendorProposalStatus.PENDING  -> listOf(NoorBlue,   NoorBlueLight,  "Pending",  "⏳")
        VendorProposalStatus.ACCEPTED -> listOf(NoorGreen,  NoorGreenLight, "Accepted", "✅")
        VendorProposalStatus.DECLINED -> listOf(NoorRed,    NoorRedLight,   "Declined", "❌")
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .width(4.dp).fillMaxHeight()
                        .background(
                            accentColor as Color,
                            RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )
                Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                    // Top row — vendor avatar + status pill
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier              = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp).clip(RoundedCornerShape(12.dp))
                                    .background(NoorBlueLight),
                                contentAlignment = Alignment.Center
                            ) { Text(proposal.vendorEmoji, fontSize = 20.sp) }
                            Column {
                                Text(proposal.vendorName, fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = NoorTextPrimary,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(proposal.vendorCity, fontSize = 11.sp, color = NoorTextHint)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(pillBg as Color)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("$statusEmoji $statusLabel", fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = accentColor)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Expandable summary row
                    Row(
                        modifier              = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(proposal.jobTitle, fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                            Text("${proposal.serviceLabel} · ${proposal.location}",
                                fontSize = 11.sp, color = NoorTextHint)
                        }
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Show less" else "Show more",
                            tint     = NoorTextHint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(8.dp))

                    // Quick-glance tags
                    Row(
                        modifier              = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VendorProposalTag("📅", proposal.startDate)
                        VendorProposalTag("⏰", proposal.schedule)
                        VendorProposalTag("💰", proposal.budget)
                        VendorProposalTag("🕐", "Sent ${proposal.sentAt}")
                    }

                    // Expanded details
                    AnimatedVisibility(
                        visible = expanded,
                        enter   = fadeIn() + expandVertically(),
                        exit    = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier            = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                            Text("PROPOSAL DETAILS", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = NoorBlue, letterSpacing = 0.5.sp)

                            DetailRow("🔧", "Service Required", proposal.serviceLabel)
                            DetailRow("📍", "Location",         proposal.location)
                            if (proposal.schedule != "TBD")
                                DetailRow("📋", "Contract Duration",    proposal.schedule)
                            if (proposal.startDate != "TBD")
                                DetailRow("🗓️", "Preferred Start Date", proposal.startDate)
                            DetailRow("💰", "Budget / Offer",   proposal.budget)
                            if (proposal.note.isNotBlank())
                                DetailRow("📝", "Note to Admin", proposal.note, isMultiline = true)

                            Text("VENDOR INFORMATION", fontSize = 10.sp,
                                fontWeight = FontWeight.Bold, color = NoorBlue, letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(top = 4.dp))

                            DetailRow("🏢", "Vendor",       proposal.vendorName)
                            DetailRow("🌆", "Headquarters", proposal.vendorCity)
                            DetailRow("📨", "Proposal Sent", proposal.sentAt)

                            Text("ID: ${proposal.id.take(8)}", fontSize = 9.sp,
                                color = NoorTextHint, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    // Cancel button — PENDING only
                    if (proposal.status == VendorProposalStatus.PENDING) {
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick  = { showCancelDlg = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                            border   = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(NoorRed, NoorRed))
                            )
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null,
                                modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel Proposal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Accepted success strip
                    if (proposal.status == VendorProposalStatus.ACCEPTED) {
                        Spacer(Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .background(NoorGreenLight).padding(10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null,
                                    tint = NoorGreen, modifier = Modifier.size(16.dp))
                                Text(
                                    "Admin has accepted and connected you with this vendor. Expect a call soon.",
                                    fontSize = 11.sp, color = NoorGreen,
                                    fontWeight = FontWeight.Medium, lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCancelDlg) {
        AlertDialog(
            onDismissRequest = { showCancelDlg = false },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = NoorSurface,
            title = {
                Text("Cancel Proposal?", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = NoorTextPrimary)
            },
            text = {
                Text(
                    "This will withdraw your proposal for ${proposal.vendorName}. " +
                            "The admin will no longer receive it.",
                    fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick  = { showCancelDlg = false; onCancel(proposal) },
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = NoorRed),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Yes, Cancel", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { showCancelDlg = false },
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("No, Keep", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small reusable composables
// ─────────────────────────────────────────────────────────────────────────────

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
        Text(label, fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (selected) Color.White else NoorTextSecondary)
    }
}

@Composable
private fun VendorInfoTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier, shape = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            Text(label, fontSize = 9.sp,  color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun VendorDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = NoorBlue, letterSpacing = 0.3.sp)
            content()
        }
    }
}

@Composable
private fun VendorMiniPill(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)).background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

@Composable
private fun VendorProposalTag(icon: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)).background(NoorBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 11.sp)
        Text(label, fontSize = 10.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun VendorProposalInputField(
    value: String, onChange: (String) -> Unit,
    label: String, placeholder: String, icon: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label       = { Text("$icon  $label", fontSize = 12.sp) },
        placeholder = { Text(placeholder, fontSize = 12.sp, color = NoorTextHint) },
        modifier    = Modifier.fillMaxWidth(),
        singleLine  = singleLine,
        maxLines    = if (singleLine) 1 else 4,
        shape       = RoundedCornerShape(10.dp),
        colors      = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NoorBlue,
            unfocusedBorderColor = NoorBorder,
            focusedLabelColor    = NoorBlue,
            cursorColor          = NoorBlue
        )
    )
}

@Composable
private fun DetailRow(
    icon: String,
    label: String,
    value: String,
    isMultiline: Boolean = false
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(icon, fontSize = 14.sp, modifier = Modifier.width(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium,
                color = NoorTextHint, letterSpacing = 0.3.sp)
            if (isMultiline) {
                Text(value, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
            } else {
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
            }
        }
    }
}