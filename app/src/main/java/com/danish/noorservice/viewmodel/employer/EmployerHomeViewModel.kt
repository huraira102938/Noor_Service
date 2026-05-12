package com.danish.noorservice.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployerHomeState(
    val isLoading: Boolean = false,
    val profile: Employer? = null,
    val workerCount: Int = 0,
    val vendorCount: Int = 0,
    val serviceCount: Int = 0,
    val hasLoaded: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmployerHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerHomeState())
    val uiState: StateFlow<EmployerHomeState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        if (_uiState.value.hasLoaded && _uiState.value.profile != null) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val profile = userRepository.getEmployerProfile(userId)
                
                val employees = userRepository.getAllApprovedEmployees()
                val vendors = userRepository.getAllApprovedVendors()
                
                val workerCount = employees.size
                val vendorCount = vendors.filter { it.isProfileApproved && it.isActive }.size
                
                var serviceCount = 0
                employees.forEach { employee ->
                    val services = userRepository.getEmployeeServices(employee.uid)
                    serviceCount += services.size
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    workerCount = workerCount,
                    vendorCount = vendorCount,
                    serviceCount = serviceCount,
                    hasLoaded = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}