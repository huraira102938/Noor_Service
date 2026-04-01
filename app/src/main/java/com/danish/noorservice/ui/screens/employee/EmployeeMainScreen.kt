package com.danish.noorservice.ui.screens.employee



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

private data class NavItem(
    val label: String,
    val emoji: String,
    val badgeCount: Int = 0
)

private val navItems = listOf(
    NavItem("Home",      "🏠"),
    NavItem("Messages",  "💬", badgeCount = 3),
    NavItem("Proposals", "📋", badgeCount = 4),
    NavItem("Settings",  "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Main screen shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EmployeeMainScreen(
    onLogout: () -> Unit,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    Scaffold(
        containerColor = NoorBackground,
        bottomBar = {
            EmployeeBottomNav(
                selectedIndex = selectedTab,
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
                0 -> EmployeeHomeScreen(
                    onNavigateToMessages  = { selectedTab = 1 },
                    onNavigateToProposals = { selectedTab = 2 },
                    onNavigateToSettings  = { selectedTab = 3 }
                )
                1 -> EmployeeMessagesScreen()
                2 -> EmployeeProposalsScreen()
                3 -> EmployeeSettingsScreen(onLogout = onLogout)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployeeBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index

            NavigationBarItem(
                selected  = isSelected,
                onClick   = { onItemSelected(index) },
                icon      = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(
                                    containerColor = NoorOrange,
                                    contentColor   = Color.White
                                ) {
                                    Text(
                                        item.badgeCount.toString(),
                                        fontSize   = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            text     = item.emoji,
                            fontSize = if (isSelected) 22.sp else 20.sp
                        )
                    }
                },
                label     = {
                    Text(
                        text       = item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) NoorBlue else NoorTextHint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor      = NoorBlue,           // Color for selected icon/text
                    selectedTextColor      = NoorBlue,           // Color for selected text
                    unselectedTextColor    = NoorTextHint,       // Color for unselected text
                    indicatorColor         = NoorBlueLight,      // Background indicator when selected
                    unselectedIconColor    = NoorTextHint        // Color for unselected icon
                )
            )
        }
    }
}