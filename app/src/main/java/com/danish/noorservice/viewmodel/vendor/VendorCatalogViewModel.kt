package com.danish.noorservice.viewmodel.vendor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error: String? = null
)

@HiltViewModel
class VendorCatalogViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorCatalogState())
    val uiState: StateFlow<VendorCatalogState> = _uiState.asStateFlow()

    fun loadServices(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val services = userRepository.getVendorServices(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
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