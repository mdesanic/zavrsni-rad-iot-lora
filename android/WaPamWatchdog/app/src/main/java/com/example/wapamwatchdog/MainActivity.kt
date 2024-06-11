package com.example.wapamwatchdog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var locationsAdapter: LocationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        locationsAdapter = LocationsAdapter(emptyList())
        recyclerView.adapter = locationsAdapter

        fetchLocationsAndDisplay()
    }

    private fun fetchLocationsAndDisplay() {
        val fetchService = FetchService()
        CoroutineScope(Dispatchers.Main).launch {
            val locations = withContext(Dispatchers.IO) {
                fetchService.fetchLocations()
            }
            updateRecyclerView(locations)
        }
    }

    private fun updateRecyclerView(locations: List<Location>) {
        val locationNames = locations.map { it.location_name }
        locationsAdapter = LocationsAdapter(locationNames)
        recyclerView.adapter = locationsAdapter
    }
}
