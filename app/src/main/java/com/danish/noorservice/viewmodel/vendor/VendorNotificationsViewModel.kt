package com.danish.noorservice.viewmodel.vendor

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

data class VendorNotificationsState(
    val isLoading: Boolean = false,
    val announcements: List<Pair<Announcement, UserAnnouncement>> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class VendorNotificationsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VendorNotificationsState())
    val uiState: StateFlow<VendorNotificationsState> = _uiState.asStateFlow()

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

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

    fun markAsRead(userId: String, announcementId: String) {
        viewModelScope.launch {
            try {
                announcementRepository.markAnnouncementAsRead(userId, announcementId)
                loadNotifications(userId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                announcementRepository.markAllAsRead(userId)
                loadNotifications(userId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}