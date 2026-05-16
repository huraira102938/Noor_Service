package com.danish.noorservice.viewmodel.employer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
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
    val vendorServices: Map<String, List<VendorService>> = emptyMap(),
    val selectedVendor: Vendor? = null,
    val selectedVendorServices: List<VendorService> = emptyList(),
    val vendorCategories: List<Category> = emptyList(),
    val searchCity: String = "",
    val selectedService: String = "",
    val hasLoaded: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EmployerVendorBrowseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerVendorBrowseState())
    val uiState: StateFlow<EmployerVendorBrowseState> = _uiState.asStateFlow()

    fun loadVendors() {
        if (_uiState.value.hasLoaded && _uiState.value.vendors.isNotEmpty()) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                Log.d("EmployerVendorBrowse", "Loading vendors...")
                val allVendors = userRepository.getAllApprovedVendors()
                val categories = userRepository.getAllCategories()
                val vendorCategories = categories.filter { it.categoryType == "vendor" }
                Log.d("EmployerVendorBrowse", "Found ${allVendors.size} total vendors")
                Log.d("EmployerVendorBrowse", "Found ${vendorCategories.size} vendor categories")
                
                // Filter: profileApproved AND active
                val vendors = allVendors.filter { it.isProfileApproved && it.isActive }
                Log.d("EmployerVendorBrowse", "Filtered to ${vendors.size} approved+active vendors")
                vendors.forEach { v ->
                    Log.d("EmployerVendorBrowse", "Vendor: ${v.businessName}, profileApproved=${v.isProfileApproved}, active=${v.isActive}")
                }

                val vendorServicesMap = mutableMapOf<String, List<VendorService>>()
                
                vendors.forEach { vendor ->
                    try {
                        val services = userRepository.getVendorServices(vendor.uid)
                        vendorServicesMap[vendor.uid] = services
                    } catch (e: Exception) {
                        vendorServicesMap[vendor.uid] = emptyList()
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    vendors = vendors,
                    vendorServices = vendorServicesMap,
                    vendorCategories = vendorCategories,
                    hasLoaded = true
                )
            } catch (e: Exception) {
                Log.e("EmployerVendorBrowse", "Error: ${e.message}")
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
                    selectedVendorServices = services
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
            selectedVendorServices = emptyList()
        )
    }
}