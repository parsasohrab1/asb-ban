package ir.asbban.app.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServiceProvider(
    val id: Int,
    val full_name: String?,
    val contact_name: String?,
    val company_name: String?,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val rating: Double,
    val specialization: String? = null,
    val distance: Double? = null
)

class ServicesMapViewModel : ViewModel() {
    private val _providers = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val providers: StateFlow<List<ServiceProvider>> = _providers.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    fun loadProviders(
        latitude: Double,
        longitude: Double,
        radius: Int,
        serviceType: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = if (serviceType == "veterinarian") {
                    RetrofitClient.apiService.getVeterinarians(
                        latitude = latitude.toString(),
                        longitude = longitude.toString(),
                        radius = radius.toString()
                    )
                } else {
                    RetrofitClient.apiService.getTransporters(
                        latitude = latitude.toString(),
                        longitude = longitude.toString(),
                        radius = radius.toString()
                    )
                }
                
                if (response.isSuccessful) {
                    val data = response.body()?.data ?: emptyList()
                    _providers.value = data.map { item ->
                        ServiceProvider(
                            id = item.id,
                            full_name = item.full_name,
                            contact_name = item.contact_name,
                            company_name = item.company_name,
                            latitude = item.latitude,
                            longitude = item.longitude,
                            phone = item.phone,
                            rating = item.rating,
                            specialization = item.specialization,
                            distance = item.distance
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
                _providers.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}

