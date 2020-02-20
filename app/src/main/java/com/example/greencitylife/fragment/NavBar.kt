package com.example.greencitylife.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.greencitylife.R

/**
 * A simple [Fragment] subclass.
 */
class NavBar : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_nav_bar, container, false)

        val garden_button = view.findViewById<Button>(R.id.myGarden_button)
        garden_button.setOnClickListener{
            it.findNavController().navigate(R.id.mygarden)
        }

        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setOnClickListener{
            it.findNavController().navigate(R.id.market)
        }

        val community_button = view.findViewById<Button>(R.id.community_button)
        community_button.setOnClickListener{
            it.findNavController().navigate(R.id.community)
        }

        return view
    }

}


