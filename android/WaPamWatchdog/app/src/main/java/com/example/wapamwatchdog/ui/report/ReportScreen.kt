package com.example.wapamwatchdog.ui.historyscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    sensorDeviceId: Long,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    nav: NavController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val reports by viewModel.reports.collectAsState()

    LaunchedEffect(sensorDeviceId, startDate, endDate) {
        viewModel.fetchReports(sensorDeviceId, startDate, endDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Displaying the sensor's name from the report data if available
                    val reportName = reports.firstOrNull()?.name ?: "Report"
                    Text("$reportName for device $sensorDeviceId")
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Date Range: ${startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))} - ${endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text("Loading report data...")
                    }
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Check if reports list is empty or if the first report has no readings
                reports.isEmpty() || reports.firstOrNull()?.readings?.isEmpty() == true -> {
                    Text(
                        text = "No data found for the selected date range.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    val report = reports.first()

                    val groupedReadings = report.readings
                        .filter { it.recordedAt != null }
                        .groupBy { it.recordedAt!!.toLocalDate() }
                        .toSortedMap()

                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        groupedReadings.forEach { (date, readingsForDay) ->
                            item(key = "date_${date}") {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(readingsForDay.sortedBy { it.recordedAt }, key = { it.recordedAt?.toString() ?: "reading_${it.temperature}_${System.identityHashCode(it)}" }) { reading ->
                                // Unique key for each reading item
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = reading.recordedAt?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "N/A",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${String.format("%.2f", reading.temperature)}°C",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            item(key = "spacer_${date}") {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}