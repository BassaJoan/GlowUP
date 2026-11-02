package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun addReview(appointmentId: Long, clientId: Long, rating: Int, comment: String?): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("appointment_id", appointmentId)
            put("client_id", clientId)
            put("rating", rating)
            put("comment", comment)
            put("created_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            ))
        }
        return db.insert(AppDatabaseHelper.TABLE_REVIEWS, null, values)
    }

    fun getAllReviews(): List<Review> {
        val list = mutableListOf<Review>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT r.*, u.full_name as client_name, s.name as service_name
            FROM ${AppDatabaseHelper.TABLE_REVIEWS} r
            JOIN ${AppDatabaseHelper.TABLE_USERS} u ON r.client_id = u.user_id
            JOIN ${AppDatabaseHelper.TABLE_APPOINTMENTS} a ON r.appointment_id = a.appointment_id
            JOIN ${AppDatabaseHelper.TABLE_SERVICES} s ON a.service_id = s.service_id
            ORDER BY r.created_at DESC
        """
        val cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Review(
                            reviewId = it.getLong(it.getColumnIndexOrThrow("review_id")),
                            appointmentId = it.getLong(it.getColumnIndexOrThrow("appointment_id")),
                            clientId = it.getLong(it.getColumnIndexOrThrow("client_id")),
                            rating = it.getInt(it.getColumnIndexOrThrow("rating")),
                            comment = it.getString(it.getColumnIndexOrThrow("comment")),
                            createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                            clientName = it.getString(it.getColumnIndexOrThrow("client_name")),
                            serviceName = it.getString(it.getColumnIndexOrThrow("service_name"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }
}
