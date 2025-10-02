package com.probe.kasirprobe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.probe.kasirprobe.data.db.DatabaseModule
import com.probe.kasirprobe.data.db.ProductEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: Int
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Load product data
    LaunchedEffect(productId) {
        scope.launch {
            try {
                val db = DatabaseModule.getDatabase(context)
                val dao = db.productDao()
                product = dao.getProductById(productId)
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Detail Produk",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            // Floating Action Button untuk Edit
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate("editProduct/${productId}")
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp, end = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Produk",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Produk", style = MaterialTheme.typography.labelLarge)
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Memuat detail produk...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = "Produk tidak ditemukan",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "Produk tidak ditemukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            ProductDetailContent(
                product = product!!,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun ProductDetailContent(
    product: ProductEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section dengan Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Icon/Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.name.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 24.sp
                    )
                }

                // Product Name
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                // Category Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Content Section
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Price Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Harga Jual Card
                PriceCard(
                    title = "Harga Jual",
                    price = product.price,
                    icon = Icons.Default.LocalOffer,
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )

                // Harga Dasar Card
                PriceCard(
                    title = "Harga Dasar",
                    price = product.cost,
                    icon = Icons.Default.Inventory2,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            // Info Cards
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stock Info Card
                InfoCard(
                    title = "Stok Tersedia",
                    value = "${product.stock} pcs",
                    icon = Icons.Default.Inventory2,
                    valueColor = when {
                        product.stock <= 0 -> MaterialTheme.colorScheme.error
                        product.stock < 5 -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.primary
                    },
                    showWarning = product.stock < 5
                )

                // Category Info Card
                InfoCard(
                    title = "Kategori",
                    value = product.category,
                    icon = Icons.Default.Category,
                    valueColor = MaterialTheme.colorScheme.onSurface
                )

                // Barcode Info Card (jika ada)
                product.barcode?.let { barcode ->
                    if (barcode.isNotBlank()) {
                        InfoCard(
                            title = "Kode Barcode",
                            value = barcode,
                            icon = Icons.Default.QrCode,
                            valueColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriceCard(
    title: String,
    price: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                // Format harga langsung tanpa fungsi formatRupiah
                "Rp ${String.format("%,.0f", price)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color,
    showWarning: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = valueColor
                    )
                }
            }

            if (showWarning) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFFA000), CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailContentPreview() {
    MaterialTheme {
        ProductDetailContent(
            product = ProductEntity(
                id = 1,
                name = "Kopi Arabika Premium",
                category = "Minuman",
                price = 25000.0,
                cost = 15000.0,
                stock = 15,
                barcode = "1234567890123"
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailContentLowStockPreview() {
    MaterialTheme {
        ProductDetailContent(
            product = ProductEntity(
                id = 2,
                name = "Teh Hijau",
                category = "Minuman",
                price = 18000.0,
                cost = 10000.0,
                stock = 3,
                barcode = "9876543210987"
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductDetailContentNoBarcodePreview() {
    MaterialTheme {
        ProductDetailContent(
            product = ProductEntity(
                id = 3,
                name = "Roti Coklat",
                category = "Makanan",
                price = 12000.0,
                cost = 7000.0,
                stock = 25,
                barcode = null
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PriceCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PriceCard(
                title = "Harga Jual",
                price = 25000.0,
                icon = Icons.Default.LocalOffer,
                isPrimary = true
            )
            PriceCard(
                title = "Harga Dasar",
                price = 15000.0,
                icon = Icons.Default.Inventory2,
                isPrimary = false
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InfoCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                title = "Stok Tersedia",
                value = "15 pcs",
                icon = Icons.Default.Inventory2,
                valueColor = MaterialTheme.colorScheme.primary
            )
            InfoCard(
                title = "Kategori",
                value = "Minuman",
                icon = Icons.Default.Category,
                valueColor = MaterialTheme.colorScheme.onSurface
            )
            InfoCard(
                title = "Stok Tersedia",
                value = "2 pcs",
                icon = Icons.Default.Inventory2,
                valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
                showWarning = true
            )
        }
    }
}

