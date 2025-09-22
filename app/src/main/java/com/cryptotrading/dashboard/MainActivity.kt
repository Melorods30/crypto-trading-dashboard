package com.cryptotrading.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cryptotrading.dashboard.ui.navigation.AppNavigation
import com.cryptotrading.dashboard.ui.theme.CryptoTradingDashboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoTradingDashboardTheme {
                AppNavigation()
            }
        }
    }
}
