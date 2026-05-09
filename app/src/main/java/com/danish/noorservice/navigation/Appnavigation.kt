package com.danish.noorservice.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danish.noorservice.ui.components.HomeScreenShimmer
import com.danish.noorservice.ui.screens.admin.AdminMainScreen
import com.danish.noorservice.ui.screens.auth.AuthScreen
import com.danish.noorservice.ui.screens.auth.EmployerRegistrationScreen
import com.danish.noorservice.ui.screens.auth.RoleSelectionScreen
import com.danish.noorservice.ui.screens.employee.*
import com.danish.noorservice.ui.screens.employer.EmployerMainScreen
import com.danish.noorservice.ui.screens.vendor.VendorMainScreen
import com.danish.noorservice.ui.screens.vendor.VendorRegistrationScreen
import com.danish.noorservice.ui.screens.vendor.VendorRegistrationSuccessScreen
import com.danish.noorservice.ui.theme.NoorBackground
import com.danish.noorservice.viewmodel.auth.AuthViewModel
import com.danish.noorservice.viewmodel.employee.EmployeeHomeViewModel
import com.danish.noorservice.viewmodel.employee.EmployeeRegistrationViewModel
import com.danish.noorservice.viewmodel.employer.EmployerRegistrationViewModel
import com.danish.noorservice.viewmodel.vendor.VendorRegistrationViewModel

object Routes {
    const val AUTH                  = "auth"
    const val ROLE_SELECTION        = "role_selection"
    const val EMPLOYER_REGISTRATION = "employer/registration"
    const val PERSONAL_INFO         = "employee/personal_info"
    const val SERVICE_SELECT        = "employee/service_select"
    const val SERVICE_DETAIL        = "employee/service_detail"
    const val SUCCESS               = "employee/success"
    const val VENDOR_REGISTRATION   = "vendor/registration"
    const val VENDOR_SUCCESS        = "vendor/success"
    const val EMPLOYEE_HOME         = "employee/home"
    const val EMPLOYER_HOME         = "employer/home"
    const val VENDOR_HOME           = "vendor/home"
    const val ADMIN_HOME            = "admin/home"
}

private fun homeDestinationFor(role: String, isProfileComplete: Boolean): String {
    return if (!isProfileComplete) {
        when (role) {
            "employee" -> Routes.PERSONAL_INFO
            "employer" -> Routes.EMPLOYER_REGISTRATION
            "vendor"   -> Routes.VENDOR_REGISTRATION
            "admin"    -> Routes.ADMIN_HOME
            else       -> Routes.ROLE_SELECTION
        }
    } else {
        when (role) {
            "employee" -> Routes.EMPLOYEE_HOME
            "employer" -> Routes.EMPLOYER_HOME
            "vendor"   -> Routes.VENDOR_HOME
            "admin"    -> Routes.ADMIN_HOME
            else       -> Routes.ROLE_SELECTION
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(),
    employeeRegistrationViewModel: EmployeeRegistrationViewModel = hiltViewModel()
) {
    var selectedServiceIds by remember { mutableStateOf<List<String>>(emptyList()) }
    val authState by authViewModel.uiState.collectAsState()

    // ✅ FIX: Hoist EmployeeHomeViewModel here at the navigation level so we
    // can START loading data the moment Firebase confirms the user — before
    // EmployeeMainScreen even enters composition.
    //
    // Previously the sequence was:
    //   Firebase confirms auth
    //   → NavHost renders EmployeeMainScreen                (frame 1 — empty UI)
    //   → LaunchedEffect fires loadProfile()               (frame 2 — shimmer)
    //   → data arrives                                     (frame 3 — content)
    //
    // Now the sequence is:
    //   Firebase confirms auth
    //   → we call loadProfile() RIGHT HERE                 (load starts immediately)
    //   → NavHost renders EmployeeMainScreen               (frame 1 — shimmer, data already in flight)
    //   → data arrives                                     (frame 2 — content)
    //
    // The empty-UI flash between the splash and the shimmer is eliminated.
    val employeeHomeViewModel: EmployeeHomeViewModel = hiltViewModel()

    // As soon as we know who is logged in, start fetching their data.
    // hasLoaded guard inside loadProfile() ensures this never double-fetches.
    LaunchedEffect(authState.currentUser) {
        val user = authState.currentUser
        if (user != null && user.role == "employee" && user.isProfileComplete) {
            employeeHomeViewModel.loadProfile(user.uid)
        }
    }

    // Show the global splash only while Firebase is confirming the session.
    // We replace the spinner with the app's background colour so there is zero
    // visible "flash" when the NavHost appears.
    if (authState.isCheckingAuth) {
        Box(
            modifier         = Modifier.fillMaxSize().background(NoorBackground),
            contentAlignment = Alignment.Center
        ) {
            // No spinner here — the shimmer in EmployeeHomeScreen handles the
            // "loading" perception once the screen appears.
            // If you want a branded splash logo instead, put it here.
        }
        return
    }

    val startDestination = remember(authState.currentUser) {
        val user = authState.currentUser
        if (user != null) homeDestinationFor(user.role, user.isProfileComplete)
        else Routes.AUTH
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.AUTH) {
            AuthScreen(
                viewModel       = authViewModel,
                onLoginSuccess  = {
                    val user = authViewModel.uiState.value.currentUser
                    val dest = if (user != null) {
                        homeDestinationFor(user.role, user.isProfileComplete)
                    } else Routes.AUTH
                    navController.navigate(dest) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    val user = authViewModel.uiState.value.currentUser
                    if (user != null) {
                        when (user.role) {
                            "employee" -> employeeRegistrationViewModel.setUserId(user.uid)
                        }
                        val dest = homeDestinationFor(user.role, isProfileComplete = false)
                        navController.navigate(dest) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    authViewModel.setSelectedRole(role)
                    when (role) {
                        "employee" -> {
                            authViewModel.getCurrentUserUid()?.let {
                                employeeRegistrationViewModel.setUserId(it)
                            }
                            navController.navigate(Routes.PERSONAL_INFO)
                        }
                        "employer" -> navController.navigate(Routes.EMPLOYER_REGISTRATION)
                        "vendor"   -> navController.navigate(Routes.VENDOR_REGISTRATION)
                        "admin"    -> navController.navigate(Routes.ADMIN_HOME) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.EMPLOYER_REGISTRATION) {
            val employerRegViewModel: EmployerRegistrationViewModel = hiltViewModel()
            val uid = authViewModel.getCurrentUserUid() ?: ""
            LaunchedEffect(uid) {
                if (uid.isNotBlank()) employerRegViewModel.setUserId(uid)
            }
            EmployerRegistrationScreen(
                viewModel    = employerRegViewModel,
                onBack       = { navController.popBackStack() },
                onRegistered = {
                    navController.navigate(Routes.EMPLOYER_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PERSONAL_INFO) {
            PersonalInfoScreen(
                viewModel = employeeRegistrationViewModel,
                onNext    = { navController.navigate(Routes.SERVICE_SELECT) },
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.SERVICE_SELECT) {
            ServiceSelectionScreen(
                viewModel = employeeRegistrationViewModel,
                onNext    = { ids ->
                    selectedServiceIds = ids
                    navController.navigate(Routes.SERVICE_DETAIL)
                },
                onBack    = { navController.popBackStack() }
            )
        }

        composable(Routes.SERVICE_DETAIL) {
            ServiceDetailScreen(
                viewModel          = employeeRegistrationViewModel,
                selectedServiceIds = selectedServiceIds,
                onSuccess          = {
                    navController.navigate(Routes.EMPLOYEE_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack             = { navController.popBackStack() }
            )
        }

        composable(Routes.SUCCESS) {
            RegistrationSuccessScreen(
                onGoToHome = {
                    navController.navigate(Routes.EMPLOYEE_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VENDOR_REGISTRATION) {
            val vendorRegViewModel: VendorRegistrationViewModel = hiltViewModel()
            val uid = authViewModel.getCurrentUserUid() ?: ""
            LaunchedEffect(uid) {
                if (uid.isNotBlank()) vendorRegViewModel.setUserId(uid)
            }
            VendorRegistrationScreen(
                viewModel    = vendorRegViewModel,
                onBack       = { navController.popBackStack() },
                onRegistered = {
                    navController.navigate(Routes.VENDOR_SUCCESS) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VENDOR_SUCCESS) {
            VendorRegistrationSuccessScreen(
                onGoToHome = {
                    navController.navigate(Routes.VENDOR_HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMPLOYEE_HOME) {
            val userId = authViewModel.getCurrentUserUid() ?: ""
            EmployeeMainScreen(
                userId        = userId,
                // ✅ Pass the already-loading VM down — no new hiltViewModel() call here
                homeViewModel = employeeHomeViewModel,
                onLogout      = {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.EMPLOYER_HOME) {
            EmployerMainScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VENDOR_HOME) {
            VendorMainScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            AdminMainScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}