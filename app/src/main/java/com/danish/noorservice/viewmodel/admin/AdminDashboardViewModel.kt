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
    val hasLoaded: Boolean = false,
    val totalEmployees: Int = 0,
    val totalEmployers: Int = 0,
    val totalVendors: Int = 0,
    val totalProposals: Int = 0,
    val pendingActions: Int = 0,
    val totalServices: Int = 0,
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
        if (_uiState.value.hasLoaded && _uiState.value.totalEmployees > 0) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val employees = userRepository.getAllApprovedEmployees()
                val employers = userRepository.getAllEmployers()
                val vendors = userRepository.getAllApprovedVendors()

                // Get total categories count from Firestore for services
                val allCategories = userRepository.getAllCategories()
                val categoryCount = allCategories.size

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLoaded = true,
                    totalEmployees = employees.size,
                    totalEmployers = employers.size,
                    totalVendors = vendors.filter { it.isProfileApproved && it.isActive }.size,
                    totalProposals = 0,
                    pendingActions = 0,
                    totalServices = categoryCount
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