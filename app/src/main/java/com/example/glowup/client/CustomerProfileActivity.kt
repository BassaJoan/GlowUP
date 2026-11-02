package com.example.glowup.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.glowup.R
import com.example.glowup.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class CustomerProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        sessionManager = SessionManager(this)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvPhone = findViewById<TextView>(R.id.tvProfilePhone)

        val currentName = sessionManager.getFullName() ?: "Tamale Matia"
        val currentUsername = sessionManager.getUsername() ?: "client1"

        tvName.text = currentName
        tvPhone.text = "Username: $currentUsername | Role: CLIENT"

        findViewById<Button>(R.id.btnEditPersonalDetails).setOnClickListener {
            showEditProfileDialog(tvName)
        }

        findViewById<Button>(R.id.btnSavedSalons).setOnClickListener {
            showSavedSalonsDialog()
        }

        findViewById<Button>(R.id.btnReferralProgram).setOnClickListener {
            showReferralBottomSheet()
        }

        findViewById<Button>(R.id.btnNotificationSettings).setOnClickListener {
            showNotificationPreferencesDialog()
        }

        findViewById<Button>(R.id.btnCustomerSupport).setOnClickListener {
            showSupportDialog()
        }
    }

    private fun showEditProfileDialog(tvName: TextView) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 30, 50, 30)
        }

        val etName = EditText(this).apply {
            hint = "Full Name"
            setText(tvName.text.toString())
        }
        val etAddress = EditText(this).apply {
            hint = "Saved Home / Studio Address"
            setText("Plot 12, Kampala Road, Uganda")
        }

        layout.addView(etName)
        layout.addView(etAddress)

        AlertDialog.Builder(this)
            .setTitle("Edit Profile & Address")
            .setView(layout)
            .setPositiveButton("Save Changes") { _, _ ->
                val newName = etName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    tvName.text = newName
                    Toast.makeText(this, "Profile and address saved successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSavedSalonsDialog() {
        val items = arrayOf(
            "Glamour Haven (Musiimenta Zam)",
            "The Hair Studio (Poni Ritah Minga)",
            "Salon De Luxe (Omoding Julius)"
        )
        AlertDialog.Builder(this)
            .setTitle("Saved Salons & Specialists")
            .setItems(items) { _, which ->
                Toast.makeText(this, "Opening ${items[which]}...", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Close", null)
            .show()
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

        // 1. Copy Code with Visual Feedback
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

        // 2. WhatsApp Direct Share
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

        // 3. Direct SMS Share
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

        // 4. Direct Email Share
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

        // 5. Android System Share Sheet Chooser
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

    private fun showNotificationPreferencesDialog() {
        val options = booleanArrayOf(true, true, false)
        val items = arrayOf("SMS Booking Reminders (30 min before)", "Push Notifications", "Email Promotional Deals")

        AlertDialog.Builder(this)
            .setTitle("Reminders & Preferences")
            .setMultiChoiceItems(items, options) { _, which, isChecked ->
                options[which] = isChecked
            }
            .setPositiveButton("Save Preferences") { _, _ ->
                Toast.makeText(this, "Notification preferences updated!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSupportDialog() {
        val etIssue = EditText(this).apply {
            hint = "Describe your problem or refund request..."
            minLines = 3
        }

        AlertDialog.Builder(this)
            .setTitle("Customer Support & Refund")
            .setView(etIssue)
            .setPositiveButton("Submit Ticket") { _, _ ->
                Toast.makeText(this, "Support ticket submitted! Our agent will respond shortly.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
