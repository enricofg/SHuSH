package com.example.shush

import com.google.firebase.Timestamp

data class RuidoFB(
    var id: String = "",
    var value: Int = 0,
    var date: Timestamp,
    var longitude: Double  = 0.0,
    var latitude: Double  = 0.0
)
