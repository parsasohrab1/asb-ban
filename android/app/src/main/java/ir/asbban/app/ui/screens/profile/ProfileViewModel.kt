package ir.asbban.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.asbban.app.data.model.User
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profile = MutableStateFlow<User?>(null)
    val profile: StateFlow<User?> = _profile.asStateFlow()
    
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    
    private var originalProfile: User? = null
    
    fun loadProfile() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = RetrofitClient.apiService.getProfile()
                if (response.isSuccessful) {
                    response.body()?.data?.let { user ->
                        _profile.value = user
                        originalProfile = user
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _loading.value = false
            }
        }
    }
    
    fun updateFullName(fullName: String) {
        _profile.value = _profile.value?.copy(full_name = fullName)
    }
    
    fun updatePhone(phone: String) {
        _profile.value = _profile.value?.copy(phone = phone)
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            _profile.value?.let { user ->
                try {
                    val response = RetrofitClient.apiService.updateProfile(user)
                    if (response.isSuccessful) {
                        response.body()?.data?.let { updatedUser ->
                            _profile.value = updatedUser
                            originalProfile = updatedUser
                        }
                    }
                } catch (e: Exception) {
                    // Handle error - revert changes
                    _profile.value = originalProfile
                }
            }
        }
    }
    
    fun cancelEdit() {
        _profile.value = originalProfile
    }
}

