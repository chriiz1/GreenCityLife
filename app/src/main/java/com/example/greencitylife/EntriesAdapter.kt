package com.example.greencitylife

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView



class EntriesAdapter(private val context: Context,
                     private val idList: MutableList<String>,
                     private val titleList: MutableList<String> ,
                     private val descriptionList: MutableList<String> ,
                     private val imageList: MutableList<String> ): BaseAdapter(){

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
        val imageID = imageList[position]
        val entry = listOf<String>(ID, title, description, imageID)
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
        val subtitleTextView = rowView.findViewById(R.id.description) as TextView

        // Get thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.entry_icon) as ImageView

        // 1
        val entry = getItem(position)

        titleTextView.text = entry[1]
        subtitleTextView.text = entry[2]

        val imageID = entry[3]


        val storageRef = storage.reference

        // Create a reference with an initial file path and name
        val pathReference = storageRef.child("entry_images/$imageID")

        //Glide
        //    .with(thumbnailImageView)
        //    .load("https://firebasestorage.googleapis.com/v0/b/greencitylife-ed3b5.appspot.com/o/entry_images%2F0385e2c0-7ad5-46af-af2a-8a48b39d130f?alt=media&token=5fd50315-e024-4413-a48b-dd8dc2a6b0cb")
        //    .into(thumbnailImageView)


        return rowView
    }





}