// repository/LocationRepository.kt
package com.example.wapamwatchdog.data.repository

import com.example.wapamwatchdog.domain.model.Location
import com.example.wapamwatchdog.network.ApiService
import com.example.wapamwatchdog.network.CreateLocationRequest
import com.example.wapamwatchdog.network.UpdateLocationRequest
import com.example.wapamwatchdog.network.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllLocations(): List<Location> {
        return apiService.getLocations()
            .locations
            .map { it.toDomain() }
    }

    suspend fun getLocation(locationId: Long): Location {
        return apiService.getLocation(locationId).toDomain()
    }

    suspend fun createLocation(name: String, description: String?): Long {
        return try {
            apiService.createLocation(
                CreateLocationRequest(name, description)
            ).locationId
        } catch (e: Exception) {
            throw LocationRepositoryException("Failed to create location", e)
        }
    }

    suspend fun updateLocation(id: Long, name: String?, description: String?) {
        return try {
            apiService.updateLocation(
                id,
                UpdateLocationRequest(name, description)
            )
        } catch (e: Exception) {
            throw LocationRepositoryException("Failed to update location", e)
        }
    }
}

class LocationRepositoryException(message: String, cause: Throwable?) :
    Exception(message, cause)