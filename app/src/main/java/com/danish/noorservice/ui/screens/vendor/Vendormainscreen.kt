package com.danish.noorservice.ui.screens.vendor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoneAll
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
import com.danish.noorservice.ui.screens.employee.ChangePasswordScreen
import com.danish.noorservice.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Nav
// ─────────────────────────────────────────────────────────────────────────────

private data class VendorNavItem(val label: String, val emoji: String, val badgeCount: Int = 0)

private val vendorNavItems = listOf(
    VendorNavItem("Home",        "🏠"),
    VendorNavItem("Catalog",     "🗂️"),
    VendorNavItem("Notifications","🔔", badgeCount = 2),
    VendorNavItem("Settings",    "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Main Screen Shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorMainScreen(
    onLogout: () -> Unit,
    initialTab: Int = 0
) {
    var selectedTab        by remember { mutableIntStateOf(initialTab) }
    var lastBackPressTime  by remember { mutableStateOf(0L) }
    val scope              = rememberCoroutineScope()
    val snackbarHostState  = remember { SnackbarHostState() }

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
            VendorBottomNav(
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
                0 -> VendorHomeScreen(
                    onNavigateToNotifications = { selectedTab = 2 },
                    onNavigateToSettings      = { selectedTab = 3 }
                )
                1 -> VendorCatalogScreen()
                2 -> VendorNotificationsScreen(onBack = { selectedTab = 0 })
                3 -> VendorSettingsScreen(onLogout = onLogout)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar  (teal accent)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NoorSurface,
        tonalElevation = 0.dp,
        modifier       = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        vendorNavItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick  = { onItemSelected(index) },
                icon     = {
                    BadgedBox(badge = {
                        if (item.badgeCount > 0) {
                            Badge(containerColor = NoorOrange, contentColor = Color.White) {
                                Text(item.badgeCount.toString(), fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }) {
                        Text(item.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
                },
                label = {
                    Text(item.label, fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) VendorTeal else NoorTextHint)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = VendorTeal,
                    selectedTextColor   = VendorTeal,
                    unselectedTextColor = NoorTextHint,
                    indicatorColor      = VendorTealLight,
                    unselectedIconColor = NoorTextHint
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Settings Screen
// ─────────────────────────────────────────────────────────────────────────────

private enum class VendorSettingsSubScreen { NONE, EDIT_PROFILE, CHANGE_PASSWORD, NOTIFICATIONS }

@Composable
fun VendorSettingsScreen(onLogout: () -> Unit = {}) {
    var subScreen by remember { mutableStateOf(VendorSettingsSubScreen.NONE) }

    // Sub-screen routing
    when (subScreen) {
        VendorSettingsSubScreen.EDIT_PROFILE -> {
            VendorEditProfileScreen(
                onBack  = { subScreen = VendorSettingsSubScreen.NONE },
                onSaved = { subScreen = VendorSettingsSubScreen.NONE }
            )
            return
        }
        VendorSettingsSubScreen.CHANGE_PASSWORD -> {
            ChangePasswordScreen(
                onBack            = { subScreen = VendorSettingsSubScreen.NONE },
                onPasswordChanged = { subScreen = VendorSettingsSubScreen.NONE }
            )
            return
        }
        VendorSettingsSubScreen.NOTIFICATIONS -> {
            VendorNotificationsScreen(onBack = { subScreen = VendorSettingsSubScreen.NONE })
            return
        }
        VendorSettingsSubScreen.NONE -> { /* fall through */ }
    }

    var pushNotifications  by remember { mutableStateOf(true) }
    var isActive           by remember { mutableStateOf(true) }
    var showLogoutDialog   by remember { mutableStateOf(false) }
    var showDeleteDialog   by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(NoorBackground)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column {
                Text("Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, letterSpacing = (-0.3).sp)
                Text("Manage your vendor account",
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile summary card
            Card(
                modifier  = Modifier.fillMaxWidth().clickable { subScreen = VendorSettingsSubScreen.EDIT_PROFILE },
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                            .background(VendorTeal),
                        contentAlignment = Alignment.Center
                    ) { Text("🏢", fontSize = 24.sp) }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Al-Noor Facility Services", fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, color = NoorTextPrimary)
                        Spacer(Modifier.height(2.dp))
                        Text("info@alnoor.com", fontSize = 12.sp, color = NoorTextHint)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VendorMiniTag("🧹 Cleaning",  VendorTealLight, VendorTeal)
                            VendorMiniTag("👥 Staffing",  VendorTealLight, VendorTeal)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = NoorTextHint, modifier = Modifier.size(20.dp))
                }
            }

            // Availability
            VendorSettingsGroup("Availability") {
                VendorSettingsToggleItem(
                    emoji = "🟢", emojiBg = NoorGreenLight,
                    title = "Open for Business",
                    subtitle = "Show your business as active to employers",
                    checked = isActive, onToggle = { isActive = it }
                )
            }

            // Notifications
            VendorSettingsGroup("Notifications") {
                VendorSettingsNavItem(emoji = "🔔", emojiBg = VendorTealLight,
                    title = "View Notifications",
                    onClick = { subScreen = VendorSettingsSubScreen.NOTIFICATIONS })
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsToggleItem(emoji = "🔔", emojiBg = VendorTealLight,
                    title = "Push Notifications",
                    subtitle = "Get alerts for new employer inquiries",
                    checked = pushNotifications, onToggle = { pushNotifications = it })
            }

            // Account
            VendorSettingsGroup("Account") {
                VendorSettingsNavItem(emoji = "🏢", emojiBg = VendorTealLight,
                    title = "Edit Business Profile",
                    onClick = { subScreen = VendorSettingsSubScreen.EDIT_PROFILE })
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(emoji = "🔒", emojiBg = Color(0xFFF3EEF9),
                    title = "Change Password",
                    onClick = { subScreen = VendorSettingsSubScreen.CHANGE_PASSWORD })
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(emoji = "📄", emojiBg = NoorBackground,
                    title = "Terms & Conditions", onClick = {})
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(emoji = "🛡️", emojiBg = NoorBackground,
                    title = "Privacy Policy", onClick = {})
            }

            // Session
            VendorSettingsGroup("Session") {
                VendorSettingsNavItem(emoji = "🚪", emojiBg = VendorTealLight,
                    title = "Log Out",
                    onClick = { showLogoutDialog = true })
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(emoji = "🗑️", emojiBg = NoorRedLight,
                    title = "Delete Account", titleColor = NoorRed,
                    onClick = { showDeleteDialog = true })
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text  = { Text("Are you sure you want to log out?", fontSize = 13.sp,
                color = NoorTextSecondary) },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Log Out", color = VendorTeal, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            })
    }

    if (showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Account", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                color = NoorRed) },
            text  = {
                Text("This will permanently delete your vendor account and all service listings. " +
                        "This cannot be undone.",
                    fontSize = 13.sp, color = NoorTextSecondary)
            },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Delete", color = NoorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            })
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Edit Profile Screen (business fields)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorEditProfileScreen(onBack: () -> Unit, onSaved: () -> Unit = {}) {
    var businessName  by remember { mutableStateOf("Al-Noor Facility Services") }
    var contactPerson by remember { mutableStateOf("Ahmad Raza") }
    var phone         by remember { mutableStateOf("0300-1234567") }
    var email         by remember { mutableStateOf("info@alnoor.com") }
    var ntn           by remember { mutableStateOf("1234567-8") }
    var city          by remember { mutableStateOf("Lahore") }
    var address       by remember { mutableStateOf("Plot 12, Sector B, DHA Phase 3, Lahore") }
    var bio           by remember { mutableStateOf("Providing integrated facility management services across Lahore for 8+ years.") }
    var showSaved     by remember { mutableStateOf(false) }

    val isValid = businessName.isNotBlank() && contactPerson.isNotBlank() && phone.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center) {
                            Text("🏢", fontSize = 26.sp)
                        }
                        Column {
                            Text("Edit Business Profile", fontSize = 20.sp,
                                fontWeight = FontWeight.Bold, color = Color.White,
                                letterSpacing = (-0.3).sp)
                            Text("Update your vendor details", fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.72f))
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                com.danish.noorservice.ui.components.NoorSectionCard {
                    VendorSectionLabel("Business Identity")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = businessName, onValueChange = { businessName = it },
                        label = "Business Name *")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = contactPerson, onValueChange = { contactPerson = it },
                        label = "Contact Person *")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = phone, onValueChange = { phone = it },
                        label = "Phone Number *",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone))
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = email, onValueChange = { email = it },
                        label = "Business Email",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email))
                }

                com.danish.noorservice.ui.components.NoorSectionCard {
                    VendorSectionLabel("Legal & Location")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = ntn, onValueChange = { ntn = it }, label = "NTN Number",
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = city, onValueChange = { city = it }, label = "Head Office City *")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = address, onValueChange = { address = it },
                        label = "Address", singleLine = false, maxLines = 3)
                }

                com.danish.noorservice.ui.components.NoorSectionCard {
                    VendorSectionLabel("About Your Business")
                    Spacer(Modifier.height(12.dp))
                    com.danish.noorservice.ui.components.NoorTextField(
                        value = bio, onValueChange = { if (it.length <= 300) bio = it },
                        label = "Business Description",
                        singleLine = false, maxLines = 5)
                    Text("${bio.length}/300", fontSize = 10.sp,
                        color = if (bio.length > 280) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
                }

                VendorPrimaryButton(
                    text    = "Save Changes",
                    enabled = isValid,
                    onClick = { showSaved = true; onSaved() }
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (showSaved) {
            LaunchedEffect(Unit) { kotlinx.coroutines.delay(2000); showSaved = false }
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                .navigationBarsPadding().padding(16.dp)) {
                Box(modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NoorTextPrimary)
                    .padding(horizontal = 18.dp, vertical = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(VendorTeal), contentAlignment = Alignment.Center) {
                            Icon(androidx.compose.material.icons.Icons.Default.Check,
                                contentDescription = null, tint = Color.White,
                                modifier = Modifier.size(14.dp))
                        }
                        Text("Business profile updated!", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Notifications Screen
// ─────────────────────────────────────────────────────────────────────────────

private data class VendorNotification(
    val id: String, val emoji: String, val iconBg: Color,
    val title: String, val body: String, val time: String,
    val isRead: Boolean = false
)

private val sampleVendorNotifications = mutableListOf(
    VendorNotification("1", "📋", Color(0xFFE8F4FD), "New Employer Inquiry",
        "DHA Lahore Phase 6 is looking for a Cleaning & Janitorial vendor for 200+ units.",
        "Just now", false),
    VendorNotification("2", "🔍", Color(0xFFE8F4FD), "Profile Viewed",
        "Your vendor profile was viewed by an employer in the Facility Maintenance category.",
        "30 min ago", false),
    VendorNotification("3", "✅", Color(0xFFE8F5E9), "Profile Approved",
        "Your vendor account has been verified by the Noor Services admin team.",
        "Yesterday", true),
    VendorNotification("4", "📋", Color(0xFFE8F4FD), "Inquiry Matched",
        "Nishat Group matched your Staffing Solutions listing. Admin will contact you shortly.",
        "Mon", true),
    VendorNotification("5", "⚙️", Color(0xFFFFF3E0), "Catalog Update Required",
        "Your Security Services listing has been paused pending document verification.",
        "Mar 28", true),
    VendorNotification("6", "🎉", Color(0xFFE8F5E9), "Welcome to Noor Services!",
        "Your vendor account is now active. Complete your service catalog to start receiving inquiries.",
        "Mar 25", true),
)

@Composable
fun VendorNotificationsScreen(onBack: () -> Unit) {
    val notifications = remember { mutableStateListOf(*sampleVendorNotifications.toTypedArray()) }
    val unreadCount   = notifications.count { !it.isRead }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(38.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onBack() },
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                                tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Row(verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Notifications", fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White,
                                    letterSpacing = (-0.3).sp)
                                if (unreadCount > 0) {
                                    Box(modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(NoorOrange)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)) {
                                        Text("$unreadCount new", fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                            Text("Inquiries, approvals & updates",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.72f))
                        }
                    }
                    if (unreadCount > 0) {
                        Box(modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .clickable {
                                val updated = notifications.map { it.copy(isRead = true) }
                                notifications.clear(); notifications.addAll(updated)
                            }
                            .padding(horizontal = 12.dp, vertical = 7.dp)) {
                            Row(verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Icon(Icons.Default.DoneAll, contentDescription = null,
                                    tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("Mark all read", fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)) {

            val unread = notifications.filter { !it.isRead }
            val read   = notifications.filter {  it.isRead }

            if (unread.isNotEmpty()) {
                item {
                    Text("NEW", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = NoorTextHint, letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp))
                }
                items(unread, key = { it.id }) { notif ->
                    VendorNotifRow(notif) { id ->
                        val idx = notifications.indexOfFirst { it.id == id }
                        if (idx != -1) notifications[idx] = notifications[idx].copy(isRead = true)
                    }
                }
            }
            if (read.isNotEmpty()) {
                item {
                    Text("EARLIER", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = NoorTextHint, letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp))
                }
                items(read, key = { it.id }) { notif ->
                    VendorNotifRow(notif, onMarkRead = {})
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun VendorNotifRow(notif: VendorNotification, onMarkRead: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notif.isRead) VendorTealLight.copy(alpha = 0.5f) else NoorSurface)
            .clickable { if (!notif.isRead) onMarkRead(notif.id) }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
            .background(notif.iconBg), contentAlignment = Alignment.Center) {
            Text(notif.emoji, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.Top) {
                Text(notif.title, fontSize = 13.sp,
                    fontWeight = if (!notif.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color = NoorTextPrimary, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Text(notif.time, fontSize = 10.sp, color = NoorTextHint)
            }
            Spacer(Modifier.height(4.dp))
            Text(notif.body, fontSize = 12.sp, color = NoorTextSecondary,
                lineHeight = 17.sp, maxLines = 2)
        }
        if (!notif.isRead) {
            Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).clip(CircleShape)
                .background(VendorTeal))
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        color = NoorDivider, thickness = 0.6.dp)
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable Settings components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorSettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(title.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold,
            color = NoorTextHint, letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content   = { Column(content = content) })
    }
}

@Composable
private fun VendorSettingsToggleItem(
    emoji: String, emojiBg: Color, title: String, subtitle: String,
    checked: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
            contentAlignment = Alignment.Center) { Text(emoji, fontSize = 16.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
            Text(subtitle, fontSize = 10.sp, color = NoorTextHint, lineHeight = 14.sp)
        }
        Switch(checked = checked, onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White, checkedTrackColor   = VendorTeal,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = NoorBorder))
    }
}

@Composable
private fun VendorSettingsNavItem(
    emoji: String, emojiBg: Color, title: String,
    titleColor: Color = NoorTextPrimary, onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }
        .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(emojiBg),
            contentAlignment = Alignment.Center) { Text(emoji, fontSize = 16.sp) }
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = titleColor, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = if (titleColor == NoorRed) NoorRed else NoorTextHint,
            modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun VendorMiniTag(label: String, bg: Color, textColor: Color) {
    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(bg)
        .padding(horizontal = 8.dp, vertical = 3.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}