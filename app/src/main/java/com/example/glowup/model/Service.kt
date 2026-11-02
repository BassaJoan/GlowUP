package com.example.glowup.model

data class Service(
    val serviceId: Long,
    val name: String,
    val category: String, // Hair, Nails, Makeup, Lash
    val description: String,
    val priceMinor: Int, // in cents/minor units
    val durationMinutes: Int,
    val isActive: Int // 1 for active, 0 for inactive
)
