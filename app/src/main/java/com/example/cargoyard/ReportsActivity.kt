package com.example.cargoyard

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.RecentActivityAdapter
import com.example.cargoyard.models.InventoryItem
import com.example.cargoyard.models.ItemStatus
import com.google.firebase.firestore.FirebaseFirestore

class ReportsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var totalItemsTextView: TextView
    private lateinit var availableItemsTextView: TextView
    private lateinit var checkedOutItemsTextView: TextView
    private lateinit var reservedItemsTextView: TextView
    private lateinit var damagedItemsTextView: TextView
    private lateinit var pendingItemsTextView: TextView
    private lateinit var recentActivityRecyclerView: RecyclerView
    private lateinit var recentActivityAdapter: RecentActivityAdapter

    private val recentActivityList = mutableListOf<InventoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

// Customize the action bar
        supportActionBar?.apply {
            title = "Reports"  // Set the title of the ActionBar
            setDisplayHomeAsUpEnabled(true)  // Show the back button in the ActionBar
            // Optional: Set a custom back arrow icon (default is an arrow)
        }
        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize views
        totalItemsTextView = findViewById(R.id.tvTotalItems)
        availableItemsTextView = findViewById(R.id.tvAvailableItems)
        checkedOutItemsTextView = findViewById(R.id.tvCheckedOutItems)
        reservedItemsTextView = findViewById(R.id.tvReservedItems)
        damagedItemsTextView = findViewById(R.id.tvDamagedItems)
        recentActivityRecyclerView = findViewById(R.id.rvRecentActivity)
        pendingItemsTextView = findViewById(R.id.tvPendingItems)
        // Setup RecyclerView
        recentActivityAdapter = RecentActivityAdapter(recentActivityList)
        recentActivityRecyclerView.layoutManager = LinearLayoutManager(this)
        recentActivityRecyclerView.adapter = recentActivityAdapter

        // Load data from Firestore
        loadInventoryData()
    }

    private fun loadInventoryData() {
        db.collection("inventory")
            .get()
            .addOnSuccessListener { result ->
                var totalItems = 0
                var availableItems = 0
                var checkedOutItems = 0
                var reservedItems = 0
                var damagedItems = 0
                var pendingItems = 0
                recentActivityList.clear()

                for (document in result) {
                    val item = document.toObject(InventoryItem::class.java)
                    totalItems++

                    when (item.status) {
                        ItemStatus.AVAILABLE -> availableItems++
                        ItemStatus.CHECKED_OUT -> checkedOutItems++
                        ItemStatus.RESERVED -> reservedItems++
                        ItemStatus.DAMAGED -> damagedItems++
                        ItemStatus.PENDING -> pendingItems++
                    }

                    // Add item to recent activity list (optional: filter for recent changes)
                    recentActivityList.add(item)
                }

                // Update summary texts
                totalItemsTextView.text = "Total Items: $totalItems"
                availableItemsTextView.text = "Available Items: $availableItems"
                checkedOutItemsTextView.text = "Checked Out Items: $checkedOutItems"
                reservedItemsTextView.text = "Reserved Items: $reservedItems"
                damagedItemsTextView.text = "Damaged Items: $damagedItems"
                pendingItemsTextView.text = "Pending Items: $pendingItems"
                // Update RecyclerView
                recentActivityAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle error
                e.printStackTrace()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to the previous screen
                onBackPressed()  // This is the default behavior for the back button
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
