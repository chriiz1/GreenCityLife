package com.example.greencitylife.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.example.greencitylife.R

import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.greencitylife.activity.TAG
import com.example.greencitylife.activity.gardenRef
import com.example.greencitylife.activity.storage
import com.example.greencitylife.activity.userRef
import com.example.greencitylife.adapter.GalleryImageAdapter
import com.example.greencitylife.adapter.GalleryImageClickListener
import com.example.greencitylife.adapter.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.*
import kotlin.collections.ArrayList

class garden_image_gallery : Fragment(), GalleryImageClickListener {
    // gallery column count
    private val SPAN_COUNT = 3
    private val imageList = ArrayList<Image>()
    private var imageURI: Uri? = null
    lateinit var galleryAdapter: GalleryImageAdapter
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // init adapter

        galleryAdapter = GalleryImageAdapter(imageList)
        galleryAdapter.listener = this
        val view = inflater.inflate(R.layout.fragment_mygarden_image_gallery, container, false)

        // init recyclerview
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerview.apply {
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = galleryAdapter
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser!!
        userId = currentUser.uid

        userRef.document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val gardenId = document.getString("gardenId")
                    loadImages(gardenId!!)
                }
            }

        val btn_pick_img = view.findViewById<Button>(R.id.garden_image_gallery_btn_upload)
        btn_pick_img.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions,
                        PERMISSION_CODE
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

        return view
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if (data != null) {
                imageURI = data.data
                val storageRef = storage.reference
                val imageName = UUID.randomUUID().toString()
                val entryImageRef = storageRef.child("entry_images/$imageName")
                val uploadTask = entryImageRef.putFile(imageURI!!)
                val progressbar = view!!.findViewById<ProgressBar>(R.id.garden_image_gallery_progressbar)
                progressbar.visibility = View.VISIBLE

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnSuccessListener {
                    saveImageInGarden(imageName)
                }
            }
        }
    }

    fun saveImageInGarden(imageURI: String){
        userRef.document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val garden = document.getString("gardenId")
                    gardenRef.document(garden!!).update("images", FieldValue.arrayUnion(imageURI))
                    Log.d(TAG, "$imageURI added to $garden")
                    reloadFragment()


                } else {
                    Log.d(TAG, "No such document")
                }
            }
        gardenRef.document()
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
            IMAGE_PICK_CODE
        )
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
    }

    private fun loadImages(gardenId: String) {
        if (imageList.size > 0)
            imageList.clear()

        gardenRef.document(gardenId!!).get().addOnSuccessListener { document ->
            if (document != null) {
                val images = document.get("images") as ArrayList<*>
                for (image in images)
                    imageList.add(Image(image.toString(), ""))

            }
        galleryAdapter.notifyDataSetChanged()
        }
    }

    override fun onClick(position: Int) {
        // handle click of image
        val bundle = Bundle()
        bundle.putSerializable("images", imageList)
        bundle.putInt("position", position)
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val galleryFragment = mygarden_image_fullscreen()
        galleryFragment.setArguments(bundle)
        galleryFragment.show(fragmentTransaction, "gallery")
    }

    fun reloadFragment(){
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            fragmentTransaction.setReorderingAllowed(false);
        }
        fragmentTransaction.detach(this).attach(this).commit();
    }


    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){
            garden_image_gallery.PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(context,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}