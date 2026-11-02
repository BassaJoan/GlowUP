package com.example.glowup.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R

class BookingConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        val serviceName = intent.getStringExtra("SERVICE_NAME") ?: "Service"
        val date = intent.getStringExtra("DATE") ?: ""
        val time = intent.getStringExtra("TIME") ?: ""
        val location = intent.getStringExtra("LOCATION") ?: ""

        val tvDetails = findViewById<TextView>(R.id.tvConfirmationDetails)
        tvDetails.text = "Service: $serviceName\nDate: $date\nTime: $time\nLocation: $location\n\nYour appointment is confirmed!"

        findViewById<Button>(R.id.btnBackToHome).setOnClickListener {
            val intent = Intent(this, ClientHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
