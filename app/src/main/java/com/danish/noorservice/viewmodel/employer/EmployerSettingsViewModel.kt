package com.danish.noorservice.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployerSettingsState(
    val isLoading: Boolean = false,
    val profile: Employer? = null,
    val error: String? = null
)

@HiltViewModel
class EmployerSettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerSettingsState())
    val uiState: StateFlow<EmployerSettingsState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val profile = userRepository.getEmployerProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}