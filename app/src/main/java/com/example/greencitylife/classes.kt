package com.example.greencitylife

import com.google.firebase.Timestamp
import java.util.*


data class Garden(
    var name: String = "",
    var address: String = ""
)

data class User(
    var UID: String = "",
    var name: String = "",
    var role: String = "",
    var gardenId: String = ""
)
//save()


data class Entry(
    var title: String = "",
    var category: String = "",
    var type: String = "",
    var additionalText: String = "",
    var userId: String = "",
    var gardenId: String = "",
    var imageID: String = "",
    var creationTime: Timestamp = Timestamp.now()
)

data class Comment(
    var text: String = "",
    var date: Date = Calendar.getInstance().time,
    var userId: String = "",
    var entryId: String = ""
)

