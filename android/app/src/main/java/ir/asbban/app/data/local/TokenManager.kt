package ir.asbban.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "asb_ban_prefs")

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    suspend fun saveToken(token: String) {
        context?.dataStore?.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
    
    suspend fun saveUserId(userId: String) {
        context?.dataStore?.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }
    
    fun getToken(): String? {
        return context?.let { ctx ->
            runBlocking {
                try {
                    ctx.dataStore.data.first()[TOKEN_KEY]
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    suspend fun clearToken() {
        context?.dataStore?.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }
    
    fun getUserId(): String? {
        return context?.let { ctx ->
            runBlocking {
                try {
                    ctx.dataStore.data.first()[USER_ID_KEY]
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}

