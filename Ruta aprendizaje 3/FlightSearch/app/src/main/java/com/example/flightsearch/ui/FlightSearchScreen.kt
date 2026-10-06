package com.example.flightsearch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.data.Airport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    viewModel: FlightSearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Flight Search") }) }) { padding ->
        Column(Modifier.padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Aeropuerto o código IATA") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                }
            )
            Spacer(Modifier.height(12.dp))

            when {
                state.query.isBlank() -> FlightList(
                    title = "Rutas favoritas",
                    emptyText = "Aún no tienes rutas favoritas",
                    flights = state.favorites,
                    onToggle = viewModel::toggleFavorite
                )
                state.selected == null -> SuggestionList(
                    airports = state.suggestions,
                    onClick = viewModel::onAirportSelected
                )
                else -> FlightList(
                    title = "Vuelos desde ${state.selected!!.iataCode}",
                    emptyText = "No hay vuelos disponibles",
                    flights = state.flights,
                    onToggle = viewModel::toggleFavorite
                )
            }
        }
    }
}

@Composable
private fun SuggestionList(airports: List<Airport>, onClick: (Airport) -> Unit) {
    if (airports.isEmpty()) {
        Text("Sin resultados")
        return
    }
    LazyColumn {
        items(airports, key = { it.id }) { airport ->
            Row(
                Modifier.fillMaxWidth().clickable { onClick(airport) }.padding(vertical = 12.dp)
            ) {
                Text(airport.iataCode, fontWeight = FontWeight.Bold, modifier = Modifier.width(52.dp))
                Text(airport.name)
            }
            HorizontalDivider()
        }
    }
}

@Composable
private fun FlightList(
    title: String,
    emptyText: String,
    flights: List<Flight>,
    onToggle: (Flight) -> Unit
) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    if (flights.isEmpty()) {
        Text(emptyText)
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(flights, key = { "${it.departureCode}-${it.destinationCode}" }) { flight ->
            FlightCard(flight, onToggle)
        }
    }
}

@Composable
private fun FlightCard(flight: Flight, onToggle: (Flight) -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("SALIDA", style = MaterialTheme.typography.labelSmall)
                AirportLine(flight.departureCode, flight.departureName)
                Text("LLEGADA", style = MaterialTheme.typography.labelSmall)
                AirportLine(flight.destinationCode, flight.destinationName)
            }
            IconButton(onClick = { onToggle(flight) }) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Favorito",
                    tint = if (flight.isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun AirportLine(code: String, name: String) {
    Row {
        Text(code, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text(name)
    }
}
