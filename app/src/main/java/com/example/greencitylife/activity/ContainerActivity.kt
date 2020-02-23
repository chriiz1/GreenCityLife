package com.example.greencitylife.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greencitylife.R
import com.example.greencitylife.users
import com.google.firebase.auth.FirebaseAuth

class ContainerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        // if user is loged in email address is shown in menu bar
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user?.let {
                // Name, email address, and profile photo Url
                val userID = user.uid
                users.document(userID)
                    .get()
                    .addOnSuccessListener{document ->
                        val userName = document.get("name").toString()
                        menu!!.findItem(R.id.action_show_user).setTitle(userName)
                    }
            }
        } else {
            val userText = "Sign in"
            menu!!.findItem(R.id.action_show_user).setTitle(userText)
        }


        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_login -> {
                val intent = Intent(this, Authentication::class.java).apply{}
                startActivity(intent)
                return true
            }

            R.id.action_settings -> {
                Toast.makeText(applicationContext, "click on share", Toast.LENGTH_LONG).show()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}


