package com.example.greencitylife.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greencitylife.Garden
import com.example.greencitylife.R
import com.example.greencitylife.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val TAG = "MainActivity"

val myDB = FirebaseFirestore.getInstance()
val storage = FirebaseStorage.getInstance()

val userRef = myDB.collection("Users")
val gardenRef = myDB.collection("Gardens")
val entryRef = myDB.collection("Entries")
val messageRef = myDB.collection("Messages")

private var mAuth: FirebaseAuth? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveData()
        //readData()
        mAuth = FirebaseAuth.getInstance()
        if (mAuth!!.currentUser != null)
            mAuth!!.signOut()
    }

    override fun onResume() {
        super.onResume()
        updateAuthInfo()
    }

    fun openRegistration() {
        val intent = Intent(this, Authentication::class.java).apply{}
        startActivity(intent)
    }

    fun dbExamples() {
        val intent = Intent(this, DbExamples::class.java).apply{}
        startActivity(intent)
    }

    fun updateAuthInfo(){
        val currentUser = mAuth!!.currentUser
        val b = a_main_btn_authentication
        a_main_btn_authentication.setOnClickListener(null)
        if(currentUser == null) {
            a_main_tv_authStatus.text = ""
            a_main_btn_authentication.text = "login/sign-up"
            a_main_btn_authentication.setOnClickListener{
                val intent = Intent(this, Authentication::class.java).apply{}
                startActivity(intent)
            }
        }
        else {
            a_main_tv_authStatus.text = "Hello, ${currentUser!!.email}"
            a_main_btn_authentication.text = "logout"
            a_main_btn_authentication.setOnClickListener{
                mAuth!!.signOut()
                updateAuthInfo()
            }
        }
    }
}



// reified means that the Type T also is available at runtime and not only at compile time. Necessary for document.toObject()
suspend inline fun <reified T: Any> getData(query: Query): List<T?> {
    val objList = mutableListOf<T?>()
    val snapshot = query.get().await()

    for (document in snapshot.documents)
        objList.add(document.toObject(T::class.java))

    return objList
}

fun readData(){
    // launch starts a coroutine. This is necessary because the reading of data from firestore happens asynchronous.
    // In this case the execution makes a break at query.get().await() and continues at this point when all data is loaded
    // In the while the execution continues at the next function
    GlobalScope.launch {
        val allUsersFromAttemsgarten =
            getData<User>(
                userRef.whereEqualTo(
                    "gardenId",
                    "Attemsgarten"
                )
            )
        val allUsers =
            getData<User>(
                userRef
            )
        val allGardens =
            getData<Garden>(
                gardenRef
            )
    }
}

fun saveData(){
    //Save example
    val gardenList = listOf(
        Garden("Attemsgarten", "Attemsgasse 24"),
        Garden("Allmende Andritz", "Ziegelstraße 35"),
        Garden(
            "Gartenzwerge Geidorf",
            "Schwimmschuhkai 110"
        ),
        Garden(
            "Niesenberger Garten",
            "Niesenbergergasse 16"
        ),
        Garden("Neuer Garten", "Niesenbergergasse 16")
    )

    val userList = listOf(
        User("chris", "Attemsgarten"),
        User("david", "Attemsgarten"),
        User("dani", "Allmende Andritz")
    )

    for (garden in gardenList)
        gardenRef.document(garden.name).set(garden)

    for (user in userList)
        userRef.document(user.name).set(user)

    //Update example
    gardenRef.document("Attemsgarten").update("address", "Attemsgasse 25")

    //Delete example
    gardenRef.document("Niesenberger Garten").delete()
}
