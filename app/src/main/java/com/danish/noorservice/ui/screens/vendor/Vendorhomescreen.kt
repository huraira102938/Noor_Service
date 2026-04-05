package com.danish.noorservice.ui.screens.vendor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.R
import com.danish.noorservice.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Home Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorHomeScreen(
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NoorBackground)
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter            = painterResource(id = R.drawable.noor_services_app_logo),
                            contentDescription = "Logo",
                            modifier           = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .graphicsLayer { scaleX = 3f; scaleY = 3f; translationY = 39f; translationX = 4f },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Noor Services",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = Color.White, letterSpacing = (-0.2).sp)
                        Text("Vendor Portal",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f), letterSpacing = 0.5.sp)
                    }
                }
            }
        }

        // ── Scrollable body ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // ── Vendor Profile Card ───────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(16.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    // Header gradient strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) { Text("🏢", fontSize = 26.sp) }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Al-Noor Facility Services",
                                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Lahore · Joined Jan 2025",
                                    fontSize = 11.sp, color = Color.White.copy(alpha = 0.72f))
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.2f)) {
                                        Text("🏅 ISO Certified", fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold, color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                    Surface(shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.2f)) {
                                        Text("✅ Verified", fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold, color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }

                        // Status badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(VendorAccent)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape)
                                    .background(Color(0xFF90EE90)))
                                Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = Color.White)
                            }
                        }
                    }

                    // Stats row
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VendorStatColumn("👥", "51–200", "Workforce")
                        Divider(color = NoorDivider,
                            modifier = Modifier.width(1.dp).height(40.dp))
                        VendorStatColumn("📍", "Lahore", "Head Office")
                        Divider(color = NoorDivider,
                            modifier = Modifier.width(1.dp).height(40.dp))
                        VendorStatColumn("⏱️", "8 yrs", "In Business")
                    }
                }
            }


            Spacer(Modifier.height(8.dp))

            // ── About ─────────────────────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📝 About Us", fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        color = VendorTeal)
                    Text(
                        "Providing integrated facility management services across Lahore for 8+ years. " +
                                "Trusted by DHA, Packages Ltd and Nishat Group for staffing and cleaning solutions.",
                        fontSize = 13.sp, color = NoorTextSecondary, lineHeight = 20.sp
                    )
                }
            }

            // ── Active Services ───────────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🛠️ Active Service Catalog", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)

                    listOf(
                        Triple("🧹", "Cleaning & Janitorial",  "Office & industrial cleaning"),
                        Triple("👥", "Staffing Solutions",      "Bulk workforce supply"),
                        Triple("🛡️", "Security Services",      "Guards & CCTV monitoring"),
                    ).forEach { (emoji, label, sub) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VendorTealLight),
                                contentAlignment = Alignment.Center
                            ) { Text(emoji, fontSize = 18.sp) }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = NoorTextPrimary)
                                Text(sub, fontSize = 11.sp, color = NoorTextHint)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VendorTealLight)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("Active", fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold, color = VendorTeal)
                            }
                        }
                        HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                    }
                }
            }

            // ── Notable Clients ───────────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🤝 Notable Clients", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)) {
                        listOf("DHA Lahore", "Nishat Group", "Packages Ltd", "Engro Corp").forEach { client ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(NoorBackground)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(client, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                    color = NoorTextSecondary)
                            }
                        }
                    }
                }
            }

            // ── Cities covered ────────────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📍 Cities We Operate In", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold, color = VendorTeal)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp)) {
                        listOf("Lahore", "Islamabad", "Rawalpindi", "Faisalabad").forEach { c ->
                            Surface(shape = RoundedCornerShape(8.dp), color = VendorTealLight) {
                                Text(c, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    color = VendorTeal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                            }
                        }
                    }
                }
            }

            // ── How It Works ──────────────────────────────────────────────────
            Card(
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = VendorTealLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top) {
                    Text("ℹ️", fontSize = 16.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("How Vendor Matching Works", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = VendorTealDark)
                        Text(
                            "Employers post requirements through Noor Services admin. " +
                                    "When a match is found, the admin contacts you with full job details and the employer's budget.",
                            fontSize = 12.sp, color = VendorTealDark, lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorStatColumn(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
        Text(label, fontSize = 10.sp, color = NoorTextHint)
    }
}

@Composable
private fun VendorQuickStatCard(
    emoji: String, value: String, label: String,
    color: Color, modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = NoorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(label, fontSize = 9.sp, color = NoorTextHint,
                fontWeight = FontWeight.Medium, lineHeight = 13.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}