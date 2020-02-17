package com.example.greencitylife

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_market_add.*
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class market_add : Fragment() {

    private var imageURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_market_add, container, false)

        // set color and text color of market button
        val market_button = view.findViewById<Button>(R.id.market_button)
        market_button.setBackgroundColor(Color.LTGRAY)
        market_button.setTextColor(Color.WHITE)


        // if load picture button is pressed user is asked to give permission to go to gallery
        val img_pick_btn = view.findViewById<Button>(R.id.img_pick_btn)

        img_pick_btn.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
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

        // create toast when submit_entry_button is pressed
        val submit_button = view.findViewById<Button>(R.id.submit_button)
        submit_button.setOnClickListener{
            val imageView = view.findViewById<ImageView>(R.id.entry_image)
            // create name for image
            val imageName = UUID.randomUUID().toString()
            // upload image
            uploadPic(imageView, imageName)
            // create entry
            add_entry(view, imageName)
            // after entry has been created --> navigation to entries news page
            it.findNavController().navigate(R.id.market)
        }
        return view
    }


    private fun add_entry(view: View, imageName: String) {
        // get entry data
        val title: String = view.findViewById<EditText>(R.id.entry_title).text.toString()
        val description: String = view.findViewById<EditText>(R.id.entry_description).text.toString()
        val category: String = view.findViewById<Spinner>(R.id.entry_category).selectedItem.toString()
        // get selection of radio button
        val radioGroup = view.findViewById<RadioGroup>(R.id.entry_type_selection)
        // get type selection
        val type: String = if (radioGroup.checkedRadioButtonId == R.id.offer__radio_button) "offer" else "search"
        // create entry
        val entry = Entry(title = title, additionalText = description, category = category, type = type, imageID = imageName)
        // write entry to database
        entryRef.document().set(entry)
        // make notification that entry has been created
        Toast.makeText(requireContext(), "Entry has been created", Toast.LENGTH_SHORT).show()
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
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
                    Toast.makeText(requireContext(),"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            if (data != null) {
                imageURI = data.data!!
            }
            entry_image.setImageURI(imageURI)
        }
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
}


