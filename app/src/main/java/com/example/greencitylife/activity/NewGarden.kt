package com.example.greencitylife.activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.greencitylife.Garden
import com.example.greencitylife.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_new_garden.*

class NewGarden : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var imageURI: Uri? = null
    private var latlng: LatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_garden)

        val mapFrag: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.new_garden_mapview) as SupportMapFragment
        mapFrag.getMapAsync(this)

        val submit_button = findViewById<Button>(R.id.new_garden_submit)
        submit_button.setOnClickListener{
            // ToDo Validation
            val garden = Garden(
                name = new_garden_et_name.text.toString(),
                address = new_garden_et_address.text.toString(),
                lat = latlng!!.latitude,
                lon = latlng!!.longitude
            )
            gardenRef.document(garden.name).set(garden)
            Toast.makeText(applicationContext, "${garden.name} created!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, chooseGarden::class.java).apply {}
            startActivity(intent)
        }

        val et_address = findViewById<EditText>(R.id.new_garden_et_address)
        val et_name = findViewById<EditText>(R.id.new_garden_et_name)
        val geocoder = Geocoder(this)


        et_address.setOnFocusChangeListener(object: View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus) {
                    val ab = geocoder.getFromLocationName(et_address.text.toString(), 5);
                    if (ab.size > 0) {
                        val location = getRightLocation(ab)
                        latlng = LatLng(location.latitude, location.longitude)
                        Toast.makeText(applicationContext, latlng.toString(), Toast.LENGTH_SHORT).show()
                        addMapMarker(latlng!!, et_name.text.toString())
                    }
                }
            }
        })
    }

    fun getRightLocation(locations : List<Address>) : Address{
        for (location in locations) {
            if (location.locality == "Graz")
                return location
        }

        for (location in locations) {
            if (location.adminArea == "Steiermark")
                return location
        }

        return locations[0]
    }

    fun addMapMarker(latlng: LatLng, title: String){
        val options = MarkerOptions().position(latlng).title(title)
        mMap.addMarker(options)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // initialize map
        mMap = googleMap
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
