package ir.asbban.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.asbban.app.ui.screens.home.HomeScreen
import ir.asbban.app.ui.screens.blog.BlogScreen
import ir.asbban.app.ui.screens.shop.ShopScreen
import ir.asbban.app.ui.screens.shop.ProductDetailScreen
import ir.asbban.app.ui.screens.shop.CartScreen
import ir.asbban.app.ui.screens.shop.CheckoutScreen
import ir.asbban.app.ui.screens.services.ServicesScreen
import ir.asbban.app.ui.screens.services.MapScreenNeshan
import ir.asbban.app.ui.screens.services.BookingScreen
import ir.asbban.app.ui.screens.competitions.CompetitionsScreen
import ir.asbban.app.ui.screens.competitions.CompetitionDetailScreen
import ir.asbban.app.ui.screens.auth.LoginScreen
import ir.asbban.app.ui.screens.profile.ProfileScreen
import ir.asbban.app.ui.screens.settings.SettingsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.material3.Text

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
                    navController.navigate("services/booking/veterinarian/$providerId")
                }
            )
        }
        composable(
            route = "services/booking/{serviceType}/{providerId}",
            arguments = listOf(
                navArgument("serviceType") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val serviceType = backStackEntry.arguments?.getString("serviceType") ?: ""
            val providerId = backStackEntry.arguments?.getInt("providerId") ?: 0
            BookingScreen(
                navController = navController,
                serviceType = serviceType,
                providerId = providerId
            )
        }
        composable("competitions") {
            CompetitionsScreen(navController = navController)
        }
        composable(
            route = "competitions/{slug}",
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: ""
            CompetitionDetailScreen(
                navController = navController,
                competitionSlug = slug
            )
        }
        composable(
            route = "shop/product/{slug}",
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: ""
            ProductDetailScreen(
                navController = navController,
                productSlug = slug
            )
        }
        composable("cart") {
            CartScreen(navController = navController)
        }
        composable("checkout") {
            CheckoutScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("profile/orders") {
            // TODO: OrdersScreen
            Text("صفحه سفارشات")
        }
        composable("profile/bookings") {
            // TODO: BookingsScreen
            Text("صفحه رزروها")
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}

