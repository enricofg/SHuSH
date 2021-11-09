package com.example.shush.baseDados

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.shush.baseDeDados.RuidoBD
import com.example.shush.baseDeDados.RuidoDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RuidoViewModel(application : Application): AndroidViewModel(application) {

    //private val readAllData : List<RuidoBD>
    val readAllData: LiveData<List<RuidoBD>>
    lateinit var readData: LiveData<List<RuidoBD>>
    private val repository : RuidoRepository

    init {
        val ruidoDao = RuidoDataBase.getDatabase(application).ruidoDao()
        repository = RuidoRepository(ruidoDao)
        readAllData = repository.readAllData
    }

    fun addRuido(ruidoBD: RuidoBD){
        viewModelScope.launch(Dispatchers.IO){
            repository.addRuido(ruidoBD)
        }
    }

    fun readAllDataByKeyword(keyword: String): LiveData<List<RuidoBD>>? {
        readData = repository.readAllDataByKeyword(keyword)!!
        return readData
    }

}