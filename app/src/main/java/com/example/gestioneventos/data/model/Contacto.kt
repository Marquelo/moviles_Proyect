package com.example.gestioneventos.data.model


data class Contacto(
    val id: String = "",
    val organizacionId: String = "", // Llave foránea
    val nombre: String = "",
    val cargoFuncion: String = "",
    val area: String = "", // Eventos, Marketing, RRHH, etc.
    val correo: String = "", // Ficticio
    val telefono: String = "", // Ficticio
    val medioContactoPreferente: String = "",
    val estadoContacto: String = "",
    val fechaUltimoContacto: String = "",
    val observaciones: String = ""
)