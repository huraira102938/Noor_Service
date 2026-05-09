package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardState(
    val isLoading: Boolean = false,
    val totalEmployees: Int = 0,
    val totalEmployers: Int = 0,
    val totalVendors: Int = 0,
    val pendingEmployees: Int = 0,
    val pendingVendors: Int = 0,
    val recentEmployees: List<Employee> = emptyList(),
    val recentVendors: List<Vendor> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardState())
    val uiState: StateFlow<AdminDashboardState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // For now, we'll just show 0 since we don't have all users fetched
                // In a real implementation, you'd have a repository method to get all users

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    totalEmployees = 0,
                    totalEmployers = 0,
                    totalVendors = 0,
                    pendingEmployees = 0,
                    pendingVendors = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun approveProfile(userId: String, role: String) {
        viewModelScope.launch {
            try {
                userRepository.updateProfileApproval(userId, role, true)
                loadDashboard()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun rejectProfile(userId: String, role: String) {
        viewModelScope.launch {
            try {
                userRepository.updateProfileApproval(userId, role, false)
                loadDashboard()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}