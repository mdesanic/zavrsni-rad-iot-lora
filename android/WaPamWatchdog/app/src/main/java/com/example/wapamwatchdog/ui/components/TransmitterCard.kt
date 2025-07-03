package com.example.wapamwatchdog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wapamwatchdog.domain.model.Transmitter

@Composable
fun TransmitterCard(
    transmitter: Transmitter,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(120.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(transmitter.name, style = MaterialTheme.typography.titleMedium)
            Text("MAC: ${transmitter.macAddress}")
        }
    }
}