package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.*
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

data class ServiceDetail(
    val serviceId: String,
    val skills: List<String>,
    val experienceYears: String,
    val availabilityDays: List<String>,
    val availabilityTime: String,
    val additionalNote: String,
    val licenceType: String = ""
)

// ─────────────────────────────────────────────────────────────────────────────
// Static data
// ─────────────────────────────────────────────────────────────────────────────

private val serviceSkillOptions = mapOf(
    "driver"     to listOf("City Driving", "Highway", "Heavy Vehicle", "Motorcycle", "Car Maintenance"),
    "security"   to listOf("CCTV Operation", "First Aid", "Patrolling", "Firearms Certified"),
    "houseBoy"   to listOf("Cleaning", "Laundry", "Ironing", "Groceries", "Minor Repairs"),
    "officeBoy"  to listOf("Photocopying", "File Management", "Tea/Coffee", "Errands"),
    "cook"       to listOf("Pakistani Cuisine", "Chinese", "Continental", "Baking", "BBQ"),
    "maid"       to listOf("Deep Cleaning", "Laundry", "Dishes", "Childcare", "Cooking"),
    "babysitter" to listOf("Infant Care", "School Drop-off", "Tutoring Assistance", "First Aid"),
    "elderCare"  to listOf("Medication Reminders", "Physical Assistance", "Companionship", "Medical Visits"),
    "gardener"   to listOf("Lawn Mowing", "Pruning", "Planting", "Pest Control"),
    "mechanic"   to listOf("Engine Repair", "Electrical", "Tyre Change", "AC Service"),
)

private val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private val timeSlots = listOf(
    "Full Day",
    "Morning",
    "Evening",
    "Night",
    "Flexible"
)

private val experienceOptions = listOf(
    "< 1 year", "1–2 yrs", "3–5 yrs", "6–10 yrs", "10+ yrs"
)

// ─────────────────────────────────────────────────────────────────────────────
// State holder (one per selected service)
// ─────────────────────────────────────────────────────────────────────────────

class ServiceDetailState(val serviceId: String) {
    var selectedSkills = mutableStateListOf<String>()
    var experience     by mutableStateOf("")
    var selectedDays   = mutableStateListOf<String>()
    var timeSlot       by mutableStateOf("")   // Fix #6: now tracked and required
    var note           by mutableStateOf("")
    var licenceType    by mutableStateOf("")
}

// ─────────────────────────────────────────────────────────────────────────────
// Main screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ServiceDetailScreen(
    selectedServiceIds: List<String>,
    onNext: (details: List<ServiceDetail>) -> Unit,
    onBack: () -> Unit
) {
    val detailStates = remember {
        selectedServiceIds.associateWith { ServiceDetailState(it) }.toMutableMap()
    }

    // Fix #6: Finish button enabled only when every service has
    //         experience + at least one day + a time slot selected.
    val allValid by remember {
        derivedStateOf {
            detailStates.values.all { s ->
                s.experience.isNotEmpty() &&
                        s.selectedDays.isNotEmpty() &&
                        s.timeSlot.isNotEmpty()        // ← time slot now required
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // Header — back arrow fix applied inside NoorScreenHeader (fix #5)
        NoorScreenHeader(
            title       = "Service Details",
            subtitle    = "Add your skills & availability",
            currentStep = 3,
            totalSteps  = 3,
            onBack      = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            selectedServiceIds.forEach { svcId ->
                val state    = detailStates[svcId] ?: return@forEach
                val category = allServiceCategories.find { it.id == svcId }
                ServiceDetailCard(
                    category = category?.label ?: svcId,
                    emoji    = category?.emoji  ?: "💼",
                    state    = state
                )
            }

            NoorPrimaryButton(
                text    = "Finish Registration  ✓",
                enabled = allValid,
                onClick = {
                    val details = detailStates.values.map { s ->
                        ServiceDetail(
                            serviceId        = s.serviceId,
                            skills           = s.selectedSkills.toList(),
                            experienceYears  = s.experience,
                            availabilityDays = s.selectedDays.toList(),
                            availabilityTime = s.timeSlot,
                            additionalNote   = s.note,
                            licenceType      = s.licenceType
                        )
                    }
                    onNext(details)
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Per-service card
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ServiceDetailCard(
    category: String,
    emoji: String,
    state: ServiceDetailState
) {
    NoorSectionCard {

        // Card header
        Row(
            modifier              = Modifier.padding(bottom = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NoorBlueLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 20.sp) }
            Text(
                text       = category,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = NoorTextPrimary
            )
        }

        HorizontalDivider(color = NoorDivider, thickness = 0.8.dp)
        Spacer(Modifier.height(16.dp))

        // ── Skills ───────────────────────────────────────────────────────────
        val skillOptions = serviceSkillOptions[state.serviceId] ?: emptyList()
        if (skillOptions.isNotEmpty()) {
            SubLabel("Skills")
            Spacer(Modifier.height(10.dp))
            // Fix #5: verticalArrangement gives proper row gap between wrapped lines
            FlowRow(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)  // ← key fix
            ) {
                skillOptions.forEach { skill ->
                    NoorSelectableChip(
                        label    = skill,
                        icon     = if (state.selectedSkills.contains(skill)) "✓" else "·",
                        selected = state.selectedSkills.contains(skill),
                        onClick  = {
                            if (state.selectedSkills.contains(skill))
                                state.selectedSkills.remove(skill)
                            else
                                state.selectedSkills.add(skill)
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Driver: Licence type ──────────────────────────────────────────────
        if (state.serviceId == "driver") {
            SubLabel("Licence Type")
            Spacer(Modifier.height(10.dp))
            NoorTextField(
                value         = state.licenceType,
                onValueChange = { state.licenceType = it },
                label         = "Licence Type",
                placeholder   = "e.g. LTV, HTV, Motorcycle"
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── Experience ────────────────────────────────────────────────────────
        SubLabel("Experience *")
        Spacer(Modifier.height(10.dp))
        // Fix #5: verticalArrangement gives proper row gap
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)  // ← key fix
        ) {
            experienceOptions.forEach { opt ->
                NoorSelectableChip(
                    label    = opt,
                    icon     = "⏱",
                    selected = state.experience == opt,
                    onClick  = { state.experience = opt }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // ── Available Days ────────────────────────────────────────────────────
        SubLabel("Available Days *")
        Spacer(Modifier.height(10.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            daysOfWeek.forEach { day ->
                val selected = state.selectedDays.contains(day)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) NoorBlue else NoorBackground)
                        .border(
                            width = 1.dp,
                            color = if (selected) NoorBlue else NoorBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (selected) state.selectedDays.remove(day)
                            else state.selectedDays.add(day)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = day,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = if (selected) Color.White else NoorTextSecondary
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // ── Preferred Time Slot ───────────────────────────────────────────────
        // Fix #5: verticalArrangement for row gap
        // Fix #6: timeSlot selection is now REQUIRED (tracked in allValid)
        SubLabel("Preferred Time Slot *")
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)  // ← key fix
        ) {
            timeSlots.forEach { slot ->
                NoorSelectableChip(
                    label    = slot,
                    icon     = "🕐",
                    selected = state.timeSlot == slot,
                    onClick  = { state.timeSlot = slot }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // ── Additional Notes ──────────────────────────────────────────────────
        NoorTextField(
            value         = state.note,
            onValueChange = { state.note = it },
            label         = "Additional Notes (optional)",
            placeholder   = "Any extra info about this service…",
            singleLine    = false,
            maxLines      = 3
        )
    }
}

@Composable
private fun SubLabel(text: String) {
    Text(
        text          = text,
        fontSize      = 11.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = NoorTextHint,
        letterSpacing = 0.5.sp
    )
}