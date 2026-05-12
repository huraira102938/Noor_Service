package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.*
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeRegistrationViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class ServiceCategory(
    val id: String,
    val label: String,
    val emoji: String
)

val allServiceCategories = listOf(
    ServiceCategory("driver",     "Driver",         "🚗"),
    ServiceCategory("security",   "Security Guard", "🛡️"),
    ServiceCategory("houseBoy",   "House Boy",      "🧹"),
    ServiceCategory("officeBoy",  "Office Boy",     "👔"),
    ServiceCategory("cook",       "Cook",           "🍳"),
    ServiceCategory("maid",       "Maid",           "🧺"),
    ServiceCategory("babysitter", "Baby Sitter",    "👶"),
    ServiceCategory("elderCare",  "Elder Care",     "👴"),
    ServiceCategory("gardener",   "Gardener",       "🌿"),
    ServiceCategory("mechanic",   "Mechanic",       "🔧"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceSelectionScreen(
    viewModel: EmployeeRegistrationViewModel,
    onNext: (selectedIds: List<String>) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedServices = remember { mutableStateListOf<String>().apply { addAll(uiState.selectedServiceIds) } }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    val serviceCategories = remember(uiState.categories) {
        if (uiState.categories.isNotEmpty()) {
            uiState.categories.map { cat ->
                ServiceCategory(
                    id = cat.id,
                    label = cat.label,
                    emoji = cat.emoji
                )
            }
        } else {
            allServiceCategories
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        NoorScreenHeader(
            title       = "Your Services",
            subtitle    = "Select all services you can offer",
            currentStep = 2,
            totalSteps  = 3,
            onBack      = onBack
        )

        if (uiState.isCategoriesLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NoorBlue)
            }
        } else if (serviceCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No categories available. Please contact admin.",
                    color = NoorTextHint,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NoorSectionCard {
                    Text(
                        text          = "Choose categories",
                        fontSize      = 13.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = NoorBlue,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    FlowRow(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(12.dp)
                    ) {
                        serviceCategories.forEach { svc ->
                            NoorSelectableChip(
                                label    = svc.label,
                                icon     = svc.emoji,
                                selected = selectedServices.contains(svc.id),
                                onClick  = {
                                    if (selectedServices.contains(svc.id))
                                        selectedServices.remove(svc.id)
                                    else
                                        selectedServices.add(svc.id)
                                    viewModel.updateSelectedServices(selectedServices.toList())
                                }
                            )
                        }
                    }
                }

                if (selectedServices.isEmpty()) {
                    Text(
                        text     = "Please select at least one service to continue.",
                        color    = NoorTextHint,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                NoorPrimaryButton(
                    text    = "Continue  →",
                    onClick = {
                        viewModel.goToStep3()
                        onNext(selectedServices.toList())
                    },
                    enabled = selectedServices.isNotEmpty()
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}