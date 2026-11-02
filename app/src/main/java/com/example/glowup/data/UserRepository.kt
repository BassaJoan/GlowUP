package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.User
import com.example.glowup.util.PasswordHasher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun registerUser(fullName: String, username: String, plainPassword: String, role: String, phone: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("username", username)
            put("password_hash", PasswordHasher.hash(plainPassword))
            put("role", role)
            put("phone", phone)
            put("created_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                Date()
            ))
        }
        return db.insert(AppDatabaseHelper.TABLE_USERS, null, values)
    }

    fun login(username: String, plainPassword: String): User? {
        val db = dbHelper.readableDatabase
        val hash = PasswordHasher.hash(plainPassword)
        val cursor = db.query(
            AppDatabaseHelper.TABLE_USERS,
            null,
            "username COLLATE NOCASE = ? AND password_hash = ?",
            arrayOf(username, hash),
            null, null, null
        )
        return cursor?.use {
            if (it.moveToFirst()) {
                User(
                    userId = it.getLong(it.getColumnIndexOrThrow("user_id")),
                    fullName = it.getString(it.getColumnIndexOrThrow("full_name")),
                    username = it.getString(it.getColumnIndexOrThrow("username")),
                    passwordHash = it.getString(it.getColumnIndexOrThrow("password_hash")),
                    role = it.getString(it.getColumnIndexOrThrow("role")),
                    phone = it.getString(it.getColumnIndexOrThrow("phone")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at"))
                )
            } else null
        }
    }

    fun getUserById(userId: Long): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            AppDatabaseHelper.TABLE_USERS,
            null,
            "user_id = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        return cursor?.use {
            if (it.moveToFirst()) {
                User(
                    userId = it.getLong(it.getColumnIndexOrThrow("user_id")),
                    fullName = it.getString(it.getColumnIndexOrThrow("full_name")),
                    username = it.getString(it.getColumnIndexOrThrow("username")),
                    passwordHash = it.getString(it.getColumnIndexOrThrow("password_hash")),
                    role = it.getString(it.getColumnIndexOrThrow("role")),
                    phone = it.getString(it.getColumnIndexOrThrow("phone")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at"))
                )
            } else null
        }
    }
}
