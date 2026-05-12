package com.danish.noorservice.ui.screens.employer


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
// Data
// ─────────────────────────────────────────────────────────────────────────────

enum class EmployerNotifType { BOOKING, MESSAGE, SYSTEM }

data class EmployerNotifItem(
    val id: String,
    val type: EmployerNotifType,
    val title: String,
    val body: String,
    val time: String,
    val isRead: Boolean = false
)

private val employerNotifications = mutableListOf(
    EmployerNotifItem("1", EmployerNotifType.BOOKING,  "Proposal Accepted",  "Muhammad Ali accepted your driver proposal for DHA Phase 3, Mon–Fri.", "Just now", false),
    EmployerNotifItem("2", EmployerNotifType.MESSAGE,  "New Message",        "Muhammad Ali: \"I will be there at 8 AM Monday.\"", "5 min ago", false),
    EmployerNotifItem("3", EmployerNotifType.BOOKING,  "New Application",    "Sana Fatima has applied for your babysitter opening.", "1 hr ago", false),
    EmployerNotifItem("4", EmployerNotifType.SYSTEM,   "Profile Verified",   "Your employer account has been verified by Noor Services.", "Yesterday", true),
    EmployerNotifItem("5", EmployerNotifType.MESSAGE,  "New Message",        "Ayesha Bibi: \"Yes, I'm available Wednesday mornings.\"", "Yesterday", true),
    EmployerNotifItem("6", EmployerNotifType.BOOKING,  "Booking Completed",  "Nazia Malik has completed her cooking assignment. Please leave a review.", "Mon", true),
    EmployerNotifItem("7", EmployerNotifType.SYSTEM,   "Welcome!",           "Welcome to Noor Services. Start by browsing available workers.", "Mar 28", true),
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerNotificationsScreen(onBack: () -> Unit) {
    val notifications = remember { mutableStateListOf(*employerNotifications.toTypedArray()) }
    val unreadCount   = notifications.count { !it.isRead }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.3).sp)
                            Text("Booking & message updates", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                        }
                    }
                }
            }
        }

        // List
        val unread = notifications.filter { !it.isRead }
        val read   = notifications.filter { it.isRead }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
            if (unread.isNotEmpty()) {
                item { EmployerNotifGroupLabel("New") }
                items(unread, key = { it.id }) { notif ->
                    EmployerNotifRow(notif) { id ->
                        val idx = notifications.indexOfFirst { it.id == id }
                        if (idx != -1) notifications[idx] = notifications[idx].copy(isRead = true)
                    }
                }
            }
            if (read.isNotEmpty()) {
                item { EmployerNotifGroupLabel("Earlier") }
                items(read, key = { it.id }) { notif ->
                    EmployerNotifRow(notif) {}
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EmployerNotifGroupLabel(title: String) {
    Text(
        text          = title.uppercase(),
        fontSize      = 10.sp,
        fontWeight    = FontWeight.Bold,
        color         = NoorTextHint,
        letterSpacing = 0.8.sp,
        modifier      = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun EmployerNotifRow(
    notif: EmployerNotifItem,
    onMarkRead: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val bgColor = if (!notif.isRead) NoorBlueLight else NoorSurface
    val (iconEmoji, iconBg) = when (notif.type) {
        EmployerNotifType.BOOKING -> "📋" to NoorOrangeLight
        EmployerNotifType.MESSAGE -> "💬" to NoorGreenLight
        EmployerNotifType.SYSTEM  -> "🔔" to NoorBlueLight
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment    = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) { Text(iconEmoji, fontSize = 20.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(notif.title, fontSize = 13.sp, fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = NoorTextPrimary, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text(notif.time, fontSize = 10.sp, color = NoorTextHint)
                }
                Spacer(Modifier.height(4.dp))
                Text(notif.body, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 17.sp, maxLines = if (expanded) Int.MAX_VALUE else 2)
            }

            if (!notif.isRead) {
                Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).clip(CircleShape).background(NoorOrange))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(start = 72.dp, end = 16.dp), color = NoorDivider, thickness = 0.6.dp)
    }
}