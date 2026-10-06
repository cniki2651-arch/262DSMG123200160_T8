package com.example.flightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.Airport
import com.example.flightsearch.data.FlightRepository
import com.example.flightsearch.data.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Flight(
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String,
    val isFavorite: Boolean
)

data class FlightSearchUiState(
    val query: String = "",
    val suggestions: List<Airport> = emptyList(),
    val selected: Airport? = null,
    val flights: List<Flight> = emptyList(),
    val favorites: List<Flight> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class FlightSearchViewModel(
    private val repository: FlightRepository,
    private val preferences: UserPreferencesRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val selected = MutableStateFlow<Airport?>(null)

    private val suggestions = query.flatMapLatest {
        if (it.isBlank()) flowOf(emptyList()) else repository.searchAirports(it.trim())
    }

    private val destinations = selected.flatMapLatest {
        if (it == null) flowOf(emptyList()) else repository.getDestinations(it.iataCode)
    }

    private val favoriteFlights = repository.getFavoriteRoutes().map { routes ->
        routes.map {
            Flight(it.departureCode, it.departureName, it.destinationCode, it.destinationName, true)
        }
    }

    val uiState: StateFlow<FlightSearchUiState> = combine(
        query, selected, suggestions, destinations, favoriteFlights
    ) { q, sel, sugg, dests, favs ->
        val flights = if (sel == null) emptyList() else dests.map { dest ->
            Flight(
                sel.iataCode, sel.name, dest.iataCode, dest.name,
                favs.any { it.departureCode == sel.iataCode && it.destinationCode == dest.iataCode }
            )
        }
        FlightSearchUiState(q, sugg, sel, flights, favs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FlightSearchUiState())

    init {
        // Restaura la última búsqueda guardada en DataStore
        viewModelScope.launch { query.value = preferences.searchQuery.first() }
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
        selected.value = null
        saveQuery(newQuery)
    }

    fun onAirportSelected(airport: Airport) {
        selected.value = airport
        query.value = airport.iataCode
        saveQuery(airport.iataCode)
    }

    fun toggleFavorite(flight: Flight) {
        viewModelScope.launch {
            if (flight.isFavorite) repository.removeFavorite(flight.departureCode, flight.destinationCode)
            else repository.addFavorite(flight.departureCode, flight.destinationCode)
        }
    }

    private fun saveQuery(value: String) {
        viewModelScope.launch { preferences.saveSearchQuery(value) }
    }
}
