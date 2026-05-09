package com.danish.noorservice.data.repository

import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.model.UserAnnouncement
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class AnnouncementRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val DAYS_TO_KEEP = 30L
    }

    suspend fun createAnnouncement(
        title: String,
        body: String,
        targetAudience: String,
        createdBy: String
    ): Result<Announcement> {
        return try {
            val announcement = Announcement(
                id = firestore.collection("announcements").document().id,
                title = title,
                body = body,
                targetAudience = targetAudience,
                createdBy = createdBy,
                createdAt = System.currentTimeMillis()
            )
            firestore.collection("announcements")
                .document(announcement.id)
                .set(announcement)
                .await()
            Result.success(announcement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnnouncementsForRole(role: String): List<Announcement> {
        val cutoffTime = System.currentTimeMillis() - (DAYS_TO_KEEP * 24 * 60 * 60 * 1000)

        val allDocs = firestore.collection("announcements")
            .whereGreaterThan("createdAt", cutoffTime)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return allDocs.documents
            .mapNotNull { it.toObject(Announcement::class.java) }
            .filter { it.targetAudience == "all" || it.targetAudience == role }
    }

    suspend fun getUserAnnouncements(userId: String): List<Pair<Announcement, UserAnnouncement>> {
        val role = getUserRole(userId)
        val announcements = getAnnouncementsForRole(role)
        val userAnnouncements = mutableListOf<Pair<Announcement, UserAnnouncement>>()

        for (announcement in announcements) {
            val userAnnouncementDoc = firestore
                .collection("userAnnouncements")
                .document(userId)
                .collection("announcements")
                .document(announcement.id)
                .get()
                .await()

            val userAnnouncement = userAnnouncementDoc.toObject(UserAnnouncement::class.java)
                ?: UserAnnouncement(announcementId = announcement.id)

            userAnnouncements.add(announcement to userAnnouncement)
        }

        return userAnnouncements
    }

    private suspend fun getUserRole(userId: String): String {
        val doc = firestore.collection("users").document(userId).get().await()
        return doc.getString("role") ?: ""
    }

    suspend fun markAnnouncementAsRead(userId: String, announcementId: String): Result<Unit> {
        return try {
            val userAnnouncement = UserAnnouncement(
                announcementId = announcementId,
                isRead = true,
                readAt = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
            firestore.collection("userAnnouncements")
                .document(userId)
                .collection("announcements")
                .document(announcementId)
                .set(userAnnouncement)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllAsRead(userId: String): Result<Unit> {
        return try {
            val announcements = getAnnouncementsForRole(getUserRole(userId))
            announcements.forEach { announcement ->
                markAnnouncementAsRead(userId, announcement.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(userId: String): Int {
        val announcements = getUserAnnouncements(userId)
        return announcements.count { !it.second.isRead }
    }
}