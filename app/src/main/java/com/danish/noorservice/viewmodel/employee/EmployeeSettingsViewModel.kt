package com.danish.noorservice.viewmodel.employee

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.ImageRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeSettingsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: Employee? = null,
    val services: List<EmployeeService> = emptyList(),
    val isActive: Boolean = true,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val hasLoaded: Boolean = false
)

@HiltViewModel
class EmployeeSettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeSettingsState())
    val uiState: StateFlow<EmployeeSettingsState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadProfile(userId: String) {
        // Reset if userId changed (new login)
        if (userId != currentUserId) {
            currentUserId = userId
            _uiState.value = EmployeeSettingsState()
        }

        if (_uiState.value.hasLoaded && _uiState.value.profile != null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val profile  = userRepository.getEmployeeProfile(userId)
                val services = userRepository.getEmployeeServices(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile   = profile,
                    services  = services,
                    isActive  = profile?.isAvailable ?: true,
                    hasLoaded = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message,
                    hasLoaded = true
                )
            }
        }
    }

    fun reset() {
        currentUserId = ""
        _uiState.value = EmployeeSettingsState()
    }

    fun saveProfile(
        userId: String,
        fullName: String,
        email: String,
        phone: String,
        cnic: String,
        city: String,
        address: String,
        gender: String,
        dob: String,
        bio: String,
        languages: List<String>,
        dailyRate: String = "",
        hourlyRate: String = "",
        monthlyRate: String = "",
        serviceIds: List<String> = emptyList(),
        updatedServices: List<EmployeeService> = emptyList(),
        photoUri: Uri? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)
            try {
                val photoUrl = if (photoUri != null) {
                    imageRepository.uploadProfileImage(photoUri, userId)
                } else {
                    _uiState.value.profile?.photoUrl ?: ""
                }

                val updated = (_uiState.value.profile ?: Employee(uid = userId)).copy(
                    fullName     = fullName,
                    email        = email,
                    phone        = phone,
                    cnic         = cnic,
                    city         = city,
                    address      = address,
                    gender       = gender,
                    dob          = dob,
                    bio          = bio,
                    languages    = languages,
                    dailyRate    = dailyRate,
                    hourlyRate   = hourlyRate,
                    monthlyRate  = monthlyRate,
                    serviceIds   = serviceIds,
                    photoUrl     = photoUrl,
                    lastUpdated  = System.currentTimeMillis()
                )

                userRepository.saveEmployeeProfile(updated)

                if (updatedServices.isNotEmpty()) {
                    userRepository.saveEmployeeServices(userId, updatedServices)
                }

                _uiState.value = _uiState.value.copy(
                    isSaving    = false,
                    profile     = updated,
                    services    = if (updatedServices.isNotEmpty()) updatedServices
                    else _uiState.value.services,
                    saveSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error    = e.message
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun updateAvailability(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile ?: return@launch
                val updatedProfile = currentProfile.copy(isAvailable = isActive)
                userRepository.saveEmployeeProfile(updatedProfile)
                _uiState.value = _uiState.value.copy(
                    isActive = isActive,
                    profile  = updatedProfile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}