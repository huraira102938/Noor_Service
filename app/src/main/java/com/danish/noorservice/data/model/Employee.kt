package com.danish.noorservice.data.model

data class Employee(
    val uid: String = "",
    val fullName: String = "",
    val gender: String = "",
    val email: String = "",
    val phone: String = "",
    val cnic: String = "",
    val dob: String = "",
    val city: String = "",
    val address: String = "",
    val bio: String = "",
    val languages: List<String> = emptyList(),
    val photoUrl: String = "",
    val isProfileApproved: Boolean = false,
    val isAvailable: Boolean = true,
    val dailyRate: String = "",
    val hourlyRate: String = "",
    val monthlyRate: String = "",
    val serviceIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class EmployeeService(
    val serviceId: String = "",
    val skills: List<String> = emptyList(),
    val experience: String = "",
    val availabilityDays: List<String> = emptyList(),
    val availabilityTime: String = "",
    val additionalNote: String = "",
    val dailyRate: String = ""
)

data class ServiceCategory(
    val id: String = "",
    val label: String = "",
    val emoji: String = "",
    val isActive: Boolean = true
)