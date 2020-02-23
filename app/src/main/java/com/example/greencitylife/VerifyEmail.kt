package com.example.greencitylife

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.greencitylife.activity.MESSAGE
import com.example.greencitylife.activity.chooseGarden
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_verify_email.*

private var mAuth: FirebaseAuth? = null
class VerifyEmail : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
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
                    this@VerifyEmail.runOnUiThread {
                        startActivity(Intent(this, chooseGarden::class.java).apply{})
                    }
                    Thread.currentThread().interrupt()
                }
            }
        }).start()
    }
}
