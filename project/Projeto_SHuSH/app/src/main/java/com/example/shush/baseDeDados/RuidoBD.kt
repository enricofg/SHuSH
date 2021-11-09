package com.example.shush.baseDeDados

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RuidoBD (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "value") val value: Int?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "longitude") var longitude : Double  = 0.0,
    @ColumnInfo(name = "latitude") var latitude : Double  = 0.0,
    @ColumnInfo(name = "maximo") var maximo : Int = 0,
    @ColumnInfo(name = "minimo") var minimo : Int = 0,
    @ColumnInfo(name = "mediana") var mediana : Int = 0,
    @ColumnInfo(name = "tempoDecorrido") var tDecorrido : String = "",
    @ColumnInfo(name = "Localizacao") var localizacao : String = ""
)
