package com.example.cargoyard

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SupervisorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cargo_yard_manager_dashboard)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Enable the back button on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dashboard" // Set the title
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val settingscard:CardView=findViewById(R.id.cardSettings)
        val usersCard:CardView=findViewById(R.id.cardUsers)
        val inventorycard:CardView=findViewById(R.id.cardinventory)
        val reportcard:CardView=findViewById(R.id.cardReports)
        val spacesCard:CardView=findViewById(R.id.cardSpaces)
        val userType="supervisor"
        reportcard.setOnClickListener {
            val intent=Intent(this,ReportsActivity::class.java)
            intent.putExtra("user",userType)
            startActivity(intent)
        }
        usersCard.setOnClickListener {
            val intent=Intent(this,UserList::class.java)
            intent.putExtra("user",userType)
            startActivity(intent)
        }
        inventorycard.setOnClickListener {
            val intent=Intent(this,ViewInventory::class.java)
            intent.putExtra("user",userType)
            startActivity(intent)
        }
        settingscard.setOnClickListener {
            val intent=Intent(this,SettingsActivity::class.java)
            intent.putExtra("user",userType)
            startActivity(intent)
        }
        spacesCard.setOnClickListener {
            val intent=Intent(this,ManageSpacesActivity::class.java)
            intent.putExtra("user",userType)
            startActivity(intent)
        }
    }
}