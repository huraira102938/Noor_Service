package com.danish.noorservice.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.screens.employer.AdminProposalStatus
import com.danish.noorservice.ui.screens.employer.AdminProposalStore
import com.danish.noorservice.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Admin Settings Screen
// ─────────────────────────────────────────────────────────────────────────────

private enum class AdminSettingsSubScreen { NONE, PROPOSAL_INBOX, NOTIFICATIONS }

@Composable
fun AdminSettingsScreen(onLogout: () -> Unit = {}) {

    var showCategoryMgmt by remember { mutableStateOf(false) }
    var subScreen by remember { mutableStateOf(AdminSettingsSubScreen.NONE) }

    when (subScreen) {
        AdminSettingsSubScreen.PROPOSAL_INBOX -> {
            AdminProposalInboxScreen(onBack = { subScreen = AdminSettingsSubScreen.NONE })
            return
        }
        AdminSettingsSubScreen.NONE -> { /* fall through */ }
        AdminSettingsSubScreen.NOTIFICATIONS -> {
            AdminNotificationsScreen(onBack = { subScreen = AdminSettingsSubScreen.NONE })
            return
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showCategoryMgmt) {
        AdminCategoryManagementScreen(onBack = { showCategoryMgmt = false })
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text(
                    "Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp
                )
                Text(
                    "Admin account & platform settings",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Admin identity card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(AdminPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛡️", fontSize = 24.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Danish Awan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text("admin_danish@noorservice.com", fontSize = 12.sp, color = NoorTextHint)
                        Spacer(Modifier.height(2.dp))
                        Text("03123339015", fontSize = 11.sp, color = NoorTextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(AdminPurpleLight).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "🔑 Full Access", fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold, color = AdminPurple
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "ID: nUAnKyVW8ieRWI71NXJMmSUDFbE3",
                    fontSize = 10.sp,
                    color = NoorTextHint,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Account
            AdminSettingsGroup("Account") {
                AdminSettingsNavItem(emoji = "📄", emojiBg = NoorBackground, title = "Terms & Conditions", onClick = {})
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                AdminSettingsNavItem(emoji = "🛡️", emojiBg = NoorBackground, title = "Privacy Policy", onClick = {})
            }

            // Session
            AdminSettingsGroup("Session") {
                AdminSettingsNavItem(
                    emoji = "🚪", emojiBg = AdminPurpleLight,
                    title = "Log Out",
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "Are you sure you want to log out of the admin panel?",
                    fontSize = 13.sp, color = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Log Out", color = AdminPurple, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Admin Proposal Inbox — full management for admin to connect or decline
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminProposalInboxScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Pending", "Accepted", "Declined")

    val proposals = AdminProposalStore.proposals

    val filtered = when (selectedTab) {
        1 -> proposals.filter { it.status == AdminProposalStatus.PENDING }
        2 -> proposals.filter { it.status == AdminProposalStatus.ACCEPTED }
        3 -> proposals.filter { it.status == AdminProposalStatus.DECLINED }
        else -> proposals.toList()
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 0.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)).clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White, modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            "Proposal Inbox", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.3).sp
                        )
                        Text(
                            "Connect employers with workers",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tab row
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, label ->
                        val count = when (index) {
                            0 -> proposals.size
                            1 -> proposals.count { it.status == AdminProposalStatus.PENDING }
                            2 -> proposals.count { it.status == AdminProposalStatus.ACCEPTED }
                            3 -> proposals.count { it.status == AdminProposalStatus.DECLINED }
                            else -> 0
                        }
                        val isSelected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f).clickable { selectedTab = index }
                        ) {
                            Text(
                                text = if (count > 0) "$label ($count)" else label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f),
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Box(
                                modifier = Modifier.height(3.dp).fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📋", fontSize = 48.sp)
                    Text("No proposals in this category", fontSize = 14.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    AdminProposalManageCard(
                        proposal = proposal,
                        onConnect = {
                            val idx = AdminProposalStore.proposals.indexOfFirst { p -> p.id == proposal.id }
                            if (idx != -1) AdminProposalStore.proposals[idx] =
                                AdminProposalStore.proposals[idx].copy(status = AdminProposalStatus.ACCEPTED)
                        },
                        onDecline = {
                            val idx = AdminProposalStore.proposals.indexOfFirst { p -> p.id == proposal.id }
                            if (idx != -1) AdminProposalStore.proposals[idx] =
                                AdminProposalStore.proposals[idx].copy(status = AdminProposalStatus.DECLINED)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminProposalManageCard(
    proposal: com.danish.noorservice.ui.screens.employer.AdminProposal,
    onConnect: () -> Unit,
    onDecline: () -> Unit
) {
    val (accentColor, pillBg, statusLabel) = when (proposal.status) {
        AdminProposalStatus.PENDING -> Triple(NoorOrange, NoorOrangeLight, "Pending")
        AdminProposalStatus.ACCEPTED -> Triple(NoorGreen, NoorGreenLight, "Accepted ✅")
        AdminProposalStatus.DECLINED -> Triple(NoorRed, NoorRedLight, "Declined ❌")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.width(4.dp).fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                // Worker info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(proposal.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                proposal.workerInitials, fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold, color = Color.White
                            )
                        }
                        Column {
                            Text(
                                proposal.workerName, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = NoorTextPrimary
                            )
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                    .background(NoorBlueLight)
                                    .border(1.dp, NoorBlue.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    proposal.workerUsername, fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold, color = NoorBlue
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp))
                            .background(pillBg).padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(statusLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    proposal.jobTitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary
                )
                Text("${proposal.service} · ${proposal.location}", fontSize = 11.sp, color = NoorTextHint)

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminProposalTag("📅", proposal.startDate)
                    AdminProposalTag("⏰", proposal.schedule)
                    AdminProposalTag("💰", proposal.offerPrice)
                }

                if (proposal.note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(NoorBackground).padding(10.dp)
                    ) {
                        Text("📝 ${proposal.note}", fontSize = 11.sp, color = NoorTextSecondary, lineHeight = 16.sp)
                    }
                }

                if (proposal.status == AdminProposalStatus.PENDING) {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onConnect,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NoorGreen),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = onDecline,
                            modifier = Modifier.weight(1f).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Decline", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                if (proposal.status == AdminProposalStatus.ACCEPTED) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(NoorGreenLight).padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NoorGreen, modifier = Modifier.size(14.dp))
                            Text(
                                "Proposal accepted. Employer and worker have been connected.",
                                fontSize = 11.sp, color = NoorGreen, lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProposalTag(icon: String, label: String) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(NoorBackground)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(icon, fontSize = 10.sp)
        Text(label, fontSize = 9.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Settings reusable components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminSettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
            color = NoorTextHint, letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = { Column(content = content) }
        )
    }
}

@Composable
private fun AdminSettingsNavItem(
    emoji: String, emojiBg: Color, title: String,
    badge: String? = null, badgeColor: Color = NoorOrange,
    titleColor: Color = NoorTextPrimary, onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }
        Text(
            title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = titleColor, modifier = Modifier.weight(1f)
        )
        if (badge != null) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(badgeColor).padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NoorTextHint, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AdminSettingsStat(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AdminPurpleLight),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }
        Text(
            label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = NoorTextPrimary, modifier = Modifier.weight(1f)
        )
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AdminPurple)
    }
}