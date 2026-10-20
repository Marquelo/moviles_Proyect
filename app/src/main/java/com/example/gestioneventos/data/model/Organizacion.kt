package com.example.gestioneventos.data.model



data class Organizacion(
    val id: String = "",
    val nombre: String = "",
    val tipoOrganizacion: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publicoAsociado: String = "",
    val observaciones: String = ""
)