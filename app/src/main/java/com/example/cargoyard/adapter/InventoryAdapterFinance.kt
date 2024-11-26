package com.example.cargoyard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.R
import com.example.cargoyard.models.InventoryItem
import com.example.cargoyard.models.ItemStatus

class InventoryAdapterFinance(
    private var inventoryList: List<InventoryItem>,
    private val onRelocate: (InventoryItem) -> Unit,
    private val onCheckout: (InventoryItem) -> Unit
) : RecyclerView.Adapter<InventoryAdapterFinance.InventoryViewHolder>() {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.tvItemName)
        val itemQuantityTextView: TextView = itemView.findViewById(R.id.tvItemQuantity)
        val itemWeightTextView: TextView = itemView.findViewById(R.id.tvItemWeight)
        val itemLocationTextView: TextView = itemView.findViewById(R.id.tvItemLocation)
        val itemStatusTextView: TextView = itemView.findViewById(R.id.tvItemStatus)
        val confirmButton: Button = itemView.findViewById(R.id.confirmButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_item_finance, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventoryItem = inventoryList[position]

        // Bind data to the views
        holder.itemNameTextView.text = inventoryItem.itemName
        holder.itemQuantityTextView.text = "Quantity: ${inventoryItem.quantity}"
        holder.itemWeightTextView.text = "Weight: ${inventoryItem.weight} kg"
        holder.itemLocationTextView.text = "Location: ${inventoryItem.location}"

        // Set status text and color
        when (inventoryItem.status) {
            ItemStatus.AVAILABLE -> {
                holder.itemStatusTextView.text = "Status: Available"
                holder.itemStatusTextView.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_green_dark))
            }
            ItemStatus.CHECKED_OUT -> {
                holder.itemStatusTextView.text = "Status: Checked Out"
                holder.itemStatusTextView.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_red_dark))
            }
            ItemStatus.RESERVED -> {
                holder.itemStatusTextView.text = "Status: Reserved"
                holder.itemStatusTextView.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_orange_dark))
            }
            ItemStatus.DAMAGED -> {
                holder.itemStatusTextView.text = "Status: Damaged"
                holder.itemStatusTextView.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_red_light))
            }
            ItemStatus.PENDING ->{
                holder.itemStatusTextView.text = "Status:Pending... Waiting for confirmation"
                holder.itemStatusTextView.setTextColor(holder.itemView.resources.getColor(android.R.color.holo_red_dark))

            }
        }

        // Button click listeners
        holder.confirmButton.setOnClickListener { onCheckout(inventoryItem) }
    }

    override fun getItemCount(): Int = inventoryList.size
    fun filter(query: String) {
        val originalList=inventoryList
        val filteredList = if (query.isEmpty()) {
            inventoryList=originalList
            inventoryList
        // Return the full list if the query is empty
        } else {
            inventoryList.filter { item ->
                item.itemName.contains(query, ignoreCase = true) || // Match by item name
                        item.location.contains(query, ignoreCase = true) // Match by location
            }
        }
        inventoryList = filteredList
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }
    // Method to update data
    fun updateData(newInventoryList: List<InventoryItem>) {
        inventoryList = newInventoryList
        notifyDataSetChanged() // Notify the adapter to refresh the data
    }
}
