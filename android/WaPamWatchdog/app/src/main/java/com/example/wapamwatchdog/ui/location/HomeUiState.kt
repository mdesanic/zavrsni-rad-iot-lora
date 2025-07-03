package com.example.wapamwatchdog.ui.location

import com.example.wapamwatchdog.domain.model.Location

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val locations: List<Location>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
