package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.employer.EmployerNotificationsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EmployerNotificationsScreen(
    userId: String,
    viewModel: EmployerNotificationsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
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
                        Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.3).sp)
                    }
                    IconButton(
                        onClick = { viewModel.loadNotifications(userId) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                    }
                }
            }
        }

        when {
            uiState.announcements.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No notifications yet", fontSize = 15.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
                        Text("You're all caught up!", fontSize = 12.sp, color = NoorTextHint)
                    }
                }
            }
            else -> {
                val dateFormat = remember { SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()) }
                val unread = uiState.announcements.filter { !it.second.isRead }
                val read = uiState.announcements.filter { it.second.isRead }

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
                    if (unread.isNotEmpty()) {
                        item {
                            Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoorTextHint, letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp))
                        }
                        items(unread, key = { it.first.id }) { (announcement, userAnn) ->
                            EmployerAnnouncementRow(
                                title = announcement.title,
                                body = announcement.body,
                                time = dateFormat.format(Date(announcement.createdAt)),
                                isRead = false
                            )
                        }
                    }
                    if (read.isNotEmpty()) {
                        item {
                            Text("Earlier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoorTextHint,
                                letterSpacing = 0.8.sp, modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp))
                        }
                        items(read, key = { it.first.id }) { (announcement, userAnn) ->
                            EmployerAnnouncementRow(
                                title = announcement.title,
                                body = announcement.body,
                                time = dateFormat.format(Date(announcement.createdAt)),
                                isRead = true
                            )
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmployerAnnouncementRow(
    title: String,
    body: String,
    time: String,
    isRead: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val bgColor = if (!isRead) NoorBlueLight else NoorSurface

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
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(NoorOrangeLight),
                contentAlignment = Alignment.Center
            ) { Text("📋", fontSize = 20.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(title, fontSize = 13.sp, fontWeight = if (!isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = NoorTextPrimary, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text(time, fontSize = 10.sp, color = NoorTextHint)
                }
                Spacer(Modifier.height(4.dp))
                Text(body, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 17.sp, maxLines = if (expanded) Int.MAX_VALUE else 2)
            }

            if (!isRead) {
                Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).clip(CircleShape).background(NoorOrange))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(start = 72.dp, end = 16.dp), color = NoorDivider, thickness = 0.6.dp)
    }
}