package com.example.greencitylife.fragment


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.greencitylife.R
import com.example.greencitylife.activity.TAG
import com.example.greencitylife.activity.myDB
import com.example.greencitylife.adapter.EntriesAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query
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

        // change color of market button in navigation bar
        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setBackgroundColor(Color.LTGRAY)
        market_button.setTextColor(Color.WHITE)

        // navigate to market_add fragment when click on add_button
        val add_button = view.findViewById<FloatingActionButton>(R.id.add_entry_button)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        // display all entries
        displayEntries(view, orderByParameter = null, typeParameter = null, categoryParameter = ArrayList())

        // get buttons needed for expansion and collapse of search parameters layout
        val market_buttons = view.findViewById<ConstraintLayout>(R.id.market_buttons)
        val search_button = view.findViewById<FloatingActionButton>(R.id.search_button)
        val collapse_button = view.findViewById<ImageButton>(R.id.collapse_button)

        // if search dialog is not expanded --> expand
        if (collapse_button.visibility == View.INVISIBLE) {
            search_button.setOnClickListener {
                // expand full layout
                val distance = -market_buttons.height.toFloat()+search_button.height.toFloat()+70
                market_buttons.animate().translationY(distance)
                collapse_button.visibility = View.VISIBLE
            }
        }

        // if submitSearch button is clicked, load entries again with specified search parameters
        val submitSearch = view.findViewById<Button>(R.id.submit_search_button)
        submitSearch.setOnClickListener{
            displayEntriesSelection(view)
        }

        // if search dialog is expanded and collapse button is clicked --> collapse
        collapse_button.setOnClickListener {
            market_buttons.animate().translationY(0F)
            collapse_button.visibility = View.INVISIBLE
            }

        // if mapButton pressed, navigate to Map; show only selected results
        val mapButton = view.findViewById<FloatingActionButton>(R.id.map_button)
        mapButton.setOnClickListener{
            // TODO: tell map fragment which entries to display
            it.findNavController().navigate(R.id.community)
        }

        // get to specific entry when clicked on entry
        val listView = view.findViewById<ListView>(R.id.entriesListView)
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


    // function for retrieving data from firestore and display them in ListView
    private fun displayEntries (view: View, orderByParameter: String?, typeParameter: String?, categoryParameter: ArrayList<String>) {
        // createQuery creates query object
        createQuery(orderByParameter, typeParameter, categoryParameter)
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
                Toast.makeText(context, "Query failed", Toast.LENGTH_LONG).show()
            }
    }

    // creates query object for data retrieval from firestore
    private fun createQuery(orderByParameter: String?, typeParameter: String?, categoryParameter: ArrayList<String>): Query {

        // if non category is selected
        if (categoryParameter.isEmpty()) {
            categoryParameter.add("Nothing")
        }

            // case: all entries are shown ordered by time; initial state
        if (orderByParameter == null) {
            val query = myDB.collection("Entries")
                .orderBy("creationTime", Query.Direction.ASCENDING)
            return query

            //  case: ordered by time + additional parameters
        } else if (orderByParameter == "time") {
            val query = myDB.collection("Entries")
                .whereEqualTo("type", typeParameter)
                .whereIn("category", categoryParameter)
                .orderBy("creationTime", Query.Direction.DESCENDING)
            return query

            // ordered by distance
        } else if (orderByParameter == "distance") {
            // TODO: add order by distance
            val query = myDB.collection("Entries")
                .whereEqualTo("type", typeParameter)
                .whereIn("category", categoryParameter)
                .orderBy("creationTime", Query.Direction.ASCENDING)
            return query

            // case: invalid query: all entries are shown
            // For development purposes
        } else {
            Toast.makeText(context, "Not a valid query", Toast.LENGTH_LONG).show()
            val query = myDB.collection("Entries")
                .orderBy("creationTime", Query.Direction.ASCENDING)
            return query
        }
    }


    // display entries searched for
    private fun displayEntriesSelection(view: View) {

        // get "Order By" result
        val OrderByRadioGroup = view.findViewById<RadioGroup>(R.id.rgOrderBy)
        val orderByParameter: String = if (OrderByRadioGroup.checkedRadioButtonId == R.id.rb_time) "time" else "distance"
        // get Type result
        val TypeRadioGroup = view.findViewById<RadioGroup>(R.id.rgType)
        val typeParameter = if (TypeRadioGroup.checkedRadioButtonId == R.id.rb_offer) "offer" else "search"
        // get Category result
        // TODO get type/types which should be searched for
        var lsCategories = ArrayList<String>()

        val rbKnowledge = view.findViewById<CheckBox>(R.id.cb_knowledge)
        val rbGoods = view.findViewById<CheckBox>(R.id.cb_goods)
        val rbPracticalHelp = view.findViewById<CheckBox>(R.id.cb_practical_help)

        if (rbKnowledge.isChecked) lsCategories.add("Knowledge")
        if (rbGoods.isChecked) lsCategories.add("Goods")
        if (rbPracticalHelp.isChecked) lsCategories.add("Practical help")

        // call displayEntries with search parameters
        displayEntries(view, orderByParameter = orderByParameter, typeParameter = typeParameter, categoryParameter = lsCategories)
        }
}








