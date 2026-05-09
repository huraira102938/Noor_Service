package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.model.UserAnnouncement
import com.danish.noorservice.ui.components.NotificationsShimmer
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employee.EmployeeNotificationsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    userId: String,
    onBack: () -> Unit,
    viewModel: EmployeeNotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✅ Only load once — subsequent visits use cached state in the VM.
    // The VM doesn't have a hasLoaded guard here because notifications are
    // relatively cheap to reload and benefit from fresh data on each visit.
    // If you want the same "load once" behaviour, add hasLoaded to
    // EmployeeNotificationsState and guard loadNotifications the same way.
    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Gradient Header ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                                tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Notifications", fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White,
                                    letterSpacing = (-0.3).sp)
                                if (uiState.unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(NoorOrange)
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${uiState.unreadCount} new", fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            Text("Stay updated on your activity", fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.72f))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // ✅ Manual refresh button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .clickable { viewModel.loadNotifications(userId) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh",
                                tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        if (uiState.unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.16f))
                                    .clickable { viewModel.markAllAsRead(userId) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(Icons.Default.DoneAll, contentDescription = null,
                                        tint = Color.White, modifier = Modifier.size(14.dp))
                                    Text("Mark all read", fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        when {
            // ✅ FIX: Shimmer on first load instead of a blocking spinner
            uiState.isLoading -> {
                NotificationsShimmer()
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️", fontSize = 36.sp)
                        Text("Failed to load notifications", fontSize = 14.sp, color = NoorTextPrimary)
                        Button(
                            onClick = { viewModel.loadNotifications(userId) },
                            colors  = ButtonDefaults.buttonColors(containerColor = NoorBlue)
                        ) { Text("Retry", color = Color.White) }
                    }
                }
            }

            uiState.announcements.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No notifications yet", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("You're all caught up!", fontSize = 13.sp, color = NoorTextHint)
                    }
                }
            }

            else -> {
                val unread = uiState.announcements.filter { !it.second.isRead }
                val read   = uiState.announcements.filter {  it.second.isRead }

                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (unread.isNotEmpty()) {
                        item { NotificationGroupLabel("New") }
                        items(unread, key = { it.first.id }) { (announcement, userAnnouncement) ->
                            AnnouncementRow(
                                announcement     = announcement,
                                userAnnouncement = userAnnouncement,
                                onMarkRead       = { viewModel.markAsRead(userId, announcement.id) }
                            )
                        }
                    }

                    if (read.isNotEmpty()) {
                        item { NotificationGroupLabel("Earlier") }
                        items(read, key = { it.first.id }) { (announcement, userAnnouncement) ->
                            AnnouncementRow(
                                announcement     = announcement,
                                userAnnouncement = userAnnouncement,
                                onMarkRead       = {}
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Group label
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NotificationGroupLabel(title: String) {
    Text(
        text          = title.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Bold,
        color         = NoorTextHint,
        letterSpacing = 0.8.sp,
        modifier      = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Announcement row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnnouncementRow(
    announcement: Announcement,
    userAnnouncement: UserAnnouncement,
    onMarkRead: () -> Unit
) {
    val isRead  = userAnnouncement.isRead
    val bgColor = if (!isRead) NoorBlueLight else NoorSurface

    val timeLabel = remember(announcement.createdAt) {
        val now  = System.currentTimeMillis()
        val diff = now - announcement.createdAt
        when {
            diff < 60_000       -> "Just now"
            diff < 3_600_000    -> "${diff / 60_000} min ago"
            diff < 86_400_000   -> "${diff / 3_600_000} hr ago"
            diff < 172_800_000  -> "Yesterday"
            else                -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(announcement.createdAt))
        }
    }

    Row(
        modifier  = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { if (!isRead) onMarkRead() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(NoorBlueLight),
            contentAlignment = Alignment.Center
        ) { Text("📢", fontSize = 20.sp) }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Text(
                    text       = announcement.title,
                    fontSize   = 13.sp,
                    fontWeight = if (!isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color      = NoorTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(timeLabel, fontSize = 10.sp, color = NoorTextHint)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text      = announcement.body,
                fontSize  = 12.sp,
                color     = NoorTextSecondary,
                lineHeight = 17.sp,
                maxLines  = 2
            )
        }

        if (!isRead) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NoorOrange)
            )
        }
    }

    HorizontalDivider(
        modifier  = Modifier.padding(start = 72.dp, end = 16.dp),
        color     = NoorDivider,
        thickness = 0.6.dp
    )
}