package com.probe.kasirprobe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.probe.kasirprobe.ui.theme.KasirProbeTheme
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

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
            composable("addProduct") {
                AddProductScreen(navController = navController)
            }
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(BottomNavItem.Inventory.route) { InventoryScreen() }
            composable(BottomNavItem.Pay.route) { PayScreen() }
            composable(BottomNavItem.Laporan.route) { LaporanScreen() }
            composable(BottomNavItem.Setting.route) { SettingScreen() }

            // Tambahan untuk menu dashboard
            composable("hutang") { HutangScreen() }
            composable("labarugi") { LabaRugiScreen() }
        }
    }
}

// ---------------------- Bottom Nav ----------------------
sealed class BottomNavItem(val route: String, val label: String) {
    object Home : BottomNavItem("home", "Home")
    object Inventory : BottomNavItem("inventory", "Inventory")
    object Pay : BottomNavItem("pay", "Pay")
    object Laporan : BottomNavItem("laporan", "Laporan")
    object Setting : BottomNavItem("setting", "Setting")
}

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
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
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
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    }
}

// ---------------------- Dashboard ----------------------
data class HomeMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun HomeMenuButton(item: HomeMenuItem) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f),
        onClick = item.onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                maxLines = 1
            )
        }
    }
}

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
        Text("Home", style = MaterialTheme.typography.headlineMedium)
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

// ------------------ Model ------------------
data class Barcode(
    val code: String,          // isi kode barcode
    val type: String = "EAN",  // tipe barcode (EAN, UPC, CODE128, QR, dll)
    val createdAt: Long = System.currentTimeMillis() // waktu generate
)

data class Product(
    val name: String,
    val price: Double,
    val cost: Double,
    val stock: Int,
    val category: String,
    val imageUrl: String,
    val barcode: Barcode? = null
)

// ------------------ Inventory ------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavHostController) {
    val dummyProducts = listOf(
        Product(
            name = "Buku Tulis",
            price = 5000.0,
            cost = 3000.0,
            stock = 20,
            category = "Alat Tulis",
            barcode = Barcode("1234567890123", "EAN-13"),
            imageUrl = "https://cdn.pixabay.com/photo/2015/09/05/20/02/blank-925882_1280.jpg"
        ),
        Product(
            name = "Pensil",
            price = 2000.0,
            cost = 1000.0,
            stock = 2,
            category = "Alat Tulis",
            barcode = Barcode("1234567890123", "EAN-13"),
            imageUrl = "https://cdn.pixabay.com/photo/2016/11/29/01/11/pencil-1868972_1280.jpg"
        ),
        Product(
            name = "Penghapus",
            price = 1500.0,
            cost = 800.0,
            stock = 0,
            category = "Alat Tulis",
            barcode = Barcode("1234567890123", "EAN-13"),
            imageUrl = "https://cdn.pixabay.com/photo/2014/04/03/10/32/eraser-311863_1280.png"
        )
    )

    val outOfStock = dummyProducts.count { it.stock == 0 }
    val lowStock = dummyProducts.count { it.stock in 1..5 }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Inventory") },
                    actions = {
                        // ⋮ Menu tetap di kanan atas
                        IconButton(onClick = { /* TODO: buka menu */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }
                )

                // 🔔 + ⇅ + Search bar dalam 1 row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Notifikasi custom
                    NotificationIcon(
                        hasNotification = (outOfStock > 0 || lowStock > 0),
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Sort
                    IconButton(onClick = { /* TODO: sort */ }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                    }

                    // Search
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addProduct") }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dummyProducts) { product ->
                    ProductItem(product)
                }
            }
        }
    }
}

// 🔔 Notifikasi dengan dot di dalam ikon
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
                    .offset(x = (-6).dp, y = 6.dp) // geser agar dot masuk ke dalam ikon
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari produk...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp)), // biar agak rounded
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar produk / placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = product.name.take(2).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Nama + barcode
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = product.barcode?.code ?: "-",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Kolom kanan: stock badge + harga
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Stock badge
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                product.stock <= 0 -> Color.Red
                                product.stock < 5 -> Color(0xFFFFA000) // Orange
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.stock.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Harga dasar • harga jual
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rp ${product.cost.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Rp ${product.price.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ---------------------- Add produk ----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavHostController,
    onSave: (Product) -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Produk") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = barcode,
                onValueChange = { barcode = it },
                label = { Text("Barcode") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Harga Dasar") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Harga Jual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stok") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }
                Button(
                    onClick = {
                        val product = Product(
                            name = name,
                            price = price.toDoubleOrNull() ?: 0.0,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            stock = stock.toIntOrNull() ?: 0,
                            category = category,
                            imageUrl = "", // nanti tambahin upload foto
                            barcode = Barcode(barcode)
                        )
                        onSave(product)
                        navController.popBackStack() // balik ke InventoryScreen
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simpan")
                }
            }
        }
    }
}

// ---------------------- Placeholder Screens ----------------------
@Composable fun PayScreen() { Text("Pay Screen") }
@Composable fun HutangScreen() { Text("Hutang Screen") }
@Composable fun LaporanScreen() { Text("Laporan Screen") }
@Composable fun LabaRugiScreen() { Text("Laba Rugi Screen") }
@Composable fun SettingScreen() { Text("Setting Screen") }

// ---------------------- Preview ----------------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInventoryScreen() {
    KasirProbeTheme {
        InventoryScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewKasirProbeApp() {
    KasirProbeTheme {
        KasirProbeApp()
    }
}