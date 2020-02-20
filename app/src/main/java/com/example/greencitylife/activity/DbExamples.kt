package com.example.greencitylife.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.greencitylife.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_db_examples.*


class DbExamples : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        var members = Garden::class.java.declaredFields
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db_examples)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser
        if (currentUser != null)
            txtUser.text = currentUser!!.uid
        else
            txtUser.text = "no User logged in!"
        val items = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        gardenRef.get().addOnSuccessListener { result ->
            for (document in result) {
                val subject = document.getString("name")!!
                items.add(subject)
            }
            adapter.notifyDataSetChanged()
        }

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Gardens", "Users"))
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCollections.adapter = adapter2

        spCollections?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var item = spCollections.selectedItem.toString()

//                val fields = when(item){
//                    "Gardens" -> Garden::class.java.kotlin.members
//                    else -> User::class.java.kotlin.members
//                }
//                print("foo")
            }
        }
    }


    fun saveUser(view: View) {
        var user = User(
            UID = txtUser.text.toString(),
            name = txtName.text.toString(),
            gardenId = spinner.selectedItem.toString()
        )
        userRef.document(user.UID).set(user)
    }

    fun displayContents(view: View){
        val item = spCollections.selectedItem.toString()
        var ref = when(item){
            "Gardens" -> gardenRef
            "Users" -> userRef
            else -> gardenRef
        }

        ref.get().addOnSuccessListener { result ->
            var text = ""
            for (document in result) {
                text += document.getString("name") + '\n'
            }
                txtMulti.setText(text)
        }
    }
}
