package com.example.glowup.employee

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.ServiceRepository
import com.example.glowup.model.Service
import com.example.glowup.util.InputValidator

class EditCatalogueItemActivity : AppCompatActivity() {
    private lateinit var serviceRepository: ServiceRepository
    private var serviceId: Long = -1L
    private var existingService: Service? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_catalogue_item)

        serviceRepository = ServiceRepository(this)
        serviceId = intent.getLongExtra("SERVICE_ID", -1L)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val etName = findViewById<EditText>(R.id.etName)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val btnSave = findViewById<Button>(R.id.btnSaveItem)

        if (serviceId != -1L) {
            tvTitle.text = "Edit Service"
            existingService = serviceRepository.getServiceById(serviceId)
            existingService?.let {
                etName.setText(it.name)
                etCategory.setText(it.category)
                etDescription.setText(it.description)
                etPrice.setText(it.priceMinor.toString())
                etDuration.setText(it.durationMinutes.toString())
            }
        } else {
            tvTitle.text = "Add Service"
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val priceStr = etPrice.text.toString().trim()
            val durationStr = etDuration.text.toString().trim()

            if (!InputValidator.isNotBlank(name, category, description, priceStr, durationStr)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priceMinor = priceStr.toIntOrNull() ?: -1
            val durationMinutes = durationStr.toIntOrNull() ?: -1

            if (!InputValidator.isValidPrice(priceMinor) || !InputValidator.isValidDuration(durationMinutes)) {
                Toast.makeText(this, "Invalid price or duration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (serviceId == -1L) {
                serviceRepository.insertService(name, category, description, priceMinor, durationMinutes)
                Toast.makeText(this, "Service added", Toast.LENGTH_SHORT).show()
            } else {
                serviceRepository.updateService(serviceId, name, category, description, priceMinor, durationMinutes)
                Toast.makeText(this, "Service updated", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}
