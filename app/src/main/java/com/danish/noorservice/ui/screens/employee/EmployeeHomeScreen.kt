package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.danish.noorservice.R
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.ui.components.HomeScreenShimmer
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeHomeViewModel

@Composable
fun EmployeeHomeScreen(
    userId: String,
    viewModel: EmployeeHomeViewModel,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
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
                                .graphicsLayer {
                                    scaleX = 3f; scaleY = 3f
                                    translationY = 39f; translationX = 4f
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Noor Services", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.2).sp)
                        Text("Provider (Pvt.) Ltd.", fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                    }
                }

                Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { viewModel.loadProfile(userId, forceRefresh = true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint     = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
            }
        }

        // ── Loading / Error / Content ─────────────────────────────────────────
        when {
            uiState.isLoading -> {
                HomeScreenShimmer()
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
                            colors  = ButtonDefaults.buttonColors(containerColor = NoorBlue)
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
                EmployeeHomeContent(
                    modifier  = Modifier.weight(1f),
                    profile   = uiState.profile,
                    services  = uiState.services,
                    onNavigateToNotifications = onNavigateToNotifications
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main content — populated from Firestore data
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployeeHomeContent(
    modifier: Modifier = Modifier,
    profile: Employee?,
    services: List<EmployeeService>,
    onNavigateToNotifications: () -> Unit
) {
    val initials = profile?.fullName
        ?.split(" ")
        ?.filter { it.isNotBlank() }
        ?.take(2)
        ?.joinToString("") { it.first().uppercaseChar().toString() }
        ?: "?"

    val firstService = services.firstOrNull()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        // ── Profile Card ──────────────────────────────────────────────────────
        Card(
            modifier  = Modifier.padding(16.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profile?.photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model              = "${profile!!.photoUrl}?t=${profile!!.lastUpdated}",
                                    contentDescription = "Profile photo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.size(58.dp).clip(CircleShape)
                                )
                            } else {
                                Text(initials, fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile?.fullName ?: "—", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                buildString {
                                    if (!profile?.city.isNullOrBlank()) append(profile!!.city)
                                },
                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.72f)
                            )
                            if (!firstService?.dailyRate.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape    = RoundedCornerShape(8.dp),
                                    color    = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    Text("💰 PKR ${firstService!!.dailyRate}/day",
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        color    = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }

                    val isAvailable = profile?.isAvailable ?: false
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isAvailable) NoorGreen else NoorTextHint)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                                .background(if (isAvailable) Color(0xFF90EE90) else Color.LightGray))
                            Text(if (isAvailable) "Available" else "Unavailable",
                                fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Row(
                    modifier              = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🕐", fontSize = 18.sp)
                        Text(firstService?.availabilityTime ?: "—",
                            fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                        Text("Time Slot", fontSize = 10.sp, color = NoorTextHint)
                    }
                    HorizontalDivider(color = NoorDivider,
                        modifier = Modifier.width(1.dp).height(40.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏱️", fontSize = 18.sp)
                        Text(firstService?.experience ?: "—",
                            fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                        Text("Experience", fontSize = 10.sp, color = NoorTextHint)
                    }
                }
            }
        }

        // ── Available Days ───────────────────────────────────────────────────────
        if (!firstService?.availabilityDays.isNullOrEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📅 My Availability", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        firstService!!.availabilityDays.forEach { day ->
                            Surface(shape = RoundedCornerShape(8.dp), color = NoorGreenLight,
                                modifier = Modifier.weight(1f)) {
                                Text(day, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = NoorGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }

        // ── My Pricing ───────────────────────────────────────────────────────────
        val hasPricing = !profile?.dailyRate.isNullOrBlank() ||
                !profile?.hourlyRate.isNullOrBlank() ||
                !profile?.monthlyRate.isNullOrBlank()
        if (hasPricing) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💰 My Pricing (PKR)", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!profile?.dailyRate.isNullOrBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📅", fontSize = 18.sp)
                                Text("PKR ${profile!!.dailyRate}", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                Text("/ day", fontSize = 10.sp, color = NoorTextHint)
                            }
                        }
                        if (!profile?.hourlyRate.isNullOrBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⏱️", fontSize = 18.sp)
                                Text("PKR ${profile!!.hourlyRate}", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                Text("/ hr", fontSize = 10.sp, color = NoorTextHint)
                            }
                        }
                        if (!profile?.monthlyRate.isNullOrBlank()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🗓️", fontSize = 18.sp)
                                Text("PKR ${profile!!.monthlyRate}", fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                                Text("/ mo", fontSize = 10.sp, color = NoorTextHint)
                            }
                        }
                    }
                }
            }
        }

        // ── Profile Approval Status Banner ────────────────────────────────────
        if (profile != null && !profile.isProfileApproved) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
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
                        Text("Admin will approve your profile soon. You'll be notified.",
                            fontSize = 11.sp, color = NoorTextSecondary)
                    }
                }
            }
        }

        // ── Languages ─────────────────────────────────────────────────────────
        if (!profile?.languages.isNullOrEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🗣️ Languages", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)



                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        profile!!.languages.forEach { lang ->
                            Surface(shape = RoundedCornerShape(8.dp), color = NoorGreenLight,
                                modifier = Modifier.wrapContentSize()) {
                                Text(lang, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = NoorGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                            }
                        }
                    }
                }
            }
        }

        // ── My Services & Skills ──────────────────────────────────────────────
        if (services.isNotEmpty()) {
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🛠️ My Services", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        services.forEach { svc ->
                            Surface(shape = RoundedCornerShape(10.dp), color = NoorBlueLight,
                                modifier = Modifier.wrapContentSize()) {
                                Text(svc.serviceId.replaceFirstChar { it.uppercaseChar() },
                                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                    color    = NoorBlue,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }

            val allSkills = services.flatMap { it.skills }.distinct()
            if (allSkills.isNotEmpty()) {
                Card(
                    modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("🔧 Skills", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorBlue)
                        Row(

                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            allSkills.forEach { skill ->
                                Surface(shape = RoundedCornerShape(8.dp), color = NoorBackground,
                                    border   = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                                    modifier = Modifier.wrapContentSize()) {
                                    Text(skill, fontSize = 11.sp, color = NoorTextSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── How it works ──────────────────────────────────────────────────────
        Card(
            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorBlueLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.Top) {
                Text("ℹ️", fontSize = 16.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("How it works", fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, color = NoorBlueDark)
                    Text(
                        "Keep your profile updated. Employers browse profiles through Noor Services admin. " +
                                "If an employer is interested, the admin will contact you directly.",
                        fontSize = 12.sp, color = NoorBlueDark, lineHeight = 18.sp
                    )
                }
            }
        }

        // ── Recent Updates ────────────────────────────────────────────────────
        Card(
            modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically) {
                    Text("🔔 Recent Updates", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    TextButton(onClick = onNavigateToNotifications,
                        contentPadding = PaddingValues(0.dp)) {
                        Text("View all", fontSize = 11.sp, color = NoorBlue,
                            fontWeight = FontWeight.SemiBold)
                    }
                }

                val isApproved = profile?.isProfileApproved ?: false
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(if (isApproved) NoorGreenLight else NoorOrangeLight),
                        contentAlignment = Alignment.Center
                    ) { Text(if (isApproved) "✅" else "⏳", fontSize = 16.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isApproved) "Profile Approved" else "Profile Under Review",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            color    = NoorTextPrimary)
                        Text(
                            if (isApproved) "Your profile has been verified by admin"
                            else "Your profile is being reviewed by admin",
                            fontSize = 11.sp, color = NoorTextHint
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}