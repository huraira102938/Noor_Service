package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

enum class ProposalStatus { PENDING, ACCEPTED, DECLINED }

data class EmployerProposal(
    val id: String,
    val workerName: String,
    val workerInitials: String,
    val avatarColor: Color,
    val service: String,
    val area: String,
    val schedule: String,
    val startDate: String,
    val timeSlot: String,
    val dailyRate: String,
    val status: ProposalStatus,
    val jobTitle: String,
    val note: String? = null
)

private val allProposals = mutableStateListOf(
    EmployerProposal(
        id = "1", workerName = "Muhammad Ali", workerInitials = "MA", avatarColor = NoorBlue,
        service = "Driver", area = "DHA Phase 5", schedule = "Mon–Fri",
        startDate = "Apr 1, 2026", timeSlot = "Full Day", dailyRate = "PKR 1,200",
        status = ProposalStatus.ACCEPTED, jobTitle = "Full-time Driver",
        note = "Need someone for daily school pickup"
    ),
    EmployerProposal(
        id = "2", workerName = "Ayesha Bibi", workerInitials = "AB", avatarColor = NoorOrange,
        service = "Maid", area = "DHA Phase 3", schedule = "Mon, Wed, Fri",
        startDate = "Apr 3, 2026", timeSlot = "Morning", dailyRate = "PKR 900",
        status = ProposalStatus.PENDING, jobTitle = "Part-time Maid",
        note = "Need cleaning 3 days a week"
    ),
    EmployerProposal(
        id = "3", workerName = "Nazia Malik", workerInitials = "NM", avatarColor = Color(0xFFE91E63),
        service = "Cook", area = "DHA Phase 3", schedule = "Daily",
        startDate = "Mar 15, 2026", timeSlot = "Morning", dailyRate = "PKR 1,300",
        status = ProposalStatus.ACCEPTED, jobTitle = "Daily Cook",
        note = "Need lunch preparation"
    ),
    EmployerProposal(
        id = "4", workerName = "Zulfiqar Ali", workerInitials = "ZA", avatarColor = Color(0xFF009688),
        service = "Driver", area = "Gulberg III", schedule = "Weekends",
        startDate = "Mar 20, 2026", timeSlot = "Full Day", dailyRate = "PKR 1,100",
        status = ProposalStatus.DECLINED, jobTitle = "Weekend Driver",
        note = "Need someone for weekend errands"
    ),
    EmployerProposal(
        id = "5", workerName = "Sana Fatima", workerInitials = "SF", avatarColor = Color(0xFF9C27B0),
        service = "Babysitter", area = "DHA Phase 3", schedule = "Mon–Thu",
        startDate = "Apr 8, 2026", timeSlot = "Flexible", dailyRate = "PKR 1,000",
        status = ProposalStatus.PENDING, jobTitle = "Evening Babysitter",
        note = "Need care for 2 children, 4-8 PM"
    ),
)

// ─────────────────────────────────────────────────────────────────────────────
// Proposals Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerProposalsScreen(
    onMessageWorker: ((EmployerProposal) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Pending", "Accepted", "Declined")

    // Use mutable state list to allow deletion
    val proposals = remember { allProposals }

    val filtered = when (selectedTab) {
        0    -> proposals.filter { it.status == ProposalStatus.PENDING }
        1    -> proposals.filter { it.status == ProposalStatus.ACCEPTED }
        else -> proposals.filter { it.status == ProposalStatus.DECLINED }
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 0.dp)
        ) {
            Column {
                Text("My Proposals", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text("Track your sent proposals", fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                Spacer(Modifier.height(16.dp))

                // Tab row
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { index, label ->
                        val count = when (index) {
                            0    -> proposals.count { it.status == ProposalStatus.PENDING }
                            1    -> proposals.count { it.status == ProposalStatus.ACCEPTED }
                            else -> proposals.count { it.status == ProposalStatus.DECLINED }
                        }
                        val isSelected = selectedTab == index
                        Column(
                            modifier            = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextButton(onClick = { selectedTab = index }) {
                                Text(
                                    "$label ($count)",
                                    fontSize   = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color      = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth().height(3.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // ── List ──────────────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (selectedTab == 0) "📝" else if (selectedTab == 1) "✅" else "❌", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("No ${tabs[selectedTab].lowercase()} proposals", fontSize = 14.sp,
                        color = NoorTextHint, fontWeight = FontWeight.Medium)
                    if (selectedTab == 0) {
                        Spacer(Modifier.height(8.dp))
                        Text("Browse workers and send proposals", fontSize = 11.sp, color = NoorTextHint)
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { proposal ->
                    EmployerProposalCard(
                        proposal = proposal,
                        onCancel = { proposalToCancel ->
                            // Remove the proposal from the list
                            val index = proposals.indexOfFirst { it.id == proposalToCancel.id }
                            if (index != -1) {
                                proposals.removeAt(index)
                            }
                        },
                        onMessageWorker = onMessageWorker
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Proposal Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployerProposalCard(
    proposal: EmployerProposal,
    onCancel: (EmployerProposal) -> Unit,
    onMessageWorker: ((EmployerProposal) -> Unit)?
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val accentColor = when (proposal.status) {
        ProposalStatus.PENDING    -> NoorOrange
        ProposalStatus.ACCEPTED   -> NoorGreen
        ProposalStatus.DECLINED   -> NoorRed
    }
    val pillBg = when (proposal.status) {
        ProposalStatus.PENDING    -> NoorOrangeLight
        ProposalStatus.ACCEPTED   -> NoorGreenLight
        ProposalStatus.DECLINED   -> NoorRedLight
    }
    val statusLabel = when (proposal.status) {
        ProposalStatus.PENDING    -> "Pending"
        ProposalStatus.ACCEPTED   -> "Accepted"
        ProposalStatus.DECLINED   -> "Declined"
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { showDetailsDialog = true },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Accent bar
            Box(
                modifier = Modifier.width(4.dp).fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Column(modifier = Modifier.padding(14.dp).weight(1f)) {
                // Top row
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Worker avatar
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(proposal.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(proposal.workerInitials, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Column {
                            Text(proposal.workerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                            Text("${proposal.service} · ${proposal.area}", fontSize = 11.sp, color = NoorTextHint)
                        }
                    }
                    // Status pill
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(pillBg).padding(horizontal = 10.dp, vertical = 4.dp)
                    ) { Text(statusLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accentColor) }
                }

                Spacer(Modifier.height(8.dp))

                // Job title
                Text(
                    proposal.jobTitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NoorTextPrimary
                )

                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                Spacer(Modifier.height(8.dp))

                // Detail tags - FIXED: Made scrollable horizontally
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProposalTag("🗓", proposal.startDate)
                    ProposalTag("⏰", proposal.timeSlot)
                    ProposalTag("💰", proposal.dailyRate)
                    ProposalTag("📅", proposal.schedule)
                }

                // Action buttons for pending proposals
                if (proposal.status == ProposalStatus.PENDING) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick  = { showCancelDialog = true },
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                            border   = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(NoorRed, NoorRed))
                            )
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Message button for accepted proposals - FIXED: Added functionality
                if (proposal.status == ProposalStatus.ACCEPTED) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick  = { onMessageWorker?.invoke(proposal) },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorBlue),
                        border   = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(NoorBlue, NoorBlue))
                        )
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Message Worker", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = NoorSurface,
            title = {
                Text(
                    "Cancel Proposal?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NoorTextPrimary
                )
            },
            text = {
                Text(
                    "Are you sure you want to abandon this offer? This action cannot be undone.",
                    fontSize = 13.sp,
                    color = NoorTextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancel(proposal)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NoorRed),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Yes, Cancel", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCancelDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("No, Keep It", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    // Proposal Details Dialog
    if (showDetailsDialog) {
        ProposalDetailsDialog(
            proposal = proposal,
            onDismiss = { showDetailsDialog = false }
        )
    }
}

@Composable
private fun ProposalTag(icon: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NoorBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon,  fontSize = 11.sp)
        Text(label, fontSize = 10.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProposalDetailsDialog(
    proposal: EmployerProposal,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = NoorSurface,
        title = {
            Column {
                Text("📄 Proposal Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NoorTextPrimary)
                Text("to ${proposal.workerName}", fontSize = 13.sp, color = NoorTextHint)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Job Information Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Job Information", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue)

                        DetailRowWithIcon("💼", "Job Title", proposal.jobTitle)
                        DetailRowWithIcon("🔧", "Service", proposal.service)
                        DetailRowWithIcon("📍", "Location", proposal.area)
                        DetailRowWithIcon("⏰", "Schedule", proposal.schedule)
                        DetailRowWithIcon("📅", "Start Date", proposal.startDate)
                        DetailRowWithIcon("🕐", "Time Slot", proposal.timeSlot)
                        DetailRowWithIcon("💰", "Daily Rate", proposal.dailyRate)
                    }
                }

                // Additional Note Section
                if (!proposal.note.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NoorBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📝 Additional Note", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue)
                            Text(
                                proposal.note,
                                fontSize = 13.sp,
                                color = NoorTextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Status Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NoorBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📊 Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorBlue)

                        val statusColor = when (proposal.status) {
                            ProposalStatus.PENDING -> NoorOrange
                            ProposalStatus.ACCEPTED -> NoorGreen
                            ProposalStatus.DECLINED -> NoorRed
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(statusColor.copy(alpha = 0.1f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                when (proposal.status) {
                                    ProposalStatus.PENDING -> "⏳ Pending"
                                    ProposalStatus.ACCEPTED -> "✅ Accepted"
                                    ProposalStatus.DECLINED -> "❌ Declined"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorBlue),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun DetailRowWithIcon(icon: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(0.4f)
        ) {
            Text(icon, fontSize = 14.sp)
            Text(label, fontSize = 12.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
        }
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = NoorTextPrimary,
            modifier = Modifier.weight(0.6f)
        )
    }
}