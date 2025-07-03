package com.example.wapamwatchdog.domain.model

data class Report(
    val sensorId: Long,
    val name: String,
    val readings: List<Reading>
)