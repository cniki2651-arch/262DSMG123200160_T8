package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class AppContainer(context: Context) {
    val flightRepository = FlightRepository(FlightDatabase.getDatabase(context).flightDao())
    val userPreferencesRepository = UserPreferencesRepository(context.dataStore)
}
