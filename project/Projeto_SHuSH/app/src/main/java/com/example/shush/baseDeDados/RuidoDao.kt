package com.example.shush.baseDeDados

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RuidoDao {
    @Query("SELECT * FROM ruidoBD")
    fun getAll(): List<RuidoBD>

    @Query("SELECT * FROM RuidoBD WHERE id = :ruidoId")
    fun loadById(ruidoId: Int): List<RuidoBD>

    @Insert
    fun insertAll(vararg ruidos: RuidoBD)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRuido(ruidoBD: RuidoBD)

    @Query("SELECT * FROM RuidoBD ORDER BY date ASC")
    fun readAllData(): LiveData<List<RuidoBD>>

    @Query("SELECT * FROM RuidoBD WHERE (value LIKE '%' + :keyword + '%' OR date LIKE '%' + :keyword + '%' OR longitude LIKE '%' + :keyword + '%' OR latitude LIKE '%' + :keyword + '%' OR tempoDecorrido LIKE '%' + :keyword + '%' OR maximo LIKE '%' + :keyword + '%' OR minimo LIKE '%' + :keyword + '%' OR mediana LIKE '%' + :keyword + '%') ORDER BY date ASC")
    fun readAllDataByKeyword(keyword: String): LiveData<List<RuidoBD>>
}