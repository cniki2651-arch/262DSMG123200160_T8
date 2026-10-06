package com.example.flightsearch.data

import kotlinx.coroutines.flow.Flow

class FlightRepository(private val dao: FlightDao) {
    fun searchAirports(query: String): Flow<List<Airport>> = dao.searchAirports(query)
    fun getDestinations(code: String): Flow<List<Airport>> = dao.getDestinations(code)
    fun getFavoriteRoutes(): Flow<List<FavoriteRoute>> = dao.getFavoriteRoutes()
    suspend fun addFavorite(dep: String, dest: String) =
        dao.insertFavorite(Favorite(departureCode = dep, destinationCode = dest))
    suspend fun removeFavorite(dep: String, dest: String) = dao.deleteFavorite(dep, dest)
}
