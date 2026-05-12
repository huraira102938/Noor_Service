package com.danish.noorservice.viewmodel.employer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.ImageRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployerSettingsState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: Employer? = null,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class EmployerSettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerSettingsState())
    val uiState: StateFlow<EmployerSettingsState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val profile = userRepository.getEmployerProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun saveProfile(employer: Employer, newPhotoUri: Uri? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)

            try {
                var photoUrl = employer.photoUrl

                newPhotoUri?.let { uri ->
                    try {
                        photoUrl = imageRepository.uploadProfileImage(uri, employer.uid)
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = "Image upload failed: ${e.message}"
                        )
                        return@launch
                    }
                }

                val updatedEmployer = employer.copy(photoUrl = photoUrl)
                val result = userRepository.saveEmployerProfile(updatedEmployer)

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        profile = updatedEmployer,
                        saveSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = result.exceptionOrNull()?.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}