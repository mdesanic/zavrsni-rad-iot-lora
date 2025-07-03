package com.example.wapamwatchdog.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.wapamwatchdog.domain.model.Transmitter
import com.example.wapamwatchdog.network.ApiService
import com.example.wapamwatchdog.network.UpdateSensorDeviceRequest
import com.example.wapamwatchdog.network.UpdateTransmitterRequest
import com.example.wapamwatchdog.network.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransmitterRepository @Inject constructor(
    private val apiService: ApiService,
    private val locationRepository: LocationRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTransmittersForLocation(locationId: Long): List<Transmitter> {
        return try {
            val locationName = try {
                locationRepository.getLocation(locationId).name
            } catch (e: Exception) {
                null
            }
            apiService.getTransmittersForLocation(locationId)
                .transmitters
                .map { it.toDomain(locationName) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllUnassignedTransmitters(): List<Transmitter> {
        return try {
            apiService.getAllUnassignedTransmitters()
                .map { it.toDomain(null) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun assignTransmitterToLocation(transmitterId: Long, locationId: Long?) {
        apiService.updateTransmitter(
            transmitterId,
            UpdateTransmitterRequest(locationId = locationId)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTransmitter(id: Long): Transmitter {
        val transmitterDto = apiService.getTransmitter(id)
        val locationName = if (transmitterDto.locationId != null && transmitterDto.locationId != -1L) {
            try {
                locationRepository.getLocation(transmitterDto.locationId).name
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
        return transmitterDto.toDomain(locationName)
    }

    suspend fun updateTransmitterName(id: Long, newName: String) {
        apiService.updateTransmitter(id, UpdateTransmitterRequest(name = newName))
    }

    suspend fun updateSensorDeviceName(id: Long, newName: String) {
        try {
            apiService.updateSensorDevice(id, UpdateSensorDeviceRequest(name = newName))
        } catch (e: Exception) {
            throw TransmitterRepositoryException("Failed to update sensor device name", e)
        }
    }
}

class TransmitterRepositoryException(message: String, cause: Throwable?) :
    Exception(message, cause)