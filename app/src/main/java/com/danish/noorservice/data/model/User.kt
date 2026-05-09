package com.danish.noorservice.data.model

import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "",
    val createdAt: Long = System.currentTimeMillis(),

    // Firestore strips "is" prefix from Boolean fields during serialization.
    // Without @PropertyName, "isProfileComplete" gets stored as "profileComplete"
    // but the getter looks for "isProfileComplete" — causing a mismatch on read.
    @get:PropertyName("isProfileComplete")
    @set:PropertyName("isProfileComplete")
    var isProfileComplete: Boolean = false
)

enum class UserRole(val value: String) {
    EMPLOYEE("employee"),
    EMPLOYER("employer"),
    VENDOR("vendor"),
    ADMIN("admin");

    companion object {
        fun fromString(value: String): UserRole? {
            return entries.find { it.value == value }
        }
    }
}