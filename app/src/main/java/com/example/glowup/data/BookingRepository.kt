package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.Appointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun createAppointment(clientId: Long, serviceId: Long, date: String, startTime: String, endTime: String, locationMode: String, notes: String?): Long {
        val db = dbHelper.writableDatabase

        val conflictQuery = """
            SELECT COUNT(*) FROM ${AppDatabaseHelper.TABLE_APPOINTMENTS}
            WHERE appointment_date = ? 
              AND status != 'Cancelled'
              AND start_time < ? 
              AND end_time > ?
        """
        val cursor = db.rawQuery(conflictQuery, arrayOf(date, endTime, startTime))
        var hasConflict = false
        cursor.use {
            if (it.moveToFirst()) {
                hasConflict = it.getInt(0) > 0
            }
        }

        if (hasConflict) {
            return -1L // Conflict detected
        }

        val values = ContentValues().apply {
            put("client_id", clientId)
            put("service_id", serviceId)
            put("appointment_date", date)
            put("start_time", startTime)
            put("end_time", endTime)
            put("location_mode", locationMode)
            put("status", "Confirmed")
            put("notes", notes ?: "")
            put("created_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            ))
        }
        return db.insert(AppDatabaseHelper.TABLE_APPOINTMENTS, null, values)
    }

    fun getAppointmentsForClient(clientId: Long): List<Appointment> {
        val list = mutableListOf<Appointment>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.*, s.name as service_name, u.full_name as client_name
            FROM ${AppDatabaseHelper.TABLE_APPOINTMENTS} a
            JOIN ${AppDatabaseHelper.TABLE_SERVICES} s ON a.service_id = s.service_id
            JOIN ${AppDatabaseHelper.TABLE_USERS} u ON a.client_id = u.user_id
            WHERE a.client_id = ?
            ORDER BY a.appointment_date DESC, a.start_time DESC
        """
        val cursor = db.rawQuery(query, arrayOf(clientId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Appointment(
                            appointmentId = it.getLong(it.getColumnIndexOrThrow("appointment_id")),
                            clientId = it.getLong(it.getColumnIndexOrThrow("client_id")),
                            serviceId = it.getLong(it.getColumnIndexOrThrow("service_id")),
                            appointmentDate = it.getString(it.getColumnIndexOrThrow("appointment_date")),
                            startTime = it.getString(it.getColumnIndexOrThrow("start_time")),
                            endTime = it.getString(it.getColumnIndexOrThrow("end_time")),
                            locationMode = it.getString(it.getColumnIndexOrThrow("location_mode")),
                            status = it.getString(it.getColumnIndexOrThrow("status")),
                            notes = it.getString(it.getColumnIndexOrThrow("notes")),
                            createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                            serviceName = it.getString(it.getColumnIndexOrThrow("service_name")),
                            clientName = it.getString(it.getColumnIndexOrThrow("client_name"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun getAllAppointments(): List<Appointment> {
        val list = mutableListOf<Appointment>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT a.*, s.name as service_name, u.full_name as client_name
            FROM ${AppDatabaseHelper.TABLE_APPOINTMENTS} a
            JOIN ${AppDatabaseHelper.TABLE_SERVICES} s ON a.service_id = s.service_id
            JOIN ${AppDatabaseHelper.TABLE_USERS} u ON a.client_id = u.user_id
            ORDER BY a.appointment_date DESC, a.start_time DESC
        """
        val cursor = db.rawQuery(query, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Appointment(
                            appointmentId = it.getLong(it.getColumnIndexOrThrow("appointment_id")),
                            clientId = it.getLong(it.getColumnIndexOrThrow("client_id")),
                            serviceId = it.getLong(it.getColumnIndexOrThrow("service_id")),
                            appointmentDate = it.getString(it.getColumnIndexOrThrow("appointment_date")),
                            startTime = it.getString(it.getColumnIndexOrThrow("start_time")),
                            endTime = it.getString(it.getColumnIndexOrThrow("end_time")),
                            locationMode = it.getString(it.getColumnIndexOrThrow("location_mode")),
                            status = it.getString(it.getColumnIndexOrThrow("status")),
                            notes = it.getString(it.getColumnIndexOrThrow("notes")),
                            createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                            serviceName = it.getString(it.getColumnIndexOrThrow("service_name")),
                            clientName = it.getString(it.getColumnIndexOrThrow("client_name"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun updateAppointmentStatus(appointmentId: Long, status: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("status", status) }
        return db.update(AppDatabaseHelper.TABLE_APPOINTMENTS, values, "appointment_id = ?", arrayOf(appointmentId.toString()))
    }

    fun cancelAppointment(appointmentId: Long): Int {
        return updateAppointmentStatus(appointmentId, "Cancelled")
    }
}
