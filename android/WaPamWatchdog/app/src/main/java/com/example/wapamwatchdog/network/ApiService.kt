package com.example.wapamwatchdog.network

import kotlinx.serialization.SerialName
import retrofit2.http.*
import kotlinx.serialization.Serializable
import retrofit2.Response

interface ApiService {
    @GET("locations")
    suspend fun getLocations(): LocationsWrapper

    @POST("location")
    suspend fun createLocation(@Body body: CreateLocationRequest): CreatedResponse

    @GET("locations/{id}")
    suspend fun getLocation(@Path("id") id: Long): LocationDto

    @PATCH("transmitters/{id}")
    suspend fun updateTransmitter(
        @Path("id") transmitterId: Long,
        @Body updates: UpdateTransmitterRequest
    ): Response<Unit>

    @GET("locations/{id}/transmitters")
    suspend fun getTransmittersForLocation(@Path("id") locationId: Long): TransmittersResponse

    @GET("transmitters")
    suspend fun getAllUnassignedTransmitters(): List<TransmitterDto>

    @PATCH("locations/{id}")
    suspend fun updateLocation(
        @Path("id") id: Long,
        @Body body: UpdateLocationRequest
    ): Unit

    @GET("transmitters/{id}")
    suspend fun getTransmitter(@Path("id") id: Long): TransmitterDto

    @GET("reports/sensor-device/{id}")
    suspend fun getReport(
        @Path("id") id: Long,
        @Query("from") from: String,
        @Query("to") to: String
    ): List<ReportDto>

    @GET("sensor-devices/{id}/latest-readings")
    suspend fun getLatestReadingsForSensorDevice(@Path("id") sensorDeviceId: Long): LatestReadingsResponse

    @PATCH("sensor-devices/{id}")
    suspend fun updateSensorDevice(
        @Path("id") id: Long,
        @Body request: UpdateSensorDeviceRequest
    ): Response<Unit>

    @PATCH("sensors/{id}")
    suspend fun updateSensor(
        @Path("id") id: Long,
        @Body request: UpdateSensorRequest
    ): Response<Unit>

    @GET("sensor-devices/{sensorDeviceId}/grouped-readings")
    suspend fun getGroupedReadings(
        @Path("sensorDeviceId") sensorDeviceId: Long,
        @Query("from") from: String,
        @Query("to") to: String
    ): ReportResponseDto
}

// Request DTOs
@Serializable
data class LocationsWrapper(
    val locations: List<LocationDto>
)

@Serializable
data class CreateLocationRequest(val name: String, val description: String? = null)

@Serializable
data class UpdateLocationRequest(val name: String? = null, val description: String? = null)

@Serializable
data class UpdateTransmitterRequest(
    val name: String? = null,
    @SerialName("location_id") val locationId: Long? = null
)

@Serializable
data class UpdateSensorDeviceRequest(
    val name: String
)

@Serializable
data class UpdateSensorRequest(
    val name: String? = null,
    @SerialName("depth_meters") val depthMeters: Float? = null
)