package com.example.greencitylife.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.greencitylife.Garden
import com.example.greencitylife.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_garden.*
import java.io.ByteArrayOutputStream
import java.util.*

class NewGarden : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var imageURI: Uri? = null
    private var existingImageID: String = ""
    private var latlng: LatLng? = null
    private var imageUpdated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_garden)

        val mapFrag: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.new_garden_mapview) as SupportMapFragment
        mapFrag.getMapAsync(this)

        val gardenId = intent.getStringExtra(MESSAGE)
        if (gardenId != null) {
            loadImage(gardenId)
            gardenRef.document(gardenId!!).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        new_garden_et_name.setText(document.get("name").toString())
                        new_garden_et_address.setText(document.get("address").toString())
                        new_garden_submit.text = "Save Changes"
                        existingImageID = document.get("titleImageID").toString()
                        setSubmitExistingGarden(document, gardenId)
                    }
                val et_address = findViewById<EditText>(R.id.new_garden_et_address)
                if(et_address.text.toString() != "") {
                    geoCodeAddress()
                }

                val btn_checkAddress = findViewById<Button>(R.id.new_garden_btn_check_address)
                btn_checkAddress.setOnClickListener{
                    if(et_address.text.toString() != "") {
                        geoCodeAddress()
                    }
                }
            }
        }
        else {
            setSubmitNewGarden()
        }



        val img_pick_btn = findViewById<Button>(R.id.new_garden_btn_upload_image)
        img_pick_btn.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions,
                        NewGarden.PERMISSION_CODE
                    )
                }
                else{
                    //permission already granted
                    pickImageFromGallery()
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }

    }

    fun updateImageinDB(gardenId: String){
        //delete existing image
        val storageRef = storage.reference
        val entryImageRef = storageRef.child("entry_images/$existingImageID")
        entryImageRef.delete()

        //upload new image
        val imageView = findViewById<ImageView>(R.id.new_garden_image_view)
        val imageName = UUID.randomUUID().toString() //ToDo: delete existing image
        gardenRef.document(gardenId).update("titleImageID", imageName)
        uploadPic(imageView, imageName)
    }


    fun setSubmitExistingGarden(document: DocumentSnapshot, gardenId: String){
        new_garden_submit.setOnClickListener{
            geoCodeAddress()
            gardenRef.document(gardenId).update("address", new_garden_et_address.text.toString())
            gardenRef.document(gardenId).update("name", new_garden_et_name.text.toString())
            gardenRef.document(gardenId).update("lat", latlng!!.latitude)
            gardenRef.document(gardenId).update("lon", latlng!!.longitude)

            if(imageUpdated) {
                updateImageinDB(gardenId)
            }

            Toast.makeText(applicationContext, "${document.get("name")} updated!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ContainerActivity::class.java).apply{}
            startActivity(intent)
//              supportFragmentManager.beginTransaction().add(R.id.Host, mygarden()).commit() // ToDo: go directly back to the garden!!
        }
    }

    fun setSubmitNewGarden(){
        new_garden_submit.setOnClickListener{
            // ToDo Validation
            geoCodeAddress()
            val imageView = findViewById<ImageView>(R.id.new_garden_mapview)
            val imageName = UUID.randomUUID().toString()
            uploadPic(imageView, imageName)
            val newId = gardenRef.document().id
            val garden = Garden(
                id = newId,
                name = new_garden_et_name.text.toString(),
                address = new_garden_et_address.text.toString(),
                lat = latlng!!.latitude,
                lon = latlng!!.longitude,
                titleImageID = imageName
            )
            gardenRef.document(newId).set(garden)


            Toast.makeText(applicationContext, "${garden.name} created!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, chooseGarden::class.java).apply {}
            startActivity(intent)
        }
    }

    fun geoCodeAddress(){
        val et_address = findViewById<EditText>(R.id.new_garden_et_address)
        if (et_address.text.toString() == "")
            return
        val geocoder = Geocoder(this)
        val et_name = findViewById<EditText>(R.id.new_garden_et_name)
        val ab = geocoder.getFromLocationName(et_address.text.toString(), 5);
        if (ab.size > 0) {
            val location = getRightLocation(ab)
            latlng = LatLng(location.latitude, location.longitude)
            addMapMarker(latlng!!, et_address.text.toString())
        }
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
        mMap.clear()
        mMap.addMarker(options)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL;
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
            IMAGE_PICK_CODE
        )
    }

    private fun uploadPic (imageView: ImageView, imageName: String) {
        if(imageURI != null){
            val storageRef = storage.reference
            val entryImageRef = storageRef.child("entry_images/$imageName")
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = entryImageRef.putBytes(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener { e ->
                    Toast.makeText(this, "Image Uploading Failed " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }else{
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }


    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if (data != null) {
                imageURI = data.data!!
            }
            new_garden_image_view.setImageURI(imageURI)
            imageUpdated = true
        }
    }

    private fun loadImage(gardenId: String){
        val title_image = findViewById<ImageView>(R.id.new_garden_image_view)
        gardenRef.document(gardenId!!).get().addOnSuccessListener { document ->
            if(document != null) {
                val image = document.get("titleImageID")
                if (image != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference
                    storageRef.child("entry_images/$image").downloadUrl
                        .addOnSuccessListener { uri ->
                            Glide
                                .with(this)
                                .load(uri)
                                .centerCrop()
                                .into(title_image)
                        }
                }
            }
        }
    }
}
