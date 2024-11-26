package com.example.cargoyard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.cargoyard.models.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore

class DialogRelocateItemFragment : DialogFragment() {

    interface RelocateItemListener {
        fun onLocationEntered(itemId: String, newLocation: String)
    }

    private lateinit var listener: RelocateItemListener
    private lateinit var itemId: String
    private lateinit var spinnerSpaces: Spinner
    private val spacesList = mutableListOf<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = when {
            context is RelocateItemListener -> context
            parentFragment is RelocateItemListener -> parentFragment as RelocateItemListener
            else -> throw ClassCastException(
                "$context must implement RelocateItemListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.dialog_relocate_item, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get itemId from arguments
        itemId = arguments?.getString(ARG_ITEM_ID) ?: ""

        spinnerSpaces = view.findViewById(R.id.spinnerLocations)
        val buttonSubmit: Button = view.findViewById(R.id.btnSubmit)
        loadSpaces()
        buttonSubmit.setOnClickListener {
            val selectedSpace = spinnerSpaces.selectedItem as String
            listener.onLocationEntered(itemId, selectedSpace)
            dismiss()
        }

    }
    private fun loadSpaces() {
        val db = FirebaseFirestore.getInstance()
        val spacesRef = db.collection("spaces") // Replace with your Firestore collection name

        spacesRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val spaceName = document.getString("spaceName") // Replace with the field name of your space
                spaceName?.let { spacesList.add(it) }
            }
            setupSpinner()
        }.addOnFailureListener { exception ->
            // Handle the error
        }
    }


    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spacesList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSpaces.adapter = adapter
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        fun newInstance(itemId: String): DialogRelocateItemFragment {
            val fragment = DialogRelocateItemFragment()
            val args = Bundle()
            args.putString(ARG_ITEM_ID, itemId)
            fragment.arguments = args
            return fragment
        }
    }
}
