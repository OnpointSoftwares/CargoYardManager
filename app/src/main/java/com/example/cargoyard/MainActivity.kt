package com.example.cargoyard

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cargoyard.controllers.AuthClass
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var email:TextInputEditText
    private lateinit var password:TextInputEditText
    private lateinit var loginBtn:AppCompatButton
    private lateinit var forgotPassword:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        email=findViewById(R.id.emailEdt)
        password=findViewById(R.id.passwordEdt)
        loginBtn=findViewById(R.id.loginBtn)
        forgotPassword=findViewById(R.id.forgotPaswordTV)
        loginBtn.setOnClickListener {

            if(password.text.toString()!="" && email.text.toString()!=="") {
                AuthClass().login(email.text.toString(),password.text.toString(),this)
            }
            else{
                Toast.makeText(this,"Make sure all the fields are filled",Toast.LENGTH_LONG).show()
            }
        }
    }
}