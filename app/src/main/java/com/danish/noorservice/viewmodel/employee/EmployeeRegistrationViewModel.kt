package com.danish.noorservice.viewmodel.employee

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.data.repository.ImageRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeRegistrationState(
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val isCategoriesLoading: Boolean = false,
    val error: String? = null,
    val fullName: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = "",
    val cnic: String = "",
    val dob: String = "",
    val city: String = "",
    val address: String = "",
    val languages: List<String> = listOf("Urdu"),
    val dailyRate: String = "",
    val hourlyRate: String = "",
    val monthlyRate: String = "",
    val photoUri: Uri? = null,
    val selectedServiceIds: List<String> = emptyList(),
    val serviceDetails: Map<String, ServiceDetailInput> = emptyMap(),
    val categories: List<Category> = emptyList()
)

data class ServiceDetailInput(
    val skills: List<String> = emptyList(),
    val experience: String = "",
    val availabilityDays: List<String> = emptyList(),
    val availabilityTime: String = "",
    val additionalNote: String = "",
    val dailyRate: String = ""
)

sealed class EmployeeRegistrationEvent {
    data object Success : EmployeeRegistrationEvent()
    data class Error(val message: String) : EmployeeRegistrationEvent()
}

@HiltViewModel
class EmployeeRegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeRegistrationState())
    val uiState: StateFlow<EmployeeRegistrationState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmployeeRegistrationEvent>()
    val events = _events.asSharedFlow()

    private var userId: String = ""

    fun setUserId(uid: String) { userId = uid }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCategoriesLoading = true)
            try {
                val allCategories = userRepository.getAllCategories()
                val individualCategories = allCategories.filter {
                    it.categoryType.equals("individual", ignoreCase = true) && it.isActive
                }
                _uiState.value = _uiState.value.copy(
                    categories = individualCategories,
                    isCategoriesLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCategoriesLoading = false,
                    error = e.message
                )
            }
        }
    }

fun updateFullName(value: String)  { _uiState.value = _uiState.value.copy(fullName  = value) }
    fun updateGender(value: String)    { _uiState.value = _uiState.value.copy(gender    = value) }
    fun updatePhone(value: String)     { _uiState.value = _uiState.value.copy(phone     = value) }
    fun updateEmail(value: String)     { _uiState.value = _uiState.value.copy(email     = value) }
    fun updateCnic(value: String)      { _uiState.value = _uiState.value.copy(cnic      = value) }
    fun updateDob(value: String)       { _uiState.value = _uiState.value.copy(dob       = value) }
    fun updateCity(value: String)      { _uiState.value = _uiState.value.copy(city      = value) }
    fun updateAddress(value: String)   { _uiState.value = _uiState.value.copy(address   = value) }
    fun updateLanguages(v: List<String>) { _uiState.value = _uiState.value.copy(languages = v) }
    fun updateDailyRate(value: String)   { _uiState.value = _uiState.value.copy(dailyRate   = value) }
    fun updateHourlyRate(value: String)  { _uiState.value = _uiState.value.copy(hourlyRate  = value) }
    fun updateMonthlyRate(value: String) { _uiState.value = _uiState.value.copy(monthlyRate = value) }
    fun setPhotoUri(uri: Uri?)         { _uiState.value = _uiState.value.copy(photoUri  = uri) }

    fun isStep1Valid(): Boolean {
        val s = _uiState.value
        return s.fullName.isNotBlank() && s.gender.isNotBlank() && s.phone.isNotBlank() &&
                s.cnic.isNotBlank() && s.city.isNotBlank() && s.languages.isNotEmpty()
    }

    fun goToStep2() {
        if (isStep1Valid()) _uiState.value = _uiState.value.copy(currentStep = 2)
    }

    fun updateSelectedServices(serviceIds: List<String>) {
        _uiState.value = _uiState.value.copy(selectedServiceIds = serviceIds)
    }

    fun isStep2Valid() = _uiState.value.selectedServiceIds.isNotEmpty()

    fun goToStep3() {
        if (isStep2Valid()) _uiState.value = _uiState.value.copy(currentStep = 3)
    }

    fun updateServiceDetail(serviceId: String, detail: ServiceDetailInput) {
        val current = _uiState.value.serviceDetails.toMutableMap()
        current[serviceId] = detail
        _uiState.value = _uiState.value.copy(serviceDetails = current)
    }

    fun isStep3Valid() = _uiState.value.serviceDetails.values.all { d ->
        d.experience.isNotBlank() && d.availabilityDays.isNotEmpty() && d.availabilityTime.isNotBlank()
    }

    fun goBack() {
        val current = _uiState.value.currentStep
        if (current > 1) _uiState.value = _uiState.value.copy(currentStep = current - 1)
    }

    fun saveEmployeeProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                var photoUrl = ""
                _uiState.value.photoUri?.let { uri ->
                    photoUrl = imageRepository.uploadProfileImage(uri, userId)
                }

                val employee = Employee(
                    uid               = userId,
                    fullName          = _uiState.value.fullName,
                    gender            = _uiState.value.gender,
                    phone             = _uiState.value.phone,
                    email             = _uiState.value.email,
                    cnic              = _uiState.value.cnic,
                    dob               = _uiState.value.dob,
                    city              = _uiState.value.city,
                    address           = _uiState.value.address,
                    languages         = _uiState.value.languages,
                    dailyRate         = _uiState.value.dailyRate,
                    hourlyRate        = _uiState.value.hourlyRate,
                    monthlyRate       = _uiState.value.monthlyRate,
                    photoUrl          = photoUrl,
                    isProfileApproved = false,
                    isAvailable       = true,
                    serviceIds        = _uiState.value.selectedServiceIds  // ← populated
                )

                userRepository.saveEmployeeProfile(employee)

                val services = _uiState.value.serviceDetails.map { (serviceId, detail) ->
                    EmployeeService(
                        serviceId        = serviceId,
                        skills           = detail.skills,
                        experience       = detail.experience,
                        availabilityDays = detail.availabilityDays,
                        availabilityTime = detail.availabilityTime,
                        additionalNote   = detail.additionalNote,
                        dailyRate        = detail.dailyRate
                    )
                }

                userRepository.saveEmployeeServices(userId, services)
                userRepository.updateUserProfileComplete(userId, true)

                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(EmployeeRegistrationEvent.Success)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Failed to save profile"
                )
                _events.emit(EmployeeRegistrationEvent.Error(e.message ?: "Failed to save profile"))
            }
        }
    }
}