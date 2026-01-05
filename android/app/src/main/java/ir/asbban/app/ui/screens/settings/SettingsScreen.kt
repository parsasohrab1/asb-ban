package ir.asbban.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ir.asbban.app.data.local.SettingsManager
import ir.asbban.app.data.local.database.AppDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val darkModeAuto by viewModel.darkModeAuto.collectAsState(initial = true)
    val darkModeManual by viewModel.darkModeManual.collectAsState(initial = false)
    val offlineMode by viewModel.offlineMode.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات") },
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
            // Appearance Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ظاهر",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Dark Mode Auto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "حالت تاریک خودکار",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "پیروی از تنظیمات سیستم",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = darkModeAuto,
                            onCheckedChange = {
                                scope.launch {
                                    viewModel.setDarkModeAuto(it)
                                }
                            }
                        )
                    }
                    
                    // Dark Mode Manual (only if auto is off)
                    if (!darkModeAuto) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "حالت تاریک",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "فعال/غیرفعال کردن حالت تاریک",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = darkModeManual,
                                onCheckedChange = {
                                    scope.launch {
                                        viewModel.setDarkMode(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Data & Storage Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "داده و ذخیره‌سازی",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Offline Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "حالت آفلاین",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "ذخیره محتوا برای استفاده آفلاین",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = offlineMode,
                            onCheckedChange = {
                                scope.launch {
                                    viewModel.setOfflineMode(it)
                                }
                            }
                        )
                    }
                    
                    Divider()
                    
                    // Clear Cache
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.clearCache(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("پاک کردن کش")
                    }
                }
            }

            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "درباره",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "اسب بان",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "نسخه 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsViewModel(): SettingsViewModel {
    return androidx.lifecycle.viewmodel.compose.viewModel()
}

class SettingsViewModel : androidx.lifecycle.ViewModel() {
    val darkModeAuto = SettingsManager.getDarkModeAuto()
    val darkModeManual = SettingsManager.getDarkMode()
    val offlineMode = SettingsManager.getOfflineMode()
    
    suspend fun setDarkModeAuto(enabled: Boolean) {
        SettingsManager.setDarkModeAuto(enabled)
    }
    
    suspend fun setDarkMode(enabled: Boolean) {
        SettingsManager.setDarkMode(enabled)
    }
    
    suspend fun setOfflineMode(enabled: Boolean) {
        SettingsManager.setOfflineMode(enabled)
    }
    
    suspend fun clearCache(context: android.content.Context) {
        val database = AppDatabase.getDatabase(context.applicationContext)
        database.productDao().clearAll()
        database.blogPostDao().clearAll()
        database.competitionDao().clearAll()
    }
}

