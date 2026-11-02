package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.Order

class OrderRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun createOrder(customerId: Long?, createdBy: Long, orderDate: String, customerName: String?, customerPhone: String?, notes: String?): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("customer_id", customerId)
            put("created_by", createdBy)
            put("order_date", orderDate)
            put("status", "Pending")
            put("customer_name", customerName)
            put("customer_phone", customerPhone)
            put("notes", notes)
        }
        return db.insert(AppDatabaseHelper.TABLE_ORDERS, null, values)
    }

    fun getAllOrders(): List<Order> {
        val list = mutableListOf<Order>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(AppDatabaseHelper.TABLE_ORDERS, null, null, null, null, null, "order_date DESC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Order(
                            orderId = it.getLong(it.getColumnIndexOrThrow("order_id")),
                            customerId = if (it.isNull(it.getColumnIndexOrThrow("customer_id"))) null else it.getLong(it.getColumnIndexOrThrow("customer_id")),
                            createdBy = it.getLong(it.getColumnIndexOrThrow("created_by")),
                            orderDate = it.getString(it.getColumnIndexOrThrow("order_date")),
                            status = it.getString(it.getColumnIndexOrThrow("status")),
                            customerName = it.getString(it.getColumnIndexOrThrow("customer_name")),
                            customerPhone = it.getString(it.getColumnIndexOrThrow("customer_phone")),
                            notes = it.getString(it.getColumnIndexOrThrow("notes"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun updateOrderStatus(orderId: Long, status: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("status", status) }
        return db.update(AppDatabaseHelper.TABLE_ORDERS, values, "order_id = ?", arrayOf(orderId.toString()))
    }
}
