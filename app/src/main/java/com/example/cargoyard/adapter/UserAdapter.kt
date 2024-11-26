package com.example.cargoyard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.R
import com.example.cargoyard.models.User

// Adapter class for the RecyclerView
class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder class that holds the views for each item
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val roleTextView: TextView = itemView.findViewById(R.id.tvRole)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.usernameTextView.text = user.username
        holder.roleTextView.text = user.role.name.replace("_", " ").capitalize()
    }

    // Return the size of the dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return userList.size
    }
}
