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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.greencitylife.R
import com.example.greencitylife.activity.TAG
import com.example.greencitylife.activity.GardenImageGallery
import com.example.greencitylife.activity.storage
import com.example.greencitylife.activity.userRef
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_mygarden.*
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class mygarden : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var imageURI: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_mygarden, container, false)
        val currentUser = mAuth!!.currentUser
        val user_id = currentUser!!.uid

        val logged_in_user = view.findViewById<TextView>(R.id.my_garden_tv_user)
        val my_garden = view.findViewById<TextView>(R.id.my_garden_tv_my_garden)

        val img_view = view.findViewById<ImageView>(R.id.my_garden_image_gallery)

        img_view.setOnClickListener{
            val intent = Intent(context, GardenImageGallery::class.java).apply {}
            startActivity(intent)
        }


        val btn_pick_img = view.findViewById<Button>(R.id.my_garden_btn_pick_img)
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

        userRef.document(user_id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val garden = document.getString("gardenId")
                    my_garden.text = garden
                    logged_in_user.text = document.getString("name")

                } else {
                    Log.d(TAG, "No such document")
                }
            }

        return view
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
            IMAGE_PICK_CODE
        )
    }


    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if (data != null) {
                imageURI = data.data!!
            }
            my_garden_image_gallery.setImageURI(imageURI)
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000
        //Permission code
        private val PERMISSION_CODE = 1001
    }

    // upload image to database
    private fun uploadPic (imageView: ImageView, imageName: String) {

        if(imageURI != null){
            // creating storage reference from our app
            val storageRef = storage.reference
            // creating a reference to images/entry.jpg
            val entryImageRef = storageRef.child("entry_images/$imageName")

            // Get the data from an ImageView as bytes
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = entryImageRef.putBytes(data)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
                })?.addOnFailureListener(OnFailureListener { e ->
                    Toast.makeText(context, "Image Uploading Failed " + e.message, Toast.LENGTH_SHORT).show()
                })
        }else{
            Toast.makeText(context, "Please Select an Image", Toast.LENGTH_SHORT).show()
        }
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
                    Toast.makeText(context,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
