package com.example.gestioneventos.data.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val perfil: String = "",
    val area: String = "",
    val zonaSucursal: String = "",
    val estadoCuenta: String = "Activa",
    val fechaCreacion: String = "",
    val fechaActualizacion: String = "",
    val cargo: String=""
)