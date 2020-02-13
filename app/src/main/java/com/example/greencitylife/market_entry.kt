package com.example.greencitylife


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

/**
 * A simple [Fragment] subclass.
 */
class market_entry : Fragment() {

    val args: market_entryArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_market_entry, container, false)

        // change color of button
        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setBackgroundColor(Color.LTGRAY)
        market_button.setTextColor(Color.WHITE)

        // change color of button
        val market_news_button = view.findViewById<ImageButton>(R.id.market_news_button)
        market_news_button.setBackgroundColor(resources.getColor(R.color.button_market_clicked))

        // get entry ID
        val entry_id = args.entryId

        // read data of entry from database by ID
        myDB.collection("Entries")
            .document(entry_id)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "${doc.id} => ${doc.data}")
                val ID: String = doc.id
                val title = doc.get("title").toString()
                display_entry_data(title)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        return view
    }


    // after retrieving data from db call this function to fill layout with this data
    private fun display_entry_data(title: String) {
        val view = requireView()
        val textView = view.findViewById<TextView>(R.id.diplay_entry_id)
        textView.text = title
    }


}
