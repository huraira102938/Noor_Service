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
import com.danish.noorservice.viewmodel.employee.EmployeeRegistrationViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

private val fallbackSkillOptions = mapOf(
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

class ServiceDetailState(val serviceId: String) {
    var selectedSkills = mutableStateListOf<String>()
    var experience     by mutableStateOf("")
    var selectedDays   = mutableStateListOf<String>()
    var timeSlot       by mutableStateOf("")
    var note           by mutableStateOf("")
    var licenceType    by mutableStateOf("")
}

@Composable
fun ServiceDetailScreen(
    viewModel: EmployeeRegistrationViewModel,
    selectedServiceIds: List<String>,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val detailStates = remember {
        selectedServiceIds.associateWith { ServiceDetailState(it) }.toMutableMap()
    }

    val categorySkillsMap = remember(uiState.categories) {
        uiState.categories.associate { it.id to it.skills.map { skill -> skill.name } }
    }

    val categoriesMap = remember(uiState.categories) {
        uiState.categories.associate { it.id to it }
    }

    val allValid by remember {
        derivedStateOf {
            detailStates.values.all { s ->
                s.experience.isNotEmpty() &&
                        s.selectedDays.isNotEmpty() &&
                        s.timeSlot.isNotEmpty()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is com.danish.noorservice.viewmodel.employee.EmployeeRegistrationEvent.Success -> {
                    onSuccess()
                }
                is com.danish.noorservice.viewmodel.employee.EmployeeRegistrationEvent.Error -> {
                    // Handle error - could show a snackbar
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
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
                val state = detailStates[svcId] ?: return@forEach
                val firestoreCategory = categoriesMap[svcId]
                val category = allServiceCategories.find { it.id == svcId }
                val skills = categorySkillsMap[svcId] ?: emptyList()
                ServiceDetailCard(
                    category = firestoreCategory?.label ?: category?.label ?: svcId,
                    emoji    = firestoreCategory?.emoji ?: category?.emoji  ?: "💼",
                    state    = state,
                    categorySkills = skills,
                    onSkillsSelected = { sk ->
                        state.selectedSkills.clear()
                        state.selectedSkills.addAll(sk)
                    },
                    onDetailUpdate = { key, value ->
                        when (key) {
                            "experience" -> state.experience = value
                            "timeSlot" -> state.timeSlot = value
                            "note" -> state.note = value
                            "licenceType" -> state.licenceType = value
                            "availabilityDays" -> {
                                state.selectedDays.clear()
                                state.selectedDays.addAll(value.split(","))
                            }
                        }
                    }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            NoorPrimaryButton(
                text    = "Finish Registration  ✓",
                enabled = allValid && !uiState.isLoading,
                onClick = {
                    detailStates.values.forEach { s ->
                        viewModel.updateServiceDetail(
                            s.serviceId,
                            com.danish.noorservice.viewmodel.employee.ServiceDetailInput(
                                skills = s.selectedSkills.toList(),
                                experience = s.experience,
                                availabilityDays = s.selectedDays.toList(),
                                availabilityTime = s.timeSlot,
                                additionalNote = s.note,
                                dailyRate = ""
                            )
                        )
                    }
                    viewModel.saveEmployeeProfile()
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ServiceDetailCard(
    category: String,
    emoji: String,
    state: ServiceDetailState,
    categorySkills: List<String>,
    onSkillsSelected: (List<String>) -> Unit,
    onDetailUpdate: (String, String) -> Unit
) {
    NoorSectionCard {

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

        val skillOptions = if (categorySkills.isNotEmpty()) categorySkills else (fallbackSkillOptions[state.serviceId] ?: emptyList())
        if (skillOptions.isNotEmpty()) {
            SubLabel("Skills")
            Spacer(Modifier.height(10.dp))
            FlowRow(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp)
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
                            onSkillsSelected(state.selectedSkills.toList())
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        if (state.serviceId == "driver") {
            SubLabel("Licence Type")
            Spacer(Modifier.height(10.dp))
            NoorTextField(
                value         = state.licenceType,
                onValueChange = { onDetailUpdate("licenceType", it) },
                label         = "Licence Type",
                placeholder   = "e.g. LTV, HTV, Motorcycle"
            )
            Spacer(Modifier.height(16.dp))
        }

        SubLabel("Experience *")
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)
        ) {
            experienceOptions.forEach { opt ->
                NoorSelectableChip(
                    label    = opt,
                    icon     = "⏱",
                    selected = state.experience == opt,
                    onClick  = { onDetailUpdate("experience", opt) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

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
                            onDetailUpdate("availabilityDays", state.selectedDays.joinToString(","))
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

        SubLabel("Preferred Time Slot *")
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp)
        ) {
            timeSlots.forEach { slot ->
                NoorSelectableChip(
                    label    = slot,
                    icon     = "🕐",
                    selected = state.timeSlot == slot,
                    onClick  = { onDetailUpdate("timeSlot", slot) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        NoorTextField(
            value         = state.note,
            onValueChange = { onDetailUpdate("note", it) },
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