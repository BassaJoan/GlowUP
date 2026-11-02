package com.example.glowup.client

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.ServiceRepository
import com.example.glowup.model.Service

class ServiceDetailsActivity : AppCompatActivity() {
    private lateinit var serviceRepository: ServiceRepository
    private var currentService: Service? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_details)

        serviceRepository = ServiceRepository(this)
        val serviceId = intent.getLongExtra("SERVICE_ID", -1L)
        currentService = serviceRepository.getServiceById(serviceId)

        val ivCategoryIcon = findViewById<ImageView>(R.id.ivCategoryIcon)
        val tvName = findViewById<TextView>(R.id.tvServiceName)
        val tvCategory = findViewById<TextView>(R.id.tvServiceCategory)
        val tvDesc = findViewById<TextView>(R.id.tvServiceDescription)
        val tvPriceDuration = findViewById<TextView>(R.id.tvServicePriceDuration)
        val btnProceed = findViewById<Button>(R.id.btnProceedToBook)

        currentService?.let { service ->
            val iconRes = when (service.category.lowercase()) {
                "hair" -> R.drawable.ic_cat_hair
                "barber" -> R.drawable.ic_cat_barber
                "nails" -> R.drawable.ic_cat_nails
                "skin care", "skincare" -> R.drawable.ic_cat_skincare
                "makeup" -> R.drawable.ic_cat_makeup
                "lash" -> R.drawable.ic_cat_lash
                "spas", "spa" -> R.drawable.ic_cat_spas
                else -> R.drawable.ic_cat_hair
            }
            ivCategoryIcon.setImageResource(iconRes)

            tvName.text = service.name
            tvCategory.text = "Category: ${service.category}"
            tvDesc.text = service.description
            tvPriceDuration.text = "Price: UGX ${service.priceMinor} | Duration: ${service.durationMinutes} mins"

            btnProceed.setOnClickListener {
                val intent = Intent(this, BookingActivity::class.java).apply {
                    putExtra("SERVICE_ID", service.serviceId)
                }
                startActivity(intent)
            }
        } ?: run {
            tvName.text = "Service not found"
            btnProceed.isEnabled = false
        }
    }
}
