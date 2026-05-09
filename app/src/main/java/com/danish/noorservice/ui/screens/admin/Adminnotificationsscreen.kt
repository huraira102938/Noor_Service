package com.danish.noorservice.ui.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.admin.AdminAnnouncementsViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Data models
// ─────────────────────────────────────────────────────────────────────────────

enum class AnnouncementAudience(val label: String, val emoji: String) {
    ALL("Everyone", "📢"),
    WORKERS("Workers", "👷"),
    EMPLOYERS("Employers", "🏠"),
    VENDORS("Vendors", "🏢")
}

enum class AnnouncementType(val label: String, val emoji: String, val color: Color) {
    INFO("Info", "ℹ️", Color(0xFF2196F3)),
    WARNING("Warning", "⚠️", Color(0xFFFF9800)),
    URGENT("Urgent", "🚨", Color(0xFFF44336)),
    SUCCESS("Update", "✅", Color(0xFF4CAF50)),
    PROMO("Promotion", "🎉", Color(0xFF9C27B0))
}

data class Announcement(
    val id: String,
    val title: String,
    val body: String,
    val audience: AnnouncementAudience,
    val type: AnnouncementType,
    val sentAt: String
)

// ─────────────────────────────────────────────────────────────────────────────
// Store
// ─────────────────────────────────────────────────────────────────────────────

object AnnouncementStore {
    val announcements = mutableStateListOf(
        Announcement(
            id = "a1", title = "Eid Holiday Notice",
            body = "The platform will have limited admin support during Eid holidays (Apr 9–11). Workers and employers can still browse and connect.",
            audience = AnnouncementAudience.ALL, type = AnnouncementType.INFO,
            sentAt = "2 hrs ago"
        ),
        Announcement(
            id = "a2", title = "New ID Verification Required",
            body = "All workers must upload a valid CNIC scan by April 15 to remain active on the platform. Go to Profile → Documents.",
            audience = AnnouncementAudience.WORKERS, type = AnnouncementType.URGENT,
            sentAt = "Yesterday"
        ),
        Announcement(
            id = "a3", title = "Proposal Fee Update",
            body = "Starting May 1st, employers sending more than 5 proposals per month will require a premium plan. See pricing for details.",
            audience = AnnouncementAudience.EMPLOYERS, type = AnnouncementType.WARNING,
            sentAt = "3 days ago"
        ),
        Announcement(
            id = "a4", title = "Vendor Registration Now Open",
            body = "We have launched vendor company registrations. Businesses can now register and post staffing requirements directly.",
            audience = AnnouncementAudience.ALL, type = AnnouncementType.SUCCESS,
            sentAt = "1 week ago"
        ),
        Announcement(
            id = "a5", title = "Ramadan Bonus for Top Workers",
            body = "Top-rated workers this month will receive a PKR 2,000 bonus. Keep your availability updated and ratings high!",
            audience = AnnouncementAudience.WORKERS, type = AnnouncementType.PROMO,
            sentAt = "2 weeks ago"
        ),
    )

    fun addAnnouncement(announcement: Announcement) {
        announcements.add(0, announcement)
    }

    fun deleteAnnouncement(id: String) {
        announcements.removeAll { it.id == id }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminNotificationsScreen(
    onBack: () -> Unit,
    viewModel: AdminAnnouncementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var filterAudience by remember { mutableStateOf<AnnouncementAudience?>(null) }
    var showCompose by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Announcement?>(null) }

    val visible = AnnouncementStore.announcements
        .filter { filterAudience == null || it.audience == filterAudience }

    LaunchedEffect(Unit) {
        viewModel.loadAnnouncements()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            Text("Announcements", fontSize = 20.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Manage and send notifications",
                                fontSize = 11.sp, color = Color.White.copy(alpha = 0.72f))
                        }
                    }

                    // Compose button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { showCompose = true }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("New", fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Simple info box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        "📢 Keep your audience informed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Create announcements for workers, employers, or everyone",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // ── Audience filter chips ─────────────────────────────────────────────
        LazyRow(
            modifier = Modifier.fillMaxWidth().background(NoorSurface),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                AnnouncementFilterChip("All", filterAudience == null) { filterAudience = null }
            }
            items(AnnouncementAudience.values()) { audience ->
                AnnouncementFilterChip(
                    "${audience.emoji} ${audience.label}",
                    filterAudience == audience
                ) { filterAudience = audience }
            }
        }
        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)

        // ── List ──────────────────────────────────────────────────────────────
        if (visible.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("No announcements yet", fontSize = 14.sp, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = { showCompose = true }) {
                        Text("Compose one →", color = AdminPurple, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visible, key = { it.id }) { ann ->
                    AnnouncementCard(
                        announcement = ann,
                        onDelete = { deleteTarget = ann }
                    )
                }
            }
        }
    }

    // ── Compose dialog ────────────────────────────────────────────────────────
    if (showCompose) {
        ComposeAnnouncementDialog(
            onSend = { ann ->
                AnnouncementStore.addAnnouncement(ann)
                showCompose = false
            },
            onDismiss = { showCompose = false }
        )
    }

    // ── Delete confirmation ───────────────────────────────────────────────────
    deleteTarget?.let { ann ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete announcement?", fontWeight = FontWeight.Bold, color = NoorRed) },
            text = { Text("\"${ann.title}\" will be permanently removed.", fontSize = 13.sp, color = NoorTextSecondary) },
            confirmButton = {
                TextButton(onClick = { AnnouncementStore.deleteAnnouncement(ann.id); deleteTarget = null }) {
                    Text("Delete", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Announcement card with expand/collapse
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnnouncementCard(
    announcement: Announcement,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val typeColor = announcement.type.color

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(typeColor, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
            )

            Column(modifier = Modifier.padding(12.dp)) {

                // Top row: type badge + audience + time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Type pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(typeColor.copy(alpha = 0.12f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "${announcement.type.emoji} ${announcement.type.label}",
                                fontSize = 9.sp, fontWeight = FontWeight.Bold, color = typeColor
                            )
                        }
                        // Audience pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AdminPurpleLight)
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "${announcement.audience.emoji} ${announcement.audience.label}",
                                fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = AdminPurple
                            )
                        }
                    }
                    Text(announcement.sentAt, fontSize = 10.sp, color = NoorTextHint)
                }

                Spacer(Modifier.height(8.dp))

                // Title row with expand/collapse icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        announcement.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NoorTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = NoorTextHint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Body text - expandable
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            announcement.body,
                            fontSize = 12.sp,
                            color = NoorTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Preview text when collapsed
                if (!expanded) {
                    Text(
                        announcement.body,
                        fontSize = 12.sp,
                        color = NoorTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Delete button only
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = NoorRed)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Compose dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ComposeAnnouncementDialog(
    onSend: (Announcement) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf(AnnouncementAudience.ALL) }
    var type by remember { mutableStateOf(AnnouncementType.INFO) }

    val isValid = title.isNotBlank() && body.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(NoorSurface)
        ) {
            // Dialog header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("New Announcement", fontSize = 16.sp,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close",
                            tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Title field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    placeholder = { Text("e.g. Eid Holiday Notice") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = composeTextFieldColors()
                )

                // Body field
                OutlinedTextField(
                    value = body,
                    onValueChange = { if (it.length <= 300) body = it },
                    label = { Text("Message *") },
                    placeholder = { Text("Write your announcement here…") },
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = composeTextFieldColors()
                )
                Text("${body.length}/300",
                    fontSize = 10.sp,
                    color = if (body.length > 270) NoorOrange else NoorTextHint,
                    modifier = Modifier.align(Alignment.End))

                // Audience selector
                ComposeLabel("Send to")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(AnnouncementAudience.values()) { a ->
                        val selected = audience == a
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) AdminPurple else AdminPurpleLight)
                                .border(1.dp, if (selected) AdminPurple else NoorBorder, RoundedCornerShape(10.dp))
                                .clickable { audience = a }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text("${a.emoji} ${a.label}",
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) Color.White else NoorTextSecondary)
                        }
                    }
                }

                // Type selector
                ComposeLabel("Type")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val typeValues = AnnouncementType.values()
                    // Row 1: first 3 types
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        typeValues.slice(0..2).forEach { t ->
                            val selected = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) t.color else t.color.copy(alpha = 0.1f))
                                    .border(1.dp, t.color.copy(alpha = if (selected) 1f else 0.3f), RoundedCornerShape(10.dp))
                                    .clickable { type = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(t.emoji, fontSize = 14.sp)
                                    Text(t.label, fontSize = 9.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) Color.White else t.color)
                                }
                            }
                        }
                    }
                    // Row 2: remaining 2 types
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        typeValues.slice(3..4).forEach { t ->
                            val selected = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) t.color else t.color.copy(alpha = 0.1f))
                                    .border(1.dp, t.color.copy(alpha = if (selected) 1f else 0.3f), RoundedCornerShape(10.dp))
                                    .clickable { type = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(t.emoji, fontSize = 14.sp)
                                    Text(t.label, fontSize = 9.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) Color.White else t.color)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // Send button
                Button(
                    onClick = {
                        if (isValid) {
                            onSend(
                                Announcement(
                                    id = "ann_${System.currentTimeMillis()}",
                                    title = title.trim(),
                                    body = body.trim(),
                                    audience = audience,
                                    type = type,
                                    sentAt = "Just now"
                                )
                            )
                        }
                    },
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AdminPurple)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Send Announcement", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnnouncementStatPill(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 14.sp)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(label, fontSize = 9.sp, color = Color.White.copy(alpha = 0.72f))
        }
    }
}

@Composable
private fun ComposeLabel(text: String) {
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = NoorTextHint, letterSpacing = 0.3.sp)
}

@Composable
private fun composeTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AdminPurple,
    focusedLabelColor = AdminPurple,
    cursorColor = AdminPurple,
    unfocusedBorderColor = NoorBorder,
    unfocusedLabelColor = NoorTextHint,
)

@Composable
private fun AnnouncementFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AdminPurple else NoorSurface,
        border = if (!isSelected) BorderStroke(1.dp, NoorBorder) else null
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color.White else NoorTextSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}