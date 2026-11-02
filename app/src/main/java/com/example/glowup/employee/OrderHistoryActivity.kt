package com.example.glowup.employee

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.BookingRepository
import com.example.glowup.data.OrderRepository

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var orderRepository: OrderRepository
    private lateinit var bookingRepository: BookingRepository
    private lateinit var lvHistory: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        orderRepository = OrderRepository(this)
        bookingRepository = BookingRepository(this)
        lvHistory = findViewById(R.id.lvOrderHistory)

        loadHistory()
    }

    private fun loadHistory() {
        val orders = orderRepository.getAllOrders()
        val appointments = bookingRepository.getAllAppointments()

        val orderItems = orders.map { "Order ${it.orderId} | ${it.customerName} (${it.customerPhone}) | Status: ${it.status} | Date: ${it.orderDate}" }
        val apptItems = appointments.map { "Booking ${it.appointmentId} | Client: ${it.clientName} | ${it.serviceName} | ${it.appointmentDate} | Status: ${it.status}" }

        val combined = orderItems + apptItems
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, combined)
        lvHistory.adapter = adapter
    }
}
