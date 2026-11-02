package com.example.glowup

import android.app.Application
import com.example.glowup.data.AppDatabaseHelper

class GlowUpApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            // Safely warm up SQLite Database Singleton on Application launch
            AppDatabaseHelper.getInstance(this).writableDatabase
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
