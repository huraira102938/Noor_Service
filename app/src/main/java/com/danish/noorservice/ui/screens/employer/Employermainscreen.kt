package com.danish.noorservice.ui.screens.employer

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.danish.noorservice.viewmodel.employer.EmployerHomeViewModel
import com.danish.noorservice.viewmodel.employer.EmployerNotificationsViewModel
import com.danish.noorservice.viewmodel.employer.EmployerSettingsViewModel
import kotlinx.coroutines.launch

private data class EmployerNavItem(
    val label: String,
    val emoji: String
)

private val employerNavItems = listOf(
    EmployerNavItem("Home",      "🏠"),
    EmployerNavItem("Workers",   "👤"),
    EmployerNavItem("Vendors",   "🏢"),
    EmployerNavItem("Proposals", "📋"),
    EmployerNavItem("Settings",  "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Main shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerMainScreen(
    userId: String,
    homeViewModel: EmployerHomeViewModel,
    notificationsViewModel: EmployerNotificationsViewModel,
    settingsViewModel: EmployerSettingsViewModel,
    onLogout: () -> Unit,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    var lastBackPressTime by remember { mutableStateOf(0L) }
    val currentTime = System.currentTimeMillis()

    val scope             = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Live count of pending proposals — drives the badge
    val pendingCount by remember {
        derivedStateOf {
            AdminProposalStore.proposals.count { it.status == AdminProposalStatus.PENDING } +
                    VendorProposalStore.proposals.count { it.status == VendorProposalStatus.PENDING }
        }
    }

    BackHandler(enabled = true) {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            if (currentTime - lastBackPressTime < 2000) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                lastBackPressTime = currentTime
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message  = "Press back again to exit",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = NoorBackground,
        bottomBar = {
            EmployerBottomNav(
                selectedIndex  = selectedTab,
                pendingCount   = pendingCount,
                onItemSelected = { selectedTab = it }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedTab) {
                0 -> EmployerHomeScreen(
                    userId = userId,
                    homeViewModel = homeViewModel,
                    notificationsViewModel = notificationsViewModel,
                    settingsViewModel = settingsViewModel,
                    onBrowse = { selectedTab = 1 },
                    onSettings = { selectedTab = 4 }
                )
                1 -> {
                    val profile = homeViewModel.uiState.value.profile
                    Log.d("EmployerMain", "EmployerBrowseScreen profile=${profile?.fullName}/${profile?.phone}")
                    EmployerBrowseScreen(employerProfile = profile)
                }
                2 -> EmployerVendorBrowseScreen(employerProfile = homeViewModel.uiState.value.profile)
                3 -> EmployerProposalsCombinedScreen()
                4 -> EmployerSettingsScreen(
                    userId = userId,
                    viewModel = settingsViewModel,
                    onLogout = onLogout
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployerBottomNav(
    selectedIndex: Int,
    pendingCount: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        employerNavItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            val badgeCount = if (index == 3) pendingCount else 0

            NavigationBarItem(
                selected = isSelected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge(
                                    containerColor = NoorOrange,
                                    contentColor   = Color.White
                                ) {
                                    Text(
                                        badgeCount.toString(),
                                        fontSize   = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Text(item.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
                },
                label  = {
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) NoorBlue else NoorTextHint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = NoorBlue,
                    selectedTextColor   = NoorBlue,
                    unselectedTextColor = NoorTextHint,
                    indicatorColor      = NoorBlueLight,
                    unselectedIconColor = NoorTextHint
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Combined Proposals Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerProposalsCombinedScreen() {
    var activeTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(NoorBlue, NoorBlueDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Column {
                Spacer(Modifier.height(18.dp))
                Text(
                    "My Proposals", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Worker & vendor proposals via admin",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    listOf("👤 Workers", "🏢 Vendors").forEachIndexed { idx, label ->
                        val selected = activeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) Color.White.copy(alpha = 0.25f) else Color.Transparent)
                                .clickable { activeTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label, fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        when (activeTab) {
            0 -> AdminProposalInboxScreen()
            1 -> VendorProposalInboxScreen()
        }
    }
}