package com.example.cargoyard.models

data class User(
    val userId: String,
    val username: String,
    val password: String,
    val role: UserRole
)

enum class UserRole {
    MANAGER,
    FINANCE_MANAGER,
    SUPERVISOR
}

