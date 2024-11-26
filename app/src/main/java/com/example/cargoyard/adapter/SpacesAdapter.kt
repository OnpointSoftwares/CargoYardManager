package com.example.cargoyard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargoyard.R
import com.example.cargoyard.models.Space

class SpacesAdapter(
    private val spacesList: MutableList<Space>,
    private val onEditClick: (Space) -> Unit,
    private val onDeleteClick: (Space) -> Unit,
    private val userType: String
) : RecyclerView.Adapter<SpacesAdapter.SpaceViewHolder>() {

    inner class SpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSpaceName: TextView = itemView.findViewById(R.id.tvSpaceName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvCapacity: TextView = itemView.findViewById(R.id.tvCapacity)
        val btnEditSpace: Button = itemView.findViewById(R.id.btnEditSpace)
        val btnDeleteSpace: Button = itemView.findViewById(R.id.btnDeleteSpace)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_space, parent, false)
        return SpaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpaceViewHolder, position: Int) {
        val space = spacesList[position]
        holder.tvSpaceName.text = space.spaceName
        holder.tvLocation.text = "Location: ${space.location}"
        holder.tvCapacity.text = "Capacity: ${space.capacity}"
        if(userType=="supervisor"){
            holder.btnEditSpace.visibility=View.GONE
            holder.btnDeleteSpace.visibility=View.GONE
        }
        // Set click listeners for edit and delete buttons
        holder.btnEditSpace.setOnClickListener { onEditClick(space) }
        holder.btnDeleteSpace.setOnClickListener { onDeleteClick(space) }
    }

    override fun getItemCount(): Int = spacesList.size

    // Helper function to update the list
    fun updateData(newSpaces: List<Space>) {
        spacesList.clear()
        spacesList.addAll(newSpaces)
        notifyDataSetChanged()
    }
}

