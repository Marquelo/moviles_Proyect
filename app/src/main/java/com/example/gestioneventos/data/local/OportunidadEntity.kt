package com.example.gestioneventos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fuente: String,
    val prioridad: String,
    val potencial: String,
    val estado: String,
    val observaciones: String,
    // Campos adicionales del Diagrama Caso 007D
    val fechaEvento: String = "",
    val comuna: String = "",
    val organizacion: String = "",
    val contactoNombre: String = "",
    val contactoMedio: String = "",
    val publicoEsperado: String = "",
    val responsable: String = "Juan Ejecutivo",
    val proximaAccion: String = "",
    val condicionesActivacion: String = "", // espacio, costo, requisitos
    val resultadoActivacion: String = "",   // impacto, evaluación y cierre
    val historialCambios: String = "",
    //recursos nativos
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null
)