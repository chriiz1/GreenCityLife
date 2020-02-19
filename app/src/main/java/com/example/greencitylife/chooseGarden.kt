package com.example.greencitylife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_choose_garden.*

class chooseGarden : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_garden)
        a_choose_garden_tv_welcome.text = getString(R.string.welcome)

        val items = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choose_garden_spinner_choose.adapter = adapter
        val btn_choose_garden = findViewById<Button>(R.id.choose_garden_btn_choose)
        val nickname = findViewById<TextView>(R.id.a_choose_garden_et_nickname)

        val btn_new_garden = findViewById<Button>(R.id.choose_garden_btn_new_garden)

        btn_choose_garden.setOnClickListener{
            val gardenId = choose_garden_spinner_choose.selectedItem.toString()
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth!!.currentUser
            val newUser = User(currentUser!!.uid, nickname.text.toString(), gardenId = gardenId)
            userRef.document(newUser.UID).set(newUser)
            val intent = Intent(this, ContainerActivity::class.java).apply{}
            startActivity(intent)
        }

        btn_new_garden.setOnClickListener{
            val intent = Intent(this, NewGarden::class.java).apply{}
            startActivity(intent)
        }



        gardenRef.get().addOnSuccessListener { result ->
            for (document in result) {
                val subject = document.getString("name")!!
                items.add(subject)
            }
            adapter.notifyDataSetChanged()
        }

    }
}
