package com.example.glowup.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.admin.AdminDashboardActivity
import com.example.glowup.auth.LoginActivity
import com.example.glowup.data.ServiceRepository
import com.example.glowup.model.Service
import com.example.glowup.ui.adapter.ServiceCardAdapter
import com.example.glowup.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class ClientHomeActivity : AppCompatActivity() {
    private lateinit var serviceRepository: ServiceRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var lvServices: ListView
    private var allServicesList: List<Service> = listOf()
    private var serviceList: List<Service> = listOf()

    private lateinit var btnProfile: Button
    private lateinit var btnAdmin: Button
    private lateinit var btnMyBookings: Button
    private lateinit var btnLogout: Button

    private lateinit var btnCategoryAll: Button
    private lateinit var btnCategoryHair: Button
    private lateinit var btnCategoryNails: Button
    private lateinit var btnCategoryMakeup: Button
    private lateinit var btnCategoryLash: Button
    private lateinit var btnCategoryBarber: Button
    private lateinit var btnCategorySkinCare: Button
    private lateinit var btnCategorySpas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)

        serviceRepository = ServiceRepository(this)
        sessionManager = SessionManager(this)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Welcome, ${sessionManager.getFullName() ?: "Client"}"

        btnProfile = findViewById(R.id.btnProfile)
        btnAdmin = findViewById(R.id.btnAdminDashboard)
        btnMyBookings = findViewById(R.id.btnMyBookings)
        btnLogout = findViewById(R.id.btnLogout)

        if (sessionManager.getRole() == "ADMIN") {
            btnAdmin.visibility = View.VISIBLE
        }

        btnProfile.setOnClickListener {
            selectHeaderTab(btnProfile)
            startActivity(Intent(this, CustomerProfileActivity::class.java))
        }

        btnAdmin.setOnClickListener {
            selectHeaderTab(btnAdmin)
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }

        btnMyBookings.setOnClickListener {
            selectHeaderTab(btnMyBookings)
            startActivity(Intent(this, MyBookingsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            selectHeaderTab(btnLogout)
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        // Special Offer Banner Click -> Opens Referral Sheet for ANY account holder
        val cardPromo = findViewById<LinearLayout>(R.id.cardSpecialOffer)
        cardPromo.setOnClickListener {
            showReferralBottomSheet()
        }

        lvServices = findViewById(R.id.lvServices)

        // Bind Category Buttons
        btnCategoryAll = findViewById(R.id.btnCategoryAll)
        btnCategoryHair = findViewById(R.id.btnCategoryHair)
        btnCategoryNails = findViewById(R.id.btnCategoryNails)
        btnCategoryMakeup = findViewById(R.id.btnCategoryMakeup)
        btnCategoryLash = findViewById(R.id.btnCategoryLash)
        btnCategoryBarber = findViewById(R.id.btnCategoryBarber)
        btnCategorySkinCare = findViewById(R.id.btnCategorySkinCare)
        btnCategorySpas = findViewById(R.id.btnCategorySpas)

        // Set default selection state to 'All'
        selectCategory(btnCategoryAll, null)

        // Attach dynamic tap handlers for instant state and list updates
        btnCategoryAll.setOnClickListener { selectCategory(btnCategoryAll, null) }
        btnCategoryHair.setOnClickListener { selectCategory(btnCategoryHair, "Hair") }
        btnCategoryNails.setOnClickListener { selectCategory(btnCategoryNails, "Nails") }
        btnCategoryMakeup.setOnClickListener { selectCategory(btnCategoryMakeup, "Makeup") }
        btnCategoryLash.setOnClickListener { selectCategory(btnCategoryLash, "Lash") }
        btnCategoryBarber.setOnClickListener { selectCategory(btnCategoryBarber, "Barber") }
        btnCategorySkinCare.setOnClickListener { selectCategory(btnCategorySkinCare, "Skin Care") }
        btnCategorySpas.setOnClickListener { selectCategory(btnCategorySpas, "Spas") }

        val etSearch = findViewById<EditText>(R.id.etSearchServices)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                filterByQuery(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        lvServices.setOnItemClickListener { _, _, position, _ ->
            val service = serviceList[position]
            val intent = Intent(this, SpecialistSelectionActivity::class.java).apply {
                putExtra("SERVICE_ID", service.serviceId)
            }
            startActivity(intent)
        }
    }

    private fun showReferralBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_referral_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        val referralCode = "GLOWUP5"
        val inviteMessage = "Hey! Use my referral code $referralCode on GlowUp Beauty to get 5% OFF your first booking! Download the app today."

        val tvCode = view.findViewById<TextView>(R.id.tvReferralCode)
        val btnCopy = view.findViewById<Button>(R.id.btnCopyReferralCode)
        val btnWhatsApp = view.findViewById<Button>(R.id.btnShareWhatsApp)
        val btnSMS = view.findViewById<Button>(R.id.btnShareSMS)
        val btnEmail = view.findViewById<Button>(R.id.btnShareEmail)
        val btnSocials = view.findViewById<Button>(R.id.btnShareSocials)

        tvCode.text = referralCode

        btnCopy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("GlowUp Referral Code", referralCode)
            clipboard.setPrimaryClip(clip)

            btnCopy.text = "Copied! ✓"
            btnCopy.isEnabled = false
            Toast.makeText(this, "Referral code $referralCode copied to clipboard!", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                btnCopy.text = "Copy Code"
                btnCopy.isEnabled = true
            }, 2000)
        }

        btnWhatsApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
                setPackage("com.whatsapp")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, inviteMessage)
                }, "Share via...")
                startActivity(chooser)
            }
        }

        btnSMS.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", inviteMessage)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Messaging app not available", Toast.LENGTH_SHORT).show()
            }
        }

        btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "5% OFF Beauty Booking Invitation - GlowUp")
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Email app not available", Toast.LENGTH_SHORT).show()
            }
        }

        btnSocials.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "GlowUp Referral Invitation")
                putExtra(Intent.EXTRA_TEXT, inviteMessage)
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share Referral Code via...")
            startActivity(shareIntent)
        }

        bottomSheetDialog.show()
    }

    private fun selectHeaderTab(activeButton: Button) {
        val headerTabs = listOf(btnProfile, btnAdmin, btnMyBookings, btnLogout)
        headerTabs.forEach { btn ->
            if (btn == activeButton) {
                btn.setBackgroundResource(R.drawable.bg_button_primary)
                btn.setTextColor(getColor(R.color.colorOnPrimary))
            } else {
                btn.setBackgroundResource(R.drawable.bg_input)
                btn.setTextColor(getColor(R.color.colorTextPrimary))
            }
        }
    }

    private fun selectCategory(selectedButton: Button, category: String?) {
        val categoryButtons = listOf(
            btnCategoryAll,
            btnCategoryHair,
            btnCategoryNails,
            btnCategoryMakeup,
            btnCategoryLash,
            btnCategoryBarber,
            btnCategorySkinCare,
            btnCategorySpas
        )

        categoryButtons.forEach { btn ->
            if (btn == selectedButton) {
                btn.setBackgroundResource(R.drawable.bg_button_primary)
                btn.setTextColor(getColor(R.color.colorOnPrimary))
            } else {
                btn.setBackgroundResource(R.drawable.bg_input)
                btn.setTextColor(getColor(R.color.colorTextPrimary))
            }
        }

        loadServices(category)
    }

    private fun loadServices(category: String?) {
        allServicesList = if (category == null) {
            serviceRepository.getAllServices()
        } else {
            serviceRepository.getServicesByCategory(category)
        }
        serviceList = allServicesList
        val adapter = ServiceCardAdapter(this, serviceList)
        lvServices.adapter = adapter
    }

    private fun filterByQuery(query: String) {
        serviceList = if (query.isEmpty()) {
            allServicesList
        } else {
            allServicesList.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }
        val adapter = ServiceCardAdapter(this, serviceList)
        lvServices.adapter = adapter
    }
}
