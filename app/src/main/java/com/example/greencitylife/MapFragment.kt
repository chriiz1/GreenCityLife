package com.example.greencitylife


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFrag: SupportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // initialize map
        mMap = googleMap
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // add my location layer
        // add zoom settings
        val guiSettings = mMap.getUiSettings()
        guiSettings.setZoomControlsEnabled(true)
        // Add a marker in Sydney and move the camera
        val Graz = LatLng(47.0, 15.26)
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Attemsgarten, 13f))


    }




}

