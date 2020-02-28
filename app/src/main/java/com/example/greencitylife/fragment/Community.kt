package com.example.greencitylife.fragment


import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager



class MarkerClusterItem(private val latLng: LatLng,
                        private val title: String,
                        private val snippet: String,
                        private val gardenId: String?) : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun getGardenId(): String? {
        return gardenId
    }

}

/**
 * A simple [Fragment] subclass.
 */
class Community : Fragment(), OnMapReadyCallback {

    val args: CommunityArgs by navArgs()

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mClusterManager: ClusterManager<MarkerClusterItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_community, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        val mapFrag: SupportMapFragment = childFragmentManager.findFragmentById(R.id.mapGardens) as SupportMapFragment
        mapFrag.getMapAsync(this)

        val community_button = view.findViewById<Button>(R.id.community_button)
        community_button.setBackgroundColor(Color.LTGRAY)
        community_button.setTextColor(Color.WHITE)


        val add_button = view.findViewById<FloatingActionButton>(R.id.add_entry_button)
        add_button.setOnClickListener{
            it.findNavController().navigate(R.id.market_add)
        }

        val b_gardens = view.findViewById<Button>(R.id.b_gardens)
        val b_entries = view.findViewById<Button>(R.id.b_entries)
        b_gardens.setOnClickListener {
            // clear all markers
            mMap.clear()
            // zoom to current location
            setUpMap()
            // add gardens to map
            readGardens()
        }

        b_entries.setOnClickListener {
            // clear all markers
            mMap.clear()
            // zoom to current location
            setUpMap()
            // add entries (as clusters to map)
            setUpClusterer(view)
        }


        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // add current location
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        view?.let { addSelectedEntries(it) }
    }


    private fun addSelectedEntries(view: View) {
        if (args.entryList != null) {
            // clear all markers
            mMap.clear()
            // zoom to current location
            setUpMap()
            // add entries (as clusters to map)
            setUpClusterer(view)
        }
    }


    // read gardens from database and create markers on map
    private fun readGardens() {
        val myDB = FirebaseFirestore.getInstance()
        val docRef = myDB.collection("Gardens")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val lon: Double = document.get("lon").toString().toDouble()
                    val lat: Double = document.get("lat").toString().toDouble()
                    val gardenPos = LatLng(lat, lon)
                    val title: String = document.get("name").toString()
                    mMap.addMarker(MarkerOptions().position(gardenPos).title(title))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    // read entries from database and create markers on map
    private fun readEntries(clusterManager: ClusterManager<MarkerClusterItem>,
                            entryList: Array<String>?) {
        val myDB = FirebaseFirestore.getInstance()

        // just read selected entries
        val query: Query = if (entryList != null) {
            myDB.collection("Entries")
        } else {
            myDB.collection("Entries")
        }

        // read entries
        query
            .get()
            .addOnSuccessListener { entries ->
                for (entry in entries) {
                    // get gardenID of each entry
                    val userID = entry.get("userId").toString()
                    val title = entry.get("title").toString()
                    val description = entry.get("description").toString()
                    // get user
                    myDB.collection("Users").document(userID)
                        .get()
                        .addOnSuccessListener { user ->
                            val gardenID = user.get("gardenId").toString()
                            myDB.collection("Gardens").document(gardenID)
                                .get()
                                .addOnSuccessListener { garden ->
                                    val gardenId: String = garden.get("id").toString()
                                    val lon: Double = garden.get("lon").toString().toDouble()
                                    val lat: Double = garden.get("lat").toString().toDouble()
                                    val gardenPos = LatLng(lat, lon)
                                    val entryItem = MarkerClusterItem(gardenPos, title, description, gardenID)
                                    clusterManager.addItem(entryItem)
                                }
                        }
                }
            }
    }

    // setup clusterer
    private fun setUpClusterer(view: View) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.503186, -0.126446), 10f))
        mClusterManager = ClusterManager(context, mMap)
        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        readEntries(mClusterManager, args.entryList)
        // initialize list where garden ids should be stored
        val gardenList: MutableList<String> = mutableListOf()

        mClusterManager.setOnClusterClickListener{cluster ->
            // Zoom in the cluster
            for (item in cluster.items) {
                val gardenId = item.getGardenId().toString()
                gardenList.add(gardenId)
            }
            val gardenList = gardenList.distinct().toTypedArray()
            val action = CommunityDirections.actionCommunityToMarket(gardenIdList = gardenList)

            view.findNavController().navigate(action)
            true
        }


    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    // set up map
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
                // zoom to current position
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

}
