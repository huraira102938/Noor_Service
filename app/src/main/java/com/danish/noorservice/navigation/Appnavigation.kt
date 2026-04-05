// ═══════════════════════════════════════════════════════════════════════════
// FILE 1:  navigation/AppNavigation.kt
// ═══════════════════════════════════════════════════════════════════════════

package com.danish.noorservice.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danish.noorservice.ui.screens.auth.AuthScreen
import com.danish.noorservice.ui.screens.auth.EmployerRegistrationScreen
import com.danish.noorservice.ui.screens.auth.RoleSelectionScreen
import com.danish.noorservice.ui.screens.employee.*
import com.danish.noorservice.ui.screens.employer.EmployerMainScreen
import com.danish.noorservice.ui.screens.vendor.VendorMainScreen           // ← NEW
import com.danish.noorservice.ui.screens.vendor.VendorRegistrationScreen   // ← NEW
import com.danish.noorservice.ui.screens.vendor.VendorRegistrationSuccessScreen

// ─────────────────────────────────────────────────────────────────────────────
// Route constants
// ─────────────────────────────────────────────────────────────────────────────

object Routes {
    const val AUTH                   = "auth"
    const val ROLE_SELECTION         = "role_selection"

    // Employer
    const val EMPLOYER_REGISTRATION  = "employer/registration"

    // Employee registration
    const val PERSONAL_INFO          = "employee/personal_info"
    const val SERVICE_SELECT         = "employee/service_select"
    const val SERVICE_DETAIL         = "employee/service_detail"
    const val SUCCESS                = "employee/success"

    // Vendor registration                                    ← NEW
    const val VENDOR_REGISTRATION    = "vendor/registration"  // ← NEW
    const val VENDOR_SUCCESS         = "vendor/success"       // ← NEW

    // Main shells (bottom nav)
    const val EMPLOYEE_HOME          = "employee/home"
    const val EMPLOYER_HOME          = "employer/home"
    const val VENDOR_HOME            = "vendor/home"           // ← NEW
}

// ─────────────────────────────────────────────────────────────────────────────
// App NavHost
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    var selectedServiceIds by remember { mutableStateOf<List<String>>(emptyList()) }

    NavHost(
        navController    = navController,
        startDestination = Routes.AUTH
    ) {

        // ── Auth ─────────────────────────────────────────────────────────────
        composable(Routes.AUTH) {
            AuthScreen(
                onLoginSuccess  = { navController.navigate(Routes.ROLE_SELECTION) },
                onSignUpSuccess = { navController.navigate(Routes.ROLE_SELECTION) }
            )
        }

        // ── Role Selection ─────────────────────────────────────────────────────
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    when (role) {
                        "employee" -> navController.navigate(Routes.PERSONAL_INFO)
                        "employer" -> navController.navigate(Routes.EMPLOYER_REGISTRATION)
                        "vendor"   -> navController.navigate(Routes.VENDOR_REGISTRATION) // ← NEW
                    }
                }
            )
        }

        // ── Employer Registration ─────────────────────────────────────────────
        composable(Routes.EMPLOYER_REGISTRATION) {
            EmployerRegistrationScreen(
                onBack = { navController.popBackStack() },
                onRegistered = {
                    navController.navigate(Routes.EMPLOYER_HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ── Employee: Step 1 ─────────────────────────────────────────────────
        composable(Routes.PERSONAL_INFO) {
            PersonalInfoScreen(
                onNext = { navController.navigate(Routes.SERVICE_SELECT) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Employee: Step 2 ─────────────────────────────────────────────────
        composable(Routes.SERVICE_SELECT) {
            ServiceSelectionScreen(
                onNext = { ids ->
                    selectedServiceIds = ids
                    navController.navigate(Routes.SERVICE_DETAIL)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Employee: Step 3 ─────────────────────────────────────────────────
        composable(Routes.SERVICE_DETAIL) {
            ServiceDetailScreen(
                selectedServiceIds = selectedServiceIds,
                onNext = { navController.navigate(Routes.SUCCESS) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Employee: Success ────────────────────────────────────────────────
        composable(Routes.SUCCESS) {
            RegistrationSuccessScreen(
                onGoToHome = {
                    navController.navigate(Routes.EMPLOYEE_HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ── Vendor Registration ──────────────────────────────────────────────  ← NEW
        composable(Routes.VENDOR_REGISTRATION) {                                // ← NEW
            VendorRegistrationScreen(                                            // ← NEW
                onBack = { navController.popBackStack() },                      // ← NEW
                onRegistered = {                                                 // ← NEW
                    navController.navigate(Routes.VENDOR_SUCCESS) {             // ← NEW
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = false }    // ← NEW
                    }                                                            // ← NEW
                }                                                                // ← NEW
            )                                                                    // ← NEW
        }                                                                        // ← NEW

        // ── Vendor: Success ──────────────────────────────────────────────────  ← NEW
        composable(Routes.VENDOR_SUCCESS) {                                     // ← NEW
            VendorRegistrationSuccessScreen(                                    // ← NEW
                onGoToHome = {                                                   // ← NEW
                    navController.navigate(Routes.VENDOR_HOME) {                // ← NEW
                        popUpTo(Routes.AUTH) { inclusive = true }               // ← NEW
                    }                                                            // ← NEW
                }                                                                // ← NEW
            )                                                                    // ← NEW
        }                                                                        // ← NEW

        // ── Employee Home ────────────────────────────────────────────────────
        composable(Routes.EMPLOYEE_HOME) {
            EmployeeMainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.EMPLOYEE_HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Employer Home ────────────────────────────────────────────────────
        composable(Routes.EMPLOYER_HOME) {
            EmployerMainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.EMPLOYER_HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Vendor Home ──────────────────────────────────────────────────────  ← NEW
        composable(Routes.VENDOR_HOME) {                                        // ← NEW
            VendorMainScreen(                                                    // ← NEW
                onLogout = {                                                     // ← NEW
                    navController.navigate(Routes.AUTH) {                       // ← NEW
                        popUpTo(Routes.VENDOR_HOME) { inclusive = true }        // ← NEW
                    }                                                            // ← NEW
                }                                                                // ← NEW
            )                                                                    // ← NEW
        }                                                                        // ← NEW
    }
}