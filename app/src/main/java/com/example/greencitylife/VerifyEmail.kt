package com.example.greencitylife

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_verify_email.*

private var mAuth: FirebaseAuth? = null
class VerifyEmail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)
        mAuth = FirebaseAuth.getInstance()
        val message = intent.getStringExtra(MESSAGE)
        a_verify_tv_message.text = "A verification link will been sent to $message. \n" +
                "                   Please click on the link to verify your account!"
        checkEmailVerified()
    }

    fun checkEmailVerified(){
        val user = mAuth!!.currentUser
        Thread(Runnable {
            while(!user!!.isEmailVerified()){
                mAuth!!.currentUser!!.reload()
                if(user!!.isEmailVerified ){
                    this@VerifyEmail.runOnUiThread(java.lang.Runnable {
                        startActivity(Intent(this, chooseGarden::class.java).apply{})
                    })
                    Thread.currentThread().interrupt()
                }
            }
        }).start()
    }
}
