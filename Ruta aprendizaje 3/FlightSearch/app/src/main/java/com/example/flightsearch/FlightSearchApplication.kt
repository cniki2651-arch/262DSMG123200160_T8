package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.AppContainer

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
