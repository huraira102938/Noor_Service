package com.danish.noorservice.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminAnnouncementsState(
    val isLoading: Boolean = false,
    val announcements: List<Announcement> = emptyList(),
    val error: String? = null,

    // Creating new announcement
    val title: String = "",
    val body: String = "",
    val targetAudience: String = "all"
)

sealed class AdminAnnouncementsEvent {
    data object CreateSuccess : AdminAnnouncementsEvent()
    data class Error(val message: String) : AdminAnnouncementsEvent()
}

@HiltViewModel
class AdminAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminAnnouncementsState())
    val uiState: StateFlow<AdminAnnouncementsState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AdminAnnouncementsEvent>()
    val events = _events.asSharedFlow()

    fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Admin sees all announcements
                val announcements = announcementRepository.getAnnouncementsForRole("admin")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    announcements = announcements
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateBody(body: String) {
        _uiState.value = _uiState.value.copy(body = body)
    }

    fun updateTargetAudience(audience: String) {
        _uiState.value = _uiState.value.copy(targetAudience = audience)
    }

    fun isFormValid(): Boolean {
        return _uiState.value.title.isNotBlank() && _uiState.value.body.isNotBlank()
    }

    fun deleteAnnouncement(announcementId: String) {
        viewModelScope.launch {
            try {
                announcementRepository.deleteAnnouncement(announcementId)
                loadAnnouncements()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun createAnnouncement(title: String, body: String, targetAudience: String, type: String, createdBy: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val result = announcementRepository.createAnnouncement(
                    title = title,
                    body = body,
                    targetAudience = targetAudience,
                    type = type,
                    createdBy = createdBy
                )

                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        title = "",
                        body = "",
                        targetAudience = "all"
                    )
                    loadAnnouncements()
                    _events.emit(AdminAnnouncementsEvent.CreateSuccess)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                    _events.emit(AdminAnnouncementsEvent.Error(e.message ?: "Failed to create"))
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
                _events.emit(AdminAnnouncementsEvent.Error(e.message ?: "Failed to create"))
            }
        }
    }
}