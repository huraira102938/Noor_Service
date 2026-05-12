package com.danish.noorservice.data.model

import com.google.firebase.firestore.PropertyName

data class Employer(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val area: String = "",
    val address: String = "",
    val about: String = "",
    val photoUrl: String = "",
    @PropertyName("profileApproved") val isProfileApproved: Boolean = true,
    @PropertyName("active") val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)