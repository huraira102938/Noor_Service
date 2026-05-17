package com.danish.noorservice.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material.icons.filled.*
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
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.screens.employer.AdminProposalStatus
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.danish.noorservice.ui.screens.employer.AdminProposalStore
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.ui.screens.employer.WorkerProfile
import com.danish.noorservice.ui.screens.employer.sampleWorkers
import com.danish.noorservice.ui.components.rememberShimmerBrush
import com.danish.noorservice.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Worker approval status
// ─────────────────────────────────────────────────────────────────────────────

enum class WorkerApprovalStatus { PENDING, APPROVED, SUSPENDED }

data class AdminWorkerEntry(
    val profile: WorkerProfile,
    var approvalStatus: WorkerApprovalStatus = WorkerApprovalStatus.APPROVED,
    val categories: List<Category> = emptyList()
)

private val adminWorkerEntries = mutableStateListOf(
    *sampleWorkers.mapIndexed { i, w ->
        AdminWorkerEntry(w, if (i < 4) WorkerApprovalStatus.APPROVED else WorkerApprovalStatus.PENDING)
    }.toTypedArray()
)

// ─────────────────────────────────────────────────────────────────────────────
// Employer record
// ─────────────────────────────────────────────────────────────────────────────

data class AdminEmployerRecord(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val photoUrl: String = "",
    val city: String,
    val area: String,
    val email: String,
    val phone: String,
    val address: String,
    val joinedDate: String,
    var isVerified: Boolean,
    val bio: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Vendor record
// ─────────────────────────────────────────────────────────────────────────────

enum class VendorVerificationStatus { PENDING, VERIFIED, REJECTED }

data class AdminVendorServiceDetail(
    val categoryLabel: String,
    val emoji: String,
    val pricingModel: String,
    val priceRange: String,
    val minContract: String,
    val coverageAreas: List<String>
)

data class AdminVendorRecord(
    val id: String,
    val businessName: String,
    val contactPerson: String,
    val avatarBg: Color,
    val logoUrl: String = "",
    val city: String,
    val serviceCount: Int,
    val joinedDate: String,
    var verificationStatus: VendorVerificationStatus,
    val email: String,
    val phone: String,
    val workforce: String,
    val ntn: String,
    val companyRegNo: String,
    val headOffice: String,
    val address: String,
    val citiesOfOperation: List<String>,
    val bio: String,
    val services: List<AdminVendorServiceDetail>,
    val isIsoCertified: Boolean,
    val hasNotableClients: Boolean,
    val notableClients: String,
    val categories: List<Category> = emptyList()
)


// ═════════════════════════════════════════════════════════════════════════════
// WORKERS SCREEN
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AdminWorkersScreen(
    viewModel: com.danish.noorservice.viewmodel.admin.AdminManagementViewModel? = null
) {
    val managementViewModel = viewModel ?: androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by managementViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        managementViewModel.loadAllData()
    }

    var query          by remember { mutableStateOf("") }
    var filterStatus   by remember { mutableStateOf<WorkerApprovalStatus?>(null) }
    var selectedWorker by remember { mutableStateOf<AdminWorkerEntry?>(null) }

    val workerEntries = uiState.workers.map { employee ->
        AdminWorkerEntry(
            profile = WorkerProfile(
                id             = employee.uid,
                name           = employee.fullName,
                initials       = employee.fullName.split(" ")
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercase() }
                    .joinToString("")
                    .ifEmpty { "W" },
                avatarColor    = avatarColors[
                    kotlin.math.abs(employee.uid.hashCode()) % avatarColors.size
                ],
                photoUrl       = employee.photoUrl ?: "",
                workerUsername = "@NS-${employee.uid.take(4).uppercase()}",
                city           = employee.city ?: "",
                area           = employee.address ?: "",
                phone          = employee.phone ?: "",
                cnic           = employee.cnic ?: "",
                dob            = employee.dob ?: "",
                address        = employee.address ?: "",
                gender         = employee.gender ?: "",
                serviceIds     = employee.serviceIds ?: emptyList(),
                skills         = emptyList(),
                experience     = "",
                licenceType    = "",
                availableDays  = emptyList(),
                timeSlot       = "9 AM - 6 PM",
                additionalNote = employee.bio ?: "",
                isAvailable    = employee.isAvailable ?: true,
                joinedDate     = "",
                dailyRate      = employee.dailyRate ?: "",
                hourlyRate     = employee.hourlyRate ?: "",
                monthlyRate    = employee.monthlyRate ?: "",
                bio            = employee.bio ?: "",
                languages      = employee.languages ?: emptyList()
            ),
            approvalStatus = when {
                employee.isProfileApproved && employee.isActive -> WorkerApprovalStatus.APPROVED
                employee.isProfileApproved && !employee.isActive -> WorkerApprovalStatus.SUSPENDED
                else -> WorkerApprovalStatus.PENDING
            },
            categories = uiState.workerCategories
        )
    }

    var pendingAction by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.isLoading, pendingAction) {
        if (pendingAction != null && !uiState.isLoading) {
            selectedWorker = null
            pendingAction = null
        }
    }

    if (selectedWorker != null) {
        AdminWorkerDetailScreen(
            entry               = selectedWorker!!,
            managementViewModel = managementViewModel,
            onBack              = { selectedWorker = null },
            onApprove           = { entry ->
                pendingAction = entry.profile.id
                managementViewModel.approveAndActivateUser(entry.profile.id, "employee")
            },
            onSuspend           = { entry ->
                pendingAction = entry.profile.id
                managementViewModel.updateUserActive(entry.profile.id, "employee", false)
            },
            onActivate          = { entry ->
                pendingAction = entry.profile.id
                managementViewModel.approveAndActivateUser(entry.profile.id, "employee")
            }
        )
        return
    }

    val filtered = workerEntries.filter { entry ->
        val matchQuery  = query.isBlank() ||
                entry.profile.name.contains(query, ignoreCase = true) ||
                entry.profile.workerUsername.contains(query, ignoreCase = true) ||
                entry.profile.city.contains(query, ignoreCase = true)
        val matchStatus = filterStatus == null || entry.approvalStatus == filterStatus
        matchQuery && matchStatus
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Text(
                    "Workers", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp
                )
                Text(
                    "${workerEntries.size} registered · ${workerEntries.count { it.approvalStatus == WorkerApprovalStatus.APPROVED }} approved",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(14.dp))
                if (uiState.isLoading || !uiState.hasLoaded) {
                    val brush = rememberShimmerBrush()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                } else {
                    OutlinedTextField(
                        value         = query,
                        onValueChange = { query = it },
                        placeholder   = {
                            Text("Search name, @ID, city…", fontSize = 13.sp, color = NoorTextHint)
                        },
                        leadingIcon   = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NoorTextHint)
                        },
                        modifier        = Modifier.fillMaxWidth(),
                        shape           = RoundedCornerShape(14.dp),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors          = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = NoorSurface,
                            unfocusedContainerColor = NoorSurface,
                            focusedBorderColor      = Color.Transparent,
                            unfocusedBorderColor    = Color.Transparent,
                            cursorColor             = AdminPurple
                        )
                    )
                }
            }
        }

        // ── Filter chips ──────────────────────────────────────────────────────
        LazyRow(
            modifier              = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { AdminFilterChip("All",         filterStatus == null)                            { filterStatus = null } }
            item { AdminFilterChip("✅ Approved",  filterStatus == WorkerApprovalStatus.APPROVED)  { filterStatus = WorkerApprovalStatus.APPROVED } }
            item { AdminFilterChip("⏳ Pending",   filterStatus == WorkerApprovalStatus.PENDING)   { filterStatus = WorkerApprovalStatus.PENDING } }
            item { AdminFilterChip("🚫 Suspended", filterStatus == WorkerApprovalStatus.SUSPENDED) { filterStatus = WorkerApprovalStatus.SUSPENDED } }
        }
        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // ── List ──────────────────────────────────────────────────────────────
        if (uiState.isLoading || !uiState.hasLoaded) {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(6) { AdminShimmerCard() }
            }
        } else if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No workers found", fontSize = 14.sp,
                        color = NoorTextHint, fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${filtered.size} worker${if (filtered.size != 1) "s" else ""} found",
                        fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold
                    )
                }
                items(filtered, key = { it.profile.id }) { entry ->
                    AdminWorkerCard(entry = entry, onClick = { selectedWorker = entry })
                }
            }
        }

    }
}
// ─────────────────────────────────────────────────────────────────────────────
// Worker Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminWorkerCard(
    entry: AdminWorkerEntry,
    onClick: () -> Unit
) {
    val (statusColor, statusBg, statusLabel) = when (entry.approvalStatus) {
        WorkerApprovalStatus.APPROVED  -> Triple(NoorGreen,  NoorGreenLight,  "Approved")
        WorkerApprovalStatus.PENDING   -> Triple(NoorOrange, NoorOrangeLight, "Pending")
        WorkerApprovalStatus.SUSPENDED -> Triple(NoorRed,    NoorRedLight,    "Suspended")
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Section with Avatar, Info, and Available Badge
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                if (entry.profile.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model         = entry.profile.photoUrl,
                        contentDescription = "Worker photo",
                        modifier      = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier           = Modifier.size(48.dp).clip(CircleShape).background(entry.profile.avatarColor),
                        contentAlignment   = Alignment.Center
                    ) {
                        Text(entry.profile.initials, fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }

                // Middle Content (Name, Status, Location, Services)
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(entry.profile.name, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(statusLabel, fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = statusColor)
                        }
                    }
                    Text(
                        "${entry.profile.workerUsername} · ${entry.profile.area}, ${entry.profile.city}",
                        fontSize = 11.sp, color = NoorTextHint
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        entry.profile.serviceIds.take(2).forEach { id ->
                            val svc = entry.categories.find { it.id == id }
                            if (svc != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NoorBlueLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${svc.emoji} ${svc.label}", fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                                }
                            }
                        }
                    }
                }

                // Available Badge - Top Right Corner
                if (entry.profile.isAvailable) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(NoorGreenLight)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Available", fontSize = 9.sp,
                            fontWeight = FontWeight.Bold, color = NoorGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Section - Price and Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price with "per day" text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        entry.profile.dailyRate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NoorBlue
                    )
                    Text(
                        "per day",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = NoorTextHint
                    )
                }

                // Click to see full details
                Text(
                    "Click to see full details",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = NoorTextHint,
                    style = androidx.compose.ui.text.TextStyle(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                )
            }
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Worker Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminWorkerDetailScreen(
    entry: AdminWorkerEntry,
    managementViewModel: com.danish.noorservice.viewmodel.admin.AdminManagementViewModel,
    onBack: () -> Unit,
    onApprove: (AdminWorkerEntry) -> Unit,
    onSuspend: (AdminWorkerEntry) -> Unit,
    onActivate: (AdminWorkerEntry) -> Unit
) {
    var showSuspendDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    val p = entry.profile
    val employeeServices by managementViewModel.employeeServices.collectAsState()
    val services = employeeServices[entry.profile.id] ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (p.photoUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable { selectedImageUrl = p.photoUrl }
                        ) {
                            AsyncImage(
                                model = p.photoUrl,
                                contentDescription = "Worker photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Box(
                            modifier         = Modifier.size(64.dp).clip(CircleShape).background(p.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.initials, fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${p.workerUsername} · ${p.city}",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        Spacer(Modifier.height(6.dp))
                        val statusLabel = when (entry.approvalStatus) {
                            WorkerApprovalStatus.APPROVED  -> "✅ Approved"
                            WorkerApprovalStatus.PENDING   -> "⏳ Pending Review"
                            WorkerApprovalStatus.SUSPENDED -> "🚫 Suspended"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(statusLabel, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } // end header Box

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Quick stats
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminInfoTile("💰", "Daily Rate", p.dailyRate,  modifier = Modifier.weight(1f))
                AdminInfoTile("🕐", "Time Slot",  p.timeSlot,   modifier = Modifier.weight(1f))
            }

            // Personal Details
            AdminDetailSection("Personal Details") {
                AdminDetailRow("🪪", "CNIC",              p.cnic.ifBlank { "Not provided" })
                AdminDetailRow("📞", "Phone Number",      p.phone.ifBlank { "Not provided" })
                AdminDetailRow("🎂", "Date of Birth",     p.dob.ifBlank { "Not provided" })
                AdminDetailRow("📍", "City",              p.city)
                AdminDetailRow("🏠", "Permanent Address", p.address.ifBlank { "Not provided" })
            }

            // Languages
            AdminDetailSection("Languages Spoken") {
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    p.languages.forEach { lang ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NoorBlueLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("🗣 $lang", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorBlue)
                        }
                    }
                }
            }

            // Services Offered
            AdminDetailSection("Services Offered") {
                if (services.isEmpty()) {
                    Text("No services added yet.", fontSize = 12.sp, color = NoorTextHint)
                } else {
                    services.forEachIndexed { index, service ->
                        if (index > 0) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                            Spacer(Modifier.height(8.dp))
                        }
                        val category = entry.categories.find { it.id == service.serviceId }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NoorBlueLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(category?.emoji ?: "🛠", fontSize = 16.sp)
                            }
                            Text(category?.label ?: "Service", fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        }
                        Spacer(Modifier.height(6.dp))
                        if (service.skills.isNotEmpty()) {
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                service.skills.forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NoorBackground)
                                            .border(1.dp, NoorBorder, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(skill, fontSize = 10.sp, color = NoorTextSecondary)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        AdminDetailRow("⏱", "Experience", service.experience.ifBlank { "Not specified" })
                        AdminDetailRow("📅", "Availability", service.availabilityTime.ifBlank { "Not specified" })
                        AdminDetailRow("💰", "Daily Rate", service.dailyRate.ifBlank { "Not set" })
                        if (service.additionalNote.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text("📝 Note: ${service.additionalNote}", fontSize = 11.sp, color = NoorTextHint)
                        }
                    }
                }
            }

            // Skills
            if (p.skills.isNotEmpty()) {
                AdminDetailSection("Skills") {
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)
                    ) {
                        p.skills.forEach { skill ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NoorBackground)
                                    .border(1.dp, NoorBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(skill, fontSize = 11.sp,
                                    color = NoorTextSecondary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Work Profile
            AdminDetailSection("Work Profile") {
                AdminDetailRow("⏱", "Experience",        p.experience)
                AdminDetailRow("🪪", "Licence / Cert.",  p.licenceType.ifBlank { "N/A" })
                AdminDetailRow("📅", "Availability",     if (p.isAvailable) "Available" else "Not Available")
                AdminDetailRow("🕐", "Preferred Time Slot", p.timeSlot)
                if (p.additionalNote.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text("📝 Additional Notes", fontSize = 10.sp,
                        color = NoorTextHint, fontWeight = FontWeight.SemiBold, letterSpacing = 0.4.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(p.additionalNote, fontSize = 12.sp,
                        color = NoorTextSecondary, lineHeight = 18.sp)
                }
            }

            // Rates
            AdminDetailSection("Rates (PKR)") {
                AdminDetailRow("📅", "Daily Rate",   p.dailyRate.ifBlank { "N/A" })
                AdminDetailRow("⏱️", "Hourly Rate",  p.hourlyRate.ifBlank { "N/A" })
                AdminDetailRow("🗓️", "Monthly Rate", p.monthlyRate.ifBlank { "N/A" })
            }

            // About
            AdminDetailSection("About") {
                Text(p.bio, fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp)
            }

            // Admin Actions
            when (entry.approvalStatus) {
                WorkerApprovalStatus.PENDING -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick  = { onApprove(entry) },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Approve", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick  = { showSuspendDialog = true },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Reject", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                WorkerApprovalStatus.APPROVED -> {
                    OutlinedButton(
                        onClick  = { showSuspendDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Suspend Worker", fontWeight = FontWeight.SemiBold)
                    }
                }
                WorkerApprovalStatus.SUSPENDED -> {
                    Button(
                        onClick  = { onActivate(entry) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Activate Worker", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        } // end scrollable body
    } // end outer Column

    // Suspend / Reject dialog
    if (showSuspendDialog) {
        AlertDialog(
            onDismissRequest = { showSuspendDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (entry.approvalStatus == WorkerApprovalStatus.PENDING) "Reject Worker?" else "Suspend Worker?",
                    fontWeight = FontWeight.Bold, color = NoorRed
                )
            },
            text = {
                Text(
                    "${p.name} will be ${if (entry.approvalStatus == WorkerApprovalStatus.PENDING) "rejected" else "suspended"} and removed from employer search results.",
                    fontSize = 13.sp, color = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showSuspendDialog = false; onSuspend(entry) }) {
                    Text(
                        if (entry.approvalStatus == WorkerApprovalStatus.PENDING) "Reject" else "Suspend",
                        color = NoorRed, fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }

    if (selectedImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = selectedImageUrl!!,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// EMPLOYERS SCREEN
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AdminEmployersScreen(
    viewModel: com.danish.noorservice.viewmodel.admin.AdminManagementViewModel? = null
) {
    val managementViewModel = viewModel ?: androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by managementViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        managementViewModel.loadAllData()
    }

    var query            by remember { mutableStateOf("") }
    var selectedEmployer by remember { mutableStateOf<AdminEmployerRecord?>(null) }

    val employerRecords = uiState.employers.map { employer ->
        AdminEmployerRecord(
            id          = employer.uid,
            name        = employer.fullName.ifBlank { "Unknown" },
            initials    = employer.fullName
                .split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "?" },
            avatarColor = avatarColors[
                kotlin.math.abs(employer.uid.hashCode()) % avatarColors.size
            ],
            photoUrl    = employer.photoUrl ?: "",
            city        = employer.city,
            area        = employer.area,
            email       = employer.email,
            phone       = employer.phone,
            address     = employer.address,
            joinedDate  = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                .format(Date(employer.createdAt)),
            isVerified  = true,
            bio         = employer.about
        )
    }

    if (selectedEmployer != null) {
        AdminEmployerDetailScreen(
            record = selectedEmployer!!,
            onBack = { selectedEmployer = null }
        )
        return
    }

    val filtered = employerRecords.filter { e ->
        query.isBlank() ||
                e.name.contains(query, ignoreCase = true) ||
                e.city.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Text(
                    "Employers", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp
                )
                Text(
                    "${employerRecords.size} registered",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(14.dp))
                if (uiState.isLoading || !uiState.hasLoaded) {
                    val brush = rememberShimmerBrush()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                } else {
                    OutlinedTextField(
                        value         = query,
                        onValueChange = { query = it },
                        placeholder   = {
                            Text("Search by name or city…", fontSize = 13.sp, color = NoorTextHint)
                        },
                        leadingIcon   = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NoorTextHint)
                        },
                        modifier  = Modifier.fillMaxWidth(),
                        shape     = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors    = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = NoorSurface,
                            unfocusedContainerColor = NoorSurface,
                            focusedBorderColor      = Color.Transparent,
                            unfocusedBorderColor    = Color.Transparent,
                            cursorColor             = AdminPurple
                        )
                    )
                }
            }
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // ── List ──────────────────────────────────────────────────────────────
        if (uiState.isLoading || !uiState.hasLoaded) {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(6) { AdminShimmerCard() }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${filtered.size} employer${if (filtered.size != 1) "s" else ""} found",
                        fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold
                    )
                }
                items(filtered, key = { it.id }) { record ->
                    AdminEmployerCard(record = record, onClick = { selectedEmployer = record })
                }
            }
        }

    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Employer Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminEmployerCard(record: AdminEmployerRecord, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar — photo or initials fallback
            if (record.photoUrl.isNotBlank()) {
                AsyncImage(
                    model              = record.photoUrl,
                    contentDescription = "Employer photo",
                    modifier           = Modifier.size(48.dp).clip(CircleShape),
                    contentScale       = ContentScale.Crop
                )
            } else {
                Box(
                    modifier         = Modifier.size(48.dp).clip(CircleShape).background(record.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(record.initials, fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(record.name, fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                    if (record.isVerified) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NoorGreenLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("✅ Verified", fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = NoorGreen)
                        }
                    }
                }
                Text("${record.area}, ${record.city}", fontSize = 11.sp, color = NoorTextHint)
                Text(record.email, fontSize = 10.sp, color = NoorTextHint,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AdminPurpleLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("📋 proposals", fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold, color = AdminPurple)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("🗓 ${record.joinedDate}", fontSize = 9.sp, color = NoorTextSecondary)
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = NoorTextHint, modifier = Modifier.size(18.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Employer Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminEmployerDetailScreen(
    record: AdminEmployerRecord,
    onBack: () -> Unit
) {
    val employerProposals = AdminProposalStore.proposals.filter { true }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Avatar — photo with tap-to-expand, or initials fallback
                    if (record.photoUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable { selectedImageUrl = record.photoUrl }
                        ) {
                            AsyncImage(
                                model              = record.photoUrl,
                                contentDescription = "Employer photo",
                                modifier           = Modifier.fillMaxSize(),
                                contentScale       = ContentScale.Crop
                            )
                        }
                    } else {
                        Box(
                            modifier         = Modifier.size(64.dp).clip(CircleShape).background(record.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(record.initials, fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.name, fontSize = 19.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${record.area}, ${record.city}",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                if (record.isVerified) "✅ Verified Employer" else "⏳ Unverified",
                                fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Contact & Location
            AdminDetailSection("Contact & Location") {
                AdminDetailRow("📧", "Email",    record.email.ifBlank { "Not provided" })
                AdminDetailRow("📞", "Phone Number", record.phone.ifBlank { "Not provided" })
                AdminDetailRow("📍", "City",     record.city.ifBlank { "Not provided" })
                AdminDetailRow("🏘", "Area",     record.area.ifBlank { "Not provided" })
                AdminDetailRow("🏠", "Address",  record.address.ifBlank { "Not provided" })
            }

            // About
            if (record.bio.isNotBlank()) {
                AdminDetailSection("About") {
                    Text(record.bio, fontSize = 13.sp,
                        color = NoorTextSecondary, lineHeight = 20.sp)
                }
            }

            // Account Info
            AdminDetailSection("Account Info") {
                AdminDetailRow("🗓", "Member Since", record.joinedDate.ifBlank { "Not provided" })
                AdminDetailRow("🏷", "User ID",      record.id.take(8))
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // Full-screen image viewer
    if (selectedImageUrl != null) {
        FullScreenImageViewer(
            imageUrl  = selectedImageUrl!!,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// VENDORS SCREEN
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AdminVendorsScreen(
    viewModel: com.danish.noorservice.viewmodel.admin.AdminManagementViewModel? = null
) {
    val managementViewModel = viewModel ?: androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by managementViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        managementViewModel.loadAllData()
    }

    var query          by remember { mutableStateOf("") }
    var filterStatus   by remember { mutableStateOf<VendorVerificationStatus?>(null) }
    var selectedVendor by remember { mutableStateOf<AdminVendorRecord?>(null) }

    val vendorServices by managementViewModel.vendorServices.collectAsState()

    val vendorRecords = uiState.vendors.map { vendor ->
        val services = vendorServices[vendor.uid] ?: emptyList()
        val adminServices = services.map { svc ->
            val category = uiState.vendorCategories.find { it.id == svc.serviceId }
            val displayName = category?.label ?: svc.serviceId.replace("_", " ").replaceFirstChar { it.uppercase() }
            AdminVendorServiceDetail(
                categoryLabel = displayName,
                emoji         = category?.emoji ?: "🛠",
                pricingModel  = svc.pricingModel,
                priceRange    = svc.priceRange,
                minContract   = svc.minContractDuration,
                coverageAreas = svc.coverageAreas
            )
        }
        AdminVendorRecord(
            id                 = vendor.uid,
            businessName       = vendor.businessName.ifBlank { "Unknown" },
            contactPerson      = vendor.contactPerson,
            avatarBg           = VendorTeal,
            logoUrl            = vendor.logoUrl ?: "",
            city               = vendor.city,
            serviceCount       = services.size,
            joinedDate         = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                .format(Date(vendor.createdAt)),
            verificationStatus = when {
                !vendor.isActive          -> VendorVerificationStatus.REJECTED
                vendor.isProfileApproved  -> VendorVerificationStatus.VERIFIED
                else                      -> VendorVerificationStatus.PENDING
            },
            email              = vendor.email,
            phone              = vendor.phone,
            workforce          = vendor.serviceScale,
            ntn                = vendor.ntn,
            companyRegNo       = vendor.regNumber,
            headOffice         = vendor.headOffice,
            address            = vendor.address,
            citiesOfOperation  = vendor.operatingCities,
            bio                = vendor.bio,
            services           = adminServices,
            isIsoCertified     = vendor.isoCertified,
            hasNotableClients  = vendor.notableClients.isNotEmpty(),
            notableClients     = vendor.notableClients.joinToString(", "),
            categories         = uiState.vendorCategories
        )
    }

    if (selectedVendor != null) {
        val vendor = uiState.vendors.find { it.uid == selectedVendor!!.id }
        AdminVendorDetailScreen(
            record              = selectedVendor!!,
            managementViewModel = managementViewModel,
            vendorId            = selectedVendor!!.id,
            isActive            = vendor?.isActive ?: true,
            onBack              = { selectedVendor = null },
            onVerify            = { r ->
                managementViewModel.approveAndActivateUser(r.id, "vendor")
                selectedVendor = null
            },
            onReject            = { r ->
                managementViewModel.updateProfileApproval(r.id, "vendor", false)
                selectedVendor = null
            },
            onSuspend           = { r ->
                managementViewModel.updateUserActive(r.id, "vendor", false)
                selectedVendor = null
            },
            onActivate          = { r ->
                managementViewModel.approveAndActivateUser(r.id, "vendor")
                selectedVendor = null
            }
        )
        return
    }

    val filtered = vendorRecords.filter { v ->
        val matchQuery  = query.isBlank() ||
                v.businessName.contains(query, ignoreCase = true) ||
                v.city.contains(query, ignoreCase = true)
        val matchStatus = filterStatus == null || v.verificationStatus == filterStatus
        matchQuery && matchStatus
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Text("Vendors", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text(
                    "${vendorRecords.size} registered · ${vendorRecords.count { it.verificationStatus == VendorVerificationStatus.VERIFIED }} verified",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(14.dp))
                if (uiState.isLoading || !uiState.hasLoaded) {
                    val brush = rememberShimmerBrush()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                    )
                } else {
                    OutlinedTextField(
                        value         = query,
                        onValueChange = { query = it },
                        placeholder   = {
                            Text("Search vendor name or city…", fontSize = 13.sp, color = NoorTextHint)
                        },
                        leadingIcon   = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NoorTextHint)
                        },
                        modifier   = Modifier.fillMaxWidth(),
                        shape      = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors     = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = NoorSurface,
                            unfocusedContainerColor = NoorSurface,
                            focusedBorderColor      = Color.Transparent,
                            unfocusedBorderColor    = Color.Transparent,
                            cursorColor             = AdminPurple
                        )
                    )
                }
            }
        }

        // ── Filter chips ──────────────────────────────────────────────────────
        LazyRow(
            modifier              = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { AdminFilterChip("All",         filterStatus == null)                               { filterStatus = null } }
            item { AdminFilterChip("✅ Verified",  filterStatus == VendorVerificationStatus.VERIFIED) { filterStatus = VendorVerificationStatus.VERIFIED } }
            item { AdminFilterChip("⏳ Pending",   filterStatus == VendorVerificationStatus.PENDING)  { filterStatus = VendorVerificationStatus.PENDING } }
            item { AdminFilterChip("❌ Rejected",  filterStatus == VendorVerificationStatus.REJECTED) { filterStatus = VendorVerificationStatus.REJECTED } }
        }
        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // ── List ──────────────────────────────────────────────────────────────
        if (uiState.isLoading || !uiState.hasLoaded) {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(6) { AdminShimmerCard() }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${filtered.size} vendor${if (filtered.size != 1) "s" else ""} found",
                        fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold
                    )
                }
                items(filtered, key = { it.id }) { record ->
                    AdminVendorCard(record = record, onClick = { selectedVendor = record })
                }
            }
        }

    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminVendorCard(
    record: AdminVendorRecord,
    onClick: () -> Unit
) {
    val (sc, sb, sl) = when (record.verificationStatus) {
        VendorVerificationStatus.VERIFIED -> Triple(VendorTeal,  VendorTealLight,  "Verified")
        VendorVerificationStatus.PENDING  -> Triple(NoorOrange,  NoorOrangeLight,  "Pending")
        VendorVerificationStatus.REJECTED -> Triple(NoorRed,     NoorRedLight,     "Rejected")
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (record.logoUrl.isNotBlank()) {
                AsyncImage(
                    model         = record.logoUrl,
                    contentDescription = "Vendor logo",
                    modifier      = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier         = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(record.avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏢", fontSize = 22.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(record.businessName, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = NoorTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(sb)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(sl, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = sc)
                    }
                }
                Text("${record.contactPerson} · ${record.city}",
                    fontSize = 11.sp, color = NoorTextHint)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VendorTealLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("🛠️ ${record.serviceCount} services", fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold, color = VendorTeal)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("👥 ${record.workforce}", fontSize = 9.sp, color = NoorTextSecondary)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminVendorDetailScreen(
    record: AdminVendorRecord,
    managementViewModel: com.danish.noorservice.viewmodel.admin.AdminManagementViewModel,
    vendorId: String,
    isActive: Boolean,
    onBack: () -> Unit,
    onVerify: (AdminVendorRecord) -> Unit,
    onReject: (AdminVendorRecord) -> Unit,
    onSuspend: (AdminVendorRecord) -> Unit,
    onActivate: (AdminVendorRecord) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var showSuspendDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    val vendorServicesFromVM by managementViewModel.vendorServices.collectAsState()
    val vendorServices = vendorServicesFromVM[vendorId] ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier         = Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (record.logoUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedImageUrl = record.logoUrl }
                        ) {
                            AsyncImage(
                                model = record.logoUrl,
                                contentDescription = "Vendor logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Box(
                            modifier         = Modifier.size(60.dp).clip(RoundedCornerShape(14.dp)).background(record.avatarBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 26.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.businessName, fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${record.contactPerson} · ${record.city}",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                        Spacer(Modifier.height(6.dp))
                        val statusLabel = when {
                            !isActive -> "🚫 Suspended"
                            record.verificationStatus == VendorVerificationStatus.VERIFIED -> "✅ Verified"
                            record.verificationStatus == VendorVerificationStatus.PENDING  -> "⏳ Pending Review"
                            else -> "❌ Rejected"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(statusLabel, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } // end header Box

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier            = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Quick stats
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminInfoTile("🛠️", "Services",  "${record.serviceCount}", modifier = Modifier.weight(1f))
                AdminInfoTile("👥", "Workforce", record.workforce,           modifier = Modifier.weight(1f))
                AdminInfoTile("🗓", "Joined",    record.joinedDate,          modifier = Modifier.weight(1f))
            }

            // Business Identity
            AdminDetailSection("Business Identity") {
                AdminDetailRow("🏢", "Business Name",  record.businessName)
                AdminDetailRow("👤", "Contact Person", record.contactPerson)
                AdminDetailRow("📞", "Phone Number",   record.phone)
                AdminDetailRow("📧", "Business Email", record.email)
            }

            // Legal & Registration
            AdminDetailSection("Legal & Registration") {
                AdminDetailRow("📄", "NTN Number",       record.ntn.ifBlank { "Not provided" })
                AdminDetailRow("🗂", "Company Reg. No.", record.companyRegNo.ifBlank { "Not provided" })
                AdminDetailRow("🗓", "Joined Platform",  record.joinedDate.ifBlank { "Not provided" })
            }

            // Location
            AdminDetailSection("Location") {
                AdminDetailRow("🏢", "Head Office", record.headOffice.ifBlank { "Not provided" })
                AdminDetailRow("📍", "Address",     record.address.ifBlank { "Not provided" })
                Spacer(Modifier.height(6.dp))
                Text("🗺 Cities of Operation", fontSize = 11.sp,
                    color = NoorTextHint, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    if (record.citiesOfOperation.isEmpty()) {
                        Text("Not specified", fontSize = 11.sp, color = NoorTextHint)
                    } else {
                        record.citiesOfOperation.forEach { cityName ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorBackground)
                                    .border(1.dp, NoorBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("📍 $cityName", fontSize = 11.sp, color = NoorTextSecondary)
                            }
                        }
                    }
                }
            }

            // About
            AdminDetailSection("About the Business") {
                Text(
                    record.bio.ifBlank { "No description provided." },
                    fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp
                )
            }

            // Services Offered
            if (vendorServices.isNotEmpty()) {
                AdminDetailSection("Services Offered") {
                    vendorServices.forEachIndexed { idx, svc ->
                        if (idx > 0) {
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                            Spacer(Modifier.height(10.dp))
                        }
                        val category = record.categories.find { it.id == svc.serviceId }
                        val displayName = category?.label ?: svc.serviceId.replace("_", " ").replaceFirstChar { it.uppercase() }
                        val displayEmoji = category?.emoji ?: "🛠"
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier         = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(VendorTealLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(displayEmoji, fontSize = 16.sp)
                            }
                            Text(displayName, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(VendorTealLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("💰 ${svc.pricingModel}", fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold, color = VendorTeal)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NoorBlueLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("📅 Min: ${svc.minContractDuration}", fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold, color = NoorBlue)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Price Range:", fontSize = 11.sp,
                                color = NoorTextHint, fontWeight = FontWeight.Medium)
                            Text(svc.priceRange, fontSize = 11.sp,
                                color = NoorTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        if (svc.description.isNotBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text("Description:", fontSize = 11.sp,
                                color = NoorTextHint, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            Text(svc.description, fontSize = 12.sp, color = NoorTextSecondary)
                        }
                        if (svc.highlights.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text("Highlights:", fontSize = 11.sp,
                                color = NoorTextHint, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement   = Arrangement.spacedBy(6.dp)
                            ) {
                                svc.highlights.forEach { highlight ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(VendorTealLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("✨ $highlight", fontSize = 10.sp, color = VendorTeal)
                                    }
                                }
                            }
                        }
                        if (svc.skills.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Text("Skills:", fontSize = 11.sp,
                                color = NoorTextHint, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(4.dp))
                            @OptIn(ExperimentalLayoutApi::class)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement   = Arrangement.spacedBy(6.dp)
                            ) {
                                svc.skills.forEach { skill ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(NoorOrangeLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("⚡ $skill", fontSize = 10.sp, color = NoorOrange)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("Coverage:", fontSize = 11.sp,
                            color = NoorTextHint, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement   = Arrangement.spacedBy(6.dp)
                        ) {
                            svc.coverageAreas.forEach { area ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(NoorBackground)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("📍 $area", fontSize = 10.sp, color = NoorTextSecondary)
                                }
                            }
                        }
                    }
                }
            } else {
                AdminDetailSection("Services Offered") {
                    Text("No services added yet.", fontSize = 12.sp, color = NoorTextHint)
                }
            }

            // Business Capacity
            AdminDetailSection("Business Capacity") {
                AdminDetailRow("👥", "Workforce Size", record.workforce.ifBlank { "Not provided" })
            }

            // Quality & Credentials
            AdminDetailSection("Quality & Credentials") {
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🏅", fontSize = 13.sp)
                        Text("ISO Quality Certified", fontSize = 11.sp,
                            color = NoorTextHint, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (record.isIsoCertified) NoorGreenLight else NoorBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (record.isIsoCertified) "✅ Yes" else "❌ No",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (record.isIsoCertified) NoorGreen else NoorTextHint
                        )
                    }
                }
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🌟", fontSize = 13.sp)
                        Text("Has Notable Clients", fontSize = 11.sp,
                            color = NoorTextHint, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (record.hasNotableClients) NoorGreenLight else NoorBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (record.hasNotableClients) "✅ Yes" else "❌ No",
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            color = if (record.hasNotableClients) NoorGreen else NoorTextHint
                        )
                    }
                }
                if (record.hasNotableClients && record.notableClients.isNotBlank()) {
                    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    Spacer(Modifier.height(6.dp))
                    Text("Notable Clients:", fontSize = 11.sp,
                        color = NoorTextHint, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    Text(record.notableClients, fontSize = 12.sp,
                        color = NoorTextSecondary, lineHeight = 18.sp,
                        fontWeight = FontWeight.SemiBold)
                }
            }

            // Admin Actions
            when {
                !isActive -> {
                    Button(
                        onClick  = { onActivate(record) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Activate Vendor", fontWeight = FontWeight.SemiBold)
                    }
                }
                record.verificationStatus == VendorVerificationStatus.PENDING -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick  = { onVerify(record) },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = VendorTeal)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Verify", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick  = { showRejectDialog = true },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Reject", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                record.verificationStatus == VendorVerificationStatus.VERIFIED -> {
                    OutlinedButton(
                        onClick  = { showSuspendDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Suspend Vendor", fontWeight = FontWeight.SemiBold)
                    }
                }
                record.verificationStatus == VendorVerificationStatus.REJECTED -> {
                    Button(
                        onClick  = { onVerify(record) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = VendorTeal)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Re-verify Vendor", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        } // end scrollable body
    } // end outer Column

    // Reject / Revoke dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    if (record.verificationStatus == VendorVerificationStatus.VERIFIED)
                        "Revoke Verification?" else "Reject Vendor?",
                    fontWeight = FontWeight.Bold, color = NoorRed
                )
            },
            text = {
                Text(
                    "${record.businessName} will be ${if (record.verificationStatus == VendorVerificationStatus.VERIFIED) "unverified" else "rejected"}.",
                    fontSize = 13.sp, color = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showRejectDialog = false; onReject(record) }) {
                    Text("Confirm", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }

    // Suspend dialog
    if (showSuspendDialog) {
        AlertDialog(
            onDismissRequest = { showSuspendDialog = false },
            shape            = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Suspend Vendor?",
                    fontWeight = FontWeight.Bold, color = NoorRed
                )
            },
            text = {
                Text(
                    "${record.businessName} will be suspended and removed from employer search results.",
                    fontSize = 13.sp, color = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showSuspendDialog = false; onSuspend(record) }) {
                    Text("Suspend", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuspendDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }

    if (selectedImageUrl != null) {
        FullScreenImageViewer(
            imageUrl = selectedImageUrl!!,
            onDismiss = { selectedImageUrl = null }
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// SHARED UI COMPONENTS
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AdminFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) AdminPurple else NoorBackground)
            .border(1.dp, if (selected) AdminPurple else NoorBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            label,
            fontSize   = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (selected) Color.White else NoorTextSecondary
        )
    }
}

@Composable
fun AdminInfoTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier              = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            Text(label, fontSize = 9.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AdminDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = AdminPurple, letterSpacing = 0.3.sp)
            content()
        }
    }
}

@Composable
fun AdminDetailRow(emoji: String, label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier              = Modifier.weight(0.4f)
        ) {
            Text(emoji, fontSize = 13.sp)
            Text(label, fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            color = NoorTextPrimary, modifier = Modifier.weight(0.6f))
    }
    HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
}

// Add this near the top of AdminManagementScreens.kt (before the enums)
private val avatarColors = listOf(
    Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800),
    Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFF00BCD4)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullScreenImageViewer(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Full screen image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Close, contentDescription = "Close",
                        tint = Color.White, modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun AdminShimmerCard() {
    val brush = rememberShimmerBrush()
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brush)
            )
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Name line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                // Subtitle line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(11.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(brush)
                )
                // Tag chips row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}