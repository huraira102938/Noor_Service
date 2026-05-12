package com.danish.noorservice.viewmodel.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
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
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val hasLoaded: Boolean = false
)

@HiltViewModel
class EmployeeHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeHomeState())
    val uiState: StateFlow<EmployeeHomeState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadProfile(userId: String, forceRefresh: Boolean = false) {
        // Reset if userId changed (new login)
        if (userId != currentUserId) {
            currentUserId = userId
            _uiState.value = EmployeeHomeState()
        }

        if (!forceRefresh && _uiState.value.hasLoaded && _uiState.value.profile != null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val profile  = userRepository.getEmployeeProfile(userId)
                val services = userRepository.getEmployeeServices(userId)
                val allCategories = userRepository.getAllCategories()
                val individualCategories = allCategories.filter {
                    it.categoryType.equals("individual", ignoreCase = true) && it.isActive
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile   = profile,
                    services  = services,
                    categories = individualCategories,
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

    fun getServiceName(serviceId: String): String {
        val category = _uiState.value.categories.find { it.id == serviceId }
        return category?.label ?: serviceId.replaceFirstChar { it.uppercaseChar() }.replace("_", " ")
    }

    fun reset() {
        currentUserId = ""
        _uiState.value = EmployeeHomeState()
    }

    fun isProfileApproved(): Boolean = _uiState.value.profile?.isProfileApproved == true

    fun getApprovalStatusText(): String =
        if (isProfileApproved()) "Approved" else "Pending Review"
}