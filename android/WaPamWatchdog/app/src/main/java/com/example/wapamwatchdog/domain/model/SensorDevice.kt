package com.example.wapamwatchdog.domain.model

data class SensorDevice(
    val id: Long,
    val name: String,
    val macAddress: String,
    val sensors: List<Sensor> = emptyList()
)