package com.danish.noorservice.viewmodel.employer

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Employer
import com.danish.noorservice.data.model.Proposal
import com.danish.noorservice.data.model.VendorServiceDetail
import com.danish.noorservice.data.model.WorkerServiceDetail
import com.danish.noorservice.data.repository.AuthRepository
import com.danish.noorservice.data.repository.ProposalRepository
import com.danish.noorservice.data.repository.UserRepository
import com.danish.noorservice.ui.screens.employer.WorkerProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EmployerProposalState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastSentId: String? = null
)

@HiltViewModel
class EmployerProposalViewModel @Inject constructor(
    private val proposalRepository: ProposalRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployerProposalState())
    val uiState: StateFlow<EmployerProposalState> = _uiState.asStateFlow()

    private var cachedEmployer: Employer? = null

    init {
        syncStores()
        loadEmployerProfile()
    }

    private fun loadEmployerProfile() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserUid()
            if (uid != null) {
                cachedEmployer = userRepository.getEmployerProfile(uid)
            }
        }
    }

    fun syncStores() {
        viewModelScope.launch {
            proposalRepository.syncStoresFromFirestore()
        }
    }

    fun sendWorkerProposal(
        worker: WorkerProfile,
        services: List<WorkerServiceDetail>,
        jobTitle: String,
        serviceLabel: String,
        location: String,
        schedule: String,
        startDate: String,
        offerPrice: String,
        note: String,
        employerProfile: com.danish.noorservice.data.model.Employer? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val currentEmployer = employerProfile ?: cachedEmployer ?: Employer()
                Log.d("EmployerProposal", "sendWorkerProposal: employerProfile=${employerProfile?.fullName}/${employerProfile?.phone}, cachedEmployer=${cachedEmployer?.fullName}/${cachedEmployer?.phone}")
                Log.d("EmployerProposal", "sendWorkerProposal: currentEmployer name=${currentEmployer.fullName} uid=${currentEmployer.uid}")
                val proposal = Proposal(
                    id = UUID.randomUUID().toString(),
                    proposalType = "worker",
                    status = "pending",
                    sentAt = System.currentTimeMillis(),
                    eUid = currentEmployer.uid,
                    eFullName = currentEmployer.fullName,
                    eEmail = currentEmployer.email,
                    ePhone = currentEmployer.phone,
                    eCity = currentEmployer.city,
                    eArea = currentEmployer.area,
                    eAddress = currentEmployer.address,
                    ePhotoUrl = currentEmployer.photoUrl,
                    wId = worker.id,
                    wName = worker.name,
                    wInitials = worker.initials,
                    wAvatarColorValue = worker.avatarColor.value.toInt(),
                    wPhotoUrl = worker.photoUrl,
                    wUsername = worker.workerUsername,
                    wCity = worker.city,
                    wArea = worker.area,
                    wPhone = worker.phone,
                    wEmail = worker.email,
                    wCnic = worker.cnic,
                    wDob = worker.dob,
                    wGender = worker.gender,
                    wAddress = worker.address,
                    wServiceIds = worker.serviceIds,
                    wSkills = worker.skills.ifEmpty { services.flatMap { it.skills } },
                    wLanguages = worker.languages,
                    wExperience = worker.experience,
                    wLicenceType = worker.licenceType,
                    wAvailableDays = worker.availableDays,
                    wTimeSlot = worker.timeSlot,
                    wAdditionalNote = worker.additionalNote,
                    wIsAvailable = worker.isAvailable,
                    wJoinedDate = worker.joinedDate,
                    wDailyRate = worker.dailyRate,
                    wHourlyRate = worker.hourlyRate,
                    wMonthlyRate = worker.monthlyRate,
                    wBio = worker.bio,
                    wServices = services,
                    jobTitle = jobTitle,
                    serviceLabel = serviceLabel,
                    location = location,
                    schedule = schedule,
                    startDate = startDate,
                    offerPrice = offerPrice,
                    note = note
                )
                val result = proposalRepository.writeProposal(proposal)
                result.fold(
                    onSuccess = { id ->
                        _uiState.value = _uiState.value.copy(isLoading = false, lastSentId = id)
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun sendVendorProposal(
        vendorId: String,
        vendorBusinessName: String,
        vendorContactPerson: String,
        vendorPhone: String,
        vendorEmail: String,
        vendorNtn: String,
        vendorRegNumber: String,
        vendorCity: String,
        vendorAddress: String,
        vendorLogoUrl: String,
        vendorBio: String,
        vendorOperatingCities: List<String>,
        vendorServiceScale: String,
        vendorYearsInBusiness: Int,
        vendorIsoCertified: Boolean,
        vendorNotableClients: List<String>,
        vendorHeadOffice: String,
        vendorWorkforceScale: String,
        services: List<VendorServiceDetail>,
        jobTitle: String,
        serviceLabel: String,
        location: String,
        schedule: String,
        startDate: String,
        offerPrice: String,
        note: String,
        employerProfile: com.danish.noorservice.data.model.Employer? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val currentEmployer = employerProfile ?: cachedEmployer ?: Employer()
                val proposal = Proposal(
                    id = UUID.randomUUID().toString(),
                    proposalType = "vendor",
                    status = "pending",
                    sentAt = System.currentTimeMillis(),
                    eUid = currentEmployer.uid,
                    eFullName = currentEmployer.fullName,
                    eEmail = currentEmployer.email,
                    ePhone = currentEmployer.phone,
                    eCity = currentEmployer.city,
                    eArea = currentEmployer.area,
                    eAddress = currentEmployer.address,
                    ePhotoUrl = currentEmployer.photoUrl,
                    vId = vendorId,
                    vBusinessName = vendorBusinessName,
                    vContactPerson = vendorContactPerson,
                    vPhone = vendorPhone,
                    vEmail = vendorEmail,
                    vNtn = vendorNtn,
                    vRegNumber = vendorRegNumber,
                    vCity = vendorCity,
                    vAddress = vendorAddress,
                    vLogoUrl = vendorLogoUrl,
                    vBio = vendorBio,
                    vOperatingCities = vendorOperatingCities,
                    vServiceScale = vendorServiceScale,
                    vYearsInBusiness = vendorYearsInBusiness,
                    vIsoCertified = vendorIsoCertified,
                    vNotableClients = vendorNotableClients,
                    vHeadOffice = vendorHeadOffice,
                    vWorkforceScale = vendorWorkforceScale,
                    vServices = services,
                    jobTitle = jobTitle,
                    serviceLabel = serviceLabel,
                    location = location,
                    schedule = schedule,
                    startDate = startDate,
                    offerPrice = offerPrice,
                    note = note
                )
                val result = proposalRepository.writeProposal(proposal)
                result.fold(
                    onSuccess = { id ->
                        _uiState.value = _uiState.value.copy(isLoading = false, lastSentId = id)
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}