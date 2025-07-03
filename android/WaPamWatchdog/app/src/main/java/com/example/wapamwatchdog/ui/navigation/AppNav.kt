package com.example.wapamwatchdog.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.wapamwatchdog.ui.location.HomeScreen
import com.example.wapamwatchdog.ui.transmitterscreen.TransmitterScreen
import com.example.wapamwatchdog.ui.historyscreen.ReportScreen

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.os.Build
import androidx.annotation.RequiresApi

object Destinations {
    const val HOME = "home"
    const val TX = "tx/{id}"
    const val REPORT = "report_screen/{sensorDeviceId}?from={from}&to={to}"
    const val LOCATION = "location/{id}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(nav: NavHostController, modifier: Modifier = Modifier) {
    NavHost(nav, startDestination = Destinations.HOME, modifier = modifier) {
        composable(Destinations.HOME) { HomeScreen(nav) }

        composable(
            route = Destinations.TX,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { back ->
            val id = back.arguments?.getLong("id") ?: -1L
            TransmitterScreen(id, nav)
        }

        composable(
            route = Destinations.REPORT,
            arguments = listOf(
                navArgument("sensorDeviceId") { type = NavType.LongType },
                navArgument("from") { type = NavType.StringType },
                navArgument("to") { type = NavType.StringType }
            )
        ) { back ->
            val sensorDeviceId = back.arguments?.getLong("sensorDeviceId")
            val fromDateTimeString = back.arguments?.getString("from")
            val toDateTimeString = back.arguments?.getString("to")

            if (sensorDeviceId != null && fromDateTimeString != null && toDateTimeString != null) {
                val fromDateTime = LocalDateTime.parse(fromDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                val toDateTime = LocalDateTime.parse(toDateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

                ReportScreen(
                    sensorDeviceId = sensorDeviceId,
                    startDate = fromDateTime,
                    endDate = toDateTime,
                    nav = nav
                )
            } else {
                Text("Error: Report arguments missing for sensorDeviceId: $sensorDeviceId, from: $fromDateTimeString, to: $toDateTimeString")
            }
        }

        composable(
            route = Destinations.LOCATION,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { back ->
            val id = back.arguments?.getLong("id") ?: -1L
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Location Detail for ID: $id")
            }
        }
    }
}