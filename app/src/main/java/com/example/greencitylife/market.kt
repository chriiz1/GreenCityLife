package com.example.greencitylife


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
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

        display_entries(view)

        return view
    }


    private fun display_entries (view: View) {
        myDB.collection("Entries")
            .get()
            .addOnSuccessListener { documents ->
                val titleList: MutableList<String> = ArrayList()
                val descriptionList: MutableList<String> = ArrayList()
                val imageNameList: MutableList<String> = ArrayList()

                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val title = document.data["title"].toString()
                    val description = document.data["additionalText"].toString()
                    var imageName = document["imageID"].toString()
                    if (imageName == "") {
                        imageName == "no_image_available.png"
                    }
                    titleList.add(title)
                    imageNameList.add(imageName)
                    descriptionList.add(description)
                }

                val listView = view.findViewById<ListView>(R.id.entriesListView)
                val adapter = EntriesAdapter(requireContext(), titleList, descriptionList, imageNameList)
                listView.adapter = adapter
                
                listView.setOnItemClickListener{parent, view, position, id ->

                    Toast.makeText(requireContext(), "You have Clicked " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show()
                }


            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}




