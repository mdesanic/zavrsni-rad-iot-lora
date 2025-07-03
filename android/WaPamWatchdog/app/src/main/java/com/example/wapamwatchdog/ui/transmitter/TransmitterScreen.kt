package com.example.wapamwatchdog.ui.transmitterscreen

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransmitterScreen(
    id: Long,
    nav: NavController,
    viewModel: TransmitterViewModel = hiltViewModel()
) {
    val transmitter by viewModel.transmitter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val latestSensorReadings by viewModel.latestSensorReadings.collectAsState()

    var editableTransmitterName by remember(transmitter) { mutableStateOf(transmitter?.name ?: "") }
    val editableSensorDeviceNames = remember(transmitter) { mutableStateMapOf<Long, String>().apply {
        transmitter?.sensorDevices?.forEach { put(it.id, it.name) }
    }}
    val editableSensorNames = remember(transmitter) { mutableStateMapOf<Long, String>().apply {
        transmitter?.sensorDevices?.flatMap { it.sensors }?.forEach { put(it.id, it.name) }
    }}
    val editableSensorDepths = remember(transmitter) { mutableStateMapOf<Long, String>().apply {
        transmitter?.sensorDevices?.flatMap { it.sensors }?.forEach { put(it.id, it.depthMeters?.toString() ?: "") }
    }}

    var showReportDatePickerDialog by remember { mutableStateOf(false) }
    var selectedSensorIdForReport by remember { mutableStateOf<Long?>(null) }
    var reportStartDate by remember { mutableStateOf(LocalDateTime.now().minusDays(7).with(LocalTime.MIN)) }
    var reportEndDate by remember { mutableStateOf(LocalDateTime.now().with(LocalTime.MAX)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(transmitter?.name ?: "Loading Transmitter...") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text("Error: $error")
                transmitter != null -> {
                    TransmitterDetailsContent(
                        transmitter = transmitter!!,
                        latestSensorReadings = latestSensorReadings,
                        editableTransmitterName = editableTransmitterName,
                        onTransmitterNameChange = { editableTransmitterName = it },
                        editableSensorDeviceNames = editableSensorDeviceNames,
                        onSensorDeviceNameChange = { id, name -> editableSensorDeviceNames[id] = name },
                        editableSensorNames = editableSensorNames,
                        onSensorNameChange = { id, name -> editableSensorNames[id] = name },
                        editableSensorDepths = editableSensorDepths,
                        onSensorDepthChange = { id, depth -> editableSensorDepths[id] = depth },
                        onReportClick = { sensorDeviceId ->
                            selectedSensorIdForReport = sensorDeviceId
                            showReportDatePickerDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showReportDatePickerDialog) {
        ReportDatePickerDialog(
            initialStartDate = reportStartDate,
            initialEndDate = reportEndDate,
            onDismiss = { showReportDatePickerDialog = false },
            onDatesSelected = { start, end ->
                reportStartDate = start
                reportEndDate = end
                showReportDatePickerDialog = false

                selectedSensorIdForReport?.let { sensorDeviceId ->
                    nav.navigate("report_screen/$sensorDeviceId?from=${start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}&to=${end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDatePickerDialog(
    initialStartDate: LocalDateTime,
    initialEndDate: LocalDateTime,
    onDismiss: () -> Unit,
    onDatesSelected: (LocalDateTime, LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    var tempStartDate by remember { mutableStateOf(initialStartDate) }
    var tempEndDate by remember { mutableStateOf(initialEndDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Report Date Range") },
        text = {
            Column {
                OutlinedTextField(
                    value = tempStartDate.format(dateFormatter),
                    onValueChange = {  },
                    label = { Text("From Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDateTimePicker(context, tempStartDate) { newDateTime ->
                                tempStartDate = newDateTime
                            }
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tempEndDate.format(dateFormatter),
                    onValueChange = {  },
                    label = { Text("To Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDateTimePicker(context, tempEndDate) { newDateTime ->
                                tempEndDate = newDateTime
                            }
                        }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onDatesSelected(tempStartDate, tempEndDate) }) {
                Text("Show Report")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun showDateTimePicker(
    context: android.content.Context,
    currentDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val year = currentDateTime.year
    val month = currentDateTime.monthValue - 1
    val day = currentDateTime.dayOfMonth
    val hour = currentDateTime.hour
    val minute = currentDateTime.minute

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val timePickerDialog = android.app.TimePickerDialog(
                context,
                { _, selectedHour, selectedMinute ->
                    val newDateTime = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDayOfMonth, selectedHour, selectedMinute)
                    onDateTimeSelected(newDateTime)
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        },
        year,
        month,
        day
    )
    datePickerDialog.show()
}