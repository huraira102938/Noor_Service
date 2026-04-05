package com.danish.noorservice.ui.screens.employee

import androidx.compose.foundation.Image
import com.danish.noorservice.R
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*

@Composable
fun EmployeeHomeScreen(
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
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
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
                            painter = painterResource(id = R.drawable.noor_services_app_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .graphicsLayer {
                                    scaleX = 3f
                                    scaleY = 3f
                                    translationY = 39f
                                    translationX = 4f
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Noor Services",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.2).sp
                        )
                        Text(
                            "Provider (Pvt.) Ltd.",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.65f),
                            letterSpacing = 0.5.sp
                        )
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
            // ── Profile Card ──────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "MA",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Muhammad Ali",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Lahore · Joined Mar 2025",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.72f)
                                )
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    Text(
                                        "💰 PKR 1,200/day",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NoorGreen)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF90EE90))
                                )
                                Text(
                                    "Available",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Availability Details Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", fontSize = 18.sp)
                            Text("Mon - Fri", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                            Text("Available Days", fontSize = 10.sp, color = NoorTextHint)
                        }
                        Divider(color = NoorDivider, modifier = Modifier.width(1.dp).height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🕐", fontSize = 18.sp)
                            Text("Full Day", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                            Text("Time Slot", fontSize = 10.sp, color = NoorTextHint)
                        }
                        Divider(color = NoorDivider, modifier = Modifier.width(1.dp).height(40.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⏱️", fontSize = 18.sp)
                            Text("3-5 yrs", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
                            Text("Experience", fontSize = 10.sp, color = NoorTextHint)
                        }
                    }
                }
            }

            // ── Bio Section ───────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "📝 About Me",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NoorBlue
                    )
                    Text(
                        "Experienced professional driver with 5+ years in DHA & Gulberg area. Punctual and trustworthy.",
                        fontSize = 13.sp,
                        color = NoorTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── My Services ────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "🛠️ My Services",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NoorBlue
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NoorBlueLight,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "🚗 Driver",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NoorBlue,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NoorBlueLight,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "🧹 House Boy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NoorBlue,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // ── Skills Section ─────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "🔧 Skills",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NoorBlue
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "City Driving",
                                fontSize = 11.sp,
                                color = NoorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "Highway",
                                fontSize = 11.sp,
                                color = NoorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NoorBorder),
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "Cleaning",
                                fontSize = 11.sp,
                                color = NoorTextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // ── Languages Section ──────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "🗣️ Languages",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NoorBlue
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorGreenLight,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "Urdu",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NoorGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorGreenLight,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "Punjabi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NoorGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NoorGreenLight,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                "English",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NoorGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // ── How it works ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorBlueLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("ℹ️", fontSize = 16.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "How it works",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NoorBlueDark
                        )
                        Text(
                            "Keep your profile updated. Employers browse profiles through Noor Services admin. " +
                                    "If an employer is interested, the admin will contact you directly.",
                            fontSize = 12.sp,
                            color = NoorBlueDark,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // ── Recent Activity ───────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔔 Recent Updates",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NoorBlue
                        )
                        TextButton(
                            onClick = onNavigateToNotifications,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "View all",
                                fontSize = 11.sp,
                                color = NoorBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NoorGreenLight),
                            contentAlignment = Alignment.Center
                        ) { Text("✅", fontSize = 16.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Profile Approved", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                            Text("Your profile has been verified by admin", fontSize = 11.sp, color = NoorTextHint)
                        }
                        Text("Today", fontSize = 10.sp, color = NoorTextHint)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NoorOrangeLight),
                            contentAlignment = Alignment.Center
                        ) { Text("📝", fontSize = 16.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Under Review", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NoorTextPrimary)
                            Text("Your updated profile is being reviewed", fontSize = 11.sp, color = NoorTextHint)
                        }
                        Text("Yesterday", fontSize = 10.sp, color = NoorTextHint)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}