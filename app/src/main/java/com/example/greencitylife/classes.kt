package com.example.greencitylife

import com.google.firebase.Timestamp


data class Image(
    var title: String = "",
    var Id: String = "",
    var author: String = ""
)

data class Garden(
    var name: String = "",
    var address: String = "",
    var titleImageID: String = "",
    var images: MutableList<String> = mutableListOf(),
    var lat: Double = 0.0,
    var lon: Double = 0.0
)

data class User(
    var UID: String = "",
    var name: String = "",
    var role: String = "",
    var gardenId: String = ""
)


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

data class Message(
    var text: String = "",
    var creationTime: Timestamp = Timestamp.now(),
    var userId: String = "",
    var gardenId: String = ""
)

