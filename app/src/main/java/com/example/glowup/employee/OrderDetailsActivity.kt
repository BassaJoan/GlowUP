package com.example.glowup.employee

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.OrderRepository

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var orderRepository: OrderRepository
    private var orderId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        orderRepository = OrderRepository(this)
        orderId = intent.getLongExtra("ORDER_ID", -1L)

        val tvTitle = findViewById<TextView>(R.id.tvOrderTitle)
        val tvCustomer = findViewById<TextView>(R.id.tvOrderCustomer)
        val tvDate = findViewById<TextView>(R.id.tvOrderDate)
        val tvStatus = findViewById<TextView>(R.id.tvOrderStatus)
        val tvNotes = findViewById<TextView>(R.id.tvOrderNotes)

        val btnResolved = findViewById<Button>(R.id.btnStatusResolved)
        val btnPending = findViewById<Button>(R.id.btnStatusPending)
        val btnDeclined = findViewById<Button>(R.id.btnStatusDeclined)

        val orders = orderRepository.getAllOrders()
        val order = orders.find { it.orderId == orderId }

        if (order != null) {
            tvTitle.text = "Order/Ticket ${order.orderId}"
            tvCustomer.text = "Customer: ${order.customerName} (${order.customerPhone})"
            tvDate.text = "Date: ${order.orderDate}"
            tvStatus.text = "Status: ${order.status}"
            tvNotes.text = "Notes: ${if (order.notes.isNullOrBlank()) "None" else order.notes}"

            btnResolved.setOnClickListener {
                orderRepository.updateOrderStatus(orderId, "Resolved / Worked Upon")
                Toast.makeText(this, "Ticket updated to: Resolved / Worked Upon", Toast.LENGTH_SHORT).show()
                finish()
            }

            btnPending.setOnClickListener {
                orderRepository.updateOrderStatus(orderId, "Pending")
                Toast.makeText(this, "Ticket updated to: Pending", Toast.LENGTH_SHORT).show()
                finish()
            }

            btnDeclined.setOnClickListener {
                orderRepository.updateOrderStatus(orderId, "Declined")
                Toast.makeText(this, "Ticket updated to: Declined", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            tvTitle.text = "Order not found"
            btnResolved.isEnabled = false
            btnPending.isEnabled = false
            btnDeclined.isEnabled = false
        }
    }
}
