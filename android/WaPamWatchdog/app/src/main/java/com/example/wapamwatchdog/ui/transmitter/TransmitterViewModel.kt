package com.example.wapamwatchdog.ui.transmitterscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapamwatchdog.data.repository.ReadingRepository
import com.example.wapamwatchdog.data.repository.TransmitterRepository
import com.example.wapamwatchdog.domain.model.Sensor
import com.example.wapamwatchdog.domain.model.SensorDevice
import com.example.wapamwatchdog.domain.model.Transmitter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TransmitterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transmitterRepository: TransmitterRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {

    private val transmitterId: Long = savedStateHandle.get<Long>("id")
        ?: throw IllegalArgumentException("Transmitter ID missing from navigation arguments")

    private val _transmitter = MutableStateFlow<Transmitter?>(null)
    val transmitter: StateFlow<Transmitter?> = _transmitter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _latestSensorReadings = MutableStateFlow<Map<Long, List<Sensor>>>(emptyMap())
    val latestSensorReadings: StateFlow<Map<Long, List<Sensor>>> = _latestSensorReadings.asStateFlow()

    private val _editableTransmitterName = MutableStateFlow("")
    val editableTransmitterName: StateFlow<String> = _editableTransmitterName.asStateFlow()

    private val _editableSensorDeviceNames = MutableStateFlow<Map<Long, String>>(emptyMap())
    val editableSensorDeviceNames: StateFlow<Map<Long, String>> = _editableSensorDeviceNames.asStateFlow()

    private val _editableSensorNames = MutableStateFlow<Map<Long, String>>(emptyMap())
    val editableSensorNames: StateFlow<Map<Long, String>> = _editableSensorNames.asStateFlow()

    private val _editableSensorDepths = MutableStateFlow<Map<Long, String>>(emptyMap())
    val editableSensorDepths: StateFlow<Map<Long, String>> = _editableSensorDepths.asStateFlow()

    init {
        fetchTransmitterDetails()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTransmitterDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val domainTransmitter = transmitterRepository.getTransmitter(transmitterId)
                _transmitter.value = domainTransmitter
                _editableTransmitterName.value = domainTransmitter.name

                val initialDeviceNames = mutableMapOf<Long, String>()
                val initialSensorNames = mutableMapOf<Long, String>()
                val initialSensorDepths = mutableMapOf<Long, String>()

                domainTransmitter.sensorDevices.forEach { sensorDevice ->
                    initialDeviceNames[sensorDevice.id] = sensorDevice.name
                    sensorDevice.sensors.forEach { sensor ->
                        initialSensorNames[sensor.id] = sensor.name
                        initialSensorDepths[sensor.id] = sensor.depthMeters?.toString() ?: ""
                    }
                    _fetchAndStoreLatestReadingsForDevice(sensorDevice.id)
                }
                _editableSensorDeviceNames.value = initialDeviceNames
                _editableSensorNames.value = initialSensorNames
                _editableSensorDepths.value = initialSensorDepths


            } catch (e: IOException) {
                _error.value = "Network/Data Error: ${e.message}"
                _transmitter.value = null
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred: ${e.message}"
                _transmitter.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun _fetchAndStoreLatestReadingsForDevice(sensorDeviceId: Long) {
        viewModelScope.launch {
            try {
                val sensors = readingRepository.getLatestReadingsForSensorDevice(sensorDeviceId)
                _latestSensorReadings.update { currentMap ->
                    currentMap.toMutableMap().apply {
                        put(sensorDeviceId, sensors)
                    }
                }
            } catch (e: Exception) {
                println("Failed to fetch latest readings for sensor device $sensorDeviceId: ${e.message}")
            }
        }
    }

    fun onTransmitterNameChange(newName: String) {
        _editableTransmitterName.value = newName
    }

    fun onSensorDeviceNameChange(sensorDeviceId: Long, newName: String) {
        _editableSensorDeviceNames.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(sensorDeviceId, newName)
            }
        }
    }

    fun onSensorNameChange(sensorId: Long, newName: String) {
        _editableSensorNames.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(sensorId, newName)
            }
        }
    }

    fun onSensorDepthChange(sensorId: Long, newDepth: String) {
        _editableSensorDepths.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(sensorId, newDepth)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveChanges() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _transmitter.value?.let { currentTransmitter ->
                    val newName = _editableTransmitterName.value
                    if (newName != currentTransmitter.name) {
                        transmitterRepository.updateTransmitterName(currentTransmitter.id, newName)
                        _transmitter.value = currentTransmitter.copy(name = newName)
                    }
                }

                _transmitter.value?.sensorDevices?.forEach { sensorDevice ->
                    val newName = _editableSensorDeviceNames.value[sensorDevice.id]
                    if (newName != null && newName != sensorDevice.name) {
                        transmitterRepository.updateSensorDeviceName(sensorDevice.id, newName)
                    }
                }

                _transmitter.value?.sensorDevices?.flatMap { it.sensors }?.forEach { sensor ->
                    val newSensorName = _editableSensorNames.value[sensor.id]
                    val newSensorDepthString = _editableSensorDepths.value[sensor.id]

                    val parsedDepth = newSensorDepthString?.toFloatOrNull()

                    val nameChanged = (newSensorName != null && newSensorName != sensor.name)
                    val depthChanged = (parsedDepth != null && parsedDepth != sensor.depthMeters) ||
                            (parsedDepth == null && sensor.depthMeters != null)

                    if (nameChanged || depthChanged) {
                        readingRepository.updateSensorDetails(
                            sensor.id,
                            newSensorName ?: sensor.name,
                            parsedDepth
                        )
                    }
                }

                fetchTransmitterDetails()

            } catch (e: IOException) {
                _error.value = "Network/Data Error during save: ${e.message}"
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred during save: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}