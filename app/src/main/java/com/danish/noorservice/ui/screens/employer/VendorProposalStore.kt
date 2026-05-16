package com.danish.noorservice.ui.screens.employer

import androidx.compose.runtime.mutableStateListOf

enum class VendorProposalStatus {
    PENDING, ACCEPTED, DECLINED
}

data class VendorProposal(
    val id: String,
    val vendorName: String,
    val vendorEmoji: String,
    val vendorCity: String,
    val serviceLabel: String,
    val jobTitle: String,
    val location: String,
    val schedule: String,
    val startDate: String,
    val budget: String,
    val note: String,
    val sentAt: String,
    val status: VendorProposalStatus = VendorProposalStatus.PENDING,
    val employerName: String = "",
    val employerPhone: String = "",
    val employerEmail: String = "",
    val employerCity: String = "",
    val employerArea: String = "",
    val employerAddress: String = "",
    val vendorId: String = "",
    val vendorContactPerson: String = "",
    val vendorPhoneFull: String = "",
    val vendorEmail: String = "",
    val vendorNtn: String = "",
    val vendorRegNumber: String = "",
    val vendorAddress: String = "",
    val vendorLogoUrl: String = "",
    val vendorBio: String = "",
    val vendorOperatingCities: List<String> = emptyList(),
    val vendorServiceScale: String = "",
    val vendorYearsInBusiness: Int = 0,
    val vendorIsoCertified: Boolean = false,
    val vendorNotableClients: List<String> = emptyList(),
    val vendorHeadOffice: String = "",
    val vendorWorkforceScale: String = ""
)

object VendorProposalStore {
    val proposals = mutableStateListOf<VendorProposal>()
}