package com.example.greencitylife.fragment


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.greencitylife.R


/**
 * A simple [Fragment] subclass.
 */
class market_search : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_market_search, container, false)

        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setBackgroundColor(Color.LTGRAY)
        market_button.setTextColor(Color.WHITE)

        val market_search_button = view.findViewById<ImageButton>(R.id.search_button)
        market_search_button.setBackgroundColor(resources.getColor(R.color.button_market_clicked))


        return view
    }


}
