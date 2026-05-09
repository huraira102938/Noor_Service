package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminSettingsState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminSettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSettingsState())
    val uiState: StateFlow<AdminSettingsState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}