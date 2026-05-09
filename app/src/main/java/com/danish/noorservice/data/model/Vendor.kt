package com.danish.noorservice.data.model

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
    val isProfileApproved: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class VendorService(
    val serviceId: String = "",
    val pricingModel: String = "",
    val priceRange: String = "",
    val minContractDuration: String = "",
    val coverageAreas: List<String> = emptyList()
)