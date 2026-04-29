package com.example.gnn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gnn.ui.dashboard.DashboardScreen
import com.example.gnn.ui.dashboard.DashboardViewModel
import com.example.gnn.ui.login.LoginScreen
import com.example.gnn.ui.theme.GNNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GNNTheme {
                val navController = rememberNavController()
                val dashboardViewModel: DashboardViewModel = viewModel()
                val context = LocalContext.current

                // Toast Listener
                LaunchedEffect(dashboardViewModel.toastMessage) {
                    dashboardViewModel.toastMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        dashboardViewModel.clearToast()
                    }
                }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            LoginScreen(
                                onLoginSuccess = { username, uid ->
                                    dashboardViewModel.init(uid, username)
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {},
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
