package com.danish.noorservice.ui.screens.employee


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

enum class ProposalStatus { ACCEPTED, PENDING, REJECTED }

data class Proposal(
    val id: String,
    val employerName: String,
    val area: String,
    val service: String,
    val schedule: String,
    val startDate: String,
    val timeSlot: String,
    val status: ProposalStatus
)

private val allProposals = listOf(
    Proposal("1", "Farhan Ahmed",  "DHA Phase 5",  "Driver",   "Mon–Fri · Full Day",   "Apr 1",  "8 AM – 6 PM",  ProposalStatus.ACCEPTED),
    Proposal("2", "Nadia Baig",    "Gulberg II",   "House Boy","2× / week",             "Ongoing","Morning",      ProposalStatus.ACCEPTED),
    Proposal("3", "Sara Khan",     "Model Town",   "Driver",   "Weekends only",         "Apr 5",  "9 AM – 3 PM",  ProposalStatus.PENDING),
    Proposal("4", "Bilal Raza",    "Johar Town",   "House Boy","Mon, Wed, Fri",         "Apr 8",  "Morning",      ProposalStatus.PENDING),
    Proposal("5", "Hina Tariq",    "Bahria Town",  "Driver",   "Mon–Sat",               "Mar 10", "Full Day",     ProposalStatus.REJECTED),
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployeeProposalsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Accepted", "Pending", "Rejected")

    val filtered = when (selectedTab) {
        0    -> allProposals.filter { it.status == ProposalStatus.ACCEPTED }
        1    -> allProposals.filter { it.status == ProposalStatus.PENDING  }
        else -> allProposals.filter { it.status == ProposalStatus.REJECTED }
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
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 0.dp)
        ) {
            Column {
                Text(
                    "Proposals",
                    fontSize      = 22.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    "Your accepted & pending jobs",
                    fontSize = 12.sp,
                    color    = Color.White.copy(alpha = 0.72f)
                )

                Spacer(Modifier.height(16.dp))

                // Tab row inside header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    tabs.forEachIndexed { index, label ->
                        val count = when (index) {
                            0 -> allProposals.count { it.status == ProposalStatus.ACCEPTED }
                            1 -> allProposals.count { it.status == ProposalStatus.PENDING }
                            else -> allProposals.count { it.status == ProposalStatus.REJECTED }
                        }
                        val isSelected = selectedTab == index
                        Column(
                            modifier = Modifier
                                .weight(1f),
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
                            // Indicator line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                    .background(
                                        if (isSelected) Color.White
                                        else Color.Transparent
                                    )
                            )
                        }
                    }
                }
            }
        }

        // ── Proposal list ─────────────────────────────────────────────────────
        if (filtered.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📭", fontSize = 48.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No ${tabs[selectedTab].lowercase()} proposals yet",
                        fontSize  = 14.sp,
                        color     = NoorTextHint,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { proposal ->
                    ProposalCard(proposal)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Proposal Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProposalCard(proposal: Proposal) {
    val accentColor = when (proposal.status) {
        ProposalStatus.ACCEPTED -> NoorGreen
        ProposalStatus.PENDING  -> NoorOrange
        ProposalStatus.REJECTED -> NoorRed
    }
    val pillBg = when (proposal.status) {
        ProposalStatus.ACCEPTED -> NoorGreenLight
        ProposalStatus.PENDING  -> NoorOrangeLight
        ProposalStatus.REJECTED -> NoorRedLight
    }
    val statusLabel = when (proposal.status) {
        ProposalStatus.ACCEPTED -> "Accepted"
        ProposalStatus.PENDING  -> "Pending"
        ProposalStatus.REJECTED -> "Rejected"
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Accent left border via Box + Column
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            )

            Column(modifier = Modifier.padding(14.dp).weight(1f)) {
                // Top row: name + status pill
                Row(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment    = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${proposal.employerName} — ${proposal.area}",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = NoorTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "${proposal.service} · ${proposal.schedule}",
                            fontSize = 11.sp,
                            color    = NoorTextHint
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            statusLabel,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = accentColor
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                Spacer(Modifier.height(10.dp))

                // Detail tags row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    DetailTag("🗓", proposal.startDate)
                    DetailTag("⏰", proposal.timeSlot)
                    DetailTag("📍", "Lahore")
                }

                // Action buttons for pending
                if (proposal.status == ProposalStatus.PENDING) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick  = {},
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = NoorRed),
                            border   = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(NoorRed, NoorRed))
                            )
                        ) { Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }

                        Button(
                            onClick  = {},
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = NoorGreen)
                        ) { Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTag(icon: String, label: String) {
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