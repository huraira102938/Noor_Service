package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.*
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeRegistrationViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalInfoScreen(
    viewModel: EmployeeRegistrationViewModel,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var gender by remember { mutableStateOf(uiState.gender) }
    val languages         = listOf("Urdu", "Punjabi", "English", "Pashto", "Sindhi", "Saraiki")
    val selectedLanguages = remember { mutableStateListOf<String>().apply { addAll(uiState.languages) } }
    val genderOptions     = listOf("Male", "Female")

    val isFormValid = uiState.fullName.isNotBlank() && gender.isNotBlank() &&
            uiState.cnic.isNotBlank() && uiState.city.isNotBlank() && selectedLanguages.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
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
            NoorSectionCard {
                SectionLabel("Basic Details")
                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = uiState.fullName,
                    onValueChange = viewModel::updateFullName,
                    label         = "Full Name *",
                    placeholder   = "As on CNIC"
                )

                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value           = uiState.phone,
                    onValueChange   = viewModel::updatePhone,
                    label           = "Phone Number *",
                    placeholder     = "03XX-XXXXXXX",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(Modifier.height(14.dp))

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
                            onClick  = {
                                gender = opt
                                viewModel.updateGender(opt)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                NoorTextField(
                    value           = uiState.email,
                    onValueChange   = viewModel::updateEmail,
                    label           = "Email (optional)",
                    placeholder     = "you@email.com",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            NoorSectionCard {
                SectionLabel("My Rates (PKR)")
                Spacer(Modifier.height(4.dp))
                Text(
                    "Set your pricing so employers know what to expect.",
                    fontSize = 11.sp,
                    color = NoorTextHint,
                    lineHeight = 16.sp
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📅", fontSize = 18.sp)
                    }
                    NoorTextField(
                        value = uiState.dailyRate,
                        onValueChange = viewModel::updateDailyRate,
                        label = "Daily Rate",
                        placeholder = "e.g. 1,200",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("/ day", fontSize = 12.sp, color = NoorTextHint)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⏱️", fontSize = 18.sp)
                    }
                    NoorTextField(
                        value = uiState.hourlyRate,
                        onValueChange = viewModel::updateHourlyRate,
                        label = "Hourly Rate (optional)",
                        placeholder = "e.g. 150",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("/ hr", fontSize = 12.sp, color = NoorTextHint)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorOrangeLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🗓️", fontSize = 18.sp)
                    }
                    NoorTextField(
                        value = uiState.monthlyRate,
                        onValueChange = viewModel::updateMonthlyRate,
                        label = "Monthly Rate (optional)",
                        placeholder = "e.g. 25,000",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("/ mo", fontSize = 12.sp, color = NoorTextHint)
                }
            }

            NoorSectionCard {
                SectionLabel("Identity & Location")
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NoorTextField(
                        value           = uiState.cnic,
                        onValueChange   = viewModel::updateCnic,
                        label           = "CNIC *",
                        placeholder     = "XXXXX-XXXXXXX-X",
                        modifier        = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    NoorTextField(
                        value         = uiState.dob,
                        onValueChange = viewModel::updateDob,
                        label         = "Date of Birth",
                        placeholder   = "DD/MM/YYYY",
                        modifier      = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = uiState.city,
                    onValueChange = viewModel::updateCity,
                    label         = "City *",
                    placeholder   = "e.g. Lahore"
                )

                Spacer(Modifier.height(12.dp))

                NoorTextField(
                    value         = uiState.address,
                    onValueChange = viewModel::updateAddress,
                    label         = "Permanent Address",
                    placeholder   = "Street, Area, City",
                    singleLine    = false,
                    maxLines      = 3
                )
            }

            NoorSectionCard {
                SectionLabel("Languages Spoken *")
                Spacer(Modifier.height(12.dp))
                FlowRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(12.dp)
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
                                viewModel.updateLanguages(selectedLanguages.toList())
                            }
                        )
                    }
                }
            }

            NoorPrimaryButton(
                text    = "Continue  →",
                onClick = {
                    viewModel.goToStep2()
                    onNext()
                },
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