package com.example.wapamwatchdog.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.wapamwatchdog.domain.model.*
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun LocationDto.toDomain(): Location = Location(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt
)

@RequiresApi(Build.VERSION_CODES.O)
fun TransmitterDto.toDomain(explicitLocationName: String? = null): Transmitter = Transmitter(
    id = id,
    name = name,
    macAddress = mac,
    locationId = locationId ?: -1,
    locationName = this.locationName ?: explicitLocationName,
    sensorDevices = sensorDevices.map { it.toDomain() }
)

@RequiresApi(Build.VERSION_CODES.O)
fun SensorDeviceDto.toDomain(): SensorDevice = SensorDevice(
    id = id,
    name = name,
    macAddress = mac,
    sensors = sensors.map { it.toDomain() }
)

@RequiresApi(Build.VERSION_CODES.O)
fun SensorDto.toDomain(): Sensor {
    return Sensor(
        id = this.id,
        name = this.name,
        address = this.sensorAddress,
        latestReading = this.latestReading?.toDomain(),
        depthMeters = this.depthMeters
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ReadingDto.toDomain(): Reading {
    val parsedRecordedAt: LocalDateTime? = try {
        val offsetDateTime = OffsetDateTime.parse(this.recordedAt)
        offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
    } catch (e: Exception) {
        println("ReadingDto.toDomain(): Error parsing recorded_at string '${this.recordedAt}', Error: ${e.message}")
        null
    }
    return Reading(
        temperature = this.temperature,
        recordedAt = parsedRecordedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun ReportDto.toDomain(): Report = Report(
    sensorId = sensorId,
    name = name,
    readings = readings.map { it.toDomain() }
)

fun CreatedResponse.toDomain(): Long = locationId

@RequiresApi(Build.VERSION_CODES.O)
fun TransmittersResponse.toDomain(): List<Transmitter> =
    transmitters.map { it.toDomain() }