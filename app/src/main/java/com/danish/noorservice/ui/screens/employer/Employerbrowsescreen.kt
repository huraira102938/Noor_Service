package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.screens.employee.allServiceCategories
import com.danish.noorservice.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// Worker data model
// workerUsername is the unique ID used for admin tracking (e.g. @NS-1042)
// No direct contact info — all hiring goes through the admin
// ─────────────────────────────────────────────────────────────────────────────

data class WorkerProfile(
    val id: String,
    val name: String,
    val initials: String,
    val workerUsername: String,
    val serviceIds: List<String>,
    val city: String,
    val area: String,
    val experience: String,
    val timeSlot: String,
    val isAvailable: Boolean,
    val dailyRate: String,
    val avatarColor: Color,
    val skills: List<String>,
    val bio: String,
    val languages: List<String>,
    val joinedDate: String
)

val sampleWorkers = listOf(
    WorkerProfile(
        id = "1", name = "Muhammad Ali", initials = "MA", workerUsername = "@NS-1001",
        serviceIds = listOf("driver", "houseBoy"),
        city = "Lahore", area = "DHA Phase 3", experience = "5 yrs",
        timeSlot = "Full Day", isAvailable = true, dailyRate = "PKR 1,200",
        avatarColor = NoorBlue,
        skills = listOf("City Driving", "Highway", "Cleaning", "Laundry"),
        bio = "Experienced driver with 5+ years in DHA & Gulberg area. Punctual and trustworthy.",
        languages = listOf("Urdu", "Punjabi"), joinedDate = "Mar 2025"
    ),
    WorkerProfile(
        id = "2", name = "Ayesha Bibi", initials = "AB", workerUsername = "@NS-1002",
        serviceIds = listOf("maid", "cook"),
        city = "Lahore", area = "Gulberg III", experience = "3 yrs",
        timeSlot = "Morning", isAvailable = true, dailyRate = "PKR 900",
        avatarColor = NoorOrange,
        skills = listOf("Deep Cleaning", "Laundry", "Pakistani Cuisine", "Baking"),
        bio = "Hardworking and reliable house maid with cooking expertise. Available mornings.",
        languages = listOf("Urdu", "Saraiki"), joinedDate = "Jan 2025"
    ),
    WorkerProfile(
        id = "3", name = "Imran Khan", initials = "IK", workerUsername = "@NS-1003",
        serviceIds = listOf("security"),
        city = "Lahore", area = "Bahria Town", experience = "7 yrs",
        timeSlot = "Full Day", isAvailable = false, dailyRate = "PKR 1,500",
        avatarColor = NoorGreen,
        skills = listOf("CCTV Operation", "First Aid", "Patrolling"),
        bio = "Ex-army trained security guard with 7 years of residential & commercial experience.",
        languages = listOf("Urdu", "English", "Pashto"), joinedDate = "Nov 2024"
    ),
    WorkerProfile(
        id = "4", name = "Sana Fatima", initials = "SF", workerUsername = "@NS-1004",
        serviceIds = listOf("babysitter", "maid"),
        city = "Lahore", area = "Model Town", experience = "4 yrs",
        timeSlot = "Flexible", isAvailable = true, dailyRate = "PKR 1,000",
        avatarColor = Color(0xFF9C27B0),
        skills = listOf("Infant Care", "School Drop-off", "Deep Cleaning", "Childcare"),
        bio = "Caring and patient babysitter. Great with infants and toddlers. First Aid certified.",
        languages = listOf("Urdu", "English"), joinedDate = "Feb 2025"
    ),
    WorkerProfile(
        id = "5", name = "Zulfiqar Ali", initials = "ZA", workerUsername = "@NS-1005",
        serviceIds = listOf("driver"),
        city = "Lahore", area = "Johar Town", experience = "10+ yrs",
        timeSlot = "Morning", isAvailable = true, dailyRate = "PKR 1,100",
        avatarColor = Color(0xFF009688),
        skills = listOf("City Driving", "Highway", "Heavy Vehicle"),
        bio = "Senior driver with LTV & HTV licence. Calm and professional with Lahore route expertise.",
        languages = listOf("Urdu", "Punjabi", "Saraiki"), joinedDate = "Sep 2024"
    ),
    WorkerProfile(
        id = "6", name = "Nazia Malik", initials = "NM", workerUsername = "@NS-1006",
        serviceIds = listOf("cook"),
        city = "Lahore", area = "Askari X", experience = "6 yrs",
        timeSlot = "Morning", isAvailable = true, dailyRate = "PKR 1,300",
        avatarColor = Color(0xFFE91E63),
        skills = listOf("Pakistani Cuisine", "Chinese", "Continental", "Baking", "BBQ"),
        bio = "Versatile home cook specialising in Pakistani and Chinese cuisine. Highly rated.",
        languages = listOf("Urdu", "Punjabi"), joinedDate = "Dec 2024"
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Browse Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerBrowseScreen() {
    var openWorker    by remember { mutableStateOf<WorkerProfile?>(null) }
    var query         by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf<String?>(null) }
    var showAvailOnly by remember { mutableStateOf(false) }

    if (openWorker != null) {
        EmployerWorkerDetailScreen(worker = openWorker!!, onBack = { openWorker = null })
        return
    }

    val filtered = sampleWorkers.filter { w ->
        val matchCat   = selectedCatId == null || w.serviceIds.contains(selectedCatId)
        val matchAvail = !showAvailOnly || w.isAvailable
        val matchQuery = query.isBlank() ||
                w.name.contains(query, ignoreCase = true) ||
                w.area.contains(query, ignoreCase = true) ||
                w.workerUsername.contains(query, ignoreCase = true) ||
                w.serviceIds.any { id ->
                    allServiceCategories.find { it.id == id }
                        ?.label?.contains(query, ignoreCase = true) == true
                }
        matchCat && matchAvail && matchQuery
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header + search ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 20.dp)
        ) {
            Column {
                Text("Find Workers", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text("Browse profiles · Send proposal to admin", fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.72f))
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value         = query,
                    onValueChange = { query = it },
                    placeholder   = {
                        Text("Search by name, @username, area, service…",
                            fontSize = 13.sp, color = NoorTextHint)
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
                        cursorColor             = NoorBlue
                    )
                )
            }
        }

        // ── Category chips ────────────────────────────────────────────────────
        LazyRow(
            modifier              = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { BrowseChip("All", selectedCatId == null) { selectedCatId = null } }
            items(allServiceCategories) { svc ->
                BrowseChip(
                    label    = "${svc.emoji} ${svc.label}",
                    selected = selectedCatId == svc.id,
                    onClick  = { selectedCatId = if (selectedCatId == svc.id) null else svc.id }
                )
            }
        }

        // ── Available-only toggle ─────────────────────────────────────────────
        Row(
            modifier             = Modifier.fillMaxWidth().background(NoorSurface)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Available now only", fontSize = 12.sp, color = NoorTextSecondary,
                fontWeight = FontWeight.Medium)
            Switch(
                checked         = showAvailOnly,
                onCheckedChange = { showAvailOnly = it },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White,
                    checkedTrackColor   = NoorBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = NoorBorder
                )
            )
        }
        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // ── Results ───────────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No workers found", fontSize = 14.sp, color = NoorTextHint,
                        fontWeight = FontWeight.Medium)
                    Text("Try adjusting your filters", fontSize = 11.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("${filtered.size} worker${if (filtered.size != 1) "s" else ""} found",
                        fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold)
                }
                items(filtered) { worker ->
                    WorkerCard(worker = worker, onClick = { openWorker = worker })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Worker Card — shows username badge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WorkerCard(worker: WorkerProfile, onClick: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(54.dp).clip(CircleShape).background(worker.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(worker.initials, fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold, color = Color.White)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(worker.name, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = NoorTextPrimary)
                        if (worker.isAvailable) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(NoorGreenLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) { Text("Available", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NoorGreen) }
                        }
                    }
                    // Area + username on the same row
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${worker.area}, ${worker.city}", fontSize = 11.sp, color = NoorTextHint)

                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(worker.dailyRate, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, color = NoorBlue)
                    Text("per day", fontSize = 9.sp, color = NoorTextHint)
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                worker.serviceIds.take(2).forEach { id ->
                    val svc = allServiceCategories.find { it.id == id }
                    if (svc != null) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(NoorBlueLight).padding(horizontal = 8.dp, vertical = 4.dp)
                        ) { Text("${svc.emoji} ${svc.label}", fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorBlue) }
                    }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(NoorBackground).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text("⏱ ${worker.experience}", fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, color = NoorTextSecondary) }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(NoorBackground).padding(horizontal = 8.dp, vertical = 4.dp)
                ) { Text("🕐 ${worker.timeSlot}", fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold, color = NoorTextSecondary) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Worker Detail Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployerWorkerDetailScreen(
    worker: WorkerProfile,
    onBack: () -> Unit
) {
    var showProposalDialog by remember { mutableStateOf(false) }
    // Proposal is considered sent if already in store for this worker
    var proposalSent by remember {
        mutableStateOf(
            AdminProposalStore.proposals.any { it.workerUsername == worker.workerUsername }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier.size(70.dp).clip(CircleShape)
                            .background(worker.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(worker.initials, fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(worker.name, fontSize = 19.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            if (worker.isAvailable) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(NoorGreen)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) { Text("Available", fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White) }
                            }
                        }
                        Text("${worker.area}, ${worker.city}", fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f))
                        Spacer(Modifier.height(6.dp))
                        // Username — prominent on the detail page
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("🪪", fontSize = 13.sp)
                                Text(worker.workerUsername, fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White,
                                    letterSpacing = 0.4.sp)
                            }
                        }
                    }
                }
            }
        }

        // ── Body ──────────────────────────────────────────────────────────────
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Info tiles
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkerInfoTile("💰", "Daily Rate",  worker.dailyRate,  Modifier.weight(1f))
                WorkerInfoTile("⏱",  "Experience",  worker.experience, Modifier.weight(1f))
                WorkerInfoTile("🕐",  "Time Slot",   worker.timeSlot,   Modifier.weight(1f))
            }

            // Services
            WorkerDetailSection("Services Offered") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    worker.serviceIds.forEach { id ->
                        val svc = allServiceCategories.find { it.id == id }
                        if (svc != null) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(10.dp))
                                .background(NoorBlueLight)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) { Text("${svc.emoji} ${svc.label}", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = NoorBlue) }
                        }
                    }
                }
            }

            // About
            WorkerDetailSection("About") {
                Text(worker.bio, fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp)
            }

            // Skills
            WorkerDetailSection("Skills") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    worker.skills.forEach { skill ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(NoorBackground)
                            .border(1.dp, NoorBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) { Text(skill, fontSize = 11.sp, color = NoorTextSecondary,
                            fontWeight = FontWeight.Medium) }
                    }
                }
            }

            // Languages
            WorkerDetailSection("Languages") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    worker.languages.forEach { lang ->
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(NoorGreenLight)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) { Text(lang, fontSize = 11.sp, color = NoorGreen,
                            fontWeight = FontWeight.SemiBold) }
                    }
                }
            }

            // Member since
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(NoorBlueLight).padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("📅", fontSize = 16.sp)
                    Text("Member since ${worker.joinedDate}", fontSize = 12.sp,
                        color = NoorBlueDark, fontWeight = FontWeight.Medium)
                }
            }

            // ── CTA ───────────────────────────────────────────────────────────
            if (proposalSent) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                        .background(NoorGreenLight).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                "Admin will review and connect you with ${worker.name.split(" ").first()}. " +
                                        "Track status in 'My Proposals'.",
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
        SendProposalToAdminDialog(
            worker    = worker,
            onDismiss = { showProposalDialog = false },
            onSent    = {
                showProposalDialog = false
                proposalSent       = true
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Send Proposal to Admin Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SendProposalToAdminDialog(
    worker: WorkerProfile,
    onDismiss: () -> Unit,
    onSent: () -> Unit
) {
    var jobTitle   by remember { mutableStateOf("") }
    var location   by remember { mutableStateOf("") }
    var schedule   by remember { mutableStateOf("") }
    var startDate  by remember { mutableStateOf("") }
    var offerPrice by remember { mutableStateOf(worker.dailyRate) }
    var note       by remember { mutableStateOf("") }

    val isValid = jobTitle.isNotBlank() && location.isNotBlank() &&
            schedule.isNotBlank() && offerPrice.isNotBlank()

    val serviceLabel = worker.serviceIds.firstOrNull()?.let { id ->
        allServiceCategories.find { it.id == id }?.label
    } ?: "Service"

    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(20.dp),
        containerColor   = NoorSurface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📩 Send Proposal to Admin",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NoorTextPrimary)

                // Worker summary row with username
                Row(
                    modifier             = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorBlueLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                            .background(worker.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(worker.initials, fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(worker.name, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text("${worker.area} · $serviceLabel",
                            fontSize = 11.sp, color = NoorTextHint)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NoorBlueDark.copy(alpha = 0.1f))
                            .border(1.dp, NoorBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(worker.workerUsername, fontSize = 12.sp,
                            fontWeight = FontWeight.Bold, color = NoorBlue)
                    }
                }
            }
        },
        text = {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("PROPOSAL DETAILS", fontSize = 9.sp, fontWeight = FontWeight.Bold,
                    color = NoorTextHint, letterSpacing = 0.8.sp)

                ProposalInputField(value = jobTitle, onChange = { jobTitle = it },
                    label = "Job Title *", placeholder = "e.g. Full-time Driver", icon = "💼")
                ProposalInputField(value = location, onChange = { location = it },
                    label = "Location *", placeholder = "e.g. DHA Phase 5, Lahore", icon = "📍")
                ProposalInputField(value = schedule, onChange = { schedule = it },
                    label = "Schedule *", placeholder = "e.g. Mon–Fri, Full Day", icon = "⏰")
                ProposalInputField(value = startDate, onChange = { startDate = it },
                    label = "Preferred Start Date", placeholder = "e.g. 15 Apr 2026", icon = "🗓")
                ProposalInputField(value = offerPrice, onChange = { offerPrice = it },
                    label = "Offer Price *", placeholder = "e.g. PKR 1,200 / day", icon = "💰")
                ProposalInputField(
                    value = note, onChange = { if (it.length <= 200) note = it },
                    label = "Note to Admin (optional)",
                    placeholder = "Any special requirements…",
                    icon = "📝", singleLine = false
                )
                if (note.isNotEmpty()) {
                    Text("${note.length}/200", fontSize = 10.sp,
                        color = if (note.length > 190) NoorOrange else NoorTextHint,
                        modifier = Modifier.align(Alignment.End))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NoorOrangeLight)
                        .padding(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top) {
                        Text("💡", fontSize = 13.sp)
                        Text(
                            "Admin will track this using ${worker.workerUsername}. " +
                                    "Check 'My Proposals' for status updates.",
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
                    AdminProposalStore.proposals.add(
                        0,
                        AdminProposal(
                            id             = UUID.randomUUID().toString(),
                            workerName     = worker.name,
                            workerUsername = worker.workerUsername,
                            workerInitials = worker.initials,
                            avatarColor    = worker.avatarColor,
                            jobTitle       = jobTitle.trim(),
                            service        = serviceLabel,
                            location       = location.trim(),
                            schedule       = schedule.trim(),
                            startDate      = startDate.trim().ifBlank { "TBD" },
                            offerPrice     = offerPrice.trim(),
                            note           = note.trim(),
                            sentAt         = now,
                            status         = AdminProposalStatus.SENT
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
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProposalInputField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: String,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onChange,
        label           = { Text("$icon  $label", fontSize = 12.sp) },
        placeholder     = { Text(placeholder, fontSize = 12.sp, color = NoorTextHint) },
        modifier        = Modifier.fillMaxWidth(),
        singleLine      = singleLine,
        maxLines        = if (singleLine) 1 else 4,
        shape           = RoundedCornerShape(10.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors          = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = NoorBlue,
            unfocusedBorderColor = NoorBorder,
            focusedLabelColor    = NoorBlue,
            cursorColor          = NoorBlue
        )
    )
}

@Composable
private fun BrowseChip(label: String, selected: Boolean, onClick: () -> Unit) {
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
private fun WorkerInfoTile(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
            Text(label, fontSize = 9.sp,  color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun WorkerDetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                color = NoorBlue, letterSpacing = 0.3.sp)
            content()
        }
    }
}
