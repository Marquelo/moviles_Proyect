package com.example.gestioneventos.data.model



data class Organizacion(
    val id: String = "",
    val nombre: String = "",
    val tipoOrganizacion: String = "", // Empresa, Municipalidad, Institución, etc.
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publicoAsociado: String = "",
    val observaciones: String = ""
    // Las relaciones (eventos, historial) se manejan por IDs
)