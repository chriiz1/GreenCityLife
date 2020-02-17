package com.example.greencitylife


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream


@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}

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

        // get entry ID
        val entry_id = args.entryId

        // read data of entry from database by ID
        myDB.collection("Entries")
            .document(entry_id)
            .get()
            .addOnSuccessListener { doc ->
                Log.d(TAG, "${doc.id} => ${doc.data}")
                val ID: String = doc.id
                display_entry_data(doc)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        return view
    }


    // after retrieving data from db call this function to fill layout with this data
    private fun display_entry_data(data: DocumentSnapshot) {
        val view = requireView()

        val title = data.get("title").toString()
        val type = data.get("type").toString()
        val description = data.get("additionalText").toString()
        val timestamp = data.data?.get("creationTime") as com.google.firebase.Timestamp
        val date = timestamp.toDate().toString()
        val imageID = data.get("imageID").toString()

        val typeView = view.findViewById<TextView>(R.id.entry_type)
        val titleView = view.findViewById<TextView>(R.id.entry_title)
        val descriptionView = view.findViewById<TextView>(R.id.entry_description)
        val imageView = view.findViewById<ImageView>(R.id.entry_image)
        val timeView = view.findViewById<TextView>(R.id.entry_time)

        typeView.text = type
        titleView.text = title
        descriptionView.text = description
        timeView.text = date

        val url = "https://firebasestorage.googleapis.com/v0/b/greencitylife-ed3b5.appspot.com/o/entry_images%2F0f1099f9-de72-4418-af3a-989b8fc6a4fa?alt=media&token=0b4ba637-59d5-4be8-89d6-0788b48e0f6d"

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child("entry_images/$imageID").downloadUrl
            .addOnSuccessListener { uri ->
                Glide
                    .with(view)
                    .load(uri)
                    .centerCrop()
                    .into(imageView)


            }
    }

}
