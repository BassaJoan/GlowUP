package com.example.glowup.model

data class Review(
    val reviewId: Long,
    val appointmentId: Long,
    val clientId: Long,
    val rating: Int, // 1 to 5
    val comment: String?,
    val createdAt: String,
    val clientName: String? = null,
    val serviceName: String? = null
)
