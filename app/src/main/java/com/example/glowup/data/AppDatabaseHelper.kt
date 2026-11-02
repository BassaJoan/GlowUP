package com.example.glowup.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.glowup.util.PasswordHasher

class AppDatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "glowup.db"
        private const val DATABASE_VERSION = 6

        const val TABLE_USERS = "users"
        const val TABLE_SERVICES = "services"
        const val TABLE_PRODUCTS = "products"
        const val TABLE_APPOINTMENTS = "appointments"
        const val TABLE_ORDERS = "orders"
        const val TABLE_ORDER_ITEMS = "order_items"
        const val TABLE_REVIEWS = "reviews"

        @Volatile
        private var INSTANCE: AppDatabaseHelper? = null

        fun getInstance(context: Context): AppDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabaseHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('CLIENT', 'EMPLOYEE', 'OWNER', 'TECHNICIAN', 'ADMIN')),
                phone TEXT NOT NULL,
                created_at TEXT NOT NULL
            )
        """

        val createServicesTable = """
            CREATE TABLE $TABLE_SERVICES (
                service_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT NOT NULL,
                price_minor INTEGER NOT NULL,
                duration_minutes INTEGER NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 1
            )
        """

        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                product_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT NOT NULL,
                price_minor INTEGER NOT NULL,
                stock_quantity INTEGER NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 1
            )
        """

        val createAppointmentsTable = """
            CREATE TABLE $TABLE_APPOINTMENTS (
                appointment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_id INTEGER NOT NULL,
                service_id INTEGER NOT NULL,
                appointment_date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                location_mode TEXT NOT NULL,
                status TEXT NOT NULL,
                notes TEXT,
                created_at TEXT NOT NULL,
                FOREIGN KEY (client_id) REFERENCES $TABLE_USERS(user_id) ON DELETE CASCADE,
                FOREIGN KEY (service_id) REFERENCES $TABLE_SERVICES(service_id) ON DELETE CASCADE
            )
        """

        val createOrdersTable = """
            CREATE TABLE $TABLE_ORDERS (
                order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                created_by INTEGER NOT NULL,
                order_date TEXT NOT NULL,
                status TEXT NOT NULL,
                customer_name TEXT,
                customer_phone TEXT,
                notes TEXT,
                FOREIGN KEY (customer_id) REFERENCES $TABLE_USERS(user_id) ON DELETE SET NULL,
                FOREIGN KEY (created_by) REFERENCES $TABLE_USERS(user_id) ON DELETE CASCADE
            )
        """

        val createOrderItemsTable = """
            CREATE TABLE $TABLE_ORDER_ITEMS (
                order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                item_type TEXT NOT NULL,
                service_id INTEGER,
                product_id INTEGER,
                quantity INTEGER NOT NULL,
                unit_price_minor INTEGER NOT NULL,
                FOREIGN KEY (order_id) REFERENCES $TABLE_ORDERS(order_id) ON DELETE CASCADE,
                FOREIGN KEY (service_id) REFERENCES $TABLE_SERVICES(service_id) ON DELETE SET NULL,
                FOREIGN KEY (product_id) REFERENCES $TABLE_PRODUCTS(product_id) ON DELETE SET NULL
            )
        """

        val createReviewsTable = """
            CREATE TABLE $TABLE_REVIEWS (
                review_id INTEGER PRIMARY KEY AUTOINCREMENT,
                appointment_id INTEGER UNIQUE NOT NULL,
                client_id INTEGER NOT NULL,
                rating INTEGER NOT NULL CHECK(rating BETWEEN 1 AND 5),
                comment TEXT,
                created_at TEXT NOT NULL,
                FOREIGN KEY (appointment_id) REFERENCES $TABLE_APPOINTMENTS(appointment_id) ON DELETE CASCADE,
                FOREIGN KEY (client_id) REFERENCES $TABLE_USERS(user_id) ON DELETE CASCADE
            )
        """

        db.execSQL(createUsersTable)
        db.execSQL(createServicesTable)
        db.execSQL(createProductsTable)
        db.execSQL(createAppointmentsTable)
        db.execSQL(createOrdersTable)
        db.execSQL(createOrderItemsTable)
        db.execSQL(createReviewsTable)

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SERVICES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val hashedPass = PasswordHasher.hash("password123")
        val adminPass = PasswordHasher.hash("123456")
        val now = "2026-10-08 10:00:00"

        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Joan Atimaku', 'JOAN ATIMAKU', '$adminPass', 'ADMIN', '555-0999', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Omoding Dickens Derrick', 'owner1', '$hashedPass', 'OWNER', '555-0100', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Musiimenta Zam', 'tech1', '$hashedPass', 'TECHNICIAN', '555-0101', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Omoding Julius', 'indie1', '$hashedPass', 'TECHNICIAN', '555-0102', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Poni Ritah Minga', 'employee1', '$hashedPass', 'EMPLOYEE', '555-0103', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Tamale Matia', 'client1', '$hashedPass', 'CLIENT', '555-0200', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Asimwe Joan Eron', 'client2', '$hashedPass', 'CLIENT', '555-0201', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Kizza Coline', 'client3', '$hashedPass', 'CLIENT', '555-0202', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('Grace Akello', 'client4', '$hashedPass', 'CLIENT', '555-0203', '$now')")
        db.execSQL("INSERT INTO $TABLE_USERS (full_name, username, password_hash, role, phone, created_at) VALUES ('David Okello', 'client5', '$hashedPass', 'CLIENT', '555-0204', '$now')")

        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Hair Styling', 'Hair', 'Professional haircut and styling', 45000, 45, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Gel Manicure', 'Nails', 'Long-lasting gel nail polish', 25000, 30, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Full Glam Makeup', 'Makeup', 'Full face makeup for special events', 75000, 60, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Classic Eyelash Extensions', 'Lash', 'Natural-look lash extensions', 50000, 60, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Classic Men Haircut', 'Barber', 'Professional fade and trim', 20000, 30, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Hydrating Facial Treatment', 'Skin Care', 'Deep cleansing and hydration', 60000, 50, 1)")
        db.execSQL("INSERT INTO $TABLE_SERVICES (name, category, description, price_minor, duration_minutes, is_active) VALUES ('Relaxing Swedish Massage', 'Spas', 'Full body relaxation massage', 50000, 60, 1)")

        db.execSQL("INSERT INTO $TABLE_PRODUCTS (name, category, description, price_minor, stock_quantity, is_active) VALUES ('Argan Hair Oil', 'Hair', 'Nourishing hair oil 100ml', 30000, 15, 1)")
        db.execSQL("INSERT INTO $TABLE_PRODUCTS (name, category, description, price_minor, stock_quantity, is_active) VALUES ('Cuticle Care Serum', 'Nails', 'Hydrating cuticle care oil', 15000, 25, 1)")
    }
}
