package com.danish.noorservice.ui.screens.employer


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

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Nav Items
// ─────────────────────────────────────────────────────────────────────────────

private data class EmployerNavItem(
    val label: String,
    val emoji: String,
    val badgeCount: Int = 0
)

private val employerNavItems = listOf(
    EmployerNavItem("Home",     "🏠"),
    EmployerNavItem("Browse",   "🔍"),
    EmployerNavItem("Proposals", "📋", badgeCount = 2),
    EmployerNavItem("Messages", "💬", badgeCount = 3),
    EmployerNavItem("Settings", "⚙️"),
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

    Scaffold(
        containerColor = NoorBackground,
        bottomBar = {
            EmployerBottomNav(
                selectedIndex  = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (selectedTab) {
                0 -> EmployerHomeScreen(
                    onBrowse   = { selectedTab = 1 },
                    onBookings = { selectedTab = 2 },
                    onMessages = { selectedTab = 3 },
                    onSettings = { selectedTab = 4 }
                )
                1 -> EmployerBrowseScreen(
                    onOpenWorker = { /* handled internally */ }
                )
                2 -> EmployerProposalsScreen()
                3 -> EmployerMessagesScreen()
                4 -> EmployerSettingsScreen(onLogout = onLogout)
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
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        employerNavItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(
                                    containerColor = NoorOrange,
                                    contentColor   = Color.White
                                ) {
                                    Text(item.badgeCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
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