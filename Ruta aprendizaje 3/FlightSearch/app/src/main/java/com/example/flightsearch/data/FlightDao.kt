package com.example.flightsearch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Query(
        """SELECT * FROM airport
           WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%'
           ORDER BY passengers DESC"""
    )
    fun searchAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :code ORDER BY passengers DESC")
    fun getDestinations(code: String): Flow<List<Airport>>

    @Query(
        """SELECT f.departure_code AS departureCode, a1.name AS departureName,
                  f.destination_code AS destinationCode, a2.name AS destinationName
           FROM favorite f
           JOIN airport a1 ON a1.iata_code = f.departure_code
           JOIN airport a2 ON a2.iata_code = f.destination_code
           ORDER BY f.id ASC"""
    )
    fun getFavoriteRoutes(): Flow<List<FavoriteRoute>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :dep AND destination_code = :dest")
    suspend fun deleteFavorite(dep: String, dest: String)
}
