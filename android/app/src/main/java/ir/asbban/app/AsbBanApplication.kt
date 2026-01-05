package ir.asbban.app

import android.app.Application
import coil.ImageLoader
import ir.asbban.app.data.local.SettingsManager
import ir.asbban.app.data.local.TokenManager
import ir.asbban.app.data.local.database.AppDatabase
import ir.asbban.app.utils.ImageCacheConfig
import ir.asbban.app.work.WorkManagerInitializer

class AsbBanApplication : Application() {
    // ImageLoader instance for Coil
    val imageLoader: ImageLoader by lazy {
        ImageCacheConfig.createImageLoader(this)
    }
    
    // Database instance
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize managers
        TokenManager.init(this)
        SettingsManager.init(this)
        
        // Initialize WorkManager for background tasks
        WorkManagerInitializer.initialize(this)
        
        // Clean old cached data (older than 7 days)
        cleanupOldCache()
    }
    
    private fun cleanupOldCache() {
        // This will run in background
        Thread {
            try {
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                database.productDao().deleteOldProducts(sevenDaysAgo)
                database.blogPostDao().deleteOldPosts(sevenDaysAgo)
                database.competitionDao().deleteOldCompetitions(sevenDaysAgo)
            } catch (e: Exception) {
                // Ignore errors in cleanup
            }
        }.start()
    }
}

