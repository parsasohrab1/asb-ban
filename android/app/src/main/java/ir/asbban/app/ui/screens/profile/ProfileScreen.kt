package ir.asbban.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ir.asbban.app.data.local.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile = viewModel.profile.collectAsState().value
    val loading = viewModel.loading.collectAsState().value
    var editing by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پروفایل کاربری") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (profile != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Card(
                    modifier = Modifier.size(120.dp),
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "آواتار",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileItem(
                            label = "ایمیل",
                            value = profile.email,
                            icon = Icons.Default.Email
                        )
                        
                        ProfileItem(
                            label = "نام کامل",
                            value = profile.full_name ?: "تعریف نشده",
                            icon = Icons.Default.Person,
                            editable = editing,
                            onValueChange = { viewModel.updateFullName(it) }
                        )
                        
                        ProfileItem(
                            label = "شماره تماس",
                            value = profile.phone ?: "تعریف نشده",
                            icon = Icons.Default.Phone,
                            editable = editing,
                            onValueChange = { viewModel.updatePhone(it) }
                        )
                        
                        ProfileItem(
                            label = "نقش",
                            value = when (profile.role) {
                                "admin" -> "مدیر"
                                "author" -> "نویسنده"
                                else -> "کاربر"
                            },
                            icon = Icons.Default.Info
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                if (editing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.saveProfile()
                                editing = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ذخیره")
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.cancelEdit()
                                editing = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("انصراف")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { editing = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ویرایش")
                        }
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("خروج")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Actions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text("سفارشات من") },
                            leadingContent = {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            },
                            trailingContent = {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("profile/orders")
                            }
                        )
                        Divider()
                        ListItem(
                            headlineContent = { Text("رزروهای من") },
                            leadingContent = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            },
                            trailingContent = {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("profile/bookings")
                            }
                        )
                        Divider()
                        ListItem(
                            headlineContent = { Text("تنظیمات") },
                            leadingContent = {
                                Icon(Icons.Default.Settings, contentDescription = null)
                            },
                            trailingContent = {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            },
                            modifier = Modifier.clickable {
                                navController.navigate("settings")
                            }
                        )
                    }
                }
            }
        }

        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("خروج از حساب کاربری") },
                text = { Text("آیا مطمئن هستید که می‌خواهید خارج شوید؟") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                TokenManager.clearToken()
                            }
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("خروج")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("انصراف")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    editable: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (editable) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

