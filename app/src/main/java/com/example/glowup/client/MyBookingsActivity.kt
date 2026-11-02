package com.example.glowup.client

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.BookingRepository
import com.example.glowup.model.Appointment
import com.example.glowup.util.SessionManager

class MyBookingsActivity : AppCompatActivity() {
    private lateinit var bookingRepository: BookingRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var lvBookings: ListView
    private lateinit var btnUpcoming: Button
    private lateinit var btnPast: Button
    private var appointmentList: List<Appointment> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        bookingRepository = BookingRepository(this)
        sessionManager = SessionManager(this)
        lvBookings = findViewById(R.id.lvBookings)

        btnUpcoming = findViewById(R.id.btnTabUpcoming)
        btnPast = findViewById(R.id.btnTabPast)

        selectTab(btnUpcoming, isUpcoming = true)

        btnUpcoming.setOnClickListener {
            selectTab(btnUpcoming, isUpcoming = true)
        }

        btnPast.setOnClickListener {
            selectTab(btnPast, isUpcoming = false)
        }

        lvBookings.setOnItemClickListener { _, _, position, _ ->
            val appt = appointmentList[position]
            if (appt.status == "Completed") {
                val intent = Intent(this, ReviewActivity::class.java).apply {
                    putExtra("APPOINTMENT_ID", appt.appointmentId)
                }
                startActivity(intent)
            } else if (appt.status != "Cancelled") {
                bookingRepository.cancelAppointment(appt.appointmentId)
                Toast.makeText(this, "Appointment cancelled", Toast.LENGTH_SHORT).show()
                selectTab(btnUpcoming, isUpcoming = true)
            }
        }
    }

    private fun selectTab(activeTab: Button, isUpcoming: Boolean) {
        val tabs = listOf(btnUpcoming, btnPast)
        tabs.forEach { tab ->
            if (tab == activeTab) {
                tab.setBackgroundResource(R.drawable.bg_button_primary)
                tab.setTextColor(getColor(R.color.colorOnPrimary))
            } else {
                tab.setBackgroundResource(R.drawable.bg_input)
                tab.setTextColor(getColor(R.color.colorTextPrimary))
            }
        }
        loadBookings(isUpcoming)
    }

    private fun loadBookings(isUpcoming: Boolean) {
        val clientId = sessionManager.getUserId()
        val allAppointments = bookingRepository.getAppointmentsForClient(clientId)

        appointmentList = if (isUpcoming) {
            allAppointments.filter { it.status != "Completed" && it.status != "Cancelled" }
        } else {
            allAppointments.filter { it.status == "Completed" || it.status == "Cancelled" }
        }

        val displayItems = appointmentList.map { "${it.serviceName} | ${it.appointmentDate} ${it.startTime} | Status: ${it.status}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayItems)
        lvBookings.adapter = adapter
    }
}
