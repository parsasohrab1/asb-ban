package ir.asbban.app.ui.screens.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import ir.asbban.app.data.model.Product
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productSlug: String
) {
    val scope = rememberCoroutineScope()
    var product by remember { mutableStateOf<Product?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var addingToCart by remember { mutableStateOf(false) }

    LaunchedEffect(productSlug) {
        try {
            val response = RetrofitClient.apiService.getProduct(productSlug)
            if (response.isSuccessful && response.body() != null) {
                product = response.body()
            } else {
                error = "محصول یافت نشد"
            }
        } catch (e: Exception) {
            error = "خطا در بارگذاری محصول: ${e.message}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("جزئیات محصول") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        },
        bottomBar = {
            if (product != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "کاهش")
                            }
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            IconButton(
                                onClick = { 
                                    if (product!!.stock_quantity > quantity) quantity++ 
                                },
                                enabled = quantity < product!!.stock_quantity
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "افزایش")
                            }
                        }

                        // Add to Cart Button
                        Button(
                            onClick = {
                                scope.launch {
                                    addingToCart = true
                                    // TODO: Add to cart logic
                                    kotlinx.coroutines.delay(500)
                                    addingToCart = false
                                    navController.navigate("cart")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !addingToCart && product!!.stock_quantity > 0
                        ) {
                            if (addingToCart) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("افزودن به سبد")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("بازگشت")
                    }
                }
            }
            product != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Product Image
                    val imageUrl = product!!.images?.firstOrNull()
                    if (imageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = product!!.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Product Info
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Name
                        Text(
                            text = product!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Price
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${product!!.price.toInt().toString().reversed().chunked(3).joinToString(",").reversed()} تومان",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            if (product!!.compare_at_price != null && product!!.compare_at_price!! > product!!.price) {
                                Text(
                                    text = "${product!!.compare_at_price!!.toInt().toString().reversed().chunked(3).joinToString(",").reversed()} تومان",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Stock Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (product!!.stock_quantity > 0) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (product!!.stock_quantity > 0) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (product!!.stock_quantity > 0) 
                                    "موجود (${product!!.stock_quantity} عدد)" 
                                else 
                                    "ناموجود",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (product!!.stock_quantity > 0) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.error
                            )
                        }

                        Divider()

                        // Description
                        if (!product!!.description.isNullOrBlank()) {
                            Text(
                                text = "توضیحات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product!!.description!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Short Description
                        if (!product!!.short_description.isNullOrBlank()) {
                            Text(
                                text = product!!.short_description!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Category
                        if (!product!!.category_name.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = product!!.category_name!!,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // SKU
                        if (!product!!.sku.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "کد محصول: ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = product!!.sku!!,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

