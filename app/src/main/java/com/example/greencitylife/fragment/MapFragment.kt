package com.example.greencitylife.fragment


import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.greencitylife.R
import com.example.greencitylife.activity.TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFrag: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        return rootView

    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission( requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }


    private fun readGardens() {
        val myDB = FirebaseFirestore.getInstance()
        val docRef = myDB.collection("Gardens").document("Attemsgarten")
        val data = docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        // initialize map
        mMap = googleMap
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // add my location layer
        // Add a marker in Sydney and move the camera
        val Attemsgarten = LatLng(47.07816, 15.44532)
        val Ökohof = LatLng(47.10070, 15.44995)
        val Gartenlabor = LatLng(47.05193, 15.42584)
        val Verein_Seed = LatLng(47.07791, 15.43177)
        val Gartenzwerge_Geidorf = LatLng(47.08632, 15.42324)
        mMap.addMarker(MarkerOptions().position(Attemsgarten).title("Attemsgarten"))
        mMap.addMarker(MarkerOptions().position(Ökohof).title("Ökohof"))
        mMap.addMarker(MarkerOptions().position(Gartenlabor).title("Gartenlabor"))
        mMap.addMarker(MarkerOptions().position(Verein_Seed).title("Verein Seed"))
        mMap.addMarker(MarkerOptions().position(Gartenzwerge_Geidorf).title("Gardenzwerge Geidorf"))
        setUpMap()

        val coords = readGardens()
    }
}

