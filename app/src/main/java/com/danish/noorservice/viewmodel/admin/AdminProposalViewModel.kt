package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.repository.ProposalRepository
import com.danish.noorservice.ui.screens.employer.AdminProposalStore
import com.danish.noorservice.ui.screens.employer.VendorProposalStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminProposalState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminProposalViewModel @Inject constructor(
    private val proposalRepository: ProposalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminProposalState())
    val uiState: StateFlow<AdminProposalState> = _uiState.asStateFlow()

    fun loadAllProposals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            proposalRepository.syncStoresFromFirestore()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun acceptWorkerProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            proposalRepository.updateProposalStatus(proposalId, "accepted").fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            )
        }
    }

    fun declineWorkerProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            proposalRepository.updateProposalStatus(proposalId, "declined").fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            )
        }
    }

    fun acceptVendorProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            proposalRepository.updateProposalStatus(proposalId, "accepted").fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            )
        }
    }

    fun declineVendorProposal(proposalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            proposalRepository.updateProposalStatus(proposalId, "declined").fold(
                onSuccess = { _uiState.value = _uiState.value.copy(isLoading = false) },
                onFailure = { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
            )
        }
    }
}