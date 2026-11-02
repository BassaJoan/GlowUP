package com.example.glowup.auth

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.UserRepository

class RegisterActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userRepository = UserRepository(this)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Password visibility toggle
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            etPassword.setSelection(etPassword.text.length)
        }

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = userRepository.registerUser(fullName, username, password, "CLIENT", phone)
            if (id != -1L) {
                // Save registered credentials for auto-fill in LoginActivity
                val prefs = getSharedPreferences("glowup_saved_creds", MODE_PRIVATE)
                prefs.edit().apply {
                    putString("saved_username", username)
                    putString("saved_password", password)
                    apply()
                }

                Toast.makeText(this, "Registration successful. Credentials auto-filled.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
