package com.danish.noorservice.viewmodel.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeHomeState(
    val isLoading: Boolean = false,
    val profile: Employee? = null,
    val services: List<EmployeeService> = emptyList(),
    val error: String? = null,
    // ✅ FIX: Track whether we've loaded at least once so we never show
    // the loading spinner again when the user switches tabs and comes back.
    val hasLoaded: Boolean = false
)

@HiltViewModel
class EmployeeHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeHomeState())
    val uiState: StateFlow<EmployeeHomeState> = _uiState.asStateFlow()

    /**
     * Load the employee profile + services from Firestore.
     *
     * ✅ FIX: If data has already been loaded once (hasLoaded = true) we skip
     * the fetch entirely so navigating Home → Notifications → Home does NOT
     * trigger a second loading spinner.  Call [forceRefresh] = true from the
     * settings screen after saving changes to get fresh data.
     */
    fun loadProfile(userId: String, forceRefresh: Boolean = false) {
        if (_uiState.value.hasLoaded && !forceRefresh) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val profile  = userRepository.getEmployeeProfile(userId)
                val services = userRepository.getEmployeeServices(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile   = profile,
                    services  = services,
                    hasLoaded = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message
                )
            }
        }
    }

    fun isProfileApproved(): Boolean = _uiState.value.profile?.isProfileApproved == true

    fun getApprovalStatusText(): String =
        if (isProfileApproved()) "Approved" else "Pending Review"
}