package com.example.cargoyard.controllers

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.cargoyard.CargoYardManagerDashboard
import com.example.cargoyard.FinanceManager
import com.example.cargoyard.SupervisorActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

public class AuthClass {
    fun login(email:String,password:String,context: Context)
    {
        val pd= ProgressDialog(context)
        pd.setTitle("Loging in...")
        pd.show()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful)
            {
                FirebaseDatabase.getInstance().reference.child("users").child(it.result.user!!.uid).child("role").addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value.toString()=="MANAGER")
                        {
                            context.startActivity(Intent(context,CargoYardManagerDashboard::class.java))
                            pd.dismiss()
                            Toast.makeText(context,"Login successful you are manager",Toast.LENGTH_LONG).show()
                        }
                        else if(snapshot.value.toString()=="SUPERVISOR")
                        {
                            context.startActivity(Intent(context,SupervisorActivity::class.java))
                            pd.dismiss()
                            Toast.makeText(context,"Login successful you are supervisor",Toast.LENGTH_LONG).show()
                        }
                        else if(snapshot.value.toString()=="FINANCE_MANAGER")
                        {
                            context.startActivity(Intent(context,FinanceManager::class.java))
                            pd.dismiss()
                            Toast.makeText(context,"Login successful you are finance manager",Toast.LENGTH_LONG).show()
                        }
                        else{
                            pd.dismiss()
                            Toast.makeText(context,"Role of the user not found",Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }

}