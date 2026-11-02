package com.example.glowup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.admin.AdminDashboardActivity
import com.example.glowup.auth.LoginActivity
import com.example.glowup.client.ClientHomeActivity
import com.example.glowup.owner.OwnerDashboardActivity
import com.example.glowup.technician.TechnicianDashboardActivity
import com.example.glowup.util.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)
            val prefs = getSharedPreferences("glowup_prefs", MODE_PRIVATE)
            val onboardingSeen = prefs.getBoolean("onboarding_seen", false)

            if (!onboardingSeen) {
                startActivity(Intent(this, OnboardingActivity::class.java))
            } else if (!sessionManager.isLoggedIn()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                when (sessionManager.getRole()) {
                    "ADMIN" -> startActivity(Intent(this, ClientHomeActivity::class.java))
                    "OWNER" -> startActivity(Intent(this, OwnerDashboardActivity::class.java))
                    "TECHNICIAN", "EMPLOYEE" -> startActivity(Intent(this, TechnicianDashboardActivity::class.java))
                    else -> startActivity(Intent(this, ClientHomeActivity::class.java))
                }
            }
            finish()
        }, 1500)
    }
}
