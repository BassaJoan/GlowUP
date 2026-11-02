package com.example.glowup.client

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.BookingRepository
import com.example.glowup.data.ServiceRepository
import com.example.glowup.util.SessionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    private lateinit var bookingRepository: BookingRepository
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        bookingRepository = BookingRepository(this)
        serviceRepository = ServiceRepository(this)
        sessionManager = SessionManager(this)

        val serviceId = intent.getLongExtra("SERVICE_ID", -1L)
        val service = serviceRepository.getServiceById(serviceId)

        val rgLocation = findViewById<RadioGroup>(R.id.rgLocationMode)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etStartTime = findViewById<EditText>(R.id.etStartTime)
        val etNotes = findViewById<EditText>(R.id.etNotes)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmBooking)

        // 1. Fetch current live date (YYYY-MM-DD) and current time (HH:mm) on initialization
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        etDate.setText(dateFormat.format(calendar.time))
        etStartTime.setText(timeFormat.format(calendar.time))

        // 2. Attach native DatePickerDialog on date field tap
        etDate.isFocusable = false
        etDate.isClickable = true
        etDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                etDate.setText(dateFormat.format(selectedCalendar.time))
            }, year, month, day)

            // Ensure client cannot pick past dates
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        // 3. Attach native TimePickerDialog on time field tap
        etStartTime.isFocusable = false
        etStartTime.isClickable = true
        etStartTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                etStartTime.setText(timeString)
            }, hour, minute, true)

            timePickerDialog.show()
        }

        btnConfirm.setOnClickListener {
            if (service == null) {
                Toast.makeText(this, "Invalid service", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (date.isEmpty() || startTime.isEmpty()) {
                Toast.makeText(this, "Please enter date and start time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate end time based on service duration
            val endTime = calculateEndTime(startTime, service.durationMinutes)
            val selectedLocationId = rgLocation.checkedRadioButtonId
            val rbLocation = findViewById<RadioButton>(selectedLocationId)
            val locationMode = rbLocation?.text?.toString() ?: "In-Studio"

            val clientId = sessionManager.getUserId()
            val appointmentId = bookingRepository.createAppointment(
                clientId = clientId,
                serviceId = serviceId,
                date = date,
                startTime = startTime,
                endTime = endTime,
                locationMode = locationMode,
                notes = notes
            )

            if (appointmentId != -1L) {
                val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
                    putExtra("APPOINTMENT_ID", appointmentId)
                    putExtra("SERVICE_NAME", service.name)
                    putExtra("DATE", date)
                    putExtra("TIME", startTime)
                    putExtra("LOCATION", locationMode)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Booking conflict: Selected time slot is already booked.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calculateEndTime(startTime: String, durationMinutes: Int): String {
        try {
            val parts = startTime.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val totalMinutes = hour * 60 + minute + durationMinutes
                val endHour = (totalMinutes / 60) % 24
                val endMin = totalMinutes % 60
                return String.format(Locale.getDefault(), "%02d:%02d", endHour, endMin)
            }
        } catch (e: Exception) {
            // fallback
        }
        return "11:00"
    }
}
