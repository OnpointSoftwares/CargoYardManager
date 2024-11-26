package com.example.cargoyard

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.adapter.SpacesAdapter
import com.example.cargoyard.models.Space
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ManageSpacesActivity : AppCompatActivity() {

    private lateinit var etSpaceName: EditText
    private lateinit var etLocation: EditText
    private lateinit var etCapacity: EditText
    private lateinit var btnAddSpace: Button
    private lateinit var rvSpaces: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private val spacesList = mutableListOf<Space>()
    private lateinit var spacesAdapter: SpacesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_spaces)
        val toolbar: Toolbar =findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply { title="Manage Spaces"
        setDisplayHomeAsUpEnabled(true)
        }
        // Setup RecyclerView and Adapter
        etSpaceName = findViewById(R.id.etSpaceName)
        etLocation = findViewById(R.id.etLocation)
        etCapacity = findViewById(R.id.etCapacity)
        btnAddSpace = findViewById(R.id.btnAddSpace)
        rvSpaces = findViewById(R.id.rvSpaces)
        val rvSpaces: RecyclerView = findViewById(R.id.rvSpaces)
        rvSpaces.layoutManager = LinearLayoutManager(this)
        val userType=intent.extras!!.getString("user")
        spacesAdapter = SpacesAdapter(spacesList, this::editSpace, this::deleteSpace,userType.toString())
        rvSpaces.adapter = spacesAdapter
        retrieveSpaces()
        btnAddSpace.setOnClickListener {
            addSpace()
        }
        if(userType=="supervisor")
        {
            etSpaceName.visibility=View.GONE
            etLocation.visibility=View.GONE
            etCapacity.visibility=View.GONE
            btnAddSpace.visibility=View.GONE
            btnAddSpace.visibility= View.GONE
        }
    }

    private fun addSpace() {
        val progressDialog= ProgressDialog(this@ManageSpacesActivity)
        progressDialog.setTitle("Adding...")
        progressDialog.show()
        val spaceName = etSpaceName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val capacity = etCapacity.text.toString().toIntOrNull() ?: 0

        if (spaceName.isEmpty() || location.isEmpty() || capacity <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val newSpace = Space(UUID.randomUUID().toString(), spaceName, location, capacity)
        db.collection("spaces")
            .document(newSpace.spaceId)
            .set(newSpace)
            .addOnSuccessListener {
                Toast.makeText(this, "Space added successfully", Toast.LENGTH_SHORT).show()
                spacesList.add(newSpace)
                spacesAdapter.notifyItemInserted(spacesList.size - 1)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add space: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        progressDialog.dismiss()
    }

    private fun retrieveSpaces() {
        db.collection("spaces")
            .get()
            .addOnSuccessListener { result ->
                spacesList.clear()
                for (document in result) {
                    document.toObject(Space::class.java)?.let { spacesList.add(it) }
                }
                spacesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve spaces: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editSpace(space: Space) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_space, null)
        val etSpaceName = dialogView.findViewById<EditText>(R.id.etSpaceName)
        val etLocation = dialogView.findViewById<EditText>(R.id.etLocation)
        val etCapacity = dialogView.findViewById<EditText>(R.id.etCapacity)

        etSpaceName.setText(space.spaceName)
        etLocation.setText(space.location)
        etCapacity.setText(space.capacity.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Space")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedSpace = space.copy(
                    spaceName = etSpaceName.text.toString().trim(),
                    location = etLocation.text.toString().trim(),
                    capacity = etCapacity.text.toString().toIntOrNull() ?: 0
                )

                db.collection("spaces").document(space.spaceId)
                    .set(updatedSpace)
                    .addOnSuccessListener {
                        retrieveSpaces()
                        Toast.makeText(this, "Space updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update space: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteSpace(space: Space) {
        val progressDialog= ProgressDialog(this@ManageSpacesActivity)
        progressDialog.setTitle("Adding...")
        progressDialog.show()
        db.collection("spaces").document(space.spaceId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Space deleted", Toast.LENGTH_SHORT).show()
                spacesList.remove(space)
                spacesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete space: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        progressDialog.dismiss()
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
