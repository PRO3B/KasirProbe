// MainActivity.kt
package com.probe.kasirprobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.probe.kasirprobe.ui.theme.KasirProbeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KasirProbeTheme {
                KasirProbeApp()
            }
        }
    }
}

@Composable
fun KasirProbeApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Inventory.route) { InventoryScreen() }
            composable(BottomNavItem.Pay.route) { PayScreen() }
            composable(BottomNavItem.Hutang.route) { HutangScreen() }
            composable(BottomNavItem.Laporan.route) { LaporanScreen() }
            composable(BottomNavItem.LabaRugi.route) { LabaRugiScreen() }
        }
    }
}

sealed class BottomNavItem(val route: String, val label: String) {
    object Home : BottomNavItem("home", "Home")
    object Inventory : BottomNavItem("inventory", "Inventory")
    object Pay : BottomNavItem("pay", "Pay")
    object Hutang : BottomNavItem("hutang", "Hutang")
    object Laporan : BottomNavItem("laporan", "Laporan")
    object LabaRugi : BottomNavItem("labarugi", "Laba Rugi")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Inventory,
        BottomNavItem.Pay,
        BottomNavItem.Hutang,
        BottomNavItem.Laporan,
        BottomNavItem.LabaRugi
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(item.label) },
                icon = { Icon(Icons.Default.Home, contentDescription = item.label) } // sementara semua icon sama
            )
        }
    }
}

// Placeholder Screens
@Composable fun HomeScreen() { Text("Home Screen") }
@Composable fun InventoryScreen() { Text("Inventory Screen") }
@Composable fun PayScreen() { Text("Pay Screen") }
@Composable fun HutangScreen() { Text("Hutang Screen") }
@Composable fun LaporanScreen() { Text("Laporan Screen") }
@Composable fun LabaRugiScreen() { Text("Laba Rugi Screen") }
