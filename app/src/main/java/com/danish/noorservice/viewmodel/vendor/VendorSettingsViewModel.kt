package com.danish.noorservice.viewmodel.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorSettingsState(
    val isLoading: Boolean = false,
    val profile: Vendor? = null,
    val isActive: Boolean = true,
    val pushNotifications: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class VendorSettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorSettingsState())
    val uiState: StateFlow<VendorSettingsState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val profile = userRepository.getVendorProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    isActive = profile?.isActive ?: true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateActiveStatus(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile ?: return@launch
                val updatedProfile = currentProfile.copy(isActive = isActive)
                userRepository.saveVendorProfile(updatedProfile)
                _uiState.value = _uiState.value.copy(isActive = isActive)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updatePushNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(pushNotifications = enabled)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}