package com.example.flightsearch.data

/** Resultado del JOIN entre favorite y airport (solo las filas necesarias). */
data class FavoriteRoute(
    val departureCode: String,
    val departureName: String,
    val destinationCode: String,
    val destinationName: String
)
