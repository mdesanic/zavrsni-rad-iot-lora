package com.example.wapamwatchdog.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerialName("created_at") val createdAt: String?
)

@Serializable
data class TransmittersResponse(
    val transmitters: List<TransmitterDto>
)

@Serializable
data class TransmitterDto(
    val id: Long,
    val name: String,
    @SerialName("transmitter_mac_address") val mac: String,
    @SerialName("location_id") val locationId: Long?,
    @SerialName("location_name") val locationName: String? = null,
    val sensorDevices: List<SensorDeviceDto> = emptyList()
)

@Serializable
data class SensorDeviceDto(
    val id: Long,
    val name: String,
    @SerialName("sensor_device_mac_address") val mac: String,
    val sensors: List<SensorDto> = emptyList()
)

@Serializable
data class SensorDto(
    val id: Long,
    val name: String,
    @SerialName("sensor_address") val sensorAddress: String,
    @SerialName("latest_reading") val latestReading: ReadingDto? = null,
    @SerialName("depth_meters") val depthMeters: Float? = null
)

@Serializable
data class ReadingDto(
    val temperature: Double,
    @SerialName("recorded_at") val recordedAt: String
)


@Serializable
data class CreatedResponse(val locationId: Long)

@Serializable
data class LatestReadingsResponse(
    val sensors: List<SensorDto>
)

@Serializable
data class ReportDto(
    val sensorId: Long,
    val name: String,
    val readings: List<ReadingDto>
)

@Serializable
data class ReportResponseDto(
    @SerialName("report") val reports: List<ReportDto>
)