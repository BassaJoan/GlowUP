package com.example.glowup.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.data.ServiceRepository
import com.example.glowup.model.Service
import com.example.glowup.ui.adapter.ServiceCardAdapter

class CatalogueActivity : AppCompatActivity() {
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var lvCatalogue: ListView
    private var serviceList: List<Service> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)

        serviceRepository = ServiceRepository(this)
        lvCatalogue = findViewById(R.id.lvCatalogue)

        findViewById<Button>(R.id.btnAddItem).setOnClickListener {
            startActivity(Intent(this, EditCatalogueItemActivity::class.java))
        }

        lvCatalogue.setOnItemClickListener { _, _, position, _ ->
            val service = serviceList[position]
            val intent = Intent(this, EditCatalogueItemActivity::class.java).apply {
                putExtra("SERVICE_ID", service.serviceId)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadCatalogue()
    }

    private fun loadCatalogue() {
        serviceList = serviceRepository.getAllServices()
        val adapter = ServiceCardAdapter(this, serviceList)
        lvCatalogue.adapter = adapter
    }
}
