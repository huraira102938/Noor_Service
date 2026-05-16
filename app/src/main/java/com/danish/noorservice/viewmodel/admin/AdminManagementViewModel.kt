package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Employee
import com.danish.noorservice.data.model.EmployeeService
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminManagementState(
    val isLoading: Boolean = false,
    val hasLoaded: Boolean = false,
    val workers: List<Employee> = emptyList(),
    val employers: List<Employer> = emptyList(),
    val vendors: List<Vendor> = emptyList(),
    val workerCategories: List<Category> = emptyList(),
    val vendorCategories: List<Category> = emptyList(),
    val error: String? = null
)

data class EmployeeWithServices(
    val employee: Employee,
    val services: List<EmployeeService>
)

data class VendorWithServices(
    val vendor: Vendor,
    val services: List<VendorService>
)

@HiltViewModel
class AdminManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminManagementState())
    val uiState: StateFlow<AdminManagementState> = _uiState.asStateFlow()

    private val _employeeServices = MutableStateFlow<Map<String, List<EmployeeService>>>(emptyMap())
    val employeeServices: StateFlow<Map<String, List<EmployeeService>>> = _employeeServices.asStateFlow()

    private val _vendorServices = MutableStateFlow<Map<String, List<VendorService>>>(emptyMap())
    val vendorServices: StateFlow<Map<String, List<VendorService>>> = _vendorServices.asStateFlow()

    fun loadAllData(forceReload: Boolean = false) {
        if (!forceReload && _uiState.value.hasLoaded && !_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val employees = userRepository.getAllEmployees()
                val employers = userRepository.getAllEmployers()
                val vendors = userRepository.getAllVendors()
                val categories = userRepository.getAllCategories()
                val workerCategories = categories.filter { it.categoryType == "individual" || it.categoryType.isEmpty() }
                val vendorCategories = categories.filter { it.categoryType == "vendor" }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasLoaded = true,
                    workers = employees,
                    employers = employers,
                    vendors = vendors,
                    workerCategories = workerCategories,
                    vendorCategories = vendorCategories
                )

                loadEmployeeServices(employees.map { it.uid })
                loadVendorServices(vendors.map { it.uid })
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun loadEmployeeServices(workerIds: List<String>) {
        viewModelScope.launch {
            val servicesMap = mutableMapOf<String, List<EmployeeService>>()
            workerIds.forEach { workerId ->
                try {
                    val services = userRepository.getEmployeeServices(workerId)
                    if (services.isNotEmpty()) {
                        servicesMap[workerId] = services
                    }
                } catch (e: Exception) {
                    // Ignore errors for individual services
                }
            }
            _employeeServices.value = servicesMap
        }
    }

    private fun loadVendorServices(vendorIds: List<String>) {
        viewModelScope.launch {
            val servicesMap = mutableMapOf<String, List<VendorService>>()
            vendorIds.forEach { vendorId ->
                try {
                    val services = userRepository.getVendorServices(vendorId)
                    if (services.isNotEmpty()) {
                        servicesMap[vendorId] = services
                    }
                } catch (e: Exception) {
                    // Ignore errors for individual services
                }
            }
            _vendorServices.value = servicesMap
        }
    }

    fun updateProfileApproval(userId: String, role: String, isApproved: Boolean) {
        viewModelScope.launch {
            try {
                val result = userRepository.updateProfileApproval(userId, role, isApproved)
                if (result.isSuccess) {
                    loadAllData(forceReload = true)
                } else {
                    _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateUserActive(userId: String, role: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val result = userRepository.updateUserActive(userId, role, isActive)
                if (result.isSuccess) {
                    loadAllData(forceReload = true)
                } else {
                    _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun refreshData() {
        _uiState.value = _uiState.value.copy(hasLoaded = false, isLoading = false)
        loadAllData()
    }
}