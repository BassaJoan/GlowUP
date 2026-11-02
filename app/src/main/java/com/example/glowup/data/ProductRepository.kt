package com.example.glowup.data

import android.content.ContentValues
import android.content.Context
import com.example.glowup.model.Product

class ProductRepository(context: Context) {
    private val dbHelper = AppDatabaseHelper.getInstance(context)

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(AppDatabaseHelper.TABLE_PRODUCTS, null, "is_active = 1", null, null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Product(
                            productId = it.getLong(it.getColumnIndexOrThrow("product_id")),
                            name = it.getString(it.getColumnIndexOrThrow("name")),
                            category = it.getString(it.getColumnIndexOrThrow("category")),
                            description = it.getString(it.getColumnIndexOrThrow("description")),
                            priceMinor = it.getInt(it.getColumnIndexOrThrow("price_minor")),
                            stockQuantity = it.getInt(it.getColumnIndexOrThrow("stock_quantity")),
                            isActive = it.getInt(it.getColumnIndexOrThrow("is_active"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun insertProduct(name: String, category: String, description: String, priceMinor: Int, stockQuantity: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("description", description)
            put("price_minor", priceMinor)
            put("stock_quantity", stockQuantity)
            put("is_active", 1)
        }
        return db.insert(AppDatabaseHelper.TABLE_PRODUCTS, null, values)
    }

    fun updateProduct(productId: Long, name: String, category: String, description: String, priceMinor: Int, stockQuantity: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("category", category)
            put("description", description)
            put("price_minor", priceMinor)
            put("stock_quantity", stockQuantity)
        }
        return db.update(AppDatabaseHelper.TABLE_PRODUCTS, values, "product_id = ?", arrayOf(productId.toString()))
    }

    fun deleteProduct(productId: Long): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put("is_active", 0) }
        return db.update(AppDatabaseHelper.TABLE_PRODUCTS, values, "product_id = ?", arrayOf(productId.toString()))
    }
}
