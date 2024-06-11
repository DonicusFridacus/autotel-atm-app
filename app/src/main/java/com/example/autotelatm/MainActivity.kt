package com.example.autotelatm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var withdrawButton: Button
    private lateinit var depositButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var paymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("ATMPreferences", Context.MODE_PRIVATE)

        balanceText = findViewById(R.id.balanceText)
        withdrawButton = findViewById(R.id.withdrawButton)
        depositButton = findViewById(R.id.depositButton)

        paymentMethod = intent.getStringExtra("paymentMethod")

        if (paymentMethod == null) {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity to prevent the user from going back to it without selecting a payment method
            return
        }

        withdrawButton.setOnClickListener {
            startActivity(Intent(this, WithdrawActivity::class.java).apply {
                putExtra("paymentMethod", paymentMethod)
            })
        }

        depositButton.setOnClickListener {
            startActivity(Intent(this, DepositActivity::class.java).apply {
                putExtra("paymentMethod", paymentMethod)
            })
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_balance -> {
                    true
                }
                R.id.nav_withdraw -> {
                    startActivity(Intent(this, WithdrawActivity::class.java).apply {
                        putExtra("paymentMethod", paymentMethod)
                    })
                    true
                }
                R.id.nav_deposit -> {
                    startActivity(Intent(this, DepositActivity::class.java).apply {
                        putExtra("paymentMethod", paymentMethod)
                    })
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

    override fun onResume() {
        super.onResume()
        updateBalance()
    }

    private fun updateBalance() {
        val balance = if (paymentMethod == "cash") {
            sharedPreferences.getFloat("cashBalance", 1000.00f)
        } else {
            sharedPreferences.getFloat("creditCardBalance", 1000.00f)
        }
        balanceText.text = "Current Balance: \n$$balance"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_tutorial -> {
                showTutorialDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTutorialDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("How to Use")
        dialogBuilder.setMessage("After you have selected the payment method, you can choose either to withdraw or deposit." +
                "\n\nIf you wish to change the payment method, you can do so by selecting the icon below.")
        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }
}

