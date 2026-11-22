package com.example.gestioneventos.data.repository

import com.example.gestioneventos.data.remote.EventoExterno
import com.example.gestioneventos.data.remote.EventosExternosApi

open class EventosExternosRepository(
    private val api: EventosExternosApi = EventosExternosApi.crear()
) {
    private val comunasReferencia = listOf(
        "Santiago Centro", "Las Condes", "Providencia",
        "Puente Alto", "Maipú", "Ñuñoa", "La Florida"
    )
    private val organizacionesReferencia = listOf(
        "Cámara de Comercio Local", "Ilustre Municipalidad",
        "Asociación Gremial PyME", "Corporación Cultural",
        "Red Empresarial Metropolitana"
    )

    open suspend fun obtenerCatalogoEventos(): Result<List<EventoExterno>> {
        return try {
            val respuesta = api.obtenerEventosPublicos().take(12)
            val mapeados = respuesta.mapIndexed { index, item ->
                EventoExterno(
                    idEvento = "EXT-${100 + item.id}",
                    nombreEvento = item.title.replaceFirstChar { it.uppercase() }.take(48),
                    fecha = "2026-12-${(index + 5).toString().padStart(2, '0')}",
                    comunaUbicacion = comunasReferencia[index % comunasReferencia.size],
                    organizacion = organizacionesReferencia[index % organizacionesReferencia.size]
                )
            }
            Result.success(mapeados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}