package com.example.wapamwatchdog.domain.model

data class Location(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: String? = null,
    val transmitters: List<Transmitter> = emptyList(),
    var isExpanded: Boolean = false
)