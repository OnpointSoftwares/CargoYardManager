package com.example.cargoyard.models

data class InventoryItem(
    val itemId: String,
    val itemName: String,
    val quantity: Int,
    val weight: Double,
    val description: String,
    val location: String,
    val status: ItemStatus,
    val ownerName:String,
    val ownerId:String,
    val ownerPhone:String
) {
    constructor() : this(
        itemId = "",
        itemName = "",
        quantity = 0,
        weight = 0.0,
        description = "",
        location ="",
        status = ItemStatus.AVAILABLE,
        ownerName="",
        ownerId="",
        ownerPhone=""
    )
}


enum class ItemStatus {
    AVAILABLE,
    CHECKED_OUT,
    RESERVED,
    DAMAGED,
    PENDING
}

