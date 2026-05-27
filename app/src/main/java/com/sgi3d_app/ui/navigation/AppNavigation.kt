package com.sgi3d_app.ui.navigation

import android.content.Context

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.compose.*

import kotlinx.coroutines.launch

import com.sgi3d_app.ui.login.LoginScreen
import com.sgi3d_app.ui.dashboard.DashboardScreen
import com.sgi3d_app.ui.dashboard.ProfileScreen

import com.sgi3d_app.data.remote.ApiRetrofitInstance
import com.sgi3d_app.data.remote.LoginRequest
import com.sgi3d_app.ui.users.CreateUserScreen

import com.sgi3d_app.ui.users.UserManagementScreen
import com.sgi3d_app.ui.requests.NewRequestScreen

import java.io.File
import java.io.FileOutputStream
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sgi3d_app.ui.requests.RequestViewModel
import com.sgi3d_app.ui.dashboard.ChangePasswordScreen

@Composable
fun AppNavigation(token: String) {

    val navController = rememberNavController()

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    // 🌙 DARK MODE

    val sharedPreferences =
        context.getSharedPreferences(
            "SGI3D_PREFS",
            Context.MODE_PRIVATE
        )

    var isDarkMode by remember {

        mutableStateOf(
            sharedPreferences.getBoolean(
                "dark_mode",
                false
            )
        )

    }

    MaterialTheme(

        colorScheme =
            if (isDarkMode)
                darkColorScheme()
            else
                lightColorScheme()

    ) {

        NavHost(

            navController = navController,

            startDestination = "login"

        ) {

            // ===============================
            // 🔐 LOGIN
            // ===============================

            composable("login") {

                LoginScreen(

                    onLoginClick = { email, password ->

                        scope.launch {

                            try {

                                val response =
                                    ApiRetrofitInstance
                                        .api
                                        .login(
                                            LoginRequest(
                                                email,
                                                password
                                            )
                                        )

                                if (
                                    response.isSuccessful &&
                                    response.body()?.success == true
                                ) {

                                    val token =
                                        response.body()
                                            ?.token
                                            ?.trim()
                                            ?: ""

                                    val role =
                                        response.body()
                                            ?.role
                                            ?.trim()
                                            ?: "user"

                                    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    sharedPref.edit().putString("token", token).apply()

                                    navController.navigate(
                                        "dashboard/$role/$token"
                                    ) {

                                        popUpTo("login") {
                                            inclusive = true
                                        }

                                    }

                                } else {

                                    println(
                                        "Login failed: ${
                                            response.body()?.message
                                        }"
                                    )

                                }

                            } catch (e: Exception) {

                                e.printStackTrace()

                            }

                        }

                    }

                )

            }

            // ===============================
            // 🖨 DASHBOARD
            // ===============================

            composable(
                "dashboard/{role}/{token}"
            ) { backStackEntry ->

                val role =
                    backStackEntry.arguments
                        ?.getString("role")
                        ?: "admin"

                val token =
                    backStackEntry.arguments
                        ?.getString("token")
                        ?.trim()
                        ?: ""

                DashboardScreen(

                    role = role,

                    token = token,

                    isDarkMode = isDarkMode,

                    onToggleDarkMode = {

                        isDarkMode = it

                        sharedPreferences
                            .edit()
                            .putBoolean(
                                "dark_mode",
                                it
                            )
                            .apply()

                    },

                    onLogout = {

                        navController.navigate(
                            "login"
                        ) {

                            popUpTo(
                                "dashboard/{role}/{token}"
                            ) {

                                inclusive = true

                            }

                        }

                    },

                    onOpenProfile = {

                        navController.navigate(
                            "profile/$role/$token"
                        )

                    },

                    navController = navController,
                )

            }

            // ===============================
// 👥 USER MANAGEMENT
// ===============================

            composable("user_management/{token}") { backStackEntry ->

                val token =
                    backStackEntry.arguments
                        ?.getString("token")
                        ?.trim() ?: ""

                UserManagementScreen(
                    token = token,

                    onBack = {
                        navController.popBackStack()
                    },

                    onNavigateToCreateUser = {
                        navController.navigate("create_user/$token")
                    }
                )
            }

// ===============================
// 📄 NEW REQUEST
// ===============================

            composable("new_request/{name}/{email}/{role}") { backStackEntry ->

                val name =
                    backStackEntry.arguments
                        ?.getString("name") ?: ""

                val email =
                    backStackEntry.arguments
                        ?.getString("email") ?: ""

                val role = backStackEntry.arguments?.getString("role") ?: "etudiant"

                val requestViewModel: RequestViewModel =
                    viewModel()

                NewRequestScreen(

                    studentName = name,
                    studentEmail = email,
                    role = role,
                    onSend = { uri, comment ->

                        uri?.let {

                            val file = File(it.toString().replace("file://", ""))

                            if (!file.exists()) {

                                println("Fichier introuvable: ${file.absolutePath}")
                                return@let
                            }

                            println("Taille fichier: ${file.length()} bytes")

                            requestViewModel.createDemande(
                                file.absolutePath,
                                name,
                                email,
                                comment,
                                context,
                                role
                            )

                        }

                    },

                    onBack = {
                        navController.popBackStack()
                    }

                )

            }


// ===============================
// ➕ CREATE USER
// ===============================

            composable("create_user/{token}") { backStackEntry ->

                val token =
                    backStackEntry.arguments
                        ?.getString("token")
                        ?.trim() ?: ""

                CreateUserScreen(
                    token = token,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                "change_password/{token}"
            ) { backStackEntry ->

                val token =
                    backStackEntry.arguments
                        ?.getString("token") ?: ""

                ChangePasswordScreen(
                    token = token,
                    onBack = {
                        navController.popBackStack()
                    }
                )

            }

            // ===============================
            // 👤 PROFILE
            // ===============================

            composable(
                "profile/{role}/{token}"
            ) { backStackEntry ->

                val role =
                    backStackEntry.arguments
                        ?.getString("role")
                        ?: "admin"

                val token =
                    backStackEntry.arguments
                        ?.getString("token")
                        ?.trim()
                        ?: ""

                ProfileScreen(

                    role = role,

                    token = token,

                    onBack = {

                        navController.popBackStack()

                    }

                )}

        }

    }

}

