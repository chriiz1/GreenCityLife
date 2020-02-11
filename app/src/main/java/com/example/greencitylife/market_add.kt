package com.example.greencitylife

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_market_add.*

/**
 * A simple [Fragment] subclass.
 */
class market_add : Fragment() {


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

        // set color and text color of add button
        val add_button = view.findViewById<ImageButton>(R.id.offer_button)
        add_button.setBackgroundColor(resources.getColor(R.color.button_market_clicked))

        // if load picture button is pressed user is asked to give permission to go to gallery
        val img_pick_btn = view.findViewById<Button>(R.id.img_pick_btn)

        img_pick_btn.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        // create toast when submit_entry_button is pressed
        val submit_button = view.findViewById<Button>(R.id.submit_button)
        val title: EditText = view.findViewById<EditText>(R.id.entry_title)
        submit_button.setOnClickListener{
            add_entry(view)
            // after entry has been created --> navigation to entries news page
            it.findNavController().navigate(R.id.market)
        }

        return view

    }


    private fun add_entry(view: View) {
        // get entry data
        var type: String = "nothing selected"
        val title: String = view.findViewById<EditText>(R.id.entry_title).text.toString()
        val description: String = view.findViewById<EditText>(R.id.entry_description).text.toString()
        val category: String = view.findViewById<Spinner>(R.id.entry_category).selectedItem.toString()
        // get selection of radio button
        val radioGroup = view.findViewById<RadioGroup>(R.id.entry_type_selection)
        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            type = if (R.id.offer__radio_button == checkedId) "offer" else "search"
        }
        // create entry
        val entry = Entry(title = title, additionalText = description, category = category, type = type)
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
            entry_image.setImageURI(data?.data)
        }
    }





}
