package com.example.shush.baseDeDados


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RuidoBD::class], version = 1, exportSchema = false)
abstract class  RuidoDataBase : RoomDatabase(){
    abstract fun ruidoDao() : RuidoDao

    companion object{
        @Volatile
        private var INSTANCE : RuidoDataBase? = null

        fun getDatabase(context: Context): RuidoDataBase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext,
                    RuidoDataBase::class.java,"ruido_database").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}