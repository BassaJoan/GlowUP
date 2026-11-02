package com.example.glowup.model

data class Order(
    val orderId: Long,
    val customerId: Long?,
    val createdBy: Long,
    val orderDate: String,
    val status: String, // Pending, Confirmed, Completed, Cancelled
    val customerName: String?,
    val customerPhone: String?,
    val notes: String?
)
