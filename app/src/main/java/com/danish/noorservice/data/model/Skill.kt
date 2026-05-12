package com.danish.noorservice.data.model

import com.google.firebase.firestore.PropertyName

data class Skill(
    val id: String = "",
    val name: String = ""
)

data class ServiceSkill(
    val id: String = "",
    val name: String = ""
)

data class Category(
    val id: String = "",
    val label: String = "",
    val emoji: String = "",
    @PropertyName("categoryType") val categoryType: String = "vendor",
    @PropertyName("active") val isActive: Boolean = true,
    val skills: List<Skill> = emptyList()
)