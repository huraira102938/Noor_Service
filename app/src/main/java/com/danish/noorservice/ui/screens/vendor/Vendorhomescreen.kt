package com.danish.noorservice.ui.screens.vendor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.danish.noorservice.R
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.ui.components.VendorHomeScreenShimmer
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.vendor.VendorHomeViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Home Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorHomeScreen(
    userId: String,
    viewModel: VendorHomeViewModel,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadProfile(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter            = painterResource(id = R.drawable.noor_services_app_logo),
                            contentDescription = "Logo",
                            modifier           = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .graphicsLayer { scaleX = 3f; scaleY = 3f; translationY = 39f; translationX = 4f },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Noor Services",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.2).sp)
                        Text("Vendor Portal",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                    }
                }

                Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { viewModel.loadProfile(userId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
            }
        }

        // ── Loading / Error / Content ─────────────────────────────────────────
when {
            uiState.isLoading -> {
                VendorHomeScreenShimmer()
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("⚠️", fontSize = 40.sp)
                        Text("Failed to load profile", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                        Text(uiState.error ?: "", fontSize = 12.sp, color = NoorTextHint)
                        Button(
                            onClick = { viewModel.loadProfile(userId) },
                            colors  = ButtonDefaults.buttonColors(containerColor = VendorTeal)
                        ) { Text("Retry", color = Color.White) }
                    }
                }
            }

            uiState.profile == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pull to refresh", color = NoorTextHint, fontSize = 14.sp)
                }
            }

            else -> {
                VendorHomeContent(
                    modifier  = Modifier.weight(1f),
                    profile   = uiState.profile,
                    services  = uiState.services,
                    viewModel = viewModel,
                    onNavigateToNotifications = onNavigateToNotifications
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content — populated from Firestore data
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VendorHomeContent(
    modifier: Modifier = Modifier,
    profile: Vendor?,
    services: List<VendorService>,
    viewModel: VendorHomeViewModel,
    onNavigateToNotifications: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // ── Vendor Profile Card ───────────────────────────────────────────
        Card(
            modifier  = Modifier.padding(16.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                // Header gradient strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                        .padding(18.dp)
                ) {
                    // ISO Certified badge - top right corner
                    if (profile?.isoCertified == true) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopEnd),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text("🏅 ISO Certified", fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = profile?.businessName?.take(2)?.uppercase() ?: "VS"
                            if (!profile?.logoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model              = profile!!.logoUrl,
                                    contentDescription = "Business logo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                                )
                            } else {
                                Text(initials, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile?.businessName ?: "—", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                buildString {
                                    if (!profile?.city.isNullOrBlank()) append(profile!!.city)
                                    append(" · ")
                                    if (profile?.yearsInBusiness != null && profile!!.yearsInBusiness > 0) {
                                        append("Joined ${profile!!.yearsInBusiness}")
                                    }
                                },
                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.72f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val isApproved = profile?.isProfileApproved == true
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.2f)) {
                                    Text(if (isApproved) "✅ Profile Approved" else "⏳ Pending Review",
                                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }

                    // Status badge
                    val isActive = profile?.isActive ?: false
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isActive) VendorAccent else NoorTextHint)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                                .background(Color(0xFF90EE90)))
                            Text(if (isActive) "Active" else "Inactive", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                color = Color.White)
                        }
                    }
                }

                // Stats row
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    VendorStatColumn("👥", profile?.serviceScale ?: "—", "Workforce")
                    HorizontalDivider(color = NoorDivider, modifier = Modifier.width(1.dp).height(40.dp))
                    VendorStatColumn("📍", profile?.city ?: "—", "Head Office")
                    HorizontalDivider(color = NoorDivider, modifier = Modifier.width(1.dp).height(40.dp))
                    VendorStatColumn("⏱️", "${profile?.yearsInBusiness ?: 0} yrs", "In Business")
                }
            }
        }

        // ── Profile Approval Status Banner ────────────────────────────────────
        if (profile != null && !profile.isProfileApproved) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorOrangeLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically) {
                    Text("⏳", fontSize = 18.sp)
                    Column {
                        Text("Profile Under Review", fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text("Our admin will review and activate your profile soon.",
                            fontSize = 11.sp, color = NoorTextSecondary)
                    }
                }
            }
        }

        // ── Profile Blocked Banner ───────────────────────────────────────────
        if (profile != null && profile.isProfileApproved && !profile.isActive) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(12.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorRedLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment     = Alignment.CenterVertically) {
                    Text("🚫", fontSize = 18.sp)
                    Column {
                        Text("Profile Blocked", fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text("Your profile has been blocked by admin. Contact support.",
                            fontSize = 11.sp, color = NoorTextSecondary)
                    }
                }
            }
        }

        // ── About ─────────────────────────────────────────────────────────
        if (!profile?.bio.isNullOrBlank()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📝 About Us", fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = VendorTeal)
                    Text(profile!!.bio, fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp)
                }
            }
        }

        // ── Active Services ───────────────────────────────────────────────
        if (services.isNotEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🛠️ Active Service Catalog", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)

                    services.forEach { svc ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (svc.isActive) VendorTealLight else NoorBackground),
                                contentAlignment = Alignment.Center
                            ) { Text(viewModel.getServiceEmoji(svc.serviceId), fontSize = 18.sp) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(viewModel.getServiceName(svc.serviceId), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = NoorTextPrimary)
                                if (svc.priceRange.isNotBlank()) {
                                    Text(svc.priceRange, fontSize = 11.sp, color = NoorTextHint)
                                }
                                if (svc.pricingModel.isNotBlank()) {
                                    Text(svc.pricingModel, fontSize = 10.sp, color = VendorTeal)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (svc.isActive) VendorTealLight else NoorBackground)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(if (svc.isActive) "Active" else "Paused", fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold, color = if (svc.isActive) VendorTeal else NoorTextHint)
                            }
                        }

                        if (svc.skills.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            Text("Skills:", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                svc.skills.take(4).forEach { skill ->
                                    Surface(shape = RoundedCornerShape(6.dp), color = VendorTealLight,
                                        modifier = Modifier.wrapContentSize()) {
                                        Text(skill, fontSize = 9.sp, fontWeight = FontWeight.Medium,
                                            color = VendorTeal, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }

                        if (svc.minContractDuration.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text("Min Contract: ${svc.minContractDuration}", fontSize = 10.sp, color = NoorTextSecondary)
                        }

                        if (svc.coverageAreas.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text("Coverage: ${svc.coverageAreas.take(3).joinToString(", ")}${if (svc.coverageAreas.size > 3) " +${svc.coverageAreas.size - 3}" else ""}", fontSize = 10.sp, color = NoorTextSecondary)
                        }

                        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    }
                }
            }
        }

        // ── Notable Clients ───────────────────────────────────────────────
        if (!profile?.notableClients.isNullOrEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🤝 Notable Clients", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)) {
                        profile!!.notableClients.forEach { client ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorBackground)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(client, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                    color = NoorTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // ── Cities covered ───────────────────────────────────────────────
        if (!profile?.operatingCities.isNullOrEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📍 Cities We Operate In", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)) {
                        profile!!.operatingCities.forEach { c ->
                            Surface(shape = RoundedCornerShape(8.dp), color = VendorTealLight) {
                                Text(c, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = VendorTeal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── How It Works ──────────────────────────────────────────────────
        Card(
            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = VendorTealLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top) {
                Text("ℹ️", fontSize = 16.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("How Vendor Matching Works", fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, color = VendorTealDark)
                    Text(
                        "Employers post requirements through Noor Services admin. " +
                                "When a match is found, the admin contacts you with full job details and the employer's budget.",
                        fontSize = 12.sp, color = VendorTealDark, lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorStatColumn(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
        Text(label, fontSize = 10.sp, color = NoorTextHint)
    }
}

@Composable
private fun VendorQuickStatCard(
    emoji: String, value: String, label: String,
    color: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 9.sp, color = NoorTextHint,
                fontWeight = FontWeight.Medium, lineHeight = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}