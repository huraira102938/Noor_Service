package com.danish.noorservice.ui.screens.vendor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.danish.noorservice.data.model.Announcement
import com.danish.noorservice.data.model.UserAnnouncement
import com.danish.noorservice.ui.components.NoorSectionCard
import com.danish.noorservice.ui.components.NoorSelectableChip
import com.danish.noorservice.ui.components.NoorTextField
import com.danish.noorservice.ui.theme.*
import com.danish.noorservice.viewmodel.vendor.VendorCatalogViewModel
import com.danish.noorservice.viewmodel.vendor.VendorHomeViewModel
import com.danish.noorservice.viewmodel.vendor.VendorNotificationsViewModel
import com.danish.noorservice.viewmodel.vendor.VendorSettingsViewModel
import kotlinx.coroutines.launch
import java.util.Date

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Nav
// ─────────────────────────────────────────────────────────────────────────────

private data class VendorNavItem(val label: String, val emoji: String, val badgeCount: Int = 0)

private val vendorNavItems = listOf(
    VendorNavItem("Home",         "🏠"),
    VendorNavItem("Catalog",      "🗂️"),
    VendorNavItem("Notifications", "🔔", badgeCount = 0),
    VendorNavItem("Settings",     "⚙️"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Main Screen Shell
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorMainScreen(
    userId: String,
    onLogout: () -> Unit,
    homeViewModel: VendorHomeViewModel,
    catalogViewModel: VendorCatalogViewModel,
    notificationsViewModel: VendorNotificationsViewModel,
    settingsViewModel: VendorSettingsViewModel,
    initialTab: Int = 0
) {
    var selectedTab       by remember { mutableIntStateOf(initialTab) }
    var lastBackPressTime by remember { mutableStateOf(0L) }
    val scope             = rememberCoroutineScope()
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
                    userId    = userId,
                    viewModel = homeViewModel,
                    onNavigateToNotifications = { selectedTab = 2 },
                    onNavigateToSettings      = { selectedTab = 3 }
                )
                1 -> VendorCatalogScreen(
                    userId    = userId,
                    viewModel = catalogViewModel
                )
                2 -> VendorNotificationsScreen(
                    userId    = userId,
                    onBack    = { selectedTab = 0 },
                    viewModel = notificationsViewModel
                )
                3 -> VendorSettingsScreen(
                    userId    = userId,
                    onLogout  = onLogout,
                    notificationsViewModel = notificationsViewModel,
                    settingsViewModel      = settingsViewModel
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
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
                icon = {
                    BadgedBox(badge = {
                        if (item.badgeCount > 0) {
                            Badge(containerColor = NoorOrange, contentColor = Color.White) {
                                Text(
                                    item.badgeCount.toString(),
                                    fontSize   = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }) {
                        Text(item.emoji, fontSize = if (isSelected) 22.sp else 20.sp)
                    }
                },
                label = {
                    Text(
                        item.label,
                        fontSize   = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color      = if (isSelected) VendorTeal else NoorTextHint
                    )
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

private enum class VendorSettingsSubScreen { NONE, EDIT_PROFILE, NOTIFICATIONS }

@Composable
fun VendorSettingsScreen(
    userId: String,
    onLogout: () -> Unit = {},
    notificationsViewModel: VendorNotificationsViewModel? = null,
    settingsViewModel: VendorSettingsViewModel
) {
    val uiState   by settingsViewModel.uiState.collectAsStateWithLifecycle()
    var subScreen by remember { mutableStateOf(VendorSettingsSubScreen.NONE) }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            settingsViewModel.loadProfile(userId)
        }
    }

    when (subScreen) {
        VendorSettingsSubScreen.EDIT_PROFILE -> {
            VendorEditProfileScreen(
                userId    = userId,
                onBack    = { subScreen = VendorSettingsSubScreen.NONE },
                onSaved   = { subScreen = VendorSettingsSubScreen.NONE },
                viewModel = settingsViewModel
            )
            return
        }
        VendorSettingsSubScreen.NOTIFICATIONS -> {
            val notifVm = notificationsViewModel
            if (notifVm != null) {
                VendorNotificationsScreen(
                    userId     = userId,
                    onBack     = { subScreen = VendorSettingsSubScreen.NONE },
                    viewModel  = notifVm,
                    standalone = false
                )
                return
            }
            subScreen = VendorSettingsSubScreen.NONE
        }
        VendorSettingsSubScreen.NONE -> { /* fall through */ }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Derive live values directly from the profile in state so the card
    // always reflects what's actually stored, not a stale snapshot.
    val isActive = uiState.isActive ?: false
    val isApproved      = uiState.profile?.isProfileApproved == true

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Settings",
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = Color.White,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Manage your vendor account",
                        fontSize = 12.sp,
                        color    = Color.White.copy(alpha = 0.72f)
                    )
                }
                IconButton(onClick = { settingsViewModel.loadProfile(userId) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VendorTeal)
            }
            return
        }

        if (uiState.profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pull to refresh", color = NoorTextHint, fontSize = 14.sp)
                }
            }
            return
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Profile summary card ──────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth().clickable { subScreen = VendorSettingsSubScreen.EDIT_PROFILE },
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = NoorSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(VendorTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!uiState.profile?.logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model              = uiState.profile!!.logoUrl,
                                contentDescription = "Logo",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } else {
                            Text("🏢", fontSize = 24.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            uiState.profile?.businessName ?: "—",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = NoorTextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            uiState.profile?.email ?: "—",
                            fontSize = 12.sp,
                            color    = NoorTextHint
                        )
                        Spacer(Modifier.height(6.dp))
                        // Live status tags — reflect actual Firestore state
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            VendorMiniTag(
                                label     = if (isActive) "🟢 Active" else "🔴 Inactive",
                                bg        = if (isActive) NoorGreenLight else NoorRedLight,
                                textColor = if (isActive) NoorGreenDark else NoorRed
                            )
                            VendorMiniTag(
                                label     = if (isApproved) "✅ Approved" else "⏳ Pending",
                                bg        = VendorTealLight,
                                textColor = VendorTealDark
                            )
                        }
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint               = NoorTextHint,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            // ── Notifications ─────────────────────────────────────────────────
            VendorSettingsGroup("Notifications") {
                VendorSettingsNavItem(
                    emoji   = "🔔",
                    emojiBg = VendorTealLight,
                    title   = "View Notifications",
                    onClick = { subScreen = VendorSettingsSubScreen.NOTIFICATIONS }
                )
            }

            // ── Account ───────────────────────────────────────────────────────
            VendorSettingsGroup("Account") {
                VendorSettingsNavItem(
                    emoji   = "🏢",
                    emojiBg = VendorTealLight,
                    title   = "Edit Business Profile",
                    onClick = { subScreen = VendorSettingsSubScreen.EDIT_PROFILE }
                )
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(
                    emoji   = "📄",
                    emojiBg = NoorBackground,
                    title   = "Terms & Conditions",
                    onClick = {}
                )
                HorizontalDivider(color = NoorDivider, thickness = 0.6.dp)
                VendorSettingsNavItem(
                    emoji   = "🛡️",
                    emojiBg = NoorBackground,
                    title   = "Privacy Policy",
                    onClick = {}
                )
            }

            // ── Session ───────────────────────────────────────────────────────
            VendorSettingsGroup("Session") {
                VendorSettingsNavItem(
                    emoji   = "🚪",
                    emojiBg = VendorTealLight,
                    title   = "Log Out",
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text(
                    "Are you sure you want to log out?",
                    fontSize = 13.sp,
                    color    = NoorTextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; onLogout() }) {
                    Text("Log Out", color = VendorTeal, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = NoorTextHint)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Edit Profile Screen
// ─────────────────────────────────────────────────────────────────────────────

private val editServiceScaleOptions = listOf("1–10 staff", "11–50 staff", "51–200 staff", "200+ staff")
private val editCityOptions = listOf(
    "Lahore", "Karachi", "Islamabad", "Rawalpindi",
    "Faisalabad", "Multan", "Peshawar", "Other"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VendorEditProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit = {},
    viewModel: VendorSettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val profile = uiState.profile

    // ── Local form state seeded from profile ──────────────────────────────────
    var logoUrl          by remember(profile) { mutableStateOf(profile?.logoUrl ?: "") }
    var businessName     by remember(profile) { mutableStateOf(profile?.businessName ?: "") }
    var contactPerson    by remember(profile) { mutableStateOf(profile?.contactPerson ?: "") }
    var phone            by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var email            by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var ntn              by remember(profile) { mutableStateOf(profile?.ntn ?: "") }
    var regNumber        by remember(profile) { mutableStateOf(profile?.regNumber ?: "") }
    var city             by remember(profile) { mutableStateOf(profile?.city ?: "") }
    var address          by remember(profile) { mutableStateOf(profile?.address ?: "") }
    var bio              by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var serviceScale     by remember(profile) { mutableStateOf(profile?.serviceScale ?: "") }
    var yearsInBusiness  by remember(profile) { mutableStateOf(profile?.yearsInBusiness?.toString() ?: "") }
    var isoCertified     by remember(profile) { mutableStateOf(profile?.isoCertified ?: false) }
    val operatingCities  = remember(profile) {
        mutableStateListOf<String>().also { list ->
            profile?.operatingCities?.let { list.addAll(it) }
        }
    }
    val notableClients   = remember(profile) {
        mutableStateListOf<String>().also { list ->
            profile?.notableClients?.let { list.addAll(it) }
        }
    }

    // Tracks a locally-chosen logo URI before it is uploaded
    var pendingLogoUri   by remember { mutableStateOf<android.net.Uri?>(null) }
    var isUploadingLogo  by remember { mutableStateOf(false) }
    var showSaved        by remember { mutableStateOf(false) }
    var isSaving         by remember { mutableStateOf(false) }

    // Image picker launcher — mirrors the registration screen
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            pendingLogoUri = uri
        }
    }

    val isValid = businessName.isNotBlank() &&
            contactPerson.isNotBlank() &&
            phone.isNotBlank() &&
            city.isNotBlank() &&          // ← was headOffice.isBlank() — fixed
            operatingCities.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint               = Color.White,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header avatar — shows pending local pick or existing URL
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                pendingLogoUri != null -> AsyncImage(
                                    model              = pendingLogoUri,
                                    contentDescription = "Logo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                                )
                                !logoUrl.isNullOrBlank() -> AsyncImage(
                                    model              = logoUrl,
                                    contentDescription = "Logo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                                )
                                else -> Text("🏢", fontSize = 26.sp)
                            }
                        }
                        Column {
                            Text(
                                "Edit Business Profile",
                                fontSize      = 20.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = Color.White,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                "Update your vendor details",
                                fontSize = 12.sp,
                                color    = Color.White.copy(alpha = 0.72f)
                            )
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

                // ── Company Logo ──────────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Company Logo")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tappable logo preview box
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VendorTealLight)
                                .border(
                                    width = 2.dp,
                                    color = if (pendingLogoUri != null || !logoUrl.isNullOrBlank())
                                        VendorTeal else NoorBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { logoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                pendingLogoUri != null -> AsyncImage(
                                    model              = pendingLogoUri,
                                    contentDescription = "Logo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                                )
                                !logoUrl.isNullOrBlank() -> AsyncImage(
                                    model              = logoUrl,
                                    contentDescription = "Logo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
                                )
                                else -> Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint               = VendorTeal,
                                        modifier           = Modifier.size(24.dp)
                                    )
                                    Text("Logo", fontSize = 10.sp, color = VendorTeal)
                                }
                            }
                        }

                        Column(
                            modifier            = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                when {
                                    pendingLogoUri != null  -> "📷 New photo selected"
                                    !logoUrl.isNullOrBlank() -> "✅ Logo set"
                                    else                    -> "No logo"
                                },
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = when {
                                    pendingLogoUri != null   -> NoorOrange
                                    !logoUrl.isNullOrBlank() -> VendorTeal
                                    else                     -> NoorTextHint
                                }
                            )
                            Text(
                                "Tap the box or button to change",
                                fontSize    = 10.sp,
                                color       = NoorTextHint,
                                lineHeight  = 15.sp
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(VendorTealLight)
                                        .clickable { logoPickerLauncher.launch("image/*") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        if (pendingLogoUri != null || !logoUrl.isNullOrBlank())
                                            "Change Logo" else "Upload Logo",
                                        fontSize   = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color      = VendorTeal
                                    )
                                }
                                // Allow removing the pending pick
                                if (pendingLogoUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NoorRedLight)
                                            .clickable { pendingLogoUri = null }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            "Remove",
                                            fontSize   = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color      = NoorRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Business Identity ─────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Business Identity")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value         = businessName,
                        onValueChange = { businessName = it },
                        label         = "Business Name *"
                    )
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value         = contactPerson,
                        onValueChange = { contactPerson = it },
                        label         = "Contact Person *"
                    )
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value           = phone,
                        onValueChange   = { phone = it },
                        label           = "Phone Number *",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value           = email,
                        onValueChange   = { email = it },
                        label           = "Business Email",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                // ── Legal & Registration ──────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Legal & Registration")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        NoorTextField(
                            value           = ntn,
                            onValueChange   = { ntn = it },
                            label           = "NTN Number",
                            modifier        = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        NoorTextField(
                            value         = regNumber,
                            onValueChange = { regNumber = it },
                            label         = "Company Reg No.",
                            modifier      = Modifier.weight(1f)
                        )
                    }
                }

                // ── Location ──────────────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Location")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value         = city,
                        onValueChange = { city = it },
                        label         = "Head Office City *"
                    )
                    // ── Validation hint (was using undefined `headOffice`) ─────
                    if (city.isBlank()) {
                        Text(
                            "Head office city is required",
                            fontSize = 11.sp,
                            color    = NoorRed,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value         = address,
                        onValueChange = { address = it },
                        label         = "Address",
                        singleLine    = false,
                        maxLines      = 3
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Cities You Operate In",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = NoorTextHint,
                        letterSpacing = 0.4.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    FlowRow(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(10.dp)
                    ) {
                        editCityOptions.forEach { c ->
                            NoorSelectableChip(
                                label    = c,
                                icon     = "📍",
                                selected = operatingCities.contains(c),
                                onClick  = {
                                    if (operatingCities.contains(c)) operatingCities.remove(c)
                                    else operatingCities.add(c)
                                }
                            )
                        }
                    }
                }

                // ── About ─────────────────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("About Your Business")
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value         = bio,
                        onValueChange = { if (it.length <= 300) bio = it },
                        label         = "Business Description",
                        singleLine    = false,
                        maxLines      = 5
                    )
                    Text(
                        "${bio.length}/300",
                        fontSize = 10.sp,
                        color    = if (bio.length > 280) NoorRed else NoorTextHint,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }

                // ── Business Capacity ─────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Business Capacity")
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Workforce Scale",
                        fontSize      = 11.sp,
                        fontWeight    = FontWeight.SemiBold,
                        color         = NoorTextHint,
                        letterSpacing = 0.4.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    FlowRow(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(10.dp)
                    ) {
                        editServiceScaleOptions.forEach { opt ->
                            NoorSelectableChip(
                                label    = opt,
                                icon     = "👥",
                                selected = serviceScale == opt,
                                onClick  = { serviceScale = opt }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    NoorTextField(
                        value           = yearsInBusiness,
                        onValueChange   = { yearsInBusiness = it },
                        label           = "Years in Business",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // ── Credentials ───────────────────────────────────────────────
                NoorSectionCard {
                    VendorSectionLabel("Credentials & Trust")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VendorTealLight),
                                contentAlignment = Alignment.Center
                            ) { Text("🏅", fontSize = 16.sp) }
                            Column {
                                Text(
                                    "ISO / Quality Certified",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color      = NoorTextPrimary
                                )
                                Text(
                                    "ISO 9001 or equivalent",
                                    fontSize = 11.sp,
                                    color    = NoorTextHint
                                )
                            }
                        }
                        Switch(
                            checked         = isoCertified,
                            onCheckedChange = { isoCertified = it },
                            colors          = SwitchDefaults.colors(
                                checkedThumbColor   = Color.White,
                                checkedTrackColor   = VendorTeal,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = NoorBorder
                            )
                        )
                    }
                }

                // ── Save button ───────────────────────────────────────────────
                VendorPrimaryButton(
                    text    = if (isSaving) "Saving…" else "Save Changes",
                    enabled = isValid && !isSaving,
                    onClick = {
                        isSaving = true
                        viewModel.saveProfile(
                            userId          = userId,
                            businessName    = businessName,
                            contactPerson   = contactPerson,
                            phone           = phone,
                            email           = email,
                            ntn             = ntn,
                            regNumber       = regNumber,
                            city            = city,
                            address         = address,
                            bio             = bio,
                            logoUrl         = logoUrl,   // existing URL; see note below
                            operatingCities = operatingCities.toList(),
                            serviceScale    = serviceScale,
                            yearsInBusiness = yearsInBusiness.toIntOrNull() ?: 0,
                            isoCertified    = isoCertified,
                            notableClients  = notableClients.toList(),
                            onSuccess       = {
                                isSaving  = false
                                showSaved = true
                                onSaved()
                            },
                            onError         = { isSaving = false }
                        )
                        /*
                         * NOTE — logo upload:
                         * If pendingLogoUri != null you should upload it via
                         * ImageRepository.uploadVendorLogo() before calling saveProfile,
                         * then pass the resulting URL as `logoUrl`.
                         * The pattern is identical to VendorRegistrationViewModel.saveVendorProfile().
                         * Wire it in once you're happy with the rest of the fixes.
                         */
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Saved toast ───────────────────────────────────────────────────────
        if (showSaved) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSaved = false
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NoorTextPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(VendorTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            "Business profile updated!",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vendor Notifications Screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun VendorNotificationsScreen(
    userId: String,
    onBack: () -> Unit,
    viewModel: VendorNotificationsViewModel,
    standalone: Boolean = true
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (standalone && uiState.announcements.isEmpty()) {
        LaunchedEffect(Unit) {
            viewModel.loadNotifications(userId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(NoorBackground)) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(VendorTeal, VendorTealDark)))
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
        ) {
            Column {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Notifications",
                                    fontSize      = 20.sp,
                                    fontWeight    = FontWeight.Bold,
                                    color         = Color.White,
                                    letterSpacing = (-0.3).sp
                                )
                            }
                            Text(
                                "Inquiries, approvals & updates",
                                fontSize = 12.sp,
                                color    = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.16f))
                                .clickable { viewModel.loadNotifications(userId) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint               = Color.White,
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

when {
            uiState.isLoading -> {
                com.danish.noorservice.ui.components.VendorNotificationsShimmer()
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚠️", fontSize = 36.sp)
                        Text("Failed to load notifications", fontSize = 14.sp, color = NoorTextPrimary)
                        Button(
                            onClick = { viewModel.loadNotifications(userId) },
                            colors  = ButtonDefaults.buttonColors(containerColor = VendorTeal)
                        ) { Text("Retry", color = Color.White) }
                    }
                }
            }

            uiState.announcements.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No notifications yet",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = NoorTextPrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("You're all caught up!", fontSize = 13.sp, color = NoorTextHint)
                    }
                }
            }

            else -> {
                val unread = uiState.announcements.filter { !it.second.isRead }
                val read   = uiState.announcements.filter {  it.second.isRead }

                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    if (unread.isNotEmpty()) {
                        item {
                            Text(
                                "NEW",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = NoorTextHint,
                                letterSpacing = 0.8.sp,
                                modifier   = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
                            )
                        }
                        items(unread, key = { it.first.id }) { (announcement, userAnnouncement) ->
                            VendorAnnouncementRow(
                                announcement     = announcement,
                                userAnnouncement = userAnnouncement,
                                onMarkRead       = { viewModel.markAsRead(userId, announcement.id) }
                            )
                        }
                    }
                    if (read.isNotEmpty()) {
                        item {
                            Text(
                                "EARLIER",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = NoorTextHint,
                                letterSpacing = 0.8.sp,
                                modifier   = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
                            )
                        }
                        items(read, key = { it.first.id }) { (announcement, userAnnouncement) ->
                            VendorAnnouncementRow(
                                announcement     = announcement,
                                userAnnouncement = userAnnouncement,
                                onMarkRead       = {}
                            )
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun VendorAnnouncementRow(
    announcement: Announcement,
    userAnnouncement: UserAnnouncement,
    onMarkRead: () -> Unit
) {
    val isRead  = userAnnouncement.isRead
    val bgColor = if (!isRead) VendorTealLight.copy(alpha = 0.5f) else NoorSurface

    val timeLabel = remember(announcement.createdAt) {
        val now  = System.currentTimeMillis()
        val diff = now - announcement.createdAt
        when {
            diff < 60_000      -> "Just now"
            diff < 3_600_000   -> "${diff / 60_000} min ago"
            diff < 86_400_000  -> "${diff / 3_600_000} hr ago"
            diff < 172_800_000 -> "Yesterday"
            else               -> java.text.SimpleDateFormat(
                "MMM d", java.util.Locale.getDefault()
            ).format(Date(announcement.createdAt))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VendorTealLight),
            contentAlignment = Alignment.Center
        ) { Text("📢", fontSize = 20.sp) }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Text(
                    announcement.title,
                    fontSize   = 13.sp,
                    fontWeight = if (!isRead) FontWeight.Bold else FontWeight.SemiBold,
                    color      = NoorTextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(timeLabel, fontSize = 10.sp, color = NoorTextHint)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                announcement.body,
                fontSize   = 12.sp,
                color      = NoorTextSecondary,
                lineHeight = 17.sp,
                maxLines   = 2
            )
        }

        if (!isRead) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(VendorTeal)
            )
        }
    }
    HorizontalDivider(
        modifier  = Modifier.padding(start = 72.dp, end = 16.dp),
        color     = NoorDivider,
        thickness = 0.6.dp
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable Settings components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VendorSettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            title.uppercase(),
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            color      = NoorTextHint,
            letterSpacing = 0.8.sp,
            modifier   = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            modifier  = Modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = NoorSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content   = { Column(content = content) }
        )
    }
}

@Composable
private fun VendorSettingsToggleItem(
    emoji: String, emojiBg: Color,
    title: String, subtitle: String,
    checked: Boolean, onToggle: (Boolean) -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(emojiBg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(title,    fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NoorTextPrimary)
            Text(subtitle, fontSize = 10.sp, color = NoorTextHint, lineHeight = 14.sp)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = VendorTeal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = NoorBorder
            )
        )
    }
}

@Composable
private fun VendorSettingsNavItem(
    emoji: String, emojiBg: Color,
    title: String,
    titleColor: Color = NoorTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(emojiBg),
            contentAlignment = Alignment.Center
        ) { Text(emoji, fontSize = 16.sp) }
        Text(
            title,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium,
            color      = titleColor,
            modifier   = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = if (titleColor == NoorRed) NoorRed else NoorTextHint,
            modifier           = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun VendorMiniTag(label: String, bg: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}