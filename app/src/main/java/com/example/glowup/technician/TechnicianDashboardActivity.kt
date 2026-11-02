package com.example.glowup.technician

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.auth.LoginActivity
import com.example.glowup.employee.OrderHistoryActivity
import com.example.glowup.util.SessionManager

class TechnicianDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private var isAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technician_dashboard)

        sessionManager = SessionManager(this)

        val tvWelcome = findViewById<TextView>(R.id.tvTechWelcome)
        val tvStatus = findViewById<TextView>(R.id.tvAvailabilityStatus)
        val btnToggle = findViewById<Button>(R.id.btnToggleAvailability)

        tvWelcome.text = "Technician: ${sessionManager.getFullName() ?: "Technician"}"

        btnToggle.setOnClickListener {
            isAvailable = !isAvailable
            if (isAvailable) {
                tvStatus.text = "Status: Available for Bookings"
                tvStatus.setTextColor(getColor(R.color.colorSuccess))
                btnToggle.text = "Set Break"
            } else {
                tvStatus.text = "Status: On Break / Unavailable"
                tvStatus.setTextColor(getColor(R.color.colorError))
                btnToggle.text = "Set Available"
            }
        }

        findViewById<Button>(R.id.btnViewBookings).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnTechLogout).setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
