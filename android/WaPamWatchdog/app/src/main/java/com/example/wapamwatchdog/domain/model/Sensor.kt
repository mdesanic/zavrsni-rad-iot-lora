package com.example.wapamwatchdog.domain.model

data class Sensor(
    val id: Long,
    val name: String,
    val address: String,
    val latestReading: Reading?,
    val depthMeters: Float?
)