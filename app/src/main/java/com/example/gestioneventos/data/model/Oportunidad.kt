package com.example.gestioneventos.model

data class Oportunidad(
    val id: String = "",
    val nombre: String = "",
    val tipoOportunidad: String = "",
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val fechaDeteccion: String = "",
    val responsableId: String = "",
    val nivelPrioridad: String = "",
    val potencialComercial: String = "",
    val estadoGestion: String = "Evento identificado",
    val fechaUltimaActualizacion: String = "",
    val observaciones: String = ""
)