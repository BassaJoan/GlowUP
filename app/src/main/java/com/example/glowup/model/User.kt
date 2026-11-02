package com.example.glowup.model

data class User(
    val userId: Long,
    val fullName: String,
    val username: String,
    val passwordHash: String,
    val role: String, // CLIENT or EMPLOYEE
    val phone: String,
    val createdAt: String
)
