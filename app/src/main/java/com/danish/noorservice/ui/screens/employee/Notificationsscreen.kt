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
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

enum class NotificationType { PROPOSAL, MESSAGE, SYSTEM, PAYMENT }

data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean = false
)

private val sampleNotifications = mutableListOf(
    NotificationItem(
        id    = "1",
        type  = NotificationType.PROPOSAL,
        title = "New Proposal Received",
        body  = "Farhan Ahmed has sent you a driver job proposal for DHA Phase 5, Mon–Fri.",
        time  = "Just now",
        isRead = false
    ),
    NotificationItem(
        id    = "2",
        type  = NotificationType.MESSAGE,
        title = "New Message",
        body  = "Sara Khan: \"Please confirm your availability for this weekend.\"",
        time  = "10 min ago",
        isRead = false
    ),
    NotificationItem(
        id    = "3",
        type  = NotificationType.PROPOSAL,
        title = "Proposal Accepted",
        body  = "Nadia Baig accepted your counter-proposal for House Boy service in Gulberg II.",
        time  = "1 hr ago",
        isRead = false
    ),
    NotificationItem(
        id    = "4",
        type  = NotificationType.SYSTEM,
        title = "Profile Under Review",
        body  = "Our team is reviewing your updated profile. You'll be notified once approved.",
        time  = "Yesterday",
        isRead = true
    ),
    NotificationItem(
        id    = "5",
        type  = NotificationType.MESSAGE,
        title = "New Message",
        body  = "Asad Malik: \"Great work today! We'd like you to continue next week as well.\"",
        time  = "Yesterday",
        isRead = true
    ),
    NotificationItem(
        id    = "6",
        type  = NotificationType.PROPOSAL,
        title = "Proposal Declined",
        body  = "Hina Tariq has declined your application for the Bahria Town driver position.",
        time  = "Mon",
        isRead = true
    ),
    NotificationItem(
        id    = "7",
        type  = NotificationType.SYSTEM,
        title = "Welcome to Noor Services!",
        body  = "Your account has been successfully created. Complete your profile to start receiving job proposals.",
        time  = "Mar 28",
        isRead = true
    ),
    NotificationItem(
        id    = "8",
        type  = NotificationType.PROPOSAL,
        title = "New Proposal Received",
        body  = "Bilal Raza has sent you a House Boy proposal for Johar Town, Mon/Wed/Fri mornings.",
        time  = "Mar 27",
        isRead = true
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val notifications = remember { mutableStateListOf(*sampleNotifications.toTypedArray()) }
    val unreadCount   = notifications.count { !it.isRead }

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
                    modifier             = Modifier.fillMaxWidth(),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Back button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint     = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Notifications",
                                    fontSize      = 20.sp,
                                    fontWeight    = FontWeight.Bold,
                                    color         = Color.White,
                                    letterSpacing = (-0.3).sp
                                )
                                if (unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(NoorOrange)
                                            .padding(horizontal = 8.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "$unreadCount new",
                                            fontSize   = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = Color.White
                                        )
                                    }
                                }
                            }
                            Text(
                                "Stay updated on your activity",
                                fontSize = 12.sp,
                                color    = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }

                    // Mark all read button
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .clickable {
                                    val updated = notifications.map { it.copy(isRead = true) }
                                    notifications.clear()
                                    notifications.addAll(updated)
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.Default.DoneAll,
                                    contentDescription = null,
                                    tint     = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Mark all read",
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Content ───────────────────────────────────────────────────────────
        if (notifications.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No notifications yet",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = NoorTextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "You're all caught up!",
                        fontSize = 13.sp,
                        color    = NoorTextHint
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Unread section
                val unread = notifications.filter { !it.isRead }
                val read   = notifications.filter  {  it.isRead }

                if (unread.isNotEmpty()) {
                    item {
                        NotificationGroupLabel("New")
                    }
                    items(unread, key = { it.id }) { notif ->
                        NotificationRow(
                            notification = notif,
                            onMarkRead   = { id ->
                                val idx = notifications.indexOfFirst { it.id == id }
                                if (idx != -1) notifications[idx] = notifications[idx].copy(isRead = true)
                            }
                        )
                    }
                }

                if (read.isNotEmpty()) {
                    item {
                        NotificationGroupLabel("Earlier")
                    }
                    items(read, key = { it.id }) { notif ->
                        NotificationRow(
                            notification = notif,
                            onMarkRead   = {}
                        )
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
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
// Notification row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NotificationRow(
    notification: NotificationItem,
    onMarkRead: (String) -> Unit
) {
    val bgColor = if (!notification.isRead) NoorBlueLight else NoorSurface

    val (iconEmoji, iconBg) = when (notification.type) {
        NotificationType.PROPOSAL -> "📋" to NoorBlueLight
        NotificationType.MESSAGE  -> "💬" to NoorGreenLight
        NotificationType.PAYMENT  -> "💰" to Color(0xFFFFF8E1)
        NotificationType.SYSTEM   -> "🔔" to NoorOrangeLight
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable { if (!notification.isRead) onMarkRead(notification.id) }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(iconEmoji, fontSize = 20.sp)
        }

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.Top
            ) {
                Text(
                    text       = notification.title,
                    fontSize   = 13.sp,
                    fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color      = NoorTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    notification.time,
                    fontSize = 10.sp,
                    color    = NoorTextHint
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text      = notification.body,
                fontSize  = 12.sp,
                color     = NoorTextSecondary,
                lineHeight = 17.sp,
                maxLines  = 2
            )
        }

        // Unread dot
        if (!notification.isRead) {
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