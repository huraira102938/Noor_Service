package com.danish.noorservice.data.repository

import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.model.UserAnnouncement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class AnnouncementRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val DAYS_TO_KEEP = 10L
    }

    suspend fun deleteOldAnnouncements() {
        try {
            val cutoffTime = System.currentTimeMillis() - (DAYS_TO_KEEP * 24 * 60 * 60 * 1000)
            val oldDocs = firestore.collection("announcements")
                .whereLessThan("createdAt", cutoffTime)
                .get()
                .await()

            for (doc in oldDocs.documents) {
                val announcementId = doc.id

                // Delete the announcement
                doc.reference.delete().await()

                // Delete associated user announcement records
                val userAnnouncementDocs = firestore.collection("userAnnouncements")
                    .get()
                    .await()

                for (userDoc in userAnnouncementDocs.documents) {
                    userDoc.reference.collection("announcements")
                        .document(announcementId)
                        .delete()
                        .await()
                }
            }
        } catch (e: Exception) {
            // Silently fail - cleanup should not block normal operations
        }
    }

    suspend fun createAnnouncement(
        title: String,
        body: String,
        targetAudience: String,
        type: String,
        createdBy: String
    ): Result<Announcement> {
        return try {
            val announcement = Announcement(
                id = firestore.collection("announcements").document().id,
                title = title,
                body = body,
                targetAudience = targetAudience,
                type = type,
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

    suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        return try {
            // Delete from announcements collection
            firestore.collection("announcements")
                .document(announcementId)
                .delete()
                .await()

            // Delete associated user announcement records
            val userAnnouncementDocs = firestore.collection("userAnnouncements")
                .get()
                .await()

            for (userDoc in userAnnouncementDocs.documents) {
                userDoc.reference.collection("announcements")
                    .document(announcementId)
                    .delete()
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnnouncementsForRole(role: String): List<Announcement> {
        // Clean up old announcements first
        deleteOldAnnouncements()

        val cutoffTime = System.currentTimeMillis() - (DAYS_TO_KEEP * 24 * 60 * 60 * 1000)

        val allDocs = firestore.collection("announcements")
            .whereGreaterThan("createdAt", cutoffTime)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get(Source.SERVER)
            .await()

        val announcements = allDocs.documents
            .mapNotNull { doc ->
                val ann = doc.toObject(Announcement::class.java)
                android.util.Log.d("AnnouncementRepo", "Loaded: ${ann?.title}, target=${ann?.targetAudience}, type=${ann?.type}")
                ann
            }

        // Admin sees all announcements, others see only their target
        return if (role == "admin") {
            announcements
        } else {
            announcements.filter { it.targetAudience == "all" || it.targetAudience == role }
        }
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
                .get(Source.SERVER)
                .await()

            val userAnnouncement = userAnnouncementDoc.toObject(UserAnnouncement::class.java)
                ?: UserAnnouncement(announcementId = announcement.id)

            userAnnouncements.add(announcement to userAnnouncement)
        }

        return userAnnouncements
    }

    private suspend fun getUserRole(userId: String): String {
        val doc = firestore.collection("users").document(userId)
            .get(Source.SERVER)
            .await()
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