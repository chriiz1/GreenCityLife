package com.example.greencitylife


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A simple [Fragment] subclass.
 */
class Community : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_community, container, false)

        val community_button = view.findViewById<Button>(R.id.community_button)
        community_button.setBackgroundColor(Color.LTGRAY)
        community_button.setTextColor(Color.WHITE)


        val add_button = view.findViewById<FloatingActionButton>(R.id.add_entry_button)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }


        return view

    }
}
