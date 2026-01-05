package ir.asbban.app.ui.screens.competitions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ir.asbban.app.data.model.Competition
import ir.asbban.app.data.model.CompetitionResult
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionDetailScreen(
    navController: NavController,
    competitionSlug: String
) {
    val scope = rememberCoroutineScope()
    var competition by remember { mutableStateOf<Competition?>(null) }
    var results by remember { mutableStateOf<List<CompetitionResult>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showResults by remember { mutableStateOf(false) }

    LaunchedEffect(competitionSlug) {
        try {
            val compResponse = RetrofitClient.apiService.getCompetition(competitionSlug)
            if (compResponse.isSuccessful && compResponse.body() != null) {
                competition = compResponse.body()
                
                // Load results
                try {
                    val resultsResponse = RetrofitClient.apiService.getCompetitionResults(compResponse.body()!!.id)
                    if (resultsResponse.isSuccessful && resultsResponse.body() != null) {
                        results = resultsResponse.body()!!
                    }
                } catch (e: Exception) {
                    // Results might not be available yet
                }
            } else {
                error = "مسابقه یافت نشد"
            }
        } catch (e: Exception) {
            error = "خطا در بارگذاری مسابقه: ${e.message}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("جزئیات مسابقه") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("بازگشت")
                    }
                }
            }
            competition != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    val comp = competition!!

                    // Competition Image
                    if (comp.image_url != null) {
                        AsyncImage(
                            model = comp.image_url,
                            contentDescription = comp.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }

                    // Competition Info
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Title
                        Text(
                            text = comp.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Type Badge
                        if (!comp.competition_type.isNullOrBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = comp.competition_type!!,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        Divider()

                        // Location
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = comp.location,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Start Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "تاریخ شروع: ${formatDate(comp.start_date)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // End Date
                        if (!comp.end_date.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "تاریخ پایان: ${formatDate(comp.end_date)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // Registration Deadline
                        if (!comp.registration_deadline.isNullOrBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "مهلت ثبت‌نام: ${formatDate(comp.registration_deadline)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // International Badge
                        if (comp.is_international) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Public,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "مسابقه بین‌المللی",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        Divider()

                        // Description
                        if (!comp.description.isNullOrBlank()) {
                            Text(
                                text = "توضیحات",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comp.description!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Prize Info
                        if (!comp.prize_info.isNullOrBlank()) {
                            Text(
                                text = "جوایز",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comp.prize_info!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Conditions
                        if (!comp.conditions.isNullOrBlank()) {
                            Text(
                                text = "شرایط شرکت",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comp.conditions!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Results Section
                        if (results.isNotEmpty()) {
                            Divider()
                            Button(
                                onClick = { showResults = !showResults },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    if (showResults) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("نتایج مسابقه (${results.size})")
                            }

                            if (showResults) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    results.sortedBy { it.position ?: Int.MAX_VALUE }.forEach { result ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "رتبه ${result.position ?: "نامشخص"}",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (result.score != null) {
                                                        Text(
                                                            text = "امتیاز: ${result.score}",
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                }
                                                if (!result.participant_name.isNullOrBlank()) {
                                                    Text(
                                                        text = "شرکت‌کننده: ${result.participant_name}",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                if (!result.horse_name.isNullOrBlank()) {
                                                    Text(
                                                        text = "اسب: ${result.horse_name}",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                if (!result.notes.isNullOrBlank()) {
                                                    Text(
                                                        text = result.notes!!,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

