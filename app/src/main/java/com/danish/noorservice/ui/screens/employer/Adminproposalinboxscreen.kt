package com.danish.noorservice.ui.screens.employer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.danish.noorservice.viewmodel.employer.EmployerProposalViewModel
import com.danish.noorservice.data.repository.ProposalRepository

// ─────────────────────────────────────────────────────────────────────────────
// Data model
// ─────────────────────────────────────────────────────────────────────────────

enum class AdminProposalStatus {
    PENDING,    // waiting for admin to act
    ACCEPTED,   // admin connected employer with worker
    DECLINED    // admin declined
}

data class AdminProposal(
    val id: String,
    val workerName: String,
    val workerUsername: String,
    val workerInitials: String,
    val workerPhone: String = "",
    val avatarColor: Color,
    val jobTitle: String,
    val service: String,
    val location: String,
    val schedule: String,
    val startDate: String,
    val offerPrice: String,
    val note: String,
    val sentAt: String,
    val employerName: String = "",
    val employerPhone: String = "",
    val employerEmail: String = "",
    val employerCity: String = "",
    val employerArea: String = "",
    val employerAddress: String = "",
    val status: AdminProposalStatus = AdminProposalStatus.PENDING,
    val workerCity: String = "",
    val workerArea: String = "",
    val workerPhoneFull: String = "",
    val workerEmail: String = "",
    val workerCnic: String = "",
    val workerDob: String = "",
    val workerGender: String = "",
    val workerAddress: String = "",
    val workerServiceIds: List<String> = emptyList(),
    val workerSkills: List<String> = emptyList(),
    val workerLanguages: List<String> = emptyList(),
    val workerExperience: String = "",
    val workerLicenceType: String = "",
    val workerAvailableDays: List<String> = emptyList(),
    val workerTimeSlot: String = "",
    val workerAdditionalNote: String = "",
    val workerIsAvailable: Boolean = true,
    val workerJoinedDate: String = "",
    val workerDailyRate: String = "",
    val workerHourlyRate: String = "",
    val workerMonthlyRate: String = "",
    val workerBio: String = "",
    val workerPhotoUrl: String = ""
)

object AdminProposalStore {
    val proposals = mutableStateListOf<AdminProposal>()
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminProposalInboxScreen(viewModel: EmployerProposalViewModel = hiltViewModel()) {
    val proposals = AdminProposalStore.proposals
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Pending", "Accepted", "Declined")

    LaunchedEffect(Unit) {
        viewModel.syncStores()
    }

    val filtered = when (selectedTab) {
        1    -> proposals.filter { it.status == AdminProposalStatus.PENDING }
        2    -> proposals.filter { it.status == AdminProposalStatus.ACCEPTED }
        3    -> proposals.filter { it.status == AdminProposalStatus.DECLINED }
        else -> proposals.toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 0.dp)
        ) {
            Column {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    contentPadding        = PaddingValues(end = 8.dp)
                ) {
                    items(tabs.size) { index ->
                        val label = tabs[index]
                        val count = when (index) {
                            0    -> proposals.size
                            1    -> proposals.count { it.status == AdminProposalStatus.PENDING }
                            2    -> proposals.count { it.status == AdminProposalStatus.ACCEPTED }
                            3    -> proposals.count { it.status == AdminProposalStatus.DECLINED }
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
                                color      = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f),
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

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📋", fontSize = 48.sp)
                    Text("No proposals yet", fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                    Text("Browse workers and send a proposal to admin.",
                        fontSize = 12.sp, color = NoorTextHint)
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    AdminProposalCard(proposal = proposal)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Proposal Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminProposalCard(proposal: AdminProposal) {
    Log.d("EmployerProposals", "AdminProposalCard: employerName=${proposal.employerName}, workerName=${proposal.workerName}")
    var showDetails by remember { mutableStateOf(false) }

    val (accentColor, pillBg, statusLabel, statusEmoji) = when (proposal.status) {
        AdminProposalStatus.PENDING  -> listOf(NoorBlue,  NoorBlueLight,  "Pending",  "⏳")
        AdminProposalStatus.ACCEPTED -> listOf(NoorGreen, NoorGreenLight, "Accepted", "✅")
        AdminProposalStatus.DECLINED -> listOf(NoorRed,   NoorRedLight,   "Declined", "❌")
    }

    Card(
        modifier  = Modifier.fillMaxWidth().clickable { showDetails = true },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp).fillMaxHeight()
                    .background(
                        accentColor as Color,
                        RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )
            Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier              = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp).clip(CircleShape)
                                .background(proposal.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(proposal.workerInitials, fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                        Column {
                            Text(proposal.workerName, fontSize = 13.sp,
                                fontWeight = FontWeight.Bold, color = NoorTextPrimary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NoorBlueDark.copy(alpha = 0.08f))
                                    .border(1.dp, NoorBlue.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(proposal.workerUsername, fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold, color = NoorBlue)
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg as Color)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("$statusEmoji $statusLabel", fontSize = 9.sp,
                            fontWeight = FontWeight.Bold, color = accentColor)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(proposal.jobTitle, fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                Text("${proposal.service} · ${proposal.location}",
                    fontSize = 11.sp, color = NoorTextHint)

                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier              = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProposalDetailTag("📅", proposal.startDate)
                    ProposalDetailTag("⏰", proposal.schedule)
                    ProposalDetailTag("💰", proposal.offerPrice)
                    ProposalDetailTag("🕐", "Sent ${proposal.sentAt}")
                }
            }
        }
    }

    if (showDetails) {
        ProposalDetailsDialog(proposal = proposal, onDismiss = { showDetails = false })
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
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
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
                modifier            = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
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
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text("Status", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = NoorBlue)
                        val (color, label, emoji) = when (proposal.status) {
                            AdminProposalStatus.PENDING  -> listOf(NoorBlue,  "Pending",  "⏳")
                            AdminProposalStatus.ACCEPTED -> listOf(NoorGreen, "Accepted", "✅")
                            AdminProposalStatus.DECLINED -> listOf(NoorRed,   "Declined", "❌")
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(20.dp))
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
            Button(onClick = onDismiss, shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NoorBlue),
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
        modifier = Modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
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
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier              = Modifier.weight(0.45f)
        ) {
            Text(icon,  fontSize = 13.sp)
            Text(label, fontSize = 11.sp, color = NoorTextSecondary, fontWeight = FontWeight.Medium)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = NoorTextPrimary, modifier = Modifier.weight(0.55f))
    }
}