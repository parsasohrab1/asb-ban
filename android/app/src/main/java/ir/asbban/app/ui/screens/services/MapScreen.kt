package ir.asbban.app.ui.screens.services

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    serviceType: String = "veterinarian",
    onProviderSelect: (Int) -> Unit = {},
    viewModel: ServicesMapViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedServiceType by remember { mutableStateOf(serviceType) }
    var radius by remember { mutableStateOf(50) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val providers = viewModel.providers.collectAsState().value
    val loading = viewModel.loading.collectAsState().value
    
    // Request location permission
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            // In production, use rememberLauncherForActivityResult
            // For now, default to Tehran
            userLocation = LatLng(35.6892, 51.3890)
        } else {
            try {
                val locationResult = fusedLocationClient.lastLocation
                locationResult.addOnSuccessListener { location: Location? ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        viewModel.loadProviders(
                            it.latitude,
                            it.longitude,
                            radius,
                            selectedServiceType
                        )
                    } ?: run {
                        // Default to Tehran if location not available
                        userLocation = LatLng(35.6892, 51.3890)
                    }
                }
            } catch (e: SecurityException) {
                userLocation = LatLng(35.6892, 51.3890)
            }
        }
    }
    
    // Load providers when service type or radius changes
    LaunchedEffect(selectedServiceType, radius, userLocation) {
        userLocation?.let {
            viewModel.loadProviders(it.latitude, it.longitude, radius, selectedServiceType)
        }
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(35.6892, 51.3890),
            12f
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("انتخاب روی نقشه") },
                navigationIcon = {
                    IconButton(onClick = { /* Navigate back */ }) {
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
        ) {
            // Service Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedServiceType == "veterinarian",
                    onClick = { selectedServiceType = "veterinarian" },
                    label = { Text("دامپزشکان") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedServiceType == "transporter",
                    onClick = { selectedServiceType = "transporter" },
                    label = { Text("اسب‌کش‌ها") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Radius Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("فاصله:")
                FilterChip(
                    selected = radius == 10,
                    onClick = { radius = 10 },
                    label = { Text("10 km") }
                )
                FilterChip(
                    selected = radius == 25,
                    onClick = { radius = 25 },
                    label = { Text("25 km") }
                )
                FilterChip(
                    selected = radius == 50,
                    onClick = { radius = 50 },
                    label = { Text("50 km") }
                )
                FilterChip(
                    selected = radius == 100,
                    onClick = { radius = 100 },
                    label = { Text("100 km") }
                )
            }
            
            // Map
            Box(modifier = Modifier.weight(1f)) {
                if (userLocation != null) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            isMyLocationEnabled = hasLocationPermission
                        ),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = hasLocationPermission,
                            zoomControlsEnabled = true
                        )
                    ) {
                        // User location marker
                        Marker(
                            state = MarkerState(position = userLocation!!),
                            title = "موقعیت شما",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                        
                        // Provider markers
                        providers.forEach { provider ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        provider.latitude,
                                        provider.longitude
                                    )
                                ),
                                title = provider.full_name ?: provider.contact_name ?: provider.company_name,
                                snippet = "امتیاز: ${provider.rating}",
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    if (selectedServiceType == "veterinarian")
                                        BitmapDescriptorFactory.HUE_GREEN
                                    else
                                        BitmapDescriptorFactory.HUE_ORANGE
                                ),
                                onClick = {
                                    onProviderSelect(provider.id)
                                    true
                                }
                            )
                        }
                    }
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            // Provider List
            if (providers.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${providers.size} ${if (selectedServiceType == "veterinarian") "دامپزشک" else "اسب‌کش"} پیدا شد",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(providers.take(3)) { provider ->
                                ProviderListItem(
                                    provider = provider,
                                    onClick = { onProviderSelect(provider.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderListItem(
    provider: ServiceProvider,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = provider.full_name ?: provider.contact_name ?: provider.company_name ?: "",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = provider.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                provider.distance?.let {
                    Text(
                        text = "${String.format("%.1f", it)} کیلومتر",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "امتیاز",
                    tint = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%.1f", provider.rating),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

