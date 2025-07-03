package com.example.wapamwatchdog.ui.historyscreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wapamwatchdog.data.repository.ReadingRepository
import com.example.wapamwatchdog.domain.model.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val readingRepository: ReadingRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchReports(
        sensorDeviceId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _reports.value = readingRepository.getGroupedReadings(
                    sensorDeviceId,
                    startDate,
                    endDate
                )
            } catch (e: Exception) {
                _error.value = "Failed to load reports: ${e.message}"
                _reports.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}