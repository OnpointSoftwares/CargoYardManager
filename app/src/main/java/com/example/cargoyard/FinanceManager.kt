package com.example.cargoyard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FinanceManager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_finance_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cardSettings:CardView=findViewById(R.id.cardSettings)
        val cardPending:CardView=findViewById(R.id.cardPending)
        val cardReports:CardView=findViewById(R.id.cardReports)
        cardSettings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java))
        }
        cardPending.setOnClickListener {
            startActivity(Intent(this,PendingItemsActivity::class.java))
        }
        cardReports.setOnClickListener {
            startActivity(Intent(this,ReportsActivity::class.java))
        }
        
    }
}