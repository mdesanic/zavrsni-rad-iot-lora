package com.example.wapamwatchdog.domain.model

import java.time.LocalDateTime

data class Reading(
    val temperature: Double,
    val recordedAt: LocalDateTime?
)