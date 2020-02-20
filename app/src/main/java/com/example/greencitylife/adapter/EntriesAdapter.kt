package com.example.greencitylife.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.greencitylife.R


class EntriesAdapter(private val context: Context,
                     private val idList: MutableList<String>,
                     private val titleList: MutableList<String> ,
                     private val descriptionList: MutableList<String> ,
                     private val timeList: MutableList<String> ): BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    //1
    override fun getCount(): Int {
        return titleList.size
    }

    //2
    override fun getItem(position: Int): List<String> {
        val ID = idList[position]
        val title = titleList[position]
        val description = descriptionList[position]
        val time = timeList[position]
        val entry = listOf<String>(ID, title, description, time)
        return entry
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.custom_list, parent, false)
        // Get title element
        val titleTextView = rowView.findViewById(R.id.title) as TextView
        // Get subtitle element
        val subtitleTextView = rowView.findViewById(R.id.entry_description) as TextView
        // Get time element
        val timeTextView = rowView.findViewById<TextView>(R.id.time) as TextView

        // get and set data
        val entry = getItem(position)
        titleTextView.text = entry[1]
        subtitleTextView.text = entry[2]
        timeTextView.text = entry[3]

        return rowView
    }





}