package com.example.greencitylife

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


data class Entry(
    var title: String = "",
    var category: String = "",
    var type: String = "",
    var additionalText: String = "",
    var userId: String = "",
    var gardenId: String = ""
)

data class Comment(
    var text: String = "",
    var date: Date = Calendar.getInstance().time,
    var userId: String = "",
    var entryId: String = ""
)

