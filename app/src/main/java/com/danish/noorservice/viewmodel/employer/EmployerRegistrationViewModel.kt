package com.danish.noorservice.viewmodel.employer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.repository.ImageRepository
import com.danish.noorservice.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployerRegistrationState(
    val isLoading: Boolean = false,
    val error: String? = null,

    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val city: String = "",
    val area: String = "",
    val address: String = "",
    val about: String = "",
    val photoUri: Uri? = null
)

sealed class EmployerRegistrationEvent {
    data object Success : EmployerRegistrationEvent()
    data class Error(val message: String) : EmployerRegistrationEvent()
}

@HiltViewModel
class EmployerRegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerRegistrationState())
    val uiState: StateFlow<EmployerRegistrationState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EmployerRegistrationEvent>()
    val events = _events.asSharedFlow()

    private var userId: String = ""

    fun setUserId(uid: String) {
        userId = uid
    }

    fun updateFullName(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value)
    }

    fun updatePhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun updateCity(value: String) {
        _uiState.value = _uiState.value.copy(city = value)
    }

    fun updateArea(value: String) {
        _uiState.value = _uiState.value.copy(area = value)
    }

    fun updateAddress(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun updateAbout(value: String) {
        if (value.length <= 200) {
            _uiState.value = _uiState.value.copy(about = value)
        }
    }

    fun setPhotoUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(photoUri = uri)
    }

    fun isFormValid(): Boolean {
        val s = _uiState.value
        return s.fullName.isNotBlank()
                && s.phone.isNotBlank()
                && s.email.isNotBlank()
                && s.city.isNotBlank()
                && s.area.isNotBlank()
                && s.address.isNotBlank()
    }

    fun saveEmployerProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Photo is optional — only upload if user picked one
                var photoUrl = ""
                _uiState.value.photoUri?.let { uri ->
                    photoUrl = imageRepository.uploadProfileImage(uri, userId)
                }

                val employer = Employer(
                    uid      = userId,
                    fullName = _uiState.value.fullName,
                    phone    = _uiState.value.phone,
                    email    = _uiState.value.email,
                    city     = _uiState.value.city,
                    area     = _uiState.value.area,
                    address  = _uiState.value.address,
                    about    = _uiState.value.about,
                    photoUrl = photoUrl
                )

                userRepository.saveEmployerProfile(employer)
                userRepository.updateUserProfileComplete(userId, true)

                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(EmployerRegistrationEvent.Success)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Failed to save profile"
                )
                _events.emit(EmployerRegistrationEvent.Error(e.message ?: "Failed to save profile"))
            }
        }
    }
}