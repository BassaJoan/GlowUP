package com.example.glowup.util

object InputValidator {
    fun isNotBlank(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() }
    }

    fun isValidPrice(price: Int): Boolean {
        return price >= 0
    }

    fun isValidDuration(duration: Int): Boolean {
        return duration > 0
    }

    fun isValidRating(rating: Int): Boolean {
        return rating in 1..5
    }
}
