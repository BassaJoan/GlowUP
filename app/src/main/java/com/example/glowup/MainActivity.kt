package com.example.glowup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.auth.LoginActivity
import com.example.glowup.client.ClientHomeActivity
import com.example.glowup.employee.EmployeeDashboardActivity
import com.example.glowup.util.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val role = sessionManager.getRole()
            if (role == "EMPLOYEE") {
                startActivity(Intent(this, EmployeeDashboardActivity::class.java))
            } else {
                startActivity(Intent(this, ClientHomeActivity::class.java))
            }
        }
        finish()
    }
}
