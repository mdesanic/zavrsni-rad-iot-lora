package com.example.wapamwatchdog

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchService {

    private val client: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    suspend fun fetchLocations(): List<Location> {
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/database/locations")
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    val objectMapper = jacksonObjectMapper()

                    // Deserialize the JSON response into a list of Location objects
                    val locations: List<Location> = objectMapper.readValue(responseBody)

                    locations // Return the list of locations
                } else {
                    emptyList()
                }
            }
        }
    }
}
