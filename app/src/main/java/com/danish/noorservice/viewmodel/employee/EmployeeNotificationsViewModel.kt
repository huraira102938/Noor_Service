package com.danish.noorservice.viewmodel.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.model.UserAnnouncement
import com.danish.noorservice.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeeNotificationsState(
    val isLoading: Boolean = false,
    val announcements: List<Pair<Announcement, UserAnnouncement>> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class EmployeeNotificationsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeNotificationsState())
    val uiState: StateFlow<EmployeeNotificationsState> = _uiState.asStateFlow()

    private var currentUserId: String = ""

    fun loadNotifications(userId: String) {
        // Reset if userId changed (new login)
        if (userId != currentUserId) {
            currentUserId = userId
            _uiState.value = EmployeeNotificationsState()
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val announcements = announcementRepository.getUserAnnouncements(userId)
                val unreadCount = announcements.count { !it.second.isRead }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    announcements = announcements,
                    unreadCount = unreadCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun reset() {
        currentUserId = ""
        _uiState.value = EmployeeNotificationsState()
    }

    fun markAsRead(userId: String, announcementId: String) {
        viewModelScope.launch {
            try {
                announcementRepository.markAnnouncementAsRead(userId, announcementId)
                // Update local state without reloading all notifications
                val updatedAnnouncements = _uiState.value.announcements.map { (ann, userAnn) ->
                    if (ann.id == announcementId) {
                        ann to userAnn.copy(isRead = true, readAt = System.currentTimeMillis())
                    } else {
                        ann to userAnn
                    }
                }
                val unreadCount = updatedAnnouncements.count { !it.second.isRead }
                _uiState.value = _uiState.value.copy(
                    announcements = updatedAnnouncements,
                    unreadCount = unreadCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                announcementRepository.markAllAsRead(userId)
                // Update local state without reloading all notifications
                val updatedAnnouncements = _uiState.value.announcements.map { (ann, userAnn) ->
                    ann to userAnn.copy(isRead = true, readAt = System.currentTimeMillis())
                }
                _uiState.value = _uiState.value.copy(
                    announcements = updatedAnnouncements,
                    unreadCount = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}