package com.example.glowup.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.BookingRepository

class BookingDetailsActivity : AppCompatActivity() {
    private lateinit var bookingRepository: BookingRepository
    private var appointmentId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_details)

        bookingRepository = BookingRepository(this)
        appointmentId = intent.getLongExtra("APPOINTMENT_ID", -1L)

        val tvServiceName = findViewById<TextView>(R.id.tvBookingServiceName)
        val tvDateTime = findViewById<TextView>(R.id.tvBookingDateTime)
        val tvLocation = findViewById<TextView>(R.id.tvBookingLocation)
        val tvStatus = findViewById<TextView>(R.id.tvBookingStatus)
        val tvNotes = findViewById<TextView>(R.id.tvBookingNotes)
        val btnCancel = findViewById<Button>(R.id.btnCancelBooking)
        val btnReview = findViewById<Button>(R.id.btnReviewBooking)

        val appts = bookingRepository.getAllAppointments()
        val appt = appts.find { it.appointmentId == appointmentId }

        if (appt != null) {
            tvServiceName.text = appt.serviceName
            tvDateTime.text = "Date & Time: ${appt.appointmentDate} at ${appt.startTime} - ${appt.endTime}"
            tvLocation.text = "Location Mode: ${appt.locationMode}"
            tvStatus.text = "Status: ${appt.status}"
            tvNotes.text = "Notes: ${if (appt.notes.isNullOrBlank()) "None" else appt.notes}"

            if (appt.status == "Cancelled") {
                btnCancel.isEnabled = false
                btnReview.isEnabled = false
            } else if (appt.status == "Completed") {
                btnCancel.isEnabled = false
                btnReview.isEnabled = true
            } else {
                btnCancel.isEnabled = true
                btnReview.isEnabled = false
            }

            btnCancel.setOnClickListener {
                bookingRepository.cancelAppointment(appointmentId)
                Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }

            btnReview.setOnClickListener {
                val intent = Intent(this, ReviewActivity::class.java).apply {
                    putExtra("APPOINTMENT_ID", appt.appointmentId)
                }
                startActivity(intent)
            }
        } else {
            tvServiceName.text = "Booking not found"
            btnCancel.isEnabled = false
            btnReview.isEnabled = false
        }
    }
}
