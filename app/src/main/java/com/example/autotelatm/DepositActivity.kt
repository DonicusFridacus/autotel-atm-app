package com.example.autotelatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class DepositActivity : AppCompatActivity() {

    private lateinit var depositAmount: EditText
    private lateinit var depositButton: Button
    private lateinit var depositStatus: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var paymentMethod: String? = null
    private var balance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("ATMPreferences", Context.MODE_PRIVATE)

        paymentMethod = intent.getStringExtra("paymentMethod")

        if (paymentMethod == null) {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        balance = if (paymentMethod == "cash") {
            sharedPreferences.getFloat("cashBalance", 1000.0f).toDouble()
        } else {
            sharedPreferences.getFloat("creditCardBalance", 1000.0f).toDouble()
        }

        depositAmount = findViewById(R.id.depositAmount)
        depositButton = findViewById(R.id.depositButton)
        depositStatus = findViewById(R.id.depositStatus)

        depositButton.setOnClickListener {
            val amount = depositAmount.text.toString().toFloat()
            deposit(amount)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_deposit
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_balance -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("paymentMethod", paymentMethod)
                    })
                    true
                }
                R.id.nav_withdraw -> {
                    startActivity(Intent(this, WithdrawActivity::class.java).apply {
                        putExtra("paymentMethod", paymentMethod)
                    })
                    true
                }
                R.id.nav_deposit -> {
                    true
                }
                R.id.nav_payment_method -> {
                    startActivity(Intent(this, PaymentMethodActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }

    private fun deposit(amount: Float) {
        val currentBalance = if (paymentMethod == "cash") {
            sharedPreferences.getFloat("cashBalance", 1000.0f)
        } else {
            sharedPreferences.getFloat("creditCardBalance", 1000.0f)
        }

        val newBalance = currentBalance + amount
        if (paymentMethod == "cash") {
            sharedPreferences.edit().putFloat("cashBalance", newBalance).apply()
        } else {
            sharedPreferences.edit().putFloat("creditCardBalance", newBalance).apply()
        }
        showSuccessPopup(newBalance)
    }

    private fun showInvalidValuePopup() {
        AlertDialog.Builder(this)
            .setTitle("Invalid Value")
            .setMessage("Please enter a valid amount.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSuccessPopup(newBalance: Float) {
        AlertDialog.Builder(this)
            .setTitle("Deposit Successful")
            .setMessage("New balance: $newBalance")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("paymentMethod", paymentMethod)
                    startActivity(intent)
                    finish()
                }, 500) // 500 millisecond delay
            }
            .show()
    }
}