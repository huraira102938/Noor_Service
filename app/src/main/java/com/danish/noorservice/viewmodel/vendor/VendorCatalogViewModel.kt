package com.danish.noorservice.viewmodel.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VendorCatalogState(
    val isLoading: Boolean = false,
    val services: List<VendorService> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val savingSuccess: Boolean = false
)

@HiltViewModel
class VendorCatalogViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorCatalogState())
    val uiState: StateFlow<VendorCatalogState> = _uiState.asStateFlow()

    fun loadServices(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val services = userRepository.getVendorServices(userId)
                val allCategories = userRepository.getAllCategories()
                val vendorCategories = allCategories.filter {
                    it.categoryType.equals("vendor", ignoreCase = true) && it.isActive
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    services = services,
                    categories = vendorCategories
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val allCategories = userRepository.getAllCategories()
                val vendorCategories = allCategories.filter {
                    it.categoryType.equals("vendor", ignoreCase = true) && it.isActive
                }
                _uiState.value = _uiState.value.copy(categories = vendorCategories)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun getServiceName(serviceId: String): String {
        val category = _uiState.value.categories.find { it.id == serviceId }
        return category?.label ?: serviceId.replaceFirstChar { it.uppercaseChar() }.replace("_", " ")
    }

    fun getServiceEmoji(serviceId: String): String {
        val category = _uiState.value.categories.find { it.id == serviceId }
        return category?.emoji ?: "💼"
    }

    fun saveService(userId: String, service: VendorService) {
        viewModelScope.launch {
            try {
                val current = _uiState.value.services.toMutableList()
                val existingIndex = current.indexOfFirst { it.serviceId == service.serviceId }
                if (existingIndex >= 0) {
                    current[existingIndex] = service
                } else {
                    current.add(service)
                }
                userRepository.saveVendorServices(userId, current)
                _uiState.value = _uiState.value.copy(savingSuccess = true, services = current)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteService(userId: String, serviceId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteVendorService(userId, serviceId)
                val current = _uiState.value.services.filter { it.serviceId != serviceId }
                _uiState.value = _uiState.value.copy(services = current)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateServiceActive(userId: String, serviceId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val current = _uiState.value.services.toMutableList()
                val idx = current.indexOfFirst { it.serviceId == serviceId }
                if (idx >= 0) {
                    current[idx] = current[idx].copy(isActive = isActive)
                }
                userRepository.saveVendorServices(userId, current)
                _uiState.value = _uiState.value.copy(services = current)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(savingSuccess = false)
    }
}