package com.example.gestioneventos.data.repository

import com.example.gestioneventos.data.local.OportunidadDao
import com.example.gestioneventos.data.local.OportunidadEntity
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val dao: OportunidadDao) {

    val oportunidades: Flow<List<OportunidadEntity>> = dao.observarTodas()

    suspend fun obtenerPorId(id: Int): OportunidadEntity? = dao.obtenerPorId(id)

    suspend fun guardar(oportunidad: OportunidadEntity) {
        dao.guardar(oportunidad)
    }
}