package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.*
import com.danish.noorservice.ui.theme.*

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
    onNext: (selectedIds: List<String>) -> Unit,
    onBack: () -> Unit
) {
    val selectedServices = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // Header — back arrow fix applied inside NoorScreenHeader via statusBarsPadding
        NoorScreenHeader(
            title       = "Your Services",
            subtitle    = "Select all services you can offer",
            currentStep = 2,
            totalSteps  = 3,
            onBack      = onBack
        )

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

                // Fix #4: verticalArrangement = spacedBy(12.dp) so chip rows
                // have breathing room between them when they wrap.
                FlowRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)  // ← key fix
                ) {
                    allServiceCategories.forEach { svc ->
                        NoorSelectableChip(
                            label    = svc.label,
                            icon     = svc.emoji,
                            selected = selectedServices.contains(svc.id),
                            onClick  = {
                                if (selectedServices.contains(svc.id))
                                    selectedServices.remove(svc.id)
                                else
                                    selectedServices.add(svc.id)
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
                onClick = { onNext(selectedServices.toList()) },
                enabled = selectedServices.isNotEmpty()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}