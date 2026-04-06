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

// ─────────────────────────────────────────────────────────────────────────────
// UPDATED WorkerProfile data class
// Replace your existing WorkerProfile with this one.
// ─────────────────────────────────────────────────────────────────────────────

data class WorkerProfile(
    val id: String,
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val workerUsername: String,
    val city: String,
    val area: String,

    // ── Personal Details (new) ─────────────────────────────────────────────
    val phone: String = "",
    val cnic: String = "",
    val dob: String = "",
    val address: String = "",
    val gender: String = "",

    // ── Work profile ───────────────────────────────────────────────────────
    val serviceIds: List<String>,
    val skills: List<String>,
    val experience: String,
    val licenceType: String = "",         // new
    val availableDays: List<String> = emptyList(), // new
    val timeSlot: String,
    val additionalNote: String = "",       // new
    val isAvailable: Boolean,
    val joinedDate: String,

    // ── Rates ──────────────────────────────────────────────────────────────
    val dailyRate: String,
    val hourlyRate: String = "",           // new
    val monthlyRate: String = "",          // new

    // ── Profile ────────────────────────────────────────────────────────────
    val bio: String,
    val languages: List<String>
)

// ─────────────────────────────────────────────────────────────────────────────
// Sample workers — updated with all new fields populated
// ─────────────────────────────────────────────────────────────────────────────

val sampleWorkers = listOf(
    WorkerProfile(
        id = "w1", name = "Muhammad Ali", initials = "MA",
        avatarColor = NoorBlue, workerUsername = "@m_ali_driver",
        city = "Lahore", area = "DHA Phase 3",
        phone = "0312-3456789", cnic = "35202-1234567-9",
        dob = "15/03/1992", address = "House 12, Street 5, DHA Phase 3, Lahore",
        gender = "Male",
        serviceIds = listOf("driver", "houseBoy"),
        skills = listOf("City Driving", "Highway", "Cleaning", "Laundry"),
        experience = "3–5 yrs",
        licenceType = "LTV",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
        timeSlot = "Full Day",
        additionalNote = "Can work late hours if required. Own car available.",
        isAvailable = true, joinedDate = "Jan 2025",
        dailyRate = "PKR 1,200", hourlyRate = "PKR 150", monthlyRate = "PKR 25,000",
        bio = "Experienced professional driver with 5+ years in DHA & Gulberg area.",
        languages = listOf("Urdu", "Punjabi")
    ),
    WorkerProfile(
        id = "w2", name = "Amina Bibi", initials = "AB",
        avatarColor = Color(0xFFE91E63), workerUsername = "@amina_maid",
        city = "Lahore", area = "Gulberg II",
        phone = "0321-9988776", cnic = "35201-9876543-2",
        dob = "22/06/1988", address = "Street 7, Gulberg II, Lahore",
        gender = "Female",
        serviceIds = listOf("maid", "cook"),
        skills = listOf("Deep Cleaning", "Laundry", "Dishes", "Pakistani Cuisine"),
        experience = "6–10 yrs",
        licenceType = "",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
        timeSlot = "Morning",
        additionalNote = "Available for live-in arrangement.",
        isAvailable = true, joinedDate = "Feb 2025",
        dailyRate = "PKR 900", hourlyRate = "", monthlyRate = "PKR 18,000",
        bio = "Dedicated housemaid and cook with 10 years experience in Lahore homes.",
        languages = listOf("Urdu", "Punjabi", "Saraiki")
    ),
    WorkerProfile(
        id = "w3", name = "Tariq Hussain", initials = "TH",
        avatarColor = Color(0xFF37474F), workerUsername = "@tariq_guard",
        city = "Lahore", area = "Model Town",
        phone = "0300-5544332", cnic = "35202-6655443-1",
        dob = "10/01/1985", address = "Block B, Model Town, Lahore",
        gender = "Male",
        serviceIds = listOf("security"),
        skills = listOf("Patrolling", "CCTV Operation", "First Aid"),
        experience = "10+ yrs",
        licenceType = "Armed Security Licence",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        timeSlot = "Night",
        additionalNote = "Firearms certified. Available for rotating shifts.",
        isAvailable = false, joinedDate = "Dec 2024",
        dailyRate = "PKR 1,500", hourlyRate = "PKR 200", monthlyRate = "PKR 35,000",
        bio = "Seasoned security professional with a decade of experience guarding residential & commercial premises.",
        languages = listOf("Urdu", "Punjabi", "Pashto")
    ),
    WorkerProfile(
        id = "w4", name = "Sana Iqbal", initials = "SI",
        avatarColor = NoorOrange, workerUsername = "@sana_sitter",
        city = "Lahore", area = "Johar Town",
        phone = "0332-7788990", cnic = "35202-3344556-3",
        dob = "05/09/1995", address = "Street 11, Johar Town, Lahore",
        gender = "Female",
        serviceIds = listOf("babysitter", "elderCare"),
        skills = listOf("Infant Care", "School Drop-off", "Medication Reminders", "Companionship"),
        experience = "1–2 yrs",
        licenceType = "First Aid Certificate",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
        timeSlot = "Morning",
        additionalNote = "Non-smoker. Comfortable with infants from 0–12 months.",
        isAvailable = true, joinedDate = "Mar 2025",
        dailyRate = "PKR 800", hourlyRate = "PKR 120", monthlyRate = "",
        bio = "Caring and patient babysitter with special interest in early childhood development.",
        languages = listOf("Urdu", "English")
    ),
    WorkerProfile(
        id = "w5", name = "Usman Qureshi", initials = "UQ",
        avatarColor = NoorGreen, workerUsername = "@usman_cook",
        city = "Islamabad", area = "F-7",
        phone = "0311-4455667", cnic = "61101-1234567-5",
        dob = "20/07/1990", address = "F-7/2, Islamabad",
        gender = "Male",
        serviceIds = listOf("cook"),
        skills = listOf("Pakistani Cuisine", "Chinese", "Continental", "BBQ"),
        experience = "6–10 yrs",
        licenceType = "Food Hygiene Certificate",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
        timeSlot = "Full Day",
        additionalNote = "Can cook for events and gatherings of up to 50 people.",
        isAvailable = true, joinedDate = "Nov 2024",
        dailyRate = "PKR 1,800", hourlyRate = "", monthlyRate = "PKR 38,000",
        bio = "Versatile chef specialising in multi-cuisine cooking for families and corporate kitchens.",
        languages = listOf("Urdu", "Punjabi", "English")
    ),
    WorkerProfile(
        id = "w6", name = "Bilal Raza", initials = "BR",
        avatarColor = Color(0xFF9C27B0), workerUsername = "@bilal_mechanic",
        city = "Lahore", area = "Bahria Town",
        phone = "0342-3322110", cnic = "35202-8877665-7",
        dob = "14/04/1987", address = "Sector C, Bahria Town, Lahore",
        gender = "Male",
        serviceIds = listOf("mechanic"),
        skills = listOf("Engine Repair", "Electrical", "Tyre Change", "AC Service"),
        experience = "6–10 yrs",
        licenceType = "LTV, HTV",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Sat"),
        timeSlot = "Morning",
        additionalNote = "Workshop available. House calls accepted in Bahria Town.",
        isAvailable = true, joinedDate = "Feb 2025",
        dailyRate = "PKR 1,400", hourlyRate = "PKR 180", monthlyRate = "",
        bio = "Skilled automobile mechanic with expertise in Toyota, Honda and Suzuki models.",
        languages = listOf("Urdu", "Punjabi")
    ),
    WorkerProfile(
        id = "w7", name = "Rehana Bibi", initials = "RB",
        avatarColor = Color(0xFFE91E63), workerUsername = "@rehana_office",
        city = "Karachi", area = "Clifton",
        phone = "0333-9900112", cnic = "42201-1122334-1",
        dob = "30/11/1993", address = "Block 4, Clifton, Karachi",
        gender = "Female",
        serviceIds = listOf("officeBoy"),
        skills = listOf("Photocopying", "File Management", "Tea/Coffee", "Errands"),
        experience = "1–2 yrs",
        licenceType = "",
        availableDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
        timeSlot = "Full Day",
        additionalNote = "Punctual and well-presented. Familiar with MS Office basics.",
        isAvailable = false, joinedDate = "Apr 2025",
        dailyRate = "PKR 700", hourlyRate = "", monthlyRate = "PKR 14,000",
        bio = "Reliable office support staff with a background in administrative assistance.",
        languages = listOf("Urdu", "Sindhi", "English")
    ),
    WorkerProfile(
        id = "w8", name = "Zahid Gardener", initials = "ZG",
        avatarColor = NoorGreen, workerUsername = "@zahid_garden",
        city = "Rawalpindi", area = "Saddar",
        phone = "0300-4433221", cnic = "37405-1234567-3",
        dob = "08/08/1980", address = "Raja Bazaar, Saddar, Rawalpindi",
        gender = "Male",
        serviceIds = listOf("gardener"),
        skills = listOf("Lawn Mowing", "Pruning", "Planting", "Pest Control"),
        experience = "10+ yrs",
        licenceType = "",
        availableDays = listOf("Tue", "Thu", "Sat"),
        timeSlot = "Morning",
        additionalNote = "Brings own tools. Available for weekly maintenance contracts.",
        isAvailable = true, joinedDate = "Jan 2025",
        dailyRate = "PKR 600", hourlyRate = "PKR 80", monthlyRate = "",
        bio = "Experienced gardener specialising in landscaping, lawn care and seasonal planting.",
        languages = listOf("Urdu", "Punjabi")
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
                            status         = AdminProposalStatus.PENDING
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
