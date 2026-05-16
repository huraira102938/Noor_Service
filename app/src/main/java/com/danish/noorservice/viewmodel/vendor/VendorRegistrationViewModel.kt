package com.danish.noorservice.viewmodel.vendor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Category
import com.danish.noorservice.data.model.Vendor
import com.danish.noorservice.data.model.VendorService
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

data class VendorRegistrationState(
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val isCategoriesLoading: Boolean = false,
    val error: String? = null,
    val categories: List<Category> = emptyList(),

    // Step 1
    val businessName: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    val ntn: String = "",
    val regNumber: String = "",
    val city: String = "",
    val address: String = "",
    val bio: String = "",
    val operatingCities: List<String> = emptyList(),
    val logoUri: Uri? = null,

    // Step 2
    val selectedServiceIds: List<String> = emptyList(),
    val serviceDetails: Map<String, VendorServiceInput> = emptyMap(),
    val serviceScale: String = "",
    val yearsInBusiness: String = "",
    val isoCertified: Boolean = false,
    val notableClients: List<String> = emptyList()
)

data class VendorServiceInput(
    val pricingModel: String = "",
    val priceRange: String = "",
    val minContractDuration: String = "",
    val coverageAreas: List<String> = emptyList(),
    val skills: List<String> = emptyList()
)

sealed class VendorRegistrationEvent {
    data object Success : VendorRegistrationEvent()
    data class Error(val message: String) : VendorRegistrationEvent()
}

@HiltViewModel
class VendorRegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorRegistrationState())
    val uiState: StateFlow<VendorRegistrationState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<VendorRegistrationEvent>()
    val events = _events.asSharedFlow()

    private var userId: String = ""

    fun setUserId(uid: String) {
        userId = uid
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCategoriesLoading = true)
            try {
                val allCategories = userRepository.getAllCategories()
                val vendorCategories = allCategories.filter {
                    it.categoryType.equals("vendor", ignoreCase = true) && it.isActive
                }
                _uiState.value = _uiState.value.copy(
                    categories = vendorCategories,
                    isCategoriesLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCategoriesLoading = false,
                    error = e.message
                )
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

    fun getCategorySkills(serviceId: String): List<String> {
        val category = _uiState.value.categories.find { it.id == serviceId }
        return category?.skills?.map { it.name } ?: emptyList()
    }

    // Step 1 fields
    fun updateBusinessName(value: String)  { _uiState.value = _uiState.value.copy(businessName  = value) }
    fun updateContactPerson(value: String) { _uiState.value = _uiState.value.copy(contactPerson = value) }
    fun updatePhone(value: String)         { _uiState.value = _uiState.value.copy(phone         = value) }
    fun updateEmail(value: String)         { _uiState.value = _uiState.value.copy(email         = value) }
    fun updateNtn(value: String)           { _uiState.value = _uiState.value.copy(ntn           = value) }
    fun updateRegNumber(value: String)     { _uiState.value = _uiState.value.copy(regNumber     = value) }
    fun updateCity(value: String)          { _uiState.value = _uiState.value.copy(city          = value) }
    fun updateAddress(value: String)       { _uiState.value = _uiState.value.copy(address       = value) }
    fun updateBio(value: String)           { if (value.length <= 300) _uiState.value = _uiState.value.copy(bio = value) }
    fun updateOperatingCities(cities: List<String>) { _uiState.value = _uiState.value.copy(operatingCities = cities) }
    fun setLogoUri(uri: Uri?)              { _uiState.value = _uiState.value.copy(logoUri       = uri) }

    fun isStep1Valid(): Boolean {
        val state = _uiState.value
        return state.businessName.isNotBlank() && state.contactPerson.isNotBlank() &&
                state.phone.isNotBlank() && state.city.isNotBlank()
    }

    fun goToStep2() {
        if (isStep1Valid()) {
            _uiState.value = _uiState.value.copy(currentStep = 2)
        }
    }

    // Step 2 fields
    fun updateSelectedServices(serviceIds: List<String>) {
        _uiState.value = _uiState.value.copy(selectedServiceIds = serviceIds)
    }

    fun updateServiceDetail(serviceId: String, detail: VendorServiceInput) {
        val current = _uiState.value.serviceDetails.toMutableMap()
        current[serviceId] = detail
        _uiState.value = _uiState.value.copy(serviceDetails = current)
    }

    fun updateServiceScale(value: String)          { _uiState.value = _uiState.value.copy(serviceScale     = value) }
    fun updateYearsInBusiness(value: String)        { _uiState.value = _uiState.value.copy(yearsInBusiness  = value) }
    fun updateIsoCertified(value: Boolean)          { _uiState.value = _uiState.value.copy(isoCertified     = value) }
    fun updateNotableClients(clients: List<String>) { _uiState.value = _uiState.value.copy(notableClients   = clients) }

    fun isStep2Valid(): Boolean {
        val state = _uiState.value
        return state.selectedServiceIds.isNotEmpty() && state.serviceScale.isNotBlank() &&
                state.serviceDetails.values.all { it.pricingModel.isNotBlank() && it.priceRange.isNotBlank() }
    }

    fun goBack() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1)
        }
    }

    fun saveVendorProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                var logoUrl = ""
                _uiState.value.logoUri?.let { uri ->
                    logoUrl = imageRepository.uploadVendorLogo(uri, userId)
                }

                val vendor = Vendor(
                    uid           = userId,
                    businessName  = _uiState.value.businessName,
                    contactPerson = _uiState.value.contactPerson,
                    phone         = _uiState.value.phone,
                    email         = _uiState.value.email,
                    ntn           = _uiState.value.ntn,
                    regNumber     = _uiState.value.regNumber,
                    city          = _uiState.value.city,
                    address       = _uiState.value.address,
                    logoUrl       = logoUrl,
                    bio           = _uiState.value.bio,
                    operatingCities  = _uiState.value.operatingCities,
                    serviceScale     = _uiState.value.serviceScale,
                    yearsInBusiness  = _uiState.value.yearsInBusiness.toIntOrNull() ?: 0,
                    isoCertified     = _uiState.value.isoCertified,
                    notableClients   = _uiState.value.notableClients,
                    isProfileApproved = false,
                    isActive          = true
                )

                userRepository.saveVendorProfile(vendor)

                val services = _uiState.value.serviceDetails.map { (serviceId, detail) ->
                    VendorService(
                        serviceId           = serviceId,
                        pricingModel        = detail.pricingModel,
                        priceRange          = detail.priceRange,
                        minContractDuration = detail.minContractDuration,
                        coverageAreas       = detail.coverageAreas,
                        skills              = detail.skills
                    )
                }

                userRepository.saveVendorServices(userId, services)

                // ✅ FIX: Mark profile complete in Firestore so the app
                // routes to VendorHome on next launch instead of back
                // to the registration screen.
                userRepository.updateUserProfileComplete(userId, true)

                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(VendorRegistrationEvent.Success)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error     = e.message ?: "Failed to save profile"
                )
                _events.emit(VendorRegistrationEvent.Error(e.message ?: "Failed to save profile"))
            }
        }
    }
}