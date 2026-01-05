package ir.asbban.app.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerInitializer {
    fun initialize(context: Context) {
        val workManager = WorkManager.getInstance(context)
        
        // Periodic cache cleanup (every 24 hours, only when charging and on WiFi)
        val cacheCleanupConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
            .setRequiresCharging(true) // Only when charging
            .build()
        
        val cacheCleanupWork = PeriodicWorkRequestBuilder<CacheCleanupWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(cacheCleanupConstraints)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "cache_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cacheCleanupWork
        )
    }
}

