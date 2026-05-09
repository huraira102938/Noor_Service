package com.danish.noorservice.viewmodel.employer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.model.VendorService
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployerVendorBrowseState(
    val isLoading: Boolean = false,
    val vendors: List<Vendor> = emptyList(),
    val selectedVendor: Vendor? = null,
    val vendorServices: List<VendorService> = emptyList(),
    val searchCity: String = "",
    val selectedService: String = "",
    val error: String? = null
)

@HiltViewModel
class EmployerVendorBrowseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerVendorBrowseState())
    val uiState: StateFlow<EmployerVendorBrowseState> = _uiState.asStateFlow()

    fun loadVendors() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val vendors = userRepository.getAllApprovedVendors()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    vendors = vendors
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun selectVendor(vendorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val vendor = userRepository.getVendorProfile(vendorId)
                val services = userRepository.getVendorServices(vendorId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedVendor = vendor,
                    vendorServices = services
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearSelectedVendor() {
        _uiState.value = _uiState.value.copy(
            selectedVendor = null,
            vendorServices = emptyList()
        )
    }
}