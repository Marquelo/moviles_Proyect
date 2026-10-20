package com.example.gestioneventos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun observarTodas(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Int): OportunidadEntity?

    // REPLACE: si el id ya existe, actualiza (edición); si es 0, inserta (nuevo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(oportunidad: OportunidadEntity): Long
}