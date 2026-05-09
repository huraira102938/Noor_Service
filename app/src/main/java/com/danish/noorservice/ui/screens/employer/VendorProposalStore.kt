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
    val status: VendorProposalStatus = VendorProposalStatus.PENDING
)

object VendorProposalStore {
    val proposals = mutableStateListOf<VendorProposal>()
}