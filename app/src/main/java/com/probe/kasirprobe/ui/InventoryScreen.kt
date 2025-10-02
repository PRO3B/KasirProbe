package com.probe.kasirprobe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.probe.kasirprobe.data.db.DatabaseModule
import com.probe.kasirprobe.data.db.ProductEntity
import com.probe.kasirprobe.ui.theme.KasirProbeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun InventoryScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val db = remember { DatabaseModule.getDatabase(context) }
    var products by remember { mutableStateOf(emptyList<ProductEntity>()) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedSearchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("nama") }
    var isGridView by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Debounce search
    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedSearchQuery = searchQuery
    }

    // Load data function
    fun loadProducts() {
        scope.launch {
            try {
                isLoading = true
                errorMessage = null
                val dao = db.productDao()
                products = dao.getAllProducts()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat produk: ${e.message}"
                println("❌ Error: ${e.message}")
            } finally {
                isLoading = false
                isRefreshing = false
            }
        }
    }

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            loadProducts()
        }
    )

    // Initial load
    LaunchedEffect(true) {
        loadProducts()
    }

    // Stats calculation
    val totalProducts = products.size
    val outOfStock = products.count { it.stock == 0 }
    val lowStock = products.count { it.stock in 1..5 }
    val totalValue = products.sumOf { it.cost * it.stock }

    // Categories
    val categories = listOf("Semua") + products.map { it.category }.distinct()

    // Filter & sort - HANYA SEARCH PRODUK SAJA
    val filteredProducts = products
        .filter { product ->
            (selectedCategory == "Semua" || product.category == selectedCategory) &&
                    product.name.contains(debouncedSearchQuery, ignoreCase = true) // HAPUS search kategori
        }
        .sortedBy {
            when (selectedSort) {
                "nama" -> it.name
                "harga" -> it.price.toString()
                "stok" -> it.stock.toString()
                else -> it.name
            }
        }

    val bottomNavHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = "Inventory",
                productCount = totalProducts
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .pullRefresh(pullRefreshState) // TAMBAHKAN PULL TO REFRESH
        ) {
            Column(modifier = Modifier.fillMaxSize()
                .padding(bottom = 80.dp)
            ) {
                // Show error message
                errorMessage?.let { message ->
                    LaunchedEffect(message) {
                        snackbarHostState.showSnackbar(message)
                        errorMessage = null
                    }
                }

                // 📊 Stats Section - HORIZONTAL SCROLL
                AnimatedVisibility(
                    visible = !isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        HorizontalStatsOverview(
                            totalProducts = totalProducts,
                            outOfStock = outOfStock,
                            lowStock = lowStock,
                            totalValue = totalValue,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Category Filter
                        CategoryFilter(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { selectedCategory = it },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Search & Controls
                        SimpleControlSection(
                            searchQuery = searchQuery,
                            onSearchChange = { searchQuery = it },
                            selectedSort = selectedSort,
                            onSortChange = { selectedSort = it },
                            isGridView = isGridView,
                            onViewToggle = { isGridView = !isGridView },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // 📦 Products List/Grid
                if (isLoading && products.isEmpty()) {
                    EnhancedLoadingShimmer()
                } else if (filteredProducts.isEmpty()) {
                    EnhancedEmptyState(
                        searchQuery = debouncedSearchQuery,
                        onAddProduct = { navController.navigate("addProduct") },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    if (isGridView) {
                        MinimalProductGrid(
                            products = filteredProducts,
                            onProductClick = { product ->
                                navController.navigate("productDetail/${product.id}")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        MinimalProductList(
                            products = filteredProducts,
                            onProductClick = { product ->
                                navController.navigate("productDetail/${product.id}")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // FAB dengan posisi absolute dan animasi
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    animationSpec = tween(durationMillis = 400, delayMillis = 200),
                    initialOffsetX = { fullWidth -> fullWidth }
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 88.dp)
            ) {

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()

                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.8f else 1f,
                    animationSpec = tween(durationMillis = 150),
                    label = "fab_scale"
                )

                FloatingActionButton(
                    onClick = { navController.navigate("addProduct") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    ),
                    interactionSource = interactionSource,
                    modifier = Modifier.scale(scale)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Tambah Produk",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // PullRefresh Indicator - GANTI refresh indicator lama
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// 🎯 COMPONENT 1: Horizontal Stats Overview dengan Scroll
@Composable
fun HorizontalStatsOverview(
    totalProducts: Int,
    outOfStock: Int,
    lowStock: Int,
    totalValue: Double,
    modifier: Modifier = Modifier
) {
    val stats = listOf(
        StatItem(totalProducts.toString(), "Total", MaterialTheme.colorScheme.primary),
        StatItem(outOfStock.toString(), "Habis", Color(0xFFF44336)),
        StatItem(lowStock.toString(), "Menipis", Color(0xFFFFA000)),
        StatItem(formatCurrency(totalValue.toInt()), "Nilai", Color(0xFF4CAF50))
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier
    ) {
        items(stats.size) { index ->
            HorizontalStatCard(stat = stats[index])
        }
    }
}

@Composable
fun HorizontalStatCard(stat: StatItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = stat.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stat.value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = stat.color,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class StatItem(val value: String, val label: String, val color: Color)

// 🎯 COMPONENT 2: Simple Top App Bar (HAPUS REFRESH BUTTON)
@Composable
fun SimpleTopAppBar(
    title: String,
    productCount: Int
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "$productCount produk",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// 🏷️ COMPONENT 3: Category Filter
@Composable
fun CategoryFilter(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = modifier
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            CategoryChip(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelect(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 1.dp
        )
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// 🎛️ COMPONENT 4: Simple Control Section
@Composable
fun SimpleControlSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    isGridView: Boolean,
    onViewToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Modern Search Bar - UBAH PLACEHOLDER
        ModernSearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Controls Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Sort Dropdown
            var expanded by remember { mutableStateOf(false) }
            val sortOptions = listOf("nama" to "Nama", "harga" to "Harga", "stok" to "Stok")

            Box {
                // Minimal Sort Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { expanded = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        sortOptions.find { it.first == selectedSort }?.second ?: "Urutkan",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    sortOptions.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSortChange(key)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // View Toggle - More Minimal
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onViewToggle() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = if (isGridView) "List View" else "Grid View",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (isGridView) "List" else "Grid",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 🔍 COMPONENT 5: Modern Search Bar - UBAH PLACEHOLDER
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = true
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Search Field
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Cari produk...", // UBAH: HANYA "Cari produk"
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        innerTextField()
                    }
                )
            }

            // Clear Button (hanya muncul ketika ada teks)
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// 📱 COMPONENT 6: Minimal Product Grid View dengan Click
@Composable
fun MinimalProductGrid(
    products: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(products.size) { index ->
            val product = products[index]
            MinimalProductGridItem(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun MinimalProductGridItem(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Product Icon & Stock
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Product Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.name.take(2).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Stock Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                product.stock <= 0 -> Color(0xFFF44336)
                                product.stock < 5 -> Color(0xFFFFA000)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (product.stock <= 0) "0" else "${product.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prices
            Column {
                // Harga Jual (utama)
                Text(
                    text = formatRupiah(product.price),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Harga Dasar (secondary)
                Text(
                    text = formatRupiah(product.cost),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Category
            Text(
                text = product.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

// 📋 COMPONENT 7: Minimal Product List View dengan Click
@Composable
fun MinimalProductList(
    products: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(products.size) { index ->
            val product = products[index]
            MinimalProductListItem(
                product = product,
                onClick = { onProductClick(product) }
            )
        }
    }
}

@Composable
fun MinimalProductListItem(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
            // Product Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(2).uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Prices
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupiah(product.price),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatRupiah(product.cost),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            // Stock
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                product.stock <= 0 -> Color(0xFFF44336)
                                product.stock < 5 -> Color(0xFFFFC800)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (product.stock <= 0) "HABIS" else "${product.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ✨ COMPONENT 8: Enhanced Loading Shimmer
@Composable
fun EnhancedLoadingShimmer() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )
            }
        }
    }
}

// 🎯 COMPONENT 9: Enhanced Empty State with Actions
@Composable
fun EnhancedEmptyState(
    searchQuery: String,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(
            if (searchQuery.isNotEmpty()) Icons.Default.Search else Icons.Default.Add,
            contentDescription = "Empty",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (searchQuery.isNotEmpty())
                "Tidak ada produk yang cocok dengan '$searchQuery'"
            else
                "Belum ada produk",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (searchQuery.isEmpty()) {
            Button(
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Tambah Produk Pertama")
            }
        } else {
            Text(
                text = "Coba kata kunci lain atau hapus pencarian",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 💰 FUNGSI FORMAT RUPIAH DENGAN DECIMAL
fun formatRupiah(amount: Double): String {
    val formatter = DecimalFormat("#,###")
    return "Rp ${formatter.format(amount)}"
}

fun formatCurrency(amount: Int): String {
    val formatter = DecimalFormat("#,###")
    return "Rp ${formatter.format(amount)}"
}

// Preview
@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    KasirProbeTheme {
        InventoryScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun MinimalProductListItemPreview() {
    KasirProbeTheme {
        MinimalProductListItem(
            product = ProductEntity(
                id = 1,
                name = "Buku Tulis Premium 80 Lembar",
                price = 15000.0,
                cost = 8000.0,
                stock = 25,
                category = "Alat Tulis",
                barcode = "123456789"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MinimalProductGridItemPreview() {
    KasirProbeTheme {
        MinimalProductGridItem(
            product = ProductEntity(
                id = 1,
                name = "Buku Tulis Premium",
                price = 15000.0,
                cost = 8000.0,
                stock = 3,
                category = "Alat Tulis",
                barcode = "123456789"
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MinimalStatsOverviewPreview() {
    KasirProbeTheme {
        HorizontalStatsOverview(
            totalProducts = 25,
            outOfStock = 3,
            lowStock = 5,
            totalValue = 1250000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EnhancedEmptyStatePreview() {
    KasirProbeTheme {
        EnhancedEmptyState(
            searchQuery = "",
            onAddProduct = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchEmptyStatePreview() {
    KasirProbeTheme {
        EnhancedEmptyState(
            searchQuery = "Keyboard",
            onAddProduct = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}