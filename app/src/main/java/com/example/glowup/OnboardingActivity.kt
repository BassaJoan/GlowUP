package com.example.glowup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            getSharedPreferences("glowup_prefs", MODE_PRIVATE).edit().putBoolean("onboarding_seen", true).apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
