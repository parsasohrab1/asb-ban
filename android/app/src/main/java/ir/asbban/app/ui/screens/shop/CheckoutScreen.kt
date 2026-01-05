package ir.asbban.app.ui.screens.shop

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var shippingAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("online") }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // TODO: Get cart items from state/storage
    val cartItems = remember { mutableStateListOf<CartItem>() }
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پرداخت") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shipping Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "آدرس ارسال",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("آدرس کامل خود را وارد کنید") },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // Payment Method
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "روش پرداخت",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = paymentMethod == "online",
                            onClick = { paymentMethod = "online" },
                            label = { Text("پرداخت آنلاین") },
                            leadingIcon = {
                                Icon(Icons.Default.CreditCard, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = paymentMethod == "cash",
                            onClick = { paymentMethod = "cash" },
                            label = { Text("پرداخت در محل") },
                            leadingIcon = {
                                Icon(Icons.Default.Money, contentDescription = null)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Order Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "خلاصه سفارش",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تعداد محصولات:")
                        Text("${cartItems.sumOf { it.quantity }} عدد")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جمع کل:")
                        Text(
                            text = "${totalPrice.toInt().toString().reversed().chunked(3).joinToString(",").reversed()} تومان",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Error Message
            if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (shippingAddress.isBlank()) {
                        error = "لطفاً آدرس ارسال را وارد کنید"
                        return@Button
                    }

                    scope.launch {
                        submitting = true
                        error = null
                        try {
                            val orderRequest = ir.asbban.app.data.model.OrderRequest(
                                items = cartItems.map {
                                    ir.asbban.app.data.model.OrderItemRequest(
                                        product_id = it.productId,
                                        quantity = it.quantity
                                    )
                                },
                                shipping_address = shippingAddress,
                                payment_method = paymentMethod
                            )

                            val response = RetrofitClient.apiService.createOrder(orderRequest)
                            if (response.isSuccessful && response.body() != null) {
                                // Navigate to success page
                                navController.navigate("orders/${response.body()!!.id}/success") {
                                    popUpTo("shop") { inclusive = false }
                                }
                            } else {
                                error = "خطا در ثبت سفارش"
                            }
                        } catch (e: Exception) {
                            error = "خطا: ${e.message}"
                        } finally {
                            submitting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !submitting && cartItems.isNotEmpty() && shippingAddress.isNotBlank()
            ) {
                if (submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تایید و پرداخت")
                }
            }
        }
    }
}

