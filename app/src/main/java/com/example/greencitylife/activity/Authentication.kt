package com.example.greencitylife.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.greencitylife.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_authentication.*

const val MESSAGE = "com.example.myfirstapp.MESSAGE"


class Authentication : AppCompatActivity(), View.OnClickListener{
    private val TAG = "FirebaseEmailPassword"

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        btn_email_sign_in.setOnClickListener(this)
        btn_email_create_account.setOnClickListener(this)
        btn_sign_out.setOnClickListener(this)
        btn_verify_email.setOnClickListener(this)
        btn_forgot_password.setOnClickListener(this)
        authentication_btn_logout.setOnClickListener(this)
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth!!.currentUser

        if (currentUser == null)
            authentication_btn_logout.visibility = View.INVISIBLE
        else
            authentication_btn_logout.visibility = View.VISIBLE


        button2.setOnClickListener{
            val intent = Intent(this, chooseGarden::class.java).apply {}
            startActivity(intent)
        }

        if(currentUser != null)
            authentication_et_email.setText(currentUser!!.email)
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        if (i == R.id.btn_email_create_account) {
            createAccount(authentication_et_email.text.toString(), authentication_et_password.text.toString())
        } else if (i == R.id.btn_email_sign_in) {
            signIn(authentication_et_email.text.toString(), authentication_et_password.text.toString())
        } else if (i == R.id.btn_verify_email) {
            sendEmailVerification()
        } else if (i == R.id.btn_forgot_password) {
            startActivity(Intent(applicationContext, resetPassword::class.java))
        } else if (i == R.id.authentication_btn_logout){
            if(mAuth != null) {
                mAuth!!.signOut()
                finish()
                startActivity(intent)
            }

        }
    }

    private fun createAccount(email: String, password: String) {
        Log.e(TAG, "createAccount:" + email)
        if (!validateForm(email, password)) {
            return
        }
        progressBar1.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        authentication_et_email.clearFocus()
        authentication_et_password.clearFocus()
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                progressBar1.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (task.isSuccessful) {
                    Log.e(TAG, "createAccount: Success!")
                    Toast.makeText(applicationContext, "createAccount: Success!", Toast.LENGTH_SHORT).show()
                    sendEmailVerification()
                } else {
                    Log.e(TAG, "createAccount: Fail!", task.exception)
                    Toast.makeText(applicationContext, "Creation of Account failed!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.e(TAG, "signIn:" + email)
        if (!validateForm(email, password)) {
            return
        }

        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.e(TAG, "signIn: Success!")
                    val intent = Intent(this, ContainerActivity::class.java).apply{}
                    startActivity(intent)
                    Toast.makeText(applicationContext, "Authentication successfull!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.e(TAG, "signIn: Fail!", task.exception)
                    Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()
                }

                if (!task.isSuccessful) {
                    Toast.makeText(applicationContext, "Authentication failed!", Toast.LENGTH_SHORT).show()                }
            }
    }

    private fun sendEmailVerification() {
        val user = mAuth!!.currentUser!!
        user.sendEmailVerification()
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Verification email sent to " + user.email!!, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, VerifyEmail::class.java).apply{putExtra(MESSAGE, user.email)}
                startActivity(intent)
            } else {
                Log.e(TAG, "sendEmailVerification failed!", task.exception)
                Toast.makeText(applicationContext, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Enter email address!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Enter password!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}
