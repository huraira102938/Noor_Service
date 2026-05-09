package com.danish.noorservice.data.model

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val targetAudience: String = "all",
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = ""
)

data class UserAnnouncement(
    val announcementId: String = "",
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AnnouncementTarget(val value: String) {
    ALL("all"),
    EMPLOYEES("employee"),
    EMPLOYERS("employer"),
    VENDORS("vendor");

    companion object {
        fun fromString(value: String): AnnouncementTarget? {
            return entries.find { it.value == value }
        }
    }
}