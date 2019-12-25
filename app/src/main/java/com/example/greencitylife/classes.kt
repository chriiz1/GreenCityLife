package com.example.greencitylife

data class Garden(
    var name: String = "",
    var address: String = ""
)

data class User(
    var name: String = "",
    var gardenId: String = ""
)

data class Entry(
    var userId: String = "",
    var gardenId: String = "",
    var gardeningStuffId: String = "",
    var type: String = "",
    var additionalText: String = ""
)

data class GardeningStuff(
    var name: String = "",
    var category: String = ""
)