package com.example.gestioneventos.ui.oportunidad

/** Opciones fijas definidas por el equipo (basadas en el caso DSY1105_007D). */
object OportunidadOpciones {
    val tipos = listOf(
        "Evento corporativo",
        "Feria empresarial",
        "Actividad municipal",
        "Jornada de beneficios",
        "Feria institucional",
        "Actividad de trabajadores",
        "Activación comercial",
        "Otra instancia relevante"
    )
    val prioridades = listOf("Alta", "Media", "Baja")
    val potenciales = listOf("Alto", "Medio", "Bajo")
    val estados = listOf(
        "Evento identificado",
        "Contacto pendiente",
        "Contacto realizado",
        "Propuesta enviada",
        "En espera de respuesta",
        "Participación confirmada",
        "Oportunidad descartada"
    )
    const val ESTADO_INICIAL = "Evento identificado"
    const val ESTADO_DESCARTADA = "Oportunidad descartada"
}

data class OportunidadUiState(
    val id: Int = 0, // 0 = nueva oportunidad; > 0 = edición
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fuente: String = "",
    val prioridad: String = "",
    val potencial: String = "",
    val estado: String = OportunidadOpciones.ESTADO_INICIAL,
    val observaciones: String = "",

    // Se conservan al editar para no borrar lo que luego guarde el recurso nativo
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null,
    val obteniendoUbicacion: Boolean = false,
    val mensajeRecurso: String? = null, // errores de cámara/ubicación/permisos

    val nombreError: String? = null,
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val fuenteError: String? = null,
    val prioridadError: String? = null,
    val potencialError: String? = null,
    val estadoError: String? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false
)