package com.danish.noorservice.viewmodel.employer

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

data class EmployerBrowseState(
    val isLoading: Boolean = false,
    val employees: List<Employee> = emptyList(),
    val selectedEmployee: Employee? = null,
    val employeeServices: List<EmployeeService> = emptyList(),
    val searchCity: String = "",
    val selectedService: String = "",
    val error: String? = null
)

@HiltViewModel
class EmployerBrowseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerBrowseState())
    val uiState: StateFlow<EmployerBrowseState> = _uiState.asStateFlow()

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val employees = userRepository.getAllApprovedEmployees()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    employees = employees
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun searchEmployees(city: String?, serviceId: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val employees = userRepository.searchEmployees(city, serviceId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    employees = employees,
                    searchCity = city ?: "",
                    selectedService = serviceId ?: ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectEmployee(employeeId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val employee = userRepository.getEmployeeProfile(employeeId)
                val services = userRepository.getEmployeeServices(employeeId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedEmployee = employee,
                    employeeServices = services
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearSelectedEmployee() {
        _uiState.value = _uiState.value.copy(
            selectedEmployee = null,
            employeeServices = emptyList()
        )
    }

    fun updateSearchCity(city: String) {
        _uiState.value = _uiState.value.copy(searchCity = city)
    }

    fun updateSelectedService(service: String) {
        _uiState.value = _uiState.value.copy(selectedService = service)
    }
}