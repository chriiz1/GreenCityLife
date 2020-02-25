package com.example.greencitylife.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController

import com.example.greencitylife.R


/**
 * A simple [Fragment] subclass.

 */
class NavBar_garden : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_nav_bar_garden, container, false)

        val home_btn = view.findViewById<ImageButton>(R.id.nav_garden_btn_home)
        val gallery_btn = view.findViewById<ImageButton>(R.id.nav_garden_btn_gallery)
        val entries_btn = view.findViewById<ImageButton>(R.id.nav_garden_btn_entries)

        home_btn.setOnClickListener{
            it.findNavController().navigate(R.id.mygarden)
        }

        gallery_btn.setOnClickListener{
            it.findNavController().navigate(R.id.my_garden_image_gallery)
        }

        entries_btn.setOnClickListener{
            it.findNavController().navigate(R.id.my_garden_entries)
        }

        return view
    }
}
