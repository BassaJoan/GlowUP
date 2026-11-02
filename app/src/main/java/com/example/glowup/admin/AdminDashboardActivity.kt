package com.example.glowup.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.auth.LoginActivity
import com.example.glowup.client.ClientHomeActivity
import com.example.glowup.employee.CatalogueActivity
import com.example.glowup.employee.OrderHistoryActivity
import com.example.glowup.util.SessionManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)

        val tvWelcome = findViewById<TextView>(R.id.tvAdminWelcome)
        tvWelcome.text = "Admin: ${sessionManager.getFullName() ?: "Joan Atimaku"}"

        val btnSwitchToClient = findViewById<Button>(R.id.btnSwitchToClient)
        val btnManageCatalogue = findViewById<Button>(R.id.btnManageCatalogueAdmin)
        val btnViewOrders = findViewById<Button>(R.id.btnViewAllOrdersAdmin)
        val btnUserMgmt = findViewById<Button>(R.id.btnUserManagement)
        val btnLogout = findViewById<Button>(R.id.btnAdminLogout)

        fun selectAdminButton(activeButton: Button) {
            val buttons = listOf(btnSwitchToClient, btnManageCatalogue, btnViewOrders, btnUserMgmt, btnLogout)
            buttons.forEach { btn ->
                if (btn == activeButton) {
                    btn.setBackgroundResource(R.drawable.bg_button_primary)
                    btn.setTextColor(getColor(R.color.colorOnPrimary))
                } else {
                    btn.setBackgroundResource(R.drawable.bg_input)
                    btn.setTextColor(getColor(R.color.colorTextPrimary))
                }
            }
        }

        btnSwitchToClient.setOnClickListener {
            selectAdminButton(btnSwitchToClient)
            startActivity(Intent(this, ClientHomeActivity::class.java))
        }

        btnManageCatalogue.setOnClickListener {
            selectAdminButton(btnManageCatalogue)
            startActivity(Intent(this, CatalogueActivity::class.java))
        }

        btnViewOrders.setOnClickListener {
            selectAdminButton(btnViewOrders)
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        btnUserMgmt.setOnClickListener {
            selectAdminButton(btnUserMgmt)
            Toast.makeText(this, "Admin Role Management: 4 Active Roles Configured", Toast.LENGTH_LONG).show()
        }

        btnLogout.setOnClickListener {
            selectAdminButton(btnLogout)
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
