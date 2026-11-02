package com.example.glowup.employee

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.OrderRepository
import com.example.glowup.util.SessionManager

class CreateOrderActivity : AppCompatActivity() {
    private lateinit var orderRepository: OrderRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order)

        orderRepository = OrderRepository(this)
        sessionManager = SessionManager(this)

        val etCustomerName = findViewById<EditText>(R.id.etCustomerName)
        val etCustomerPhone = findViewById<EditText>(R.id.etCustomerPhone)
        val etOrderDate = findViewById<EditText>(R.id.etOrderDate)
        val etNotes = findViewById<EditText>(R.id.etNotes)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitOrder)

        btnSubmit.setOnClickListener {
            val customerName = etCustomerName.text.toString().trim()
            val customerPhone = etCustomerPhone.text.toString().trim()
            val orderDate = etOrderDate.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            if (customerName.isEmpty() || customerPhone.isEmpty() || orderDate.isEmpty()) {
                Toast.makeText(this, "Please fill in customer details and date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val createdBy = sessionManager.getUserId()
            val orderId = orderRepository.createOrder(
                customerId = null,
                createdBy = createdBy,
                orderDate = orderDate,
                customerName = customerName,
                customerPhone = customerPhone,
                notes = notes
            )

            if (orderId != -1L) {
                Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to create order", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
