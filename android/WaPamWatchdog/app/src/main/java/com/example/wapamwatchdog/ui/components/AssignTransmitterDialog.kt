// ui/components/AssignTransmitterDialog.kt
package com.example.wapamwatchdog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wapamwatchdog.domain.model.Location
import com.example.wapamwatchdog.domain.model.Transmitter
import com.example.wapamwatchdog.ui.location.HomeViewModel

@Composable
fun AssignTransmitterDialog(
    location: Location,
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    val unassignedTransmitters by viewModel.unassignedTransmitters.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign to ${location.name}") },
        text = {
            Column {
                if (unassignedTransmitters.isEmpty()) {
                    Text("No unassigned transmitters available")
                } else {
                    LazyColumn (
                        verticalArrangement = Arrangement.spacedBy(16.dp)

                    ){
                        items(unassignedTransmitters) { transmitter ->
                            TransmitterCard(
                                transmitter = transmitter,
                                onClick = {
                                    viewModel.assignTransmitter(transmitter.id, location.id)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}