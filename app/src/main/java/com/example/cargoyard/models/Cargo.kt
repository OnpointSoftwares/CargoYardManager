package com.example.cargoyard.models

data class Cargo(
    val id: String,
    val weight: Double,
    val description: String,
    val origin: String,
    val destination: String,
    val status: CargoStatus
)

enum class CargoStatus {
    CHECKED_IN, CHECKED_OUT, PENDING
}
