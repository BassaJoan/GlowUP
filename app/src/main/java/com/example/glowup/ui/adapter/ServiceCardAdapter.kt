package com.example.glowup.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.glowup.R
import com.example.glowup.model.Service

class ServiceCardAdapter(context: Context, private val services: List<Service>) :
    ArrayAdapter<Service>(context, R.layout.item_service_card, services) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_service_card, parent, false)
        val service = services[position]

        val ivIcon = view.findViewById<ImageView>(R.id.ivCategoryIcon)
        val tvName = view.findViewById<TextView>(R.id.tvServiceName)
        val tvPrice = view.findViewById<TextView>(R.id.tvServicePrice)
        val tvCategoryDuration = view.findViewById<TextView>(R.id.tvServiceCategoryDuration)

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
        ivIcon.setImageResource(iconRes)

        tvName.text = service.name
        tvPrice.text = "UGX ${service.priceMinor}"
        tvCategoryDuration.text = "${service.category} • ${service.durationMinutes} mins"

        return view
    }
}
