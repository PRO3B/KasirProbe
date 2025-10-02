package com.probe.kasirprobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.probe.kasirprobe.ui.AddProductScreen
import com.probe.kasirprobe.ui.EditProductScreen
import com.probe.kasirprobe.ui.InventoryScreen
import com.probe.kasirprobe.ui.ProductDetailScreen
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route != "addProduct"

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) BottomNavigationBar(navController)
            },
        ) { innerPadding ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

data class HomeMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

sealed class BottomNavItem(val route: String, val label: String) {
    object Home : BottomNavItem("home", "Home")
    object Inventory : BottomNavItem("inventory", "Inventory")
    object Pay : BottomNavItem("pay", "Pay")
    object Laporan : BottomNavItem("laporan", "Laporan")
    object Setting : BottomNavItem("setting", "Setting")
}

// 🔹 Bottom Navigation Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Inventory,
        BottomNavItem.Pay,
        BottomNavItem.Laporan,
        BottomNavItem.Setting
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) { launchSingleTop = true }
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = {
                    if (item == BottomNavItem.Pay) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = item.label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Icon(
                            imageVector = when (item) {
                                BottomNavItem.Home -> Icons.Default.Home
                                BottomNavItem.Inventory -> Icons.AutoMirrored.Filled.List
                                BottomNavItem.Laporan -> Icons.Default.Description
                                BottomNavItem.Setting -> Icons.Default.Settings
                                else -> Icons.Default.Home
                            },
                            contentDescription = item.label,
                            modifier = Modifier.size(28.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}

// 🔹 Notification Icon
@Composable
fun NotificationIcon(
    hasNotification: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifikasi",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        if (hasNotification) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = 6.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}


// 🔹 Home Screen
@Composable
fun HomeScreen(navController: NavHostController) {
    val menuItems = listOf(
        HomeMenuItem("Inventory", Icons.AutoMirrored.Filled.List) {
            navController.navigate(BottomNavItem.Inventory.route)
        },
        HomeMenuItem("Laporan", Icons.Default.Description) {
            navController.navigate(BottomNavItem.Laporan.route)
        },
        HomeMenuItem("Hutang", Icons.Default.AttachMoney) {
            navController.navigate("hutang")
        },
        HomeMenuItem("Laba Rugi", Icons.Default.BarChart) {
            navController.navigate("labarugi")
        },
        HomeMenuItem("Database", Icons.Default.Storage) { },
        HomeMenuItem("Staff", Icons.Default.Person) { },
        HomeMenuItem("Kategori", Icons.Default.Category) { },
        HomeMenuItem("Pelanggan", Icons.Default.Group) { },
        HomeMenuItem("Setting", Icons.Default.Settings) {
            navController.navigate(BottomNavItem.Setting.route)
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                HomeMenuButton(item)
            }
        }
    }
}

@Composable
fun HomeMenuButton(item: HomeMenuItem) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        onClick = item.onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

// 🔹 NavGraph
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
            ProductDetailScreen(navController = navController, productId = productId)
        }

        composable("editProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull() ?: 0
            EditProductScreen(navController = navController, productId = productId)
        }

        composable("addProduct") { AddProductScreen(navController = navController) }
        composable(BottomNavItem.Home.route) { HomeScreen(navController = navController) }
        composable(BottomNavItem.Inventory.route) { InventoryScreen(navController = navController) }
        composable(BottomNavItem.Pay.route) { PayScreen() }
        composable(BottomNavItem.Laporan.route) { LaporanScreen() }
        composable(BottomNavItem.Setting.route) { SettingScreen() }
        composable("hutang") { HutangScreen() }
        composable("labarugi") { LabaRugiScreen() }
        }
    }


// 🔹 Dummy screens
@Composable fun PayScreen() { Text("Pay Screen") }
@Composable fun HutangScreen() { Text("Hutang Screen") }
@Composable fun LaporanScreen() { Text("Laporan Screen") }
@Composable fun LabaRugiScreen() { Text("Laba Rugi Screen") }
@Composable fun SettingScreen() { Text("Setting Screen") }

@Preview(showBackground = true)
@Composable
fun PreviewKasirProbeApp() {
    KasirProbeTheme { KasirProbeApp() }
}

@Preview(showBackground = true, showSystemUi = true, name = "Inventory - Light")
@Composable
fun InventoryScreenPreview() {
    KasirProbeTheme { InventoryScreen(navController = rememberNavController()) }
}