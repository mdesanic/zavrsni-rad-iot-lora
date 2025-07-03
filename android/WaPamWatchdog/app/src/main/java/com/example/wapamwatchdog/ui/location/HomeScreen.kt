// ui/location/HomeScreen.kt
package com.example.wapamwatchdog.ui.location

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wapamwatchdog.domain.model.Location
import com.example.wapamwatchdog.domain.model.Transmitter
import com.example.wapamwatchdog.ui.components.AssignTransmitterDialog
import com.example.wapamwatchdog.ui.components.LocationCard
import com.example.wapamwatchdog.ui.components.TransmitterCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val unassignedTransmitters by viewModel.unassignedTransmitters.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedLocationId by remember { mutableStateOf<Long?>(null) }
    val expandedLocations = remember { mutableStateMapOf<Long, Boolean>() }

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.loadUnassignedTransmitters()
    }

    // Location creation dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                newName = ""
                newDescription = ""
            },
            title = { Text("Add Location") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Location name*") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Description (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createLocation(newName, newDescription.takeIf { it.isNotBlank() })
                            showCreateDialog = false
                            newName = ""
                            newDescription = ""
                        }
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newName = ""
                    newDescription = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Transmitter assignment dialog
    if (showAssignDialog && selectedLocationId != null) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Transmitter") },
            text = {
                Column {
                    if (unassignedTransmitters.isEmpty()) {
                        Text("No unassigned transmitters available")
                    } else {
                        LazyColumn(Modifier.heightIn(max = 400.dp)) {
                            items(unassignedTransmitters, key = { it.id }) { transmitter ->
                                TransmitterCard(
                                    transmitter = transmitter,
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.assignTransmitter(transmitter.id, selectedLocationId!!)
                                            showAssignDialog = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Locations") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { padding ->
        when (state) {
            is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as HomeUiState.Error).message)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshAllData() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is HomeUiState.Success -> {
                val locations = (state as HomeUiState.Success).locations

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(locations, key = { it.id }) { location ->
                        val isExpanded = expandedLocations[location.id] ?: false
                        val transmitters = viewModel.transmittersByLocation[location.id] ?: emptyList()
                        val isLoading = isExpanded && transmitters.isEmpty()

                        LocationCard(
                            location = location,
                            transmitters = transmitters,
                            isExpanded = isExpanded,
                            isLoading = isLoading,
                            onExpandToggle = {
                                expandedLocations[location.id] = !isExpanded
                                if (!isExpanded) {
                                    coroutineScope.launch {
                                        viewModel.loadTransmittersForLocation(location.id)
                                    }
                                }
                            },
                            onAssignClick = {
                                selectedLocationId = location.id
                                showAssignDialog = true
                            },
                            onTransmitterClick = { transmitterId ->
                                navController.navigate("tx/$transmitterId")
                            }
                        )
                    }
                }
            }
        }
    }
}