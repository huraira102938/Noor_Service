package com.danish.noorservice.ui.screens.employer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danish.noorservice.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Nav Items
// Home | Browse | My Proposals | Settings
// Proposals badge = number of SENT (pending) proposals in AdminProposalStore
// ─────────────────────────────────────────────────────────────────────────────

private data class EmployerNavItem(
    val label: String,
    val emoji: String
)

private val employerNavItems = listOf(
    EmployerNavItem("Home",      "🏠"),
    EmployerNavItem("Browse",    "🔍"),
    EmployerNavItem("Proposals", "📋"),
    EmployerNavItem("Settings",  "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Main shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployerMainScreen(
    onLogout: () -> Unit,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    var lastBackPressTime by remember { mutableStateOf(0L) }
    val currentTime = System.currentTimeMillis()

    val scope             = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Live count of pending (SENT) proposals — drives the badge
    val pendingCount by remember {
        derivedStateOf {
            AdminProposalStore.proposals.count { it.status == AdminProposalStatus.SENT }
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
                selectedIndex = selectedTab,
                pendingCount  = pendingCount,
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
                    onBrowse   = { selectedTab = 1 },
                    onSettings = { selectedTab = 3 }
                )
                1 -> EmployerBrowseScreen()
                2 -> AdminProposalInboxScreen()
                3 -> EmployerSettingsScreen(onLogout = onLogout)
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
            // Show badge on Proposals tab (index 2) when there are pending proposals
            val badgeCount = if (index == 2) pendingCount else 0

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