package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.Service

class ServiceRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun getAllServices(): List<Service> {
        val list = mutableListOf<Service>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(AppDatabaseHelper.TABLE_SERVICES, null, "is_active = 1", null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Service(
                            serviceId = it.getLong(it.getColumnIndexOrThrow("service_id")),
                            name = it.getString(it.getColumnIndexOrThrow("name")),
                            category = it.getString(it.getColumnIndexOrThrow("category")),
                            description = it.getString(it.getColumnIndexOrThrow("description")),
                            priceMinor = it.getInt(it.getColumnIndexOrThrow("price_minor")),
                            durationMinutes = it.getInt(it.getColumnIndexOrThrow("duration_minutes")),
                            isActive = it.getInt(it.getColumnIndexOrThrow("is_active"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun getServicesByCategory(category: String): List<Service> {
        val list = mutableListOf<Service>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            AppDatabaseHelper.TABLE_SERVICES,
            null,
            "category = ? AND is_active = 1",
            arrayOf(category),
            null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Service(
                            serviceId = it.getLong(it.getColumnIndexOrThrow("service_id")),
                            name = it.getString(it.getColumnIndexOrThrow("name")),
                            category = it.getString(it.getColumnIndexOrThrow("category")),
                            description = it.getString(it.getColumnIndexOrThrow("description")),
                            priceMinor = it.getInt(it.getColumnIndexOrThrow("price_minor")),
                            durationMinutes = it.getInt(it.getColumnIndexOrThrow("duration_minutes")),
                            isActive = it.getInt(it.getColumnIndexOrThrow("is_active"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun getServiceById(serviceId: Long): Service? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            AppDatabaseHelper.TABLE_SERVICES,
            null,
            "service_id = ?",
            arrayOf(serviceId.toString()),
            null, null, null
        )
        return cursor?.use {
            if (it.moveToFirst()) {
                Service(
                    serviceId = it.getLong(it.getColumnIndexOrThrow("service_id")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    category = it.getString(it.getColumnIndexOrThrow("category")),
                    description = it.getString(it.getColumnIndexOrThrow("description")),
                    priceMinor = it.getInt(it.getColumnIndexOrThrow("price_minor")),
                    durationMinutes = it.getInt(it.getColumnIndexOrThrow("duration_minutes")),
                    isActive = it.getInt(it.getColumnIndexOrThrow("is_active"))
                )
            } else null
        }
    }

    fun insertService(name: String, category: String, description: String, priceMinor: Int, durationMinutes: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("description", description)
            put("price_minor", priceMinor)
            put("duration_minutes", durationMinutes)
            put("is_active", 1)
        }
        return db.insert(AppDatabaseHelper.TABLE_SERVICES, null, values)
    }

    fun updateService(serviceId: Long, name: String, category: String, description: String, priceMinor: Int, durationMinutes: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("description", description)
            put("price_minor", priceMinor)
            put("duration_minutes", durationMinutes)
        }
        return db.update(AppDatabaseHelper.TABLE_SERVICES, values, "service_id = ?", arrayOf(serviceId.toString()))
    }

    fun deleteService(serviceId: Long): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("is_active", 0) }
        return db.update(AppDatabaseHelper.TABLE_SERVICES, values, "service_id = ?", arrayOf(serviceId.toString()))
    }
}
