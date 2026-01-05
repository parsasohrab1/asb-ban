package ir.asbban.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import ir.asbban.app.data.local.TokenManager
import ir.asbban.app.ui.navigation.AppNavigation
import ir.asbban.app.ui.theme.AsbBanTheme
import coil.ImageLoader
import coil.compose.LocalImageLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Initialize TokenManager
        TokenManager.init(this)
        
        val application = application as AsbBanApplication
        val imageLoader = application.imageLoader
        
        setContent {
            AsbBanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Provide ImageLoader to Coil
                    coil.compose.LocalImageLoader.current = imageLoader
                    AppNavigation()
                }
            }
        }
    }
}

