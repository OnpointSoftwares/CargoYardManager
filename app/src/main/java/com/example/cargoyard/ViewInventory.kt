package com.example.cargoyard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.adapter.InventoryAdapter
import com.example.cargoyard.models.InventoryItem
import com.example.cargoyard.models.ItemStatus
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ViewInventory : AppCompatActivity(), DialogRelocateItemFragment.RelocateItemListener {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var inventoryAdapter: InventoryAdapter
    private val inventoryList = mutableListOf<InventoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_inventory)
        inventoryAdapter = InventoryAdapter(inventoryList, "",this::showRelocateDialog, this::checkoutItem)

        val rvInventory = findViewById<RecyclerView>(R.id.rvInventory)
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = inventoryAdapter
        val userType=intent.extras!!.get("user")
        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        val searchView: SearchView = findViewById(R.id.searchView)
        if(userType=="supervisor")
        {
            addButton.visibility= View.GONE
        }
        addButton.setOnClickListener {
            showAddItemDialog()
        }

        retrieveInventoryItems(userType.toString())

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
                retrieveInventoryItems("") // Refresh the inventory list to show the updated location
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to relocate item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_inventory_item, null)

        val locationSpinner: Spinner = dialogView.findViewById(R.id.spinnerLocation)

        // Fetch available spaces from Firestore and populate the spinner
        fetchAvailableSpaces { spaces ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spaces)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Inventory Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val itemId = UUID.randomUUID().toString()
                val itemName = dialogView.findViewById<EditText>(R.id.itemNameInput).text.toString()
                val quantity = dialogView.findViewById<EditText>(R.id.quantityInput).text.toString().toIntOrNull() ?: 0
                val weight = dialogView.findViewById<EditText>(R.id.weightInput).text.toString().toDoubleOrNull() ?: 0.0
                val description = dialogView.findViewById<EditText>(R.id.descriptionInput).text.toString()
                val location = locationSpinner.selectedItem.toString() // Get selected location
                val ownerName=dialogView.findViewById<EditText>(R.id.ownerNameInput).text.toString()
                val ownerId=dialogView.findViewById<EditText>(R.id.OwnerIdInput).text.toString()
                val ownerPhone=dialogView.findViewById<EditText>(R.id.OwnerPhoneInput).text.toString()
                val newItem = InventoryItem(
                    itemId = itemId,
                    itemName = itemName,
                    quantity = quantity,
                    weight = weight,
                    description = description,
                    location = location,
                    status = ItemStatus.AVAILABLE,
                    ownerName=ownerName,
                    ownerId=ownerId,
                    ownerPhone=ownerPhone
                )

                addItemToFirestore(newItem)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
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
            .update("status", ItemStatus.PENDING.name)
            .addOnSuccessListener {
                Toast.makeText(this, "Item checked out successfully", Toast.LENGTH_SHORT).show()
                retrieveInventoryItems("admin")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to checkout item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun retrieveInventoryItems(userType:String) {
        db.collection("inventory")
            .get()
            .addOnSuccessListener { result ->
                val inventoryList = result.map { document ->
                    document.toObject(InventoryItem::class.java)
                }
                inventoryAdapter.updateData(inventoryList,userType)
                Toast.makeText(this, "Retrieved ${inventoryList.size} items", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve items: ${e.message}", Toast.LENGTH_SHORT).show()
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
