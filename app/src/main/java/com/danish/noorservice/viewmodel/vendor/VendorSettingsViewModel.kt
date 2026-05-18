package com.danish.noorservice.viewmodel.vendor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.ImageRepository
import com.danish.noorservice.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class VendorSettingsState(
    val isLoading: Boolean = false,
    val profile: Vendor? = null,
    val isActive: Boolean? = null,
    val pushNotifications: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class VendorSettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val imageRepository: ImageRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorSettingsState())
    val uiState: StateFlow<VendorSettingsState> = _uiState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val profile = userRepository.getVendorProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile   = profile,
                    isActive  = profile?.isActive
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message
                )
            }
        }
    }

    /**
     * Updates ONLY the isActive field in Firestore.
     * Using a targeted field update (not set/copy) guarantees isProfileApproved
     * and every other field on the document are never touched.
     */
    fun updateActiveStatus(userId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("vendors")
                    .document(userId)
                    .update("isActive", isActive)
                    .await()

                val updatedProfile = _uiState.value.profile?.copy(isActive = isActive)
                _uiState.value = _uiState.value.copy(
                    isActive = isActive,
                    profile  = updatedProfile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updatePushNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(pushNotifications = enabled)
    }

    fun saveProfile(
        userId: String,
        businessName: String,
        contactPerson: String,
        phone: String,
        email: String,
        ntn: String,
        regNumber: String,
        city: String,
        address: String,
        bio: String,
        logoUrl: String,
        pendingLogoUri: Uri? = null,
        operatingCities: List<String>,
        serviceScale: String,
        yearsInBusiness: Int,
        isoCertified: Boolean,
        notableClients: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // ── Upload new logo if one was picked ──────────────────────
                var resolvedLogoUrl = logoUrl
                pendingLogoUri?.let { uri ->
                    try {
                        resolvedLogoUrl = imageRepository.uploadProfileImage(uri, userId)
                    } catch (e: Exception) {
                        onError("Image upload failed: ${e.message}")
                        return@launch
                    }
                }

                val current = _uiState.value.profile ?: Vendor(uid = userId)
                val updated = current.copy(
                    businessName    = businessName,
                    contactPerson   = contactPerson,
                    phone           = phone,
                    email           = email,
                    ntn             = ntn,
                    regNumber       = regNumber,
                    city            = city,
                    address         = address,
                    bio             = bio,
                    logoUrl         = resolvedLogoUrl,
                    operatingCities = operatingCities,
                    serviceScale    = serviceScale,
                    yearsInBusiness = yearsInBusiness,
                    isoCertified    = isoCertified,
                    notableClients  = notableClients
                    // isActive and isProfileApproved are preserved from `current`
                )

                userRepository.saveVendorProfile(updated)

                _uiState.value = _uiState.value.copy(
                    profile  = updated,
                    isActive = updated.isActive
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
                onError(e.message ?: "Failed to save")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}