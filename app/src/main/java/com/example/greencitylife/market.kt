package com.example.greencitylife


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

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

        val market_news_button = view.findViewById<ImageButton>(R.id.market_news_button)
        market_news_button.setBackgroundColor(resources.getColor(R.color.button_market_clicked))

        read_entries()

        return view
    }


    private fun read_entries() {
        // myDB is initialized in MainActivity
        myDB.collection("Entries")
            .get()
            .addOnSuccessListener { documents ->
                val titleList = ArrayList<String>()
                val descriptionList = ArrayList<String>()
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val title = document.data["title"].toString()
                    val description = document.data["additionalText"].toString()
                    titleList.add(title)
                    descriptionList.add(description)
                }
                displayEntries(titleList, descriptionList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


    private fun displayEntries(titleList: ArrayList<String>,
                               descriptionList: ArrayList<String>) {

        val myListAdapter = MyListAdapter(requireActivity(), titleList, descriptionList)
        val listView = view?.findViewById<ListView>(R.id.listView)
        if (listView != null) {
            listView.adapter = myListAdapter
        }
    }
}



class MyListAdapter(private val context: Activity, private val title: ArrayList<String>, private val description: ArrayList<String>)
    : ArrayAdapter<String>(context, R.layout.custom_list, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val subtitleText = rowView.findViewById(R.id.description) as TextView

        titleText.text = title[position]
        subtitleText.text = description[position]

        return rowView
    }
}
