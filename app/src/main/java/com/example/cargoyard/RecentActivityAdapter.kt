package com.example.cargoyard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.R
import com.example.cargoyard.models.InventoryItem

class RecentActivityAdapter(private val activityList: List<InventoryItem>) :
    RecyclerView.Adapter<RecentActivityAdapter.ActivityViewHolder>() {

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.tvItemName)
        val itemStatusTextView: TextView = itemView.findViewById(R.id.tvItemStatus)
        val itemLocationTextView: TextView = itemView.findViewById(R.id.tvItemLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_activity, parent, false)
        return ActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val item = activityList[position]
        holder.itemNameTextView.text = item.itemName
        holder.itemStatusTextView.text = "Status: ${item.status}"
        holder.itemLocationTextView.text = "Location: ${item.location}"
    }

    override fun getItemCount(): Int = activityList.size
}
