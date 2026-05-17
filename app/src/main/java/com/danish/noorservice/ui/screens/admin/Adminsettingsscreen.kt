package com.danish.noorservice.ui.screens.admin

import android.content.Context
import android.util.Log
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
import com.danish.noorservice.viewmodel.admin.AdminProposalViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.danish.noorservice.ui.screens.employer.AdminProposal
import com.danish.noorservice.ui.screens.employer.VendorProposal

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
            AdminProposalInboxScreen(
                onBack = { subScreen = AdminSettingsSubScreen.NONE },
                proposalViewModel = null
            )
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
fun AdminProposalInboxScreen(
    onBack: () -> Unit,
    proposalViewModel: AdminProposalViewModel? = null
) {
    LaunchedEffect(Unit) {
        proposalViewModel?.loadAllProposals()
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showWorkerDetail by remember { mutableStateOf<AdminProposal?>(null) }
    var showVendorDetail by remember { mutableStateOf<VendorProposal?>(null) }
    val tabs = listOf("Workers", "Vendors")

    val workerProposals = AdminProposalStore.proposals
    val vendorProposals = com.danish.noorservice.ui.screens.employer.VendorProposalStore.proposals
    Log.d("AdminInbox", "AdminProposalInboxScreen: workerProposals.size=${workerProposals.size}, first employerName=${workerProposals.firstOrNull()?.employerName}")

    if (showWorkerDetail != null) {
        AdminWorkerProposalDetailScreen(
            proposal = showWorkerDetail!!,
            onBack = { showWorkerDetail = null },
            proposalViewModel = proposalViewModel
        )
        return
    }

    if (showVendorDetail != null) {
        AdminVendorProposalDetailScreen(
            proposal = showVendorDetail!!,
            onBack = { showVendorDetail = null },
            proposalViewModel = proposalViewModel
        )
        return
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
                            "Connect employers with workers & vendors",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, label ->
                        val count = if (index == 0) workerProposals.size else vendorProposals.size
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

        val items = if (selectedTab == 0) workerProposals else vendorProposals

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(if (selectedTab == 0) "👷" else "🏢", fontSize = 48.sp)
                    Text("No ${if (selectedTab == 0) "worker" else "vendor"} proposals", fontSize = 14.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == 0) {
                    items(workerProposals, key = { it.id }) { proposal ->
                        AdminProposalManageCard(
                            proposal = proposal,
                            onCardClick = { showWorkerDetail = proposal },
                            onConnect = { proposalViewModel?.acceptWorkerProposal(proposal.id) },
                            onDecline = { proposalViewModel?.declineWorkerProposal(proposal.id) }
                        )
                    }
                } else {
                    items(vendorProposals, key = { it.id }) { proposal ->
                        AdminVendorProposalManageCard(
                            proposal = proposal,
                            onCardClick = { showVendorDetail = proposal },
                            onConnect = { proposalViewModel?.acceptVendorProposal(proposal.id) },
                            onDecline = { proposalViewModel?.declineVendorProposal(proposal.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProposalManageCard(
    proposal: AdminProposal,
    onCardClick: () -> Unit,
    onConnect: () -> Unit,
    onDecline: () -> Unit
) {
    val (accentColor, pillBg, statusLabel) = when (proposal.status) {
        AdminProposalStatus.PENDING -> Triple(NoorOrange, NoorOrangeLight, "⏳ Pending")
        AdminProposalStatus.ACCEPTED -> Triple(NoorGreen, NoorGreenLight, "✅ Accepted")
        AdminProposalStatus.DECLINED -> Triple(NoorRed, NoorRedLight, "❌ Declined")
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() },
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
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NoorBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                when (proposal.proposalType) {
                                    "vendor" -> "🏢"
                                    else     -> "👷"
                                },
                                fontSize = 20.sp
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

                Spacer(Modifier.height(6.dp))

                if (proposal.employerName.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤", fontSize = 10.sp)
                        Text(
                            "From: ${proposal.employerName}",
                            fontSize = 10.sp,
                            color = NoorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }

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
private fun AdminVendorProposalManageCard(
    proposal: VendorProposal,
    onCardClick: () -> Unit,
    onConnect: () -> Unit,
    onDecline: () -> Unit
) {
    val (accentColor, pillBg, statusLabel) = when (proposal.status) {
        com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING -> Triple(NoorBlue, NoorBlueLight, "Pending")
        com.danish.noorservice.ui.screens.employer.VendorProposalStatus.ACCEPTED -> Triple(NoorGreen, NoorGreenLight, "Accepted")
        com.danish.noorservice.ui.screens.employer.VendorProposalStatus.DECLINED -> Triple(NoorRed, NoorRedLight, "Declined")
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onCardClick() },
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
                        // FIXED: Hardcoded emoji instead of proposal.vendorEmoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(VendorTealLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                proposal.vendorName, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = NoorTextPrimary
                            )
                            Text(proposal.vendorCity, fontSize = 11.sp, color = NoorTextHint)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
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
                Text("${proposal.serviceLabel} · ${proposal.location}", fontSize = 11.sp, color = NoorTextHint)

                Spacer(Modifier.height(6.dp))

                if (proposal.employerName.isNotBlank()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👤", fontSize = 10.sp)
                        Text(
                            "From: ${proposal.employerName}",
                            fontSize = 10.sp,
                            color = NoorTextHint,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AdminProposalTag("📅", proposal.startDate)
                    AdminProposalTag("💰", proposal.budget)
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

                if (proposal.status == com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING) {
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

                if (proposal.status == com.danish.noorservice.ui.screens.employer.VendorProposalStatus.ACCEPTED) {
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
                            Text("Proposal accepted. Employer and vendor have been connected.",
                                fontSize = 11.sp, color = NoorGreen, lineHeight = 15.sp)
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
        modifier = Modifier.padding(horizontal = 2.dp),
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


// ═════════════════════════════════════════════════════════════════════════════
// Admin Worker Proposal Detail Screen
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminWorkerProposalDetailScreen(
    proposal: AdminProposal,
    onBack: () -> Unit,
    proposalViewModel: AdminProposalViewModel? = null
) {
    Log.d("AdminDetail", "AdminWorkerProposalDetailScreen: proposal.employerName=${proposal.employerName}, employerPhone=${proposal.employerPhone}, employerCity=${proposal.employerCity}")
    val context = LocalContext.current
    var workerExpanded by remember { mutableStateOf(true) }
    var employerExpanded by remember { mutableStateOf(true) }
    var jobExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Worker Proposal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(proposal.workerName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val (statusColor, statusBg, statusLabel) = when (proposal.status) {
                AdminProposalStatus.PENDING -> Triple(NoorBlue, NoorBlueLight, "⏳ Pending")
                AdminProposalStatus.ACCEPTED -> Triple(NoorGreen, NoorGreenLight, "✅ Accepted")
                AdminProposalStatus.DECLINED -> Triple(NoorRed, NoorRedLight, "❌ Declined")
            }
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(statusBg).padding(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(statusLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    Text("Sent ${proposal.sentAt}", fontSize = 12.sp, color = NoorTextHint)
                }
            }

            AdminDetailSection(title = "👤 EMPLOYER INFO", expanded = employerExpanded, onToggle = { employerExpanded = !employerExpanded }) {
                AdminContactCard(
                    name = proposal.employerName,
                    phone = proposal.employerPhone,
                    email = proposal.employerEmail,
                    city = proposal.employerCity,
                    area = proposal.employerArea,
                    address = proposal.employerAddress,
                    context = context,
                    photoUrl = proposal.employerPhotoUrl
                )
            }

            AdminDetailSection(title = "👷 WORKER INFO", expanded = workerExpanded, onToggle = { workerExpanded = !workerExpanded }) {
                AdminContactCard(
                    name = proposal.workerName,
                    phone = proposal.workerPhoneFull,
                    email = proposal.workerEmail,
                    city = proposal.workerCity,
                    area = proposal.workerArea,
                    address = proposal.workerAddress,
                    context = context,
                    photoUrl = proposal.workerPhotoUrl
                )

                Spacer(Modifier.height(10.dp))

                if (proposal.workerCnic.isNotBlank()) {
                    AdminSettingDetailRow("🪪", "CNIC", proposal.workerCnic)
                }
                if (proposal.workerGender.isNotBlank()) {
                    AdminSettingDetailRow("⚧", "Gender", proposal.workerGender)
                }
                if (proposal.workerDob.isNotBlank()) {
                    AdminSettingDetailRow("🎂", "Date of Birth", proposal.workerDob)
                }
                if (proposal.workerLicenceType.isNotBlank()) {
                    AdminSettingDetailRow("🪪", "Licence", proposal.workerLicenceType)
                }

                if (proposal.workerSkills.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("⚡ Skills", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        proposal.workerSkills.forEach { skill ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(NoorOrangeLight).padding(horizontal = 10.dp, vertical = 5.dp)
                            ) { Text(skill, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorOrange) }
                        }
                    }
                }

                if (proposal.workerLanguages.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("🌐 Languages", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        proposal.workerLanguages.forEach { lang ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(NoorGreenLight).padding(horizontal = 10.dp, vertical = 5.dp)
                            ) { Text(lang, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorGreen) }
                        }
                    }
                }

                if (proposal.workerDailyRate.isNotBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AdminRateChip("Daily", proposal.workerDailyRate)
                        if (proposal.workerHourlyRate.isNotBlank()) {
                            AdminRateChip("Hourly", proposal.workerHourlyRate)
                        }
                        if (proposal.workerMonthlyRate.isNotBlank()) {
                            AdminRateChip("Monthly", proposal.workerMonthlyRate)
                        }
                    }
                }

                if (proposal.workerTimeSlot.isNotBlank()) {
                    AdminSettingDetailRow("⏰", "Time Slot", proposal.workerTimeSlot)
                }
                if (proposal.workerAvailableDays.isNotEmpty()) {
                    AdminSettingDetailRow("📅", "Available Days", proposal.workerAvailableDays.joinToString(", "))
                }
                if (proposal.workerBio.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("About", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(4.dp))
                    Text(proposal.workerBio, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
                }
            }

            AdminDetailSection(title = "📋 JOB DETAILS", expanded = jobExpanded, onToggle = { jobExpanded = !jobExpanded }) {
                AdminSettingDetailRow("💼", "Job Title", proposal.jobTitle)
                AdminSettingDetailRow("🔧", "Service", proposal.service)
                AdminSettingDetailRow("📍", "Location", proposal.location)
                AdminSettingDetailRow("⏰", "Schedule", proposal.schedule)
                AdminSettingDetailRow("🗓", "Start Date", proposal.startDate)
                AdminSettingDetailRow("💰", "Offer Price", proposal.offerPrice)
                if (proposal.note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("📝 Note", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(4.dp))
                    Text(proposal.note, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
                }
            }

            if (proposal.status == AdminProposalStatus.PENDING) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { proposalViewModel?.acceptWorkerProposal(proposal.id) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Accept", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = { proposalViewModel?.declineWorkerProposal(proposal.id) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = NoorRed)
                        Spacer(Modifier.width(6.dp))
                        Text("Decline", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Admin Vendor Proposal Detail Screen
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminVendorProposalDetailScreen(
    proposal: VendorProposal,
    onBack: () -> Unit,
    proposalViewModel: AdminProposalViewModel? = null
) {
    val context = LocalContext.current
    var employerExpanded by remember { mutableStateOf(true) }
    var vendorExpanded by remember { mutableStateOf(true) }
    var jobExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Vendor Proposal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(proposal.vendorName, fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val (statusColor, statusBg, statusLabel) = when (proposal.status) {
                com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING -> Triple(NoorBlue, NoorBlueLight, "⏳ Pending")
                com.danish.noorservice.ui.screens.employer.VendorProposalStatus.ACCEPTED -> Triple(NoorGreen, NoorGreenLight, "✅ Accepted")
                com.danish.noorservice.ui.screens.employer.VendorProposalStatus.DECLINED -> Triple(NoorRed, NoorRedLight, "❌ Declined")
            }
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(statusBg).padding(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(statusLabel, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    Text("Sent ${proposal.sentAt}", fontSize = 12.sp, color = NoorTextHint)
                }
            }

            AdminDetailSection(title = "👤 EMPLOYER INFO", expanded = employerExpanded, onToggle = { employerExpanded = !employerExpanded }) {
                AdminContactCard(
                    name = proposal.employerName,
                    phone = proposal.employerPhone,
                    email = proposal.employerEmail,
                    city = proposal.employerCity,
                    area = proposal.employerArea,
                    address = proposal.employerAddress,
                    context = context,
                    photoUrl = proposal.employerphotourl
                )
            }

            AdminDetailSection(title = "🏢 VENDOR INFO", expanded = vendorExpanded, onToggle = { vendorExpanded = !vendorExpanded }) {
                AdminContactCard(
                    name = proposal.vendorName,
                    phone = proposal.vendorPhoneFull,
                    email = proposal.vendorEmail,
                    city = proposal.vendorCity,
                    area = "",
                    address = proposal.vendorAddress,
                    context = context,
                    photoUrl = proposal.vendorLogoUrl
                )

                Spacer(Modifier.height(10.dp))

                if (proposal.vendorContactPerson.isNotBlank()) {
                    AdminSettingDetailRow("👔", "Contact Person", proposal.vendorContactPerson)
                }
                if (proposal.vendorNtn.isNotBlank()) {
                    AdminSettingDetailRow("🪪", "NTN", proposal.vendorNtn)
                }
                if (proposal.vendorRegNumber.isNotBlank()) {
                    AdminSettingDetailRow("📋", "Reg Number", proposal.vendorRegNumber)
                }
                if (proposal.vendorHeadOffice.isNotBlank()) {
                    AdminSettingDetailRow("🏢", "Head Office", proposal.vendorHeadOffice)
                }
                if (proposal.vendorWorkforceScale.isNotBlank()) {
                    AdminSettingDetailRow("👥", "Workforce", proposal.vendorWorkforceScale)
                }
                if (proposal.vendorYearsInBusiness > 0) {
                    AdminSettingDetailRow("📅", "Years in Business", "${proposal.vendorYearsInBusiness} years")
                }
                if (proposal.vendorIsoCertified) {
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(NoorGreenLight).padding(horizontal = 10.dp, vertical = 5.dp)
                    ) { Text("✓ ISO Certified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NoorGreen) }
                }

                if (proposal.vendorOperatingCities.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("📍 Operating Cities", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        proposal.vendorOperatingCities.forEach { city ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(NoorBlueLight).padding(horizontal = 10.dp, vertical = 5.dp)
                            ) { Text(city, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue) }
                        }
                    }
                }

                if (proposal.vendorNotableClients.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("🏆 Notable Clients", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        proposal.vendorNotableClients.forEach { client ->
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(NoorBackground).border(1.dp, NoorBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) { Text(client, fontSize = 11.sp, color = NoorTextSecondary) }
                        }
                    }
                }

                if (proposal.vendorBio.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("About", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(4.dp))
                    Text(proposal.vendorBio, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
                }
            }

            AdminDetailSection(title = "📋 JOB DETAILS", expanded = jobExpanded, onToggle = { jobExpanded = !jobExpanded }) {
                AdminSettingDetailRow("💼", "Service Required", proposal.serviceLabel)
                AdminSettingDetailRow("📍", "Location", proposal.location)
                AdminSettingDetailRow("📅", "Duration", proposal.schedule)
                AdminSettingDetailRow("🗓", "Start Date", proposal.startDate)
                AdminSettingDetailRow("💰", "Budget", proposal.budget)
                if (proposal.note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("📝 Note", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NoorTextHint)
                    Spacer(Modifier.height(4.dp))
                    Text(proposal.note, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 18.sp)
                }
            }

            if (proposal.status == com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { proposalViewModel?.acceptVendorProposal(proposal.id) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Accept", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = { proposalViewModel?.declineVendorProposal(proposal.id) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = NoorRed)
                        Spacer(Modifier.width(6.dp))
                        Text("Decline", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable components for detail screens
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminDetailSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = AdminPurple, letterSpacing = 0.8.sp)
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null, tint = NoorTextHint, modifier = Modifier.size(20.dp)
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
private fun AdminContactCard(
    name: String,
    phone: String,
    email: String,
    city: String,
    area: String,
    address: String,
    context: Context,
    photoUrl: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NoorBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model              = photoUrl,
                        contentDescription = "Photo",
                        modifier           = Modifier.size(40.dp).clip(CircleShape),
                        contentScale       = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier         = Modifier.size(40.dp).clip(CircleShape).background(AdminPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.take(2).uppercase(), fontSize = 14.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(name.ifBlank { "N/A" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                    if (city.isNotBlank() || area.isNotBlank()) {
                        Text("$area, $city".trimStart(',', ' '), fontSize = 11.sp, color = NoorTextHint)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (phone.isNotBlank()) {
                    Button(
                        onClick = {
                            val cleanPhone = phone.replace(Regex("[^0-9]"), "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/92$cleanPhone"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("💬", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(phone, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
                if (email.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:$email") }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("📧", fontSize = 14.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(email, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                }
            }
            if (address.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Top) {
                    Text("🏠", fontSize = 12.sp)
                    Text(address, fontSize = 11.sp, color = NoorTextSecondary)
                }
            }
        }
    }
}

@Composable
private fun AdminSettingDetailRow(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(icon, fontSize = 13.sp)
            Text(label, fontSize = 11.sp, color = NoorTextHint, fontWeight = FontWeight.Medium)
        }
        Text(value.ifBlank { "—" }, fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = NoorTextPrimary, modifier = Modifier.weight(0.55f))
    }
}

@Composable
private fun AdminRateChip(label: String, value: String) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(NoorGreenLight).padding(horizontal = 10.dp, vertical = 5.dp)
    ) { Text("$label: $value", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = NoorGreen) }
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