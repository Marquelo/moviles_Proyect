package com.example.gestioneventos.data.model


data class Contacto(
    val id: String = "",
    val organizacionId: String = "",
    val nombre: String = "",
    val cargoFuncion: String = "",
    val area: String = "",
    val correo: String = "",
    val telefono: String = "",
    val medioContactoPreferente: String = "",
    val estadoContacto: String = "",
    val fechaUltimoContacto: String = "",
    val observaciones: String = ""
)