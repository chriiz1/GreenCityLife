package com.example.greencitylife


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

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


        val add_button = view.findViewById<ImageButton>(R.id.add_entry_button)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        val search_button = view.findViewById<ImageButton>(R.id.search_entry_button)
        search_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_search)
        }

        val auth_button = view.findViewById<Button>(R.id.a_main_btn_authentication2)

        val this_context = container!!.context

        auth_button.setOnClickListener{openRegistration(this_context)}

        return view


    }

    fun openRegistration(context:Context) {
        val intent = Intent(context, Authentication::class.java).apply{}
        startActivity(intent)
    }
}
