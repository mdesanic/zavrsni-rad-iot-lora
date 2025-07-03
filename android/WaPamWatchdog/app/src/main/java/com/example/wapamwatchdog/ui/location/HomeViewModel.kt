// ui/location/HomeViewModel.kt
package com.example.wapamwatchdog.ui.location

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapamwatchdog.data.repository.LocationRepository
import com.example.wapamwatchdog.data.repository.LocationRepositoryException
import com.example.wapamwatchdog.data.repository.TransmitterRepository
import com.example.wapamwatchdog.domain.model.Transmitter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val transmitterRepo: TransmitterRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    private val _transmittersByLocation = mutableStateMapOf<Long, List<Transmitter>>()
    val transmittersByLocation: SnapshotStateMap<Long, List<Transmitter>> = _transmittersByLocation

    private val _unassignedTransmitters = MutableStateFlow<List<Transmitter>>(emptyList())
    val unassignedTransmitters: StateFlow<List<Transmitter>> = _unassignedTransmitters

    init { refreshAllData() }

    fun refreshAllData() = viewModelScope.launch {
        _state.value = HomeUiState.Loading
        try {
            val locations = locationRepo.getAllLocations()
            _state.value = HomeUiState.Success(locations)
            loadUnassignedTransmitters()
        } catch (e: Exception) {
            _state.value = HomeUiState.Error(e.message ?: "Failed to load data")
        }
    }

    suspend fun loadTransmittersForLocation(locationId: Long) {
        try {
            val transmitters = transmitterRepo.getTransmittersForLocation(locationId)
            _transmittersByLocation[locationId] = transmitters
        } catch (e: Exception) {
            _transmittersByLocation[locationId] = emptyList()
        }
    }

    suspend fun loadUnassignedTransmitters() {
        try {
            _unassignedTransmitters.value = transmitterRepo.getAllUnassignedTransmitters()
        } catch (e: Exception) {
            _unassignedTransmitters.value = emptyList()
        }
    }

    fun assignTransmitter(transmitterId: Long, locationId: Long) = viewModelScope.launch {
        try {
            // 1. Perform assignment
            transmitterRepo.assignTransmitterToLocation(transmitterId, locationId)

            // 2. Force immediate UI updates
            val currentTransmitters = _transmittersByLocation[locationId] ?: emptyList()
            val newTransmitter = transmitterRepo.getTransmitter(transmitterId)

            _transmittersByLocation[locationId] = currentTransmitters + newTransmitter
            _unassignedTransmitters.value = _unassignedTransmitters.value.filter { it.id != transmitterId }

            // 3. Refresh from server to ensure consistency
            loadTransmittersForLocation(locationId)
            loadUnassignedTransmitters()
        } catch (e: Exception) {
            _state.value = HomeUiState.Error("Failed to assign transmitter")
        }
    }

    fun createLocation(name: String, description: String? = null) = viewModelScope.launch {
        try {
            locationRepo.createLocation(name, description)
            refreshAllData()
        } catch (e: Exception) {
            _state.value = HomeUiState.Error("Failed to create location")
        }
    }
}