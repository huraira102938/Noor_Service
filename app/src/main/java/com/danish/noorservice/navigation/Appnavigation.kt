package com.danish.noorservice.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danish.noorservice.ui.screens.auth.AuthScreen
import com.danish.noorservice.ui.screens.auth.RoleSelectionScreen
import com.danish.noorservice.ui.screens.employee.*
import com.danish.noorservice.ui.screens.employer.EmployerMainScreen

// ─────────────────────────────────────────────────────────────────────────────
// Route constants
// ─────────────────────────────────────────────────────────────────────────────

object Routes {
    const val AUTH           = "auth"
    const val ROLE_SELECTION = "role_selection"

    // Employee registration flow
    const val PERSONAL_INFO  = "employee/personal_info"
    const val SERVICE_SELECT = "employee/service_select"
    const val SERVICE_DETAIL = "employee/service_detail"
    const val SUCCESS        = "employee/success"

    // Employee main (bottom nav shell)
    const val EMPLOYEE_HOME  = "employee/home"

    // Employer main (bottom nav shell)
    const val EMPLOYER_HOME  = "employer/home"
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
        startDestination = Routes.EMPLOYER_HOME
    ) {

        // ── Auth ─────────────────────────────────────────────────────────────
        composable(Routes.AUTH) {
            AuthScreen(
                onLoginSuccess  = { navController.navigate(Routes.ROLE_SELECTION) },
                onSignUpSuccess = { navController.navigate(Routes.ROLE_SELECTION) }
            )
        }

        // ── Role Selection ────────────────────────────────────────────────────
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    when (role) {
                        "employee" -> navController.navigate(Routes.PERSONAL_INFO)
                        "employer" -> navController.navigate(Routes.EMPLOYER_HOME)
                    }
                }
            )
        }

        // ── Registration Step 1: Personal Info ────────────────────────────────
        composable(Routes.PERSONAL_INFO) {
            PersonalInfoScreen(
                onNext = { navController.navigate(Routes.SERVICE_SELECT) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Registration Step 2: Service Selection ────────────────────────────
        composable(Routes.SERVICE_SELECT) {
            ServiceSelectionScreen(
                onNext = { ids ->
                    selectedServiceIds = ids
                    navController.navigate(Routes.SERVICE_DETAIL)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Registration Step 3: Service Details ──────────────────────────────
        composable(Routes.SERVICE_DETAIL) {
            ServiceDetailScreen(
                selectedServiceIds = selectedServiceIds,
                onNext = { navController.navigate(Routes.SUCCESS) },
                onBack = { navController.popBackStack() }
            )
        }

        // ── Registration Success ───────────────────────────────────────────────
        composable(Routes.SUCCESS) {
            RegistrationSuccessScreen(
                onGoToHome = {
                    navController.navigate(Routes.EMPLOYEE_HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ── Employee Home (Bottom Nav Shell) ──────────────────────────────────
        composable(Routes.EMPLOYEE_HOME) {
            EmployeeMainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.EMPLOYEE_HOME) { inclusive = true }
                    }
                }
            )
        }

        // ── Employer Home (Bottom Nav Shell) ──────────────────────────────────
        composable(Routes.EMPLOYER_HOME) {
            EmployerMainScreen(
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.EMPLOYER_HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}