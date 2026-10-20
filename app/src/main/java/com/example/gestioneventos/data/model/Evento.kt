package com.example.gestioneventos.model

data class Evento(
    val id: String = "",
    val oportunidadId: String = "",
    val organizacionId: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fechaInicio: String = "",
    val fechaTermino: String = "",
    val horario: String = "",
    val direccionUbicacion: String = "",
    val comuna: String = "",
    val region: String = "",
    val publicoEsperado: String = "",
    val publicoObjetivo: String = "",
    val cantidadEstimadaAsistentes: Int = 0,
    val condicionesParticipacion: String = "",
    val requisitos: String = "",
    val costosReferenciales: String = "",
    val espaciosDisponibles: String = "",
    val observaciones: String = ""
)