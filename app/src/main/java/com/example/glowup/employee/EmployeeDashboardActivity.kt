package com.example.glowup.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R

class EmployeeDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_dashboard)

        findViewById<Button>(R.id.btnManageCatalogue).setOnClickListener {
            startActivity(Intent(this, CatalogueActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreateOrder).setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }

        findViewById<Button>(R.id.btnOrderHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }
    }
}
