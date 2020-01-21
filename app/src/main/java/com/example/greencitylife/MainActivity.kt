package com.example.greencitylife

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

const val TAG = "MainActivity"

val myDB = FirebaseFirestore.getInstance()
val userRef = myDB.collection("Users")
val gardenRef = myDB.collection("Gardens")
val entryRef = myDB.collection("Entries")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //saveData()
        readData()
    }

    fun openRegistration2(view: View) {
        val intent = Intent(this, Registration2::class.java).apply{}
        startActivity(intent)
    }

    fun dbExamples(view: View) {
        val intent = Intent(this, DbExamples::class.java).apply{}
        startActivity(intent)
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
        val allUsersFromAttemsgarten = getData<User>(userRef.whereEqualTo("gardenId", "Attemsgarten"))
        val allUsers = getData<User>(userRef)
        val allGardens = getData<Garden>(gardenRef)
    }
}

fun saveData(){
    //Save example
    val gardenList = listOf(
        Garden("Attemsgarten", "Attemsgasse 24"),
        Garden("Allmende Andritz", "Ziegelstraße 35"),
        Garden("Gartenzwerge Geidorf", "Schwimmschuhkai 110"),
        Garden("Niesenberger Garten", "Niesenbergergasse 16")
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
