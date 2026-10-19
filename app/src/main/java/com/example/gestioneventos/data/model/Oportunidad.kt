package com.example.gestioneventos.model

data class Oportunidad(
    val id: String = "",
    val nombre: String = "",
    val tipoOportunidad: String = "", // Evento corporativo, Feria, etc.
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val fechaDeteccion: String = "",
    val responsableId: String = "", // ID del Usuario asignado
    val nivelPrioridad: String = "", // Alta, Media, Baja
    val potencialComercial: String = "",
    val estadoGestion: String = "Evento identificado", // Los estados de la página 10
    val fechaUltimaActualizacion: String = "",
    val observaciones: String = ""
)