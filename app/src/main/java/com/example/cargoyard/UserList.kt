package com.example.cargoyard

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.adapter.UserAdapter
import com.example.cargoyard.models.User
import com.example.cargoyard.models.UserRole
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val users = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User List"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userType=intent.extras?.getString("user")
        recyclerView = findViewById(R.id.rvUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(users)
        recyclerView.adapter = userAdapter

        // Load users from Firebase
        loadUsersFromFirebase()

        // Set up FloatingActionButton to show dialog for adding a new user
        val fab: FloatingActionButton = findViewById(R.id.fabAddUser)
        fab.setOnClickListener { showAddUserDialog() }
        if(userType=="supervisor")
        {
            fab.visibility= View.GONE
        }
    }

    private fun loadUsersFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                users.clear()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.key ?: continue
                    val name = userSnapshot.child("username").getValue(String::class.java) ?: "Unknown"
                    val phone = userSnapshot.child("phone").getValue(String::class.java) ?: "Unknown"
                    val roleString = userSnapshot.child("role").getValue(String::class.java) ?: "Unknown"
                    val role = UserRole.valueOf(roleString.uppercase(Locale.ROOT))

                    users.add(User(userId, name, phone, role))
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UserList, "Failed to load users: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.etUserName)
        val editTextPhone = dialogView.findViewById<EditText>(R.id.etUserPhone)
        val spinnerRole = dialogView.findViewById<Spinner>(R.id.spinnerUserRole)

        // Set up role options in the Spinner
        val roles = UserRole.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        // Show the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New User")
            .setPositiveButton("Add") { _, _ ->
                val name = editTextName.text.toString().trim()
                val phone = editTextPhone.text.toString().trim()
                val role = UserRole.valueOf(spinnerRole.selectedItem.toString().replace(" ", "_").uppercase(Locale.ROOT))

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    addUserToFirebase(name, phone, role)
                } else {
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun addUserToFirebase(name: String, phone: String, role: UserRole) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        // Generate a new ID for the user



                FirebaseAuth.getInstance().createUserWithEmailAndPassword(name,phone).addOnCompleteListener {
                    if(it.isSuccessful)
                    {val userId=it.result.user!!.uid
                        val newUser = User(userId, name, phone, role)
                        usersRef.child(userId).setValue(newUser)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to add user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Error encountered", Toast.LENGTH_SHORT).show()
                    }
                }


    }
}
