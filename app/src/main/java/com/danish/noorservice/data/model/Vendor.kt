package com.danish.noorservice.data.model

import com.google.firebase.firestore.PropertyName

data class Vendor(
    val uid: String = "",
    val businessName: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    val ntn: String = "",
    val regNumber: String = "",
    val city: String = "",
    val address: String = "",
    val logoUrl: String = "",
    val bio: String = "",
    val operatingCities: List<String> = emptyList(),
    val serviceScale: String = "",
    val yearsInBusiness: Int = 0,
    val isoCertified: Boolean = false,
    val notableClients: List<String> = emptyList(),
    val headOffice: String = "",
    @PropertyName("profileApproved") val isProfileApproved: Boolean = false,
    @PropertyName("active") val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class VendorService(
    val serviceId: String = "",
    val pricingModel: String = "",
    val priceRange: String = "",
    val minContractDuration: String = "",
    val coverageAreas: List<String> = emptyList(),
    @PropertyName("active") val isActive: Boolean = true,
    val description: String = "",
    val highlights: List<String> = emptyList(),
    val skills: List<String> = emptyList()
)