package com.example.greencitylife

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val user = FirebaseAuth.getInstance().currentUser
val myDB = FirebaseFirestore.getInstance()
val users = myDB.collection("Users")



// get ID of current User
fun getCurrentUserID(): String {
    val uid = user!!.uid
    return uid
}

