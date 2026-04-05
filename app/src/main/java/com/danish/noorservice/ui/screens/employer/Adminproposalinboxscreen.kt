package com.danish.noorservice.ui.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Data model — proposal sent to admin
// ─────────────────────────────────────────────────────────────────────────────

enum class AdminProposalStatus {
    SENT,       // waiting for admin to act
    REVIEWED,   // admin has seen it
    CONNECTED,  // admin connected employer with worker
    DECLINED    // admin declined (worker unavailable etc.)
}

data class AdminProposal(
    val id: String,
    val workerName: String,
    val workerUsername: String,   // e.g. @NS-1042 — unique identifier for admin tracking
    val workerInitials: String,
    val avatarColor: Color,
    val jobTitle: String,
    val service: String,
    val location: String,
    val schedule: String,
    val startDate: String,
    val offerPrice: String,
    val note: String,
    val sentAt: String,
    val status: AdminProposalStatus = AdminProposalStatus.SENT
)

// ─────────────────────────────────────────────────────────────────────────────
// Shared in-memory store (single source of truth for this session)
// In production this would be a ViewModel / repository backed by a server.
// ─────────────────────────────────────────────────────────────────────────────

object AdminProposalStore {
    val proposals = mutableStateListOf<AdminProposal>()
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminProposalInboxScreen() {
    val proposals = AdminProposalStore.proposals

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Sent", "Reviewed", "Connected", "Declined")

    val filtered = when (selectedTab) {
        1    -> proposals.filter { it.status == AdminProposalStatus.SENT }
        2    -> proposals.filter { it.status == AdminProposalStatus.REVIEWED }
        3    -> proposals.filter { it.status == AdminProposalStatus.CONNECTED }
        4    -> proposals.filter { it.status == AdminProposalStatus.DECLINED }
        else -> proposals.toList()
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
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 0.dp)
        ) {
            Column {
                Text(
                    "My Proposals",
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Proposals you've sent to the admin",
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.72f)
                )

                Spacer(Modifier.height(16.dp))

                // ── Tab Row ───────────────────────────────────────────────────
                androidx.compose.foundation.lazy.LazyRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding        = PaddingValues(end = 8.dp)
                ) {
                    items(tabs.size) { index ->
                        val label = tabs[index]
                        val count = when (index) {
                            0    -> proposals.size
                            1    -> proposals.count { it.status == AdminProposalStatus.SENT }
                            2    -> proposals.count { it.status == AdminProposalStatus.REVIEWED }
                            3    -> proposals.count { it.status == AdminProposalStatus.CONNECTED }
                            4    -> proposals.count { it.status == AdminProposalStatus.DECLINED }
                            else -> 0
                        }
                        val isSelected = selectedTab == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.clickable { selectedTab = index }
                        ) {
                            Text(
                                text       = if (count > 0) "$label ($count)" else label,
                                fontSize   = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color      = if (isSelected) Color.White
                                else Color.White.copy(alpha = 0.55f),
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .width(if (isSelected) 40.dp else 0.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }

        // ── List ──────────────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📋", fontSize = 48.sp)
                    Text(
                        "No proposals yet",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = NoorTextPrimary
                    )
                    Text(
                        "Browse workers and send a proposal to admin.",
                        fontSize = 12.sp,
                        color    = NoorTextHint
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    AdminProposalCard(
                        proposal = proposal,
                        onCancel = {
                            val idx = AdminProposalStore.proposals.indexOfFirst { p -> p.id == it.id }
                            if (idx != -1) AdminProposalStore.proposals.removeAt(idx)
                        }
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
private fun AdminProposalCard(
    proposal: AdminProposal,
    onCancel: (AdminProposal) -> Unit
) {
    var showDetails    by remember { mutableStateOf(false) }
    var showCancelDlg  by remember { mutableStateOf(false) }

    val (accentColor, pillBg, statusLabel, statusEmoji) = when (proposal.status) {
        AdminProposalStatus.SENT      -> listOf(NoorBlue,   NoorBlueLight,   "Sent to Admin",  "📤")
        AdminProposalStatus.REVIEWED  -> listOf(NoorOrange, NoorOrangeLight, "Under Review",   "👀")
        AdminProposalStatus.CONNECTED -> listOf(NoorGreen,  NoorGreenLight,  "Admin Connected","✅")
        AdminProposalStatus.DECLINED  -> listOf(NoorRed,    NoorRedLight,    "Declined",       "❌")
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { showDetails = true },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        accentColor as Color,
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // ── Top row ───────────────────────────────────────────────────
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.Top
                ) {
                    // Worker avatar + name
                    Row(
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier             = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(proposal.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                proposal.workerInitials,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.White
                            )
                        }
                        Column {
                            Text(
                                proposal.workerName,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color      = NoorTextPrimary,
                                maxLines   = 1,
                                overflow   = TextOverflow.Ellipsis
                            )
                            // ── Username badge ─────────────────────────────────
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NoorBlueDark.copy(alpha = 0.08f))
                                    .border(1.dp, NoorBlue.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    proposal.workerUsername,
                                    fontSize   = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = NoorBlue
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg as Color)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "$statusEmoji $statusLabel",
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color      = accentColor
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Job title
                Text(
                    proposal.jobTitle,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = NoorTextPrimary
                )
                Text(
                    "${proposal.service} · ${proposal.location}",
                    fontSize = 11.sp,
                    color    = NoorTextHint
                )

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                Spacer(Modifier.height(8.dp))

                // Detail tags
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProposalDetailTag("📅", proposal.startDate)
                    ProposalDetailTag("⏰", proposal.schedule)
                    ProposalDetailTag("💰", proposal.offerPrice)
                    ProposalDetailTag("🕐", "Sent ${proposal.sentAt}")
                }

                // Cancel button — only for SENT proposals
                if (proposal.status == AdminProposalStatus.SENT) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick  = { showCancelDlg = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                        border   = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(listOf(NoorRed, NoorRed))
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null,
                            modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cancel Proposal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Connected success strip
                if (proposal.status == AdminProposalStatus.CONNECTED) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NoorGreenLight)
                            .padding(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null,
                                tint     = NoorGreen,
                                modifier = Modifier.size(16.dp))
                            Text(
                                "Admin has connected you with this worker. Expect a call soon.",
                                fontSize   = 11.sp,
                                color      = NoorGreen,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Details Dialog ────────────────────────────────────────────────────────
    if (showDetails) {
        ProposalDetailsDialog(proposal = proposal, onDismiss = { showDetails = false })
    }

    // ── Cancel Confirm Dialog ─────────────────────────────────────────────────
    if (showCancelDlg) {
        AlertDialog(
            onDismissRequest = { showCancelDlg = false },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = NoorSurface,
            title = {
                Text("Cancel Proposal?", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = NoorTextPrimary)
            },
            text = {
                Text(
                    "This will withdraw your proposal for ${proposal.workerName} (${proposal.workerUsername}). " +
                            "The admin will no longer receive it.",
                    fontSize   = 13.sp,
                    color      = NoorTextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCancelDlg = false; onCancel(proposal) },
                    shape   = RoundedCornerShape(10.dp),
                    colors  = ButtonDefaults.buttonColors(containerColor = NoorRed),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Yes, Cancel", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick  = { showCancelDlg = false },
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("No, Keep", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Proposal Details Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProposalDetailsDialog(
    proposal: AdminProposal,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape            = RoundedCornerShape(20.dp),
        containerColor   = NoorSurface,
        title = {
            Column {
                Text("📄 Proposal Details",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NoorTextPrimary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("for ${proposal.workerName}", fontSize = 12.sp, color = NoorTextHint)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NoorBlueLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(proposal.workerUsername, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold, color = NoorBlue)
                    }
                }
            }
        },
        text = {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailCard {
                    Text("Job Information", fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorBlue)
                    Spacer(Modifier.height(8.dp))
                    DetailInfoRow("💼", "Job Title",   proposal.jobTitle)
                    DetailInfoRow("🔧", "Service",     proposal.service)
                    DetailInfoRow("📍", "Location",    proposal.location)
                    DetailInfoRow("📅", "Schedule",    proposal.schedule)
                    DetailInfoRow("🗓", "Start Date",  proposal.startDate)
                    DetailInfoRow("💰", "Offer Price", proposal.offerPrice)
                }

                if (proposal.note.isNotBlank()) {
                    DetailCard {
                        Text("📝 Note to Admin", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorBlue)
                        Spacer(Modifier.height(6.dp))
                        Text(proposal.note, fontSize = 13.sp,
                            color = NoorTextSecondary, lineHeight = 19.sp)
                    }
                }

                DetailCard {
                    Row(
                        modifier             = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment    = Alignment.CenterVertically
                    ) {
                        Text("Status", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorBlue)

                        val (color, label, emoji) = when (proposal.status) {
                            AdminProposalStatus.SENT      -> listOf(NoorBlue,   "Sent to Admin",  "📤")
                            AdminProposalStatus.REVIEWED  -> listOf(NoorOrange, "Under Review",   "👀")
                            AdminProposalStatus.CONNECTED -> listOf(NoorGreen,  "Connected",      "✅")
                            AdminProposalStatus.DECLINED  -> listOf(NoorRed,    "Declined",       "❌")
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background((color as Color).copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("$emoji $label", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold, color = color)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape   = RoundedCornerShape(10.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = NoorBlue),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Small reusable composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProposalDetailTag(icon: String, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NoorBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon,  fontSize = 11.sp)
        Text(label, fontSize = 10.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), content = content)
    }
}

@Composable
private fun DetailInfoRow(icon: String, label: String, value: String) {
    Row(
        modifier             = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment    = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier             = Modifier.weight(0.45f)
        ) {
            Text(icon, fontSize = 13.sp)
            Text(label, fontSize = 11.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = NoorTextPrimary, modifier = Modifier.weight(0.55f))
    }
}