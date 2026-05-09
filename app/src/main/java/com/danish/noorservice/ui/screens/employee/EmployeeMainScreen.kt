package com.danish.noorservice.ui.screens.employee

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
import com.danish.noorservice.viewmodel.employee.EmployeeHomeViewModel
import kotlinx.coroutines.launch

private data class NavItem(
    val label: String,
    val emoji: String,
    val badgeCount: Int = 0
)

private val navItems = listOf(
    NavItem("Home",          "🏠"),
    NavItem("Notifications", "🔔"),
    NavItem("Settings",      "⚙️"),
)

@Composable
fun EmployeeMainScreen(
    userId: String,
    onLogout: () -> Unit,
    // ✅ FIX: Accept the VM from AppNavigation instead of creating it here.
    // This means loadProfile() was already called the moment Firebase confirmed
    // the user — before this composable even entered composition.
    homeViewModel: EmployeeHomeViewModel,
    initialTab: Int = 0
) {
    var selectedTab       by remember { mutableIntStateOf(initialTab) }
    var lastBackPressTime by remember { mutableStateOf(0L) }
    val scope             = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ✅ No LaunchedEffect / loadProfile() call here anymore.
    // The load was already started in AppNavigation as soon as auth confirmed.
    // The hasLoaded guard in the VM prevents any double-fetch if this
    // composable somehow re-enters composition.

    BackHandler(enabled = true) {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressTime < 2000) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                lastBackPressTime = now
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
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            EmployeeBottomNav(
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
                0 -> EmployeeHomeScreen(
                    userId    = userId,
                    viewModel = homeViewModel,
                    onNavigateToNotifications = { selectedTab = 1 },
                    onNavigateToSettings      = { selectedTab = 2 }
                )
                1 -> NotificationsScreen(
                    userId = userId,
                    onBack = { selectedTab = 0 }
                )
                2 -> EmployeeSettingsScreen(
                    userId    = userId,
                    onLogout  = onLogout,
                    onProfileSaved = { homeViewModel.loadProfile(userId, forceRefresh = true) }
                )
            }
        }
    }
}

@Composable
private fun EmployeeBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(containerColor = NoorOrange, contentColor = Color.White) {
                                    Text(item.badgeCount.toString(),
                                        fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Text(item.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
                },
                label = {
                    Text(item.label, fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) NoorBlue else NoorTextHint)
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