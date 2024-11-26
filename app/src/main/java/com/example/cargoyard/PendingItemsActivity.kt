package com.example.cargoyard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.adapter.InventoryAdapterFinance
import com.example.cargoyard.models.InventoryItem
import com.example.cargoyard.models.ItemStatus
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PendingItemsActivity : AppCompatActivity(), DialogRelocateItemFragment.RelocateItemListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var inventoryAdapter: InventoryAdapterFinance
    private val inventoryList = mutableListOf<InventoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_inventory_finance)
        inventoryAdapter = InventoryAdapterFinance(inventoryList, this::showRelocateDialog, this::checkoutItem)
        val toolbar:Toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pending transactions"
        val rvInventory = findViewById<RecyclerView>(R.id.rvInventory)
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = inventoryAdapter
        val searchView: SearchView = findViewById(R.id.searchView)


        retrieveInventoryItems()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                inventoryAdapter.filter(newText ?: "")
                return true
            }
        })
    }

    // Implement the onLocationEntered method from RelocateItemListener
    override fun onLocationEntered(itemId: String, newLocation: String) {
        db.collection("inventory").document(itemId)
            .update("location", newLocation)
            .addOnSuccessListener {
                Toast.makeText(this, "Item relocated successfully", Toast.LENGTH_SHORT).show()
                retrieveInventoryItems() // Refresh the inventory list to show the updated location
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to relocate item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchAvailableSpaces(callback: (List<String>) -> Unit) {
        val spaces = mutableListOf<String>()
        val db = FirebaseFirestore.getInstance()

        db.collection("spaces") // Assuming your spaces are stored in a "spaces" collection
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val spaceName = document.getString("spaceName") // Assuming space has a field named "name"
                    if (spaceName != null) {
                        spaces.add(spaceName)
                    }
                }
                callback(spaces) // Return the list of spaces through the callback
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve spaces: ${e.message}", Toast.LENGTH_SHORT).show()
                callback(emptyList()) // Return an empty list in case of failure
            }
    }

    private fun addItemToFirestore(item: InventoryItem) {
        db.collection("inventory").document(item.itemId)
            .set(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRelocateDialog(item: InventoryItem) {
        val dialog = DialogRelocateItemFragment.newInstance(item.itemId)
        dialog.show(supportFragmentManager, "RelocateItemDialogFragment")
    }

    private fun checkoutItem(item: InventoryItem) {
        db.collection("inventory").document(item.itemId)
            .update("status", ItemStatus.CHECKED_OUT.name)
            .addOnSuccessListener {
                Toast.makeText(this, "Item checked out successfully", Toast.LENGTH_SHORT).show()
                retrieveInventoryItems()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to checkout item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun retrieveInventoryItems() {
        db.collection("inventory")
            .get()
            .addOnSuccessListener { result ->
                val inventoryList = result.mapNotNull { document ->
                    val item = document.toObject(InventoryItem::class.java)
                    if (item.status == ItemStatus.PENDING) item else null
                }
                inventoryAdapter.updateData(inventoryList)
                Toast.makeText(
                    this,
                    "Retrieved ${inventoryList.size} pending items",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve items: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
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

