package com.danish.noorservice.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.R
import com.danish.noorservice.ui.components.ShimmerBox
import com.danish.noorservice.ui.components.rememberShimmerBrush
import com.danish.noorservice.ui.screens.employer.AdminProposalStatus
import com.danish.noorservice.ui.screens.employer.AdminProposalStore
import com.danish.noorservice.ui.screens.employer.sampleWorkers
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminDashboardScreen(
    viewModel: com.danish.noorservice.viewmodel.admin.AdminDashboardViewModel? = null,
    onNavigate: (tab: Int) -> Unit = {},
    onNavigateToProposalInbox: () -> Unit = {},
    onNavigateToAnnouncements: () -> Unit = {},
    onNavigateToCategoryManagement: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val dashboardViewModel = viewModel ?: androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by dashboardViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.loadDashboard()
    }

    val pendingProposals by remember {
        derivedStateOf {
            AdminProposalStore.proposals.count { it.status == AdminProposalStatus.PENDING }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(AdminPurple, AdminPurpleDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter            = painterResource(id = R.drawable.noor_services_app_logo),
                            contentDescription = "Logo",
                            modifier           = Modifier
                                .size(45.dp).clip(CircleShape)
                                .graphicsLayer { scaleX = 3f; scaleY = 3f; translationY = 39f; translationX = 4f },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Danish Awan",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.2).sp)
                        Text("admin_danish@noorservice.com",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                    }
                }
                IconButton(onClick = onNavigateToSettings, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            val showShimmer = uiState.isLoading || !uiState.hasLoaded
            
            if (showShimmer) {
                val brush = rememberShimmerBrush()
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(18.dp)), brush = brush)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                        ShimmerBox(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)), brush = brush)
                    }
                }
            } else if (uiState.hasLoaded) {
            // ── Welcome banner ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(AdminPurple, AdminAccent)))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Welcome back, Admin 👋",
                            fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                            color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "You have $pendingProposals pending ${if (pendingProposals == 1) "proposal" else "proposals"} to action.",
                            fontSize = 12.sp, color = Color.White.copy(alpha = 0.82f),
                            lineHeight = 17.sp
                        )
                        if (pendingProposals > 0) {
                            Spacer(Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .clickable { onNavigateToProposalInbox() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Review Proposals →",
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    color = Color.White)
                            }
                        }
                    }
                    Text("🛡️", fontSize = 40.sp)
                }
            }

            // ── KPI Stats Grid ────────────────────────────────────────────────
            Text("Platform Overview", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = NoorTextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Spacer(Modifier.height(6.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard("${uiState.totalEmployees}", "Workers", "👷", AdminPurple, AdminPurpleLight,
                    modifier = Modifier.weight(1f), onClick = { onNavigate(1) })
                AdminStatCard("${uiState.totalEmployers}", "Employers", "🏠", NoorOrange, NoorOrangeLight,
                    modifier = Modifier.weight(1f), onClick = { onNavigate(2) })
                AdminStatCard("${uiState.totalVendors}", "Vendors", "🏢", VendorTeal, VendorTealLight,
                    modifier = Modifier.weight(1f), onClick = { onNavigate(3) })
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier              = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard("0", "Proposals Received", "📋", NoorGreen, NoorGreenLight,
                    modifier = Modifier.weight(1f))
                AdminStatCard("$pendingProposals", "Pending Actions", "⚡", NoorRed, NoorRedLight,
                    modifier = Modifier.weight(1f))
                AdminStatCard("${uiState.totalServices}", "Services", "🛠️", NoorBlue, NoorBlueLight,
                    modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            // ── Quick Actions ─────────────────────────────────────────────────
            Text("Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = NoorTextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Spacer(Modifier.height(6.dp))

            AdminActionCard(
                emoji = "📋", emojiBg = NoorOrangeLight,
                title = "Proposal Inbox",
                description = "Review and manage worker proposals from employers",
                badge = if (pendingProposals > 0) "$pendingProposals pending" else null,
                badgeColor = NoorOrange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                onClick = onNavigateToProposalInbox
            )

            AdminActionCard(
                emoji = "📢", emojiBg = AdminPurpleLight,
                title = "Announcements",
                description = "Create and manage platform-wide announcements for all users",
                badge = null, badgeColor = NoorOrange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                onClick = onNavigateToAnnouncements
            )

            AdminActionCard(
                emoji = "🗂️", emojiBg = VendorTealLight,
                title = "Manage Categories & Skills",
                description = "Add, edit, or remove worker categories and skill sets",
                badge = null, badgeColor = NoorOrange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                onClick = onNavigateToCategoryManagement
            )

            Spacer(Modifier.height(8.dp))

            // ── Pending Proposals ─────────────────────────────────────────────
            if (pendingProposals > 0) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("⚡ Recent Pending Proposals",
                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                    TextButton(onClick = onNavigateToProposalInbox,
                        contentPadding = PaddingValues(0.dp)) {
                        Text("View all", fontSize = 11.sp, color = AdminPurple,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(8.dp))

                AdminProposalStore.proposals
                    .filter { it.status == AdminProposalStatus.PENDING }  // ← was SENT
                    .take(3)
                    .forEach { proposal ->
                        DashboardProposalCard(
                            workerName     = proposal.workerName,
                            workerUsername = proposal.workerUsername,
                            jobTitle       = proposal.jobTitle,
                            sentAt         = proposal.sentAt,
                            avatarColor    = proposal.avatarColor,
                            initials       = proposal.workerInitials,
                            onConnect = {
                                val idx = AdminProposalStore.proposals.indexOfFirst { p -> p.id == proposal.id }
                                if (idx != -1) {
                                    AdminProposalStore.proposals[idx] = AdminProposalStore.proposals[idx]
                                        .copy(status = AdminProposalStatus.ACCEPTED)  // ← was CONNECTED
                                }
                            },
                            onDecline = {
                                val idx = AdminProposalStore.proposals.indexOfFirst { p -> p.id == proposal.id }
                                if (idx != -1) {
                                    AdminProposalStore.proposals[idx] = AdminProposalStore.proposals[idx]
                                        .copy(status = AdminProposalStatus.DECLINED)
                                }
                            }
                        )
                    }
                Spacer(Modifier.height(8.dp))
            }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Admin Action Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminActionCard(
    emoji: String,
    emojiBg: Color,
    title: String,
    description: String,
    badge: String? = null,
    badgeColor: Color = NoorOrange,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth().clickable { onClick() },
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 20.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = NoorTextPrimary)
                    if (badge != null) {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                .background(badgeColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge, fontSize = 8.sp,
                                fontWeight = FontWeight.Medium, color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(description, fontSize = 10.sp, color = NoorTextHint,
                    lineHeight = 13.sp, maxLines = 2)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = NoorTextHint, modifier = Modifier.size(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stat card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminStatCard(
    value: String, label: String, emoji: String,
    valueColor: Color, bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier  = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(bgColor),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 16.sp) }
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Text(label, fontSize = 9.sp, color = NoorTextHint, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center, lineHeight = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard proposal card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardProposalCard(
    workerName: String, workerUsername: String, jobTitle: String,
    sentAt: String, avatarColor: Color, initials: String,
    onConnect: () -> Unit, onDecline: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.width(4.dp).fillMaxHeight()
                    .background(NoorOrange, RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(workerName, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Text("$workerUsername · $jobTitle", fontSize = 10.sp, color = NoorTextHint)
                    }
                    Text(sentAt, fontSize = 9.sp, color = NoorTextHint)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick         = onConnect,
                        modifier        = Modifier.weight(1f).height(34.dp),
                        shape           = RoundedCornerShape(8.dp),
                        colors          = ButtonDefaults.buttonColors(containerColor = NoorGreen),
                        contentPadding  = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null,
                            modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Accept", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick         = onDecline,
                        modifier        = Modifier.weight(1f).height(34.dp),
                        shape           = RoundedCornerShape(8.dp),
                        colors          = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                        contentPadding  = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null,
                            modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Decline", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}