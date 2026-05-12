package com.danish.noorservice.viewmodel.vendor

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

data class VendorHomeState(
    val isLoading: Boolean = false,
    val profile: Vendor? = null,
    val services: List<VendorService> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class VendorHomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorHomeState())
    val uiState: StateFlow<VendorHomeState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val profile = userRepository.getVendorProfile(userId)
                val services = userRepository.getVendorServices(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile,
                    services = services
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