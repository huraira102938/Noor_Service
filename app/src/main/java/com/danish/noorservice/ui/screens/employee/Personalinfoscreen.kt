package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.*
import com.danish.noorservice.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalInfoScreen(
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var fullName by remember { mutableStateOf("") }
    var gender   by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var cnic     by remember { mutableStateOf("") }
    var dob      by remember { mutableStateOf("") }
    var city     by remember { mutableStateOf("") }
    var address  by remember { mutableStateOf("") }

    val languages         = listOf("Urdu", "Punjabi", "English", "Pashto", "Sindhi", "Saraiki")
    val selectedLanguages = remember { mutableStateListOf("Urdu") }
    val genderOptions     = listOf("Male", "Female")

    val isFormValid = fullName.isNotBlank() && gender.isNotBlank() &&
            cnic.isNotBlank() && city.isNotBlank() && selectedLanguages.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // Header — statusBarsPadding is applied inside NoorScreenHeader (fix #2)
        NoorScreenHeader(
            title       = "Personal Information",
            subtitle    = "Tell us about yourself",
            currentStep = 1,
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

            // ── Basic Details ────────────────────────────────────────────────
            NoorSectionCard {
                SectionLabel("Basic Details")
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = fullName,
                    onValueChange = { fullName = it },
                    label         = "Full Name *",
                    placeholder   = "As on CNIC"
                )

                Spacer(Modifier.height(14.dp))

                // Gender label + chips
                Text(
                    text          = "Gender *",
                    fontSize      = 11.sp,
                    color         = NoorTextHint,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    genderOptions.forEach { opt ->
                        NoorSelectableChip(
                            label    = opt,
                            icon     = if (opt == "Male") "👨" else "👩",
                            selected = gender == opt,
                            onClick  = { gender = opt }
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                NoorTextField(
                    value           = email,
                    onValueChange   = { email = it },
                    label           = "Email (optional)",
                    placeholder     = "you@email.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            // ── Identity & Location ──────────────────────────────────────────
            NoorSectionCard {
                SectionLabel("Identity & Location")
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NoorTextField(
                        value           = cnic,
                        onValueChange   = { cnic = it },
                        label           = "CNIC *",
                        placeholder     = "XXXXX-XXXXXXX-X",
                        modifier        = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    NoorTextField(
                        value         = dob,
                        onValueChange = { dob = it },
                        label         = "Date of Birth",
                        placeholder   = "DD/MM/YYYY",
                        modifier      = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = city,
                    onValueChange = { city = it },
                    label         = "City *",
                    placeholder   = "e.g. Lahore"
                )

                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = address,
                    onValueChange = { address = it },
                    label         = "Permanent Address",
                    placeholder   = "Street, Area, City",
                    singleLine    = false,
                    maxLines      = 3
                )
            }

            // ── Languages Spoken ─────────────────────────────────────────────
            NoorSectionCard {
                SectionLabel("Languages Spoken *")
                Spacer(Modifier.height(12.dp))

                // Fix #3: verticalArrangement = spacedBy(12.dp) gives proper
                // vertical breathing room between wrapped rows of chips.
                FlowRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)  // ← key fix
                ) {
                    languages.forEach { lang ->
                        val emoji = when (lang) {
                            "Urdu"    -> "🇵🇰"
                            "Punjabi" -> "🌾"
                            "English" -> "🇬🇧"
                            "Pashto"  -> "🏔️"
                            "Sindhi"  -> "🎋"
                            "Saraiki" -> "🌿"
                            else      -> "💬"
                        }
                        NoorSelectableChip(
                            label    = lang,
                            icon     = emoji,
                            selected = selectedLanguages.contains(lang),
                            onClick  = {
                                if (selectedLanguages.contains(lang))
                                    selectedLanguages.remove(lang)
                                else
                                    selectedLanguages.add(lang)
                            }
                        )
                    }
                }
            }

            // ── Continue ─────────────────────────────────────────────────────
            NoorPrimaryButton(
                text    = "Continue  →",
                onClick = onNext,
                enabled = isFormValid
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text          = text,
        fontSize      = 13.sp,
        fontWeight    = FontWeight.SemiBold,
        color         = NoorBlue,
        letterSpacing = 0.3.sp
    )
}