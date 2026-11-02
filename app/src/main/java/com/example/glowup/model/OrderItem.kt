package com.example.glowup.model

data class OrderItem(
    val orderItemId: Long,
    val orderId: Long,
    val itemType: String, // SERVICE or PRODUCT
    val serviceId: Long?,
    val productId: Long?,
    val quantity: Int,
    val unitPriceMinor: Int,
    val itemName: String? = null
)
