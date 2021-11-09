package com.example.shush.baseDeDados

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(RuidoBD::class), version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun ruidoDao(): RuidoDao
}