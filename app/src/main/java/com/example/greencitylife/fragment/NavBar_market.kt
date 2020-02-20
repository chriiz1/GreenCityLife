package com.example.greencitylife.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.greencitylife.R

/**
 * A simple [Fragment] subclass.
 */
class NavBar_market : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_nav_bar_market, container, false)

        val market_search_button = view.findViewById<ImageButton>(R.id.search_button)
        market_search_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_search)
        }

        val market_news_button = view.findViewById<ImageButton>(R.id.market_news_button)
        market_news_button.setOnClickListener{
            it.findNavController().navigate(R.id.market)
        }

        val market_add_button = view.findViewById<ImageButton>(R.id.offer_button)
        market_add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        return view
    }


}
