package com.example.greencitylife.fragment



import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.greencitylife.Garden
import com.example.greencitylife.R
import com.example.greencitylife.User
import com.example.greencitylife.activity.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

/**
 * A simple [Fragment] subclass.
 */

const val EXTRA_MESSAGE = "com.example.greencitylife.MESSAGE"
class mygarden : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var gardenView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser!!
        userId = currentUser.uid

        val view = inflater.inflate(R.layout.fragment_mygarden, container, false)
        gardenView = view
        val garden_name = view.findViewById<TextView>(R.id.my_garden_tv_garden_name)

        val mapFrag: SupportMapFragment = childFragmentManager.findFragmentById(R.id.my_garden_map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        userRef.document(userId.toString()).get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            gardenRef.document(user!!.gardenId).get().addOnSuccessListener { documentSnapshot ->
                val garden = documentSnapshot.toObject(Garden::class.java)
                garden_name.text = garden!!.name
                val edit_garden = view.findViewById<Button>(R.id.my_garden_btn_edit)

                addMapMarker(LatLng(garden.lat,garden.lon), garden.address)
                edit_garden.setOnClickListener{
                    val intent = Intent(context, NewGarden::class.java).apply {putExtra(MESSAGE, garden.id)}
                    startActivity(intent)
                }
                loadImage(view, garden.id)

            }
        }
        return view
    }


    private fun loadImage(view: View, gardenId: String){
        val title_image = view!!.findViewById<ImageView>(R.id.my_garden_title_image)
        gardenRef.document(gardenId!!).get().addOnSuccessListener { document ->
            if(document != null) {
                val image = document.get("titleImageID")
                if (image != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference
                    storageRef.child("entry_images/$image").downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide
                                .with(view!!)
                                .load(uri)
                                .centerCrop()
                                .into(title_image)
                        }
                }
            }
        }
    }


    fun addMapMarker(latlng: LatLng, title: String){
        val options = MarkerOptions().position(latlng).title(title)
        mMap.clear()
        mMap.addMarker(options)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
    }


    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
    }





}
