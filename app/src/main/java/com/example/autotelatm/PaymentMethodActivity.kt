package com.example.autotelatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Button

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        findViewById<Button>(R.id.cashButton).setOnClickListener {
            selectPaymentMethod("cash")
        }

        findViewById<Button>(R.id.creditCardButton).setOnClickListener {
            selectPaymentMethod("credit_card")
        }
    }

    private fun selectPaymentMethod(method: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("paymentMethod", method)
        startActivity(intent)
        finish()
    }
}