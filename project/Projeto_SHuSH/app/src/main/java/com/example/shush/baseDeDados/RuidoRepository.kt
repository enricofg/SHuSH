package com.example.shush.baseDados

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.example.shush.baseDeDados.RuidoBD
import com.example.shush.baseDeDados.RuidoDao

class RuidoRepository(private val ruidoDao: RuidoDao) {
    val readAllData: LiveData<List<RuidoBD>> = ruidoDao.readAllData()

    suspend fun addRuido(ruidoBD: RuidoBD){
        ruidoDao.insertAll(ruidoBD)
    }

    fun readAllDataByKeyword(keyword: String): LiveData<List<RuidoBD>>? {
        return ruidoDao.readAllDataByKeyword(keyword = keyword)
    }
}