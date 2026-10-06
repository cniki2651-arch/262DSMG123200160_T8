package com.example.flightsearch.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.FlightSearchApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app = flightSearchApplication()
            FlightSearchViewModel(
                app.container.flightRepository,
                app.container.userPreferencesRepository
            )
        }
    }
}

fun CreationExtras.flightSearchApplication(): FlightSearchApplication =
    this[APPLICATION_KEY] as FlightSearchApplication
