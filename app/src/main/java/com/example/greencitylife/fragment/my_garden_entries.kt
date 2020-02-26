package com.example.greencitylife.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.greencitylife.Entry
import com.example.greencitylife.Garden

import com.example.greencitylife.R
import com.example.greencitylife.User
import com.example.greencitylife.activity.*
import com.example.greencitylife.adapter.EntriesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class my_garden_entries : Fragment() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_garden_entries, container, false)

        val add_button = view.findViewById<FloatingActionButton>(R.id.my_garden_entries_btn_add)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        mAuth = FirebaseAuth.getInstance()

        display_entries(view)

        val listView = view.findViewById<ListView>(R.id.my_garden_entries_list)
        listView.setOnItemClickListener{parent, view, position, id ->
            // get data of entry clicked on
            val entry_data: List<String> = parent.getItemAtPosition(position) as List<String>
            // pass firebase id of entry
            val action =
                marketDirections.actionMarketToMarketEntry(
                    entry_data[0]
                )
            findNavController().navigate(action)
        }

        return view
    }

    private fun display_entries (view: View) {

        userRef.document(mAuth!!.currentUser!!.uid).get().addOnSuccessListener { userDoc ->
            val user = userDoc.toObject(User::class.java)

            entryRef.get().addOnSuccessListener { documents ->
                val titleList: MutableList<String> = ArrayList()
                val descriptionList: MutableList<String> = ArrayList()
                val idList: MutableList<String> = ArrayList()
                val timeList: MutableList<String> = ArrayList()

                for (document in documents) {
                    if(document.data["gardenId"] == user!!.gardenId) {
                        val entry = document.toObject(Entry::class.java)
                        idList.add(document.id)
                        timeList.add(SimpleDateFormat("MM/dd/yyyy").format(entry.creationTime.toDate()))
                        titleList.add(entry.title)
                        descriptionList.add("${entry.type} : ${entry.category}")
                    }
                }

                val listView = view.findViewById<ListView>(R.id.my_garden_entries_list)
                val adapter = EntriesAdapter(
                    requireContext(),
                    idList,
                    titleList,
                    descriptionList,
                    timeList
                )
                listView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

}
