package com.danish.noorservice.ui.screens.admin

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.admin.AdminDashboardViewModel
import com.danish.noorservice.viewmodel.admin.AdminManagementViewModel
import com.danish.noorservice.viewmodel.admin.AdminProposalViewModel
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Admin color palette (deep purple / indigo for distinction)
// ─────────────────────────────────────────────────────────────────────────────

val AdminPurple      = Color(0xFF5C35D4)
val AdminPurpleDark  = Color(0xFF3B1FA8)
val AdminPurpleLight = Color(0xFFEDE8FF)
val AdminAccent      = Color(0xFF7C5CE8)

// ─────────────────────────────────────────────────────────────────────────────
// Bottom nav items
// ─────────────────────────────────────────────────────────────────────────────

private data class AdminNavItem(
    val label: String,
    val emoji: String,
    val badgeCount: Int = 0
)

private val adminNavItems = listOf(
    AdminNavItem("Dashboard", "📊"),
    AdminNavItem("Workers",   "👷"),
    AdminNavItem("Employers", "🏠"),
    AdminNavItem("Vendors",   "🏢"),
    AdminNavItem("Settings",  "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Admin Main Screen Shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AdminMainScreen(
    dashboardViewModel: AdminDashboardViewModel? = null,
    managementViewModel: AdminManagementViewModel? = null,
    onLogout: () -> Unit,
    initialTab: Int = 0
) {
    val proposalViewModel: AdminProposalViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        proposalViewModel.loadAllProposals()
    }
    var selectedTab       by remember { mutableIntStateOf(initialTab) }
    var lastBackPressTime by remember { mutableStateOf(0L) }
    val scope             = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State for managing sub-screens within Dashboard
    var showProposalInbox by remember { mutableStateOf(false) }
    var showAnnouncements by remember { mutableStateOf(false) }
    var showCategoryMgmt by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Live proposal badge count
    val pendingProposals by remember {
        derivedStateOf {
            com.danish.noorservice.ui.screens.employer.AdminProposalStore.proposals
                .count { it.status == com.danish.noorservice.ui.screens.employer.AdminProposalStatus.PENDING } +
            com.danish.noorservice.ui.screens.employer.VendorProposalStore.proposals
                .count { it.status == com.danish.noorservice.ui.screens.employer.VendorProposalStatus.PENDING }
        }
    }

    BackHandler(enabled = true) {
        when {
            showProposalInbox -> showProposalInbox = false
            showAnnouncements -> showAnnouncements = false
            showCategoryMgmt -> showCategoryMgmt = false
            showSettings -> showSettings = false
            selectedTab != 0 -> selectedTab = 0
            else -> {
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
    }

    Scaffold(
        containerColor = NoorBackground,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom bar when not in sub-screens
            if (!showProposalInbox && !showAnnouncements && !showCategoryMgmt && !showSettings) {
                AdminBottomNav(
                    selectedIndex    = selectedTab,
                    pendingProposals = pendingProposals,
                    onItemSelected   = { selectedTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showProposalInbox || showAnnouncements || showCategoryMgmt || showSettings) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            when {
                showProposalInbox -> {
                    AdminProposalInboxScreen(
                        onBack = { showProposalInbox = false },
                        proposalViewModel = proposalViewModel
                    )
                }
                showAnnouncements -> {
                    AdminNotificationsScreen(onBack = { showAnnouncements = false })
                }
                showCategoryMgmt -> {
                    AdminCategoryManagementScreen(onBack = { showCategoryMgmt = false })
                }
                showSettings -> {
                    AdminSettingsScreen(onLogout = onLogout)
                }
                else -> {
                    when (selectedTab) {
                        0 -> AdminDashboardScreen(
                            viewModel = dashboardViewModel,
                            proposalViewModel = proposalViewModel,
                            onNavigate = { selectedTab = it },
                            onNavigateToProposalInbox = { showProposalInbox = true },
                            onNavigateToAnnouncements = { showAnnouncements = true },
                            onNavigateToCategoryManagement = { showCategoryMgmt = true },
                            onNavigateToSettings = { showSettings = true }
                        )
                        1 -> AdminWorkersScreen(viewModel = managementViewModel)
                        2 -> AdminEmployersScreen(viewModel = managementViewModel)
                        3 -> AdminVendorsScreen(viewModel = managementViewModel)
                        4 -> AdminSettingsScreen(onLogout = onLogout)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminBottomNav(
    selectedIndex: Int,
    pendingProposals: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        adminNavItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    BadgedBox(badge = {
                        if (index == 0 && pendingProposals > 0) {
                            Badge(containerColor = NoorRed, contentColor = Color.White) {
                                Text(pendingProposals.toString(), fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }) {
                        Text(item.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
                },
                label = {
                    Text(item.label, fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) AdminPurple else NoorTextHint)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = AdminPurple,
                    selectedTextColor   = AdminPurple,
                    unselectedTextColor = NoorTextHint,
                    indicatorColor      = AdminPurpleLight,
                    unselectedIconColor = NoorTextHint
                )
            )
        }
    }
}