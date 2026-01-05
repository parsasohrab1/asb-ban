package ir.asbban.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "asb_ban_settings")

object SettingsManager {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    private val DARK_MODE_AUTO_KEY = booleanPreferencesKey("dark_mode_auto")
    private val OFFLINE_MODE_KEY = booleanPreferencesKey("offline_mode")
    
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    // Dark Mode Settings
    suspend fun setDarkMode(enabled: Boolean) {
        context?.settingsDataStore?.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
    
    fun getDarkMode(): Flow<Boolean> {
        return context?.settingsDataStore?.data?.map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        } ?: kotlinx.coroutines.flow.flowOf(false)
    }
    
    suspend fun setDarkModeAuto(enabled: Boolean) {
        context?.settingsDataStore?.edit { preferences ->
            preferences[DARK_MODE_AUTO_KEY] = enabled
        }
    }
    
    fun getDarkModeAuto(): Flow<Boolean> {
        return context?.settingsDataStore?.data?.map { preferences ->
            preferences[DARK_MODE_AUTO_KEY] ?: true
        } ?: kotlinx.coroutines.flow.flowOf(true)
    }
    
    // Offline Mode Settings
    suspend fun setOfflineMode(enabled: Boolean) {
        context?.settingsDataStore?.edit { preferences ->
            preferences[OFFLINE_MODE_KEY] = enabled
        }
    }
    
    fun getOfflineMode(): Flow<Boolean> {
        return context?.settingsDataStore?.data?.map { preferences ->
            preferences[OFFLINE_MODE_KEY] ?: false
        } ?: kotlinx.coroutines.flow.flowOf(false)
    }
    
    suspend fun isOfflineModeEnabled(): Boolean {
        return context?.let { ctx ->
            try {
                ctx.settingsDataStore.data.first()[OFFLINE_MODE_KEY] ?: false
            } catch (e: Exception) {
                false
            }
        } ?: false
    }
}

