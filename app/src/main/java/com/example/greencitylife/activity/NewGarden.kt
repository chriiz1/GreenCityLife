package com.example.greencitylife.activity

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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_new_garden.*

class NewGarden : AppCompatActivity() {
    private lateinit var mMap: GoogleMap
    private var imageURI: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_garden)

//        val mapfr: SupportMapFragment? =
        val mapfragment = supportFragmentManager.findFragmentById(R.id.new_garden_mapview)
//        mapfragment.getMapAs

        val submit_button = findViewById<Button>(R.id.new_garden_submit)
        submit_button.setOnClickListener{
            val garden = Garden(
                name = new_garden_et_name.text.toString(),
                address = new_garden_et_address.text.toString()
            )
            gardenRef.document(garden.name).set(garden)
        }

        val et_address = findViewById<EditText>(R.id.new_garden_et_address)
        val et_name = findViewById<EditText>(R.id.new_garden_et_name)
        val geocoder = Geocoder(this)


        et_address.setOnFocusChangeListener(object: View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(!hasFocus) {
                    val ab = geocoder.getFromLocationName(et_address.text.toString(), 1);
                    if (ab.size > 0) {
                        val output = "${ab[0].longitude} / ${ab[0].latitude}"
                        Toast.makeText(applicationContext, output, Toast.LENGTH_SHORT).show()
//                        addMapMarker(ab[0].latitude, ab[0].longitude, et_name.text.toString())
                    }
                }
            }
        })


    }
    fun addMapMarker(lat: Double, lng: Double, title: String){
        val latlng = LatLng(lat, lng)
        val options = MarkerOptions().position(latlng).title(title)
        mMap.addMarker(options)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        // initialize map
//        mMap = googleMap
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//    }
}
