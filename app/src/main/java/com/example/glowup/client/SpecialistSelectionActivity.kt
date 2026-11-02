package com.example.glowup.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R

class SpecialistSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_selection)

        val serviceId = intent.getLongExtra("SERVICE_ID", -1L)

        val proceedToBooking = { specialistName: String ->
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra("SERVICE_ID", serviceId)
                putExtra("SPECIALIST_NAME", specialistName)
            }
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnBookMusiimenta).setOnClickListener {
            proceedToBooking("Musiimenta Zam")
        }

        findViewById<Button>(R.id.btnBookPoni).setOnClickListener {
            proceedToBooking("Poni Ritah Minga")
        }

        findViewById<Button>(R.id.btnBookOmoding).setOnClickListener {
            proceedToBooking("Omoding Julius")
        }
    }
}
