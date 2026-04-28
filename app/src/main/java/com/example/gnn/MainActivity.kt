package com.example.gnn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.gnn.ui.login.LoginScreen
import com.example.gnn.ui.theme.GNNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GNNTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        onLoginSuccess = { /* TODO: Navigate to Home */ },
                        onNavigateToRegister = { /* TODO: Navigate to Register */ },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}