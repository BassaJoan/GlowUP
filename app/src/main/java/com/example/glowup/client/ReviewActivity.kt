package com.example.glowup.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.ReviewRepository
import com.example.glowup.util.InputValidator
import com.example.glowup.util.SessionManager

class ReviewActivity : AppCompatActivity() {
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reviewRepository = ReviewRepository(this)
        sessionManager = SessionManager(this)

        val appointmentId = intent.getLongExtra("APPOINTMENT_ID", -1L)
        val etRating = findViewById<EditText>(R.id.etRating)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitReview)

        btnSubmit.setOnClickListener {
            val ratingStr = etRating.text.toString().trim()
            val comment = etComment.text.toString().trim()

            if (ratingStr.isEmpty()) {
                Toast.makeText(this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rating = ratingStr.toIntOrNull() ?: 0
            if (!InputValidator.isValidRating(rating)) {
                Toast.makeText(this, "Rating must be between 1 and 5", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val clientId = sessionManager.getUserId()
            val reviewId = reviewRepository.addReview(appointmentId, clientId, rating, comment)

            if (reviewId != -1L) {
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to submit review. Review may already exist for this appointment.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
