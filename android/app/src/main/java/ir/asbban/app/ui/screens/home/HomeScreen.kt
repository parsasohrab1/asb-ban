package ir.asbban.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اسب بان") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                    label = { Text("خانه") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Article, contentDescription = "مقالات") },
                    label = { Text("مقالات") },
                    selected = false,
                    onClick = { navController.navigate("blog") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "فروشگاه") },
                    label = { Text("فروشگاه") },
                    selected = false,
                    onClick = { navController.navigate("shop") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MedicalServices, contentDescription = "خدمات") },
                    label = { Text("خدمات") },
                    selected = false,
                    onClick = { navController.navigate("services") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "مسابقات") },
                    label = { Text("مسابقات") },
                    selected = false,
                    onClick = { navController.navigate("competitions") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Hero Section
            Text(
                text = "پلتفرم جامع اطلاعات اسب",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "مرجع کامل اطلاعات، خدمات و فروشگاه آنلاین برای علاقه‌مندان به اسب",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Feature Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    title = "مقالات",
                    icon = Icons.Default.Article,
                    onClick = { navController.navigate("blog") },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = "فروشگاه",
                    icon = Icons.Default.ShoppingCart,
                    onClick = { navController.navigate("shop") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    title = "خدمات",
                    icon = Icons.Default.MedicalServices,
                    onClick = { navController.navigate("services") },
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = "مسابقات",
                    icon = Icons.Default.Event,
                    onClick = { navController.navigate("competitions") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

