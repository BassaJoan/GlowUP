package com.example.glowup.model

data class Product(
    val productId: Long,
    val name: String,
    val category: String,
    val description: String,
    val priceMinor: Int,
    val stockQuantity: Int,
    val isActive: Int
)
