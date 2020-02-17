package com.example.greencitylife


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat

/**
 * A simple [Fragment] subclass.
 */
class market : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_market, container, false)

        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setBackgroundColor(Color.LTGRAY)
        market_button.setTextColor(Color.WHITE)

        val add_button = view.findViewById<FloatingActionButton>(R.id.add_entry_button)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        display_entries(view)

        val listView = view.findViewById<ListView>(R.id.entriesListView)
        listView.setOnItemClickListener{parent, view, position, id ->

            // get data of entry clicked on
            val entry_data: List<String> = parent.getItemAtPosition(position) as List<String>
            // pass firebase id of entry
            val action = marketDirections.actionMarketToMarketEntry(entry_data[0])
            findNavController().navigate(action)
        }

        return view
    }


    private fun display_entries (view: View) {
        myDB.collection("Entries")
            .get()
            .addOnSuccessListener { documents ->
                val titleList: MutableList<String> = ArrayList()
                val descriptionList: MutableList<String> = ArrayList()
                val idList: MutableList<String> = ArrayList()
                val timeList: MutableList<String> = ArrayList()

                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val ID: String = document.id
                    val title = document.data["title"].toString()
                    val type = document.data["type"].toString()
                    val category = document.data["category"].toString()
                    val description =  "$type : $category"
                    // get time as Timestamp
                    val timestamp = document.data["creationTime"] as com.google.firebase.Timestamp
                    // convert to Date
                    val date = timestamp.toDate()
                    // set format for date
                    val sdf = SimpleDateFormat("MM/dd/yyyy")
                    val dateStr = sdf.format(date)

                    // add entry attributes to lists
                    idList.add(ID)
                    titleList.add(title)
                    descriptionList.add(description)
                    timeList.add(dateStr)
                }


                val listView = view.findViewById<ListView>(R.id.entriesListView)
                val adapter = EntriesAdapter(requireContext(), idList, titleList, descriptionList, timeList)
                listView.adapter = adapter

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

}




