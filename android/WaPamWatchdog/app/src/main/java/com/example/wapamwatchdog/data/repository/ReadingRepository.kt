package com.example.wapamwatchdog.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.wapamwatchdog.domain.model.Report
import com.example.wapamwatchdog.domain.model.Sensor
import com.example.wapamwatchdog.network.ApiService
import com.example.wapamwatchdog.network.UpdateSensorRequest // Import the DTO
import com.example.wapamwatchdog.network.toDomain
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadingRepository @Inject constructor(
    private val apiService: ApiService
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLatestReadingsForSensorDevice(sensorDeviceId: Long): List<Sensor> {
        return try {
            val response = apiService.getLatestReadingsForSensorDevice(sensorDeviceId)
            response.sensors.map { it.toDomain() }
        } catch (e: Exception) {
            println("Error fetching latest readings for sensor device $sensorDeviceId: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateSensorDetails(id: Long, newName: String?, newDepth: Float?) {
        try {
            apiService.updateSensor(id, UpdateSensorRequest(name = newName, depthMeters = newDepth))
        } catch (e: Exception) {
            throw ReadingRepositoryException("Failed to update sensor details", e)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getGroupedReadings(
        sensorDeviceId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Report> {
        val fromString = from.atZone(ZoneOffset.UTC).toInstant().toString()
        val toString = to.atZone(ZoneOffset.UTC).toInstant().toString()

        return apiService.getGroupedReadings(sensorDeviceId, fromString, toString)
            .reports //
            .map { it.toDomain() }
    }

}

class ReadingRepositoryException(message: String, cause: Throwable?) :
    Exception(message, cause)