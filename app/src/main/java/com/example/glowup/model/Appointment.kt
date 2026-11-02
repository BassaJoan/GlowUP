package com.example.glowup.model

data class Appointment(
    val appointmentId: Long,
    val clientId: Long,
    val serviceId: Long,
    val appointmentDate: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val locationMode: String, // In-Studio or Home
    val status: String, // Pending, Confirmed, Completed, Cancelled
    val notes: String?,
    val createdAt: String,
    val serviceName: String? = null,
    val clientName: String? = null
)
