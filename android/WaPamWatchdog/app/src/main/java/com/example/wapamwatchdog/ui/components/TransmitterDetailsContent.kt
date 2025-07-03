package com.example.wapamwatchdog.ui.transmitterscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wapamwatchdog.domain.model.Sensor
import com.example.wapamwatchdog.domain.model.Transmitter
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransmitterDetailsContent(
    transmitter: Transmitter,
    latestSensorReadings: Map<Long, List<Sensor>>,
    editableTransmitterName: String,
    onTransmitterNameChange: (String) -> Unit,
    editableSensorDeviceNames: Map<Long, String>,
    onSensorDeviceNameChange: (Long, String) -> Unit,
    editableSensorNames: Map<Long, String>,
    onSensorNameChange: (Long, String) -> Unit,
    editableSensorDepths: Map<Long, String>,
    onSensorDepthChange: (Long, String) -> Unit,
    onReportClick: (sensorDeviceId: Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = editableTransmitterName,
            onValueChange = onTransmitterNameChange,
            label = { Text("Transmitter Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )
        Text(
            text = "MAC Address: ${transmitter.macAddress}",
            style = MaterialTheme.typography.bodyMedium
        )
        transmitter.locationName?.let {
            Text(
                text = "Location: $it",
                style = MaterialTheme.typography.bodyMedium
            )
        } ?: Text(
            text = "Location: Unassigned",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sensor Devices:",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (transmitter.sensorDevices.isEmpty()) {
                item {
                    Text("No sensor devices associated with this transmitter.")
                }
            } else {
                items(transmitter.sensorDevices) { sensorDevice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = editableSensorDeviceNames[sensorDevice.id] ?: sensorDevice.name,
                                onValueChange = { newName -> onSensorDeviceNameChange(sensorDevice.id, newName) },
                                label = { Text("Device Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                singleLine = true
                            )
                            Text(
                                text = "Device MAC: ${sensorDevice.macAddress}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val sensorsWithLatestData = latestSensorReadings[sensorDevice.id]

                            if (sensorsWithLatestData == null || sensorsWithLatestData.isEmpty()) {
                                Text(
                                    "Loading sensor readings...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                sensorsWithLatestData.forEach { sensor ->
                                    Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                                        OutlinedTextField(
                                            value = editableSensorNames[sensor.id] ?: sensor.name,
                                            onValueChange = { newName -> onSensorNameChange(sensor.id, newName) },
                                            label = { Text("Sensor Name") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            singleLine = true
                                        )
                                        Text(
                                            text = "Sensor Address: ${sensor.address}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        OutlinedTextField(
                                            value = editableSensorDepths[sensor.id] ?: (sensor.depthMeters?.toString() ?: ""),
                                            onValueChange = { newDepth -> onSensorDepthChange(sensor.id, newDepth) },
                                            label = { Text("Depth (meters)") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true
                                        )

                                        sensor.latestReading?.let { reading ->
                                            Text(
                                                text = "Temperature: ${String.format("%.2f", reading.temperature)}°C",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "Recorded At: ${reading.recordedAt?.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")) ?: "N/A"}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        } ?: Text(
                                            text = "Temperature: N/A (No recent reading)",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = { onReportClick(sensorDevice.id) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Show Report")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}