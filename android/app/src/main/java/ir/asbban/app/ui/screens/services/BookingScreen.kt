package ir.asbban.app.ui.screens.services

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
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.DatePickerState
import ir.asbban.app.data.model.BookingRequest
import ir.asbban.app.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    navController: NavController,
    serviceType: String,
    providerId: Int
) {
    val scope = rememberCoroutineScope()
    var provider by remember { mutableStateOf<Any?>(null) }
    var loading by remember { mutableStateOf(true) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<String>("") }
    var description by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(providerId) {
        try {
            val response = if (serviceType == "veterinarian") {
                RetrofitClient.apiService.getVeterinarian(providerId)
            } else {
                RetrofitClient.apiService.getTransporter(providerId)
            }
            if (response.isSuccessful && response.body() != null) {
                provider = response.body()
            } else {
                error = "ارائه‌دهنده خدمات یافت نشد"
            }
        } catch (e: Exception) {
            error = "خطا در بارگذاری اطلاعات: ${e.message}"
        } finally {
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("رزرو خدمات") },
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
            provider != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Provider Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (serviceType == "veterinarian") {
                                    (provider as ir.asbban.app.data.model.Veterinarian).full_name
                                } else {
                                    (provider as ir.asbban.app.data.model.Transporter).contact_name
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (serviceType == "veterinarian") {
                                val vet = provider as ir.asbban.app.data.model.Veterinarian
                                if (!vet.specialization.isNullOrBlank()) {
                                    Text(
                                        text = "تخصص: ${vet.specialization}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    // Date Selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "تاریخ رزرو",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedDate != null) {
                                        SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR"))
                                            .format(Date(selectedDate!!))
                                    } else {
                                        "انتخاب تاریخ"
                                    }
                                )
                            }
                        }
                    }

                    // Time Selection
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ساعت رزرو",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = selectedTime,
                                onValueChange = { selectedTime = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("مثال: 14:00") },
                                leadingIcon = {
                                    Icon(Icons.Default.Schedule, contentDescription = null)
                                }
                            )
                        }
                    }

                    // Description
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "توضیحات (اختیاری)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("توضیحات اضافی...") },
                                minLines = 3,
                                maxLines = 5
                            )
                        }
                    }

                    // Error Message
                    if (error != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = error!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedDate == null) {
                                error = "لطفاً تاریخ را انتخاب کنید"
                                return@Button
                            }
                            if (selectedTime.isBlank()) {
                                error = "لطفاً ساعت را وارد کنید"
                                return@Button
                            }

                            scope.launch {
                                submitting = true
                                error = null
                                try {
                                    val bookingDateTime = "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDate!!))}T${selectedTime}:00"
                                    val bookingRequest = BookingRequest(
                                        service_type = serviceType,
                                        service_provider_id = providerId,
                                        booking_date = bookingDateTime,
                                        description = description.ifBlank { null }
                                    )

                                    val response = RetrofitClient.apiService.createBooking(bookingRequest)
                                    if (response.isSuccessful && response.body() != null) {
                                        navController.navigate("profile/bookings") {
                                            popUpTo("services") { inclusive = false }
                                        }
                                    } else {
                                        error = "خطا در ثبت رزرو"
                                    }
                                } catch (e: Exception) {
                                    error = "خطا: ${e.message}"
                                } finally {
                                    submitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !submitting && selectedDate != null && selectedTime.isNotBlank()
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ثبت رزرو")
                        }
                    }
                }

                // Date Picker Dialog
                if (showDatePicker) {
                    DatePickerDialog(
                        onDateSelected = { date ->
                            selectedDate = date
                            showDatePicker = false
                        },
                        onDismiss = { showDatePicker = false },
                        datePickerState = datePickerState
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    datePickerState: DatePickerState
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it) }
            }) {
                Text("تایید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

