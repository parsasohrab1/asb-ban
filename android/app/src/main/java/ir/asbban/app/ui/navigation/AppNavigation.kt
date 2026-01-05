package ir.asbban.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.asbban.app.ui.screens.home.HomeScreen
import ir.asbban.app.ui.screens.blog.BlogScreen
import ir.asbban.app.ui.screens.shop.ShopScreen
import ir.asbban.app.ui.screens.services.ServicesScreen
import ir.asbban.app.ui.screens.services.MapScreenNeshan
import ir.asbban.app.ui.screens.competitions.CompetitionsScreen
import ir.asbban.app.ui.screens.auth.LoginScreen
import ir.asbban.app.ui.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("blog") {
            BlogScreen(navController = navController)
        }
        composable("shop") {
            ShopScreen(navController = navController)
        }
        composable("services") {
            ServicesScreen(navController = navController)
        }
        composable("services/map") {
            MapScreenNeshan(
                serviceType = "veterinarian",
                onProviderSelect = { providerId ->
                    // Navigate to booking screen
                    navController.navigate("services/booking/$providerId")
                }
            )
        }
        composable("competitions") {
            CompetitionsScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}

