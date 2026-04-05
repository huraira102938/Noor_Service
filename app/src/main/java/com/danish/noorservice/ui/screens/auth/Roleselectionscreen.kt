package com.danish.noorservice.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.components.NoorPrimaryButton
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Role Selection Screen  — now supports "employer", "employee" AND "vendor"
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (role: String) -> Unit  // "employer" | "employee" | "vendor"
) {
    var selectedRole by remember { mutableStateOf("") }

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
                .padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
        ) {
            Column {
                Text(
                    "Welcome Aboard! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "How would you like to use Noor Services?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // ── Role Cards (scrollable to accommodate all three) ──────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Employer ──────────────────────────────────────────────────────
            RoleCard(
                emoji       = "🏠",
                title       = "I want to Hire",
                subtitle    = "Find trusted drivers, house staff, cooks, and more",
                tag         = null,
                accentColor = NoorBlue,
                selected    = selectedRole == "employer",
                onClick     = { selectedRole = "employer" }
            )

            // ── Employee ──────────────────────────────────────────────────────
            RoleCard(
                emoji       = "💼",
                title       = "I'm looking for a Job",
                subtitle    = "Register as a service provider and get hired",
                tag         = null,
                accentColor = NoorGreen,
                selected    = selectedRole == "employee",
                onClick     = { selectedRole = "employee" }
            )

            // ── Vendor (NEW) ──────────────────────────────────────────────────
            RoleCard(
                emoji       = "🏢",
                title       = "I'm a Service Vendor",
                subtitle    = "Offer B2B facility services — staffing, cleaning, security & more",
                tag         = "B2B",
                accentColor = VendorTeal,
                selected    = selectedRole == "vendor",
                onClick     = { selectedRole = "vendor" }
            )

            // Tip text for vendors
            if (selectedRole == "vendor") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VendorTealLight)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        "💡  As a vendor, you offer services in bulk to employers — not directly to individual households.",
                        fontSize   = 12.sp,
                        color      = VendorTealDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ── Continue button ───────────────────────────────────────────────────
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
            NoorPrimaryButton(
                text    = "Continue",
                onClick = { if (selectedRole.isNotEmpty()) onRoleSelected(selectedRole) },
                enabled = selectedRole.isNotEmpty()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Role card component — now accepts an optional tag badge
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RoleCard(
    emoji: String,
    title: String,
    subtitle: String,
    tag: String?,                           // e.g. "B2B" badge shown on the card
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) accentColor else NoorBorder
    val bgColor     = if (selected) accentColor.copy(alpha = 0.06f) else NoorSurface

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { Text(emoji, fontSize = 26.sp) }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = NoorTextPrimary)
                    if (tag != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tag, fontSize = 9.sp, fontWeight = FontWeight.Bold,
                                color = Color.White, letterSpacing = 0.5.sp)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 12.sp, color = NoorTextSecondary, lineHeight = 17.sp)
            }

            // Radio indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (selected) accentColor else Color.Transparent)
                    .border(2.dp, if (selected) accentColor else NoorBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                }
            }
        }
    }
}