package com.example.glowup.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.admin.AdminDashboardActivity
import com.example.glowup.client.ClientHomeActivity
import com.example.glowup.data.UserRepository
import com.example.glowup.owner.OwnerDashboardActivity
import com.example.glowup.technician.TechnicianDashboardActivity
import com.example.glowup.util.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegisterLink = findViewById<Button>(R.id.btnRegisterLink)

        // Toggle password visibility
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            etPassword.setSelection(etPassword.text.length)
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userRepository.login(username, password)
            if (user != null) {
                sessionManager.saveUserSession(user.userId, user.username, user.role, user.fullName)
                when (user.role) {
                    "ADMIN" -> {
                        // Joan Atimaku first takes her to the client interface, with an Admin tab to access admin dashboard
                        startActivity(Intent(this, ClientHomeActivity::class.java))
                    }
                    "OWNER" -> {
                        startActivity(Intent(this, OwnerDashboardActivity::class.java))
                    }
                    "TECHNICIAN", "EMPLOYEE" -> {
                        startActivity(Intent(this, TechnicianDashboardActivity::class.java))
                    }
                    else -> {
                        startActivity(Intent(this, ClientHomeActivity::class.java))
                    }
                }
                finish()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
