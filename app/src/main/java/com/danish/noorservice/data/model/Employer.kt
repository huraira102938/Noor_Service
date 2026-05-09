package com.danish.noorservice.data.model

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
    val createdAt: Long = System.currentTimeMillis()
)