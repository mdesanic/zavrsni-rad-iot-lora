package com.example.wapamwatchdog.domain.model

data class Transmitter(
    val id: Long,
    val name: String,
    val macAddress: String,
    val locationId: Long,
    val locationName: String?,
    val sensorDevices: List<SensorDevice> = emptyList()
)