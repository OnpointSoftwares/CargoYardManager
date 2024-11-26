package com.example.cargoyard.models

data class Payment(
    val paymentId: String,
    val cargoId: String,
    val amount: Double,
    val paymentStatus: PaymentStatus
)

enum class PaymentStatus {
    PAID, PENDING, OVERDUE
}

