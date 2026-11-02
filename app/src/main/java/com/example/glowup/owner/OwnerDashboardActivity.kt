package com.example.glowup.owner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.auth.LoginActivity
import com.example.glowup.employee.CatalogueActivity
import com.example.glowup.employee.CreateOrderActivity
import com.example.glowup.employee.OrderHistoryActivity
import com.example.glowup.util.SessionManager

class OwnerDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_owner_dashboard)

        sessionManager = SessionManager(this)

        val tvWelcome = findViewById<TextView>(R.id.tvOwnerWelcome)
        tvWelcome.text = "Salon Owner: ${sessionManager.getFullName() ?: "Owner"}"

        findViewById<Button>(R.id.btnManageCatalogue).setOnClickListener {
            startActivity(Intent(this, CatalogueActivity::class.java))
        }

        findViewById<Button>(R.id.btnManageStaff).setOnClickListener {
            Toast.makeText(this, "Technician Staff Roster updated", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnViewAllOrders).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreateOrder).setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }

        findViewById<Button>(R.id.btnOwnerLogout).setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
