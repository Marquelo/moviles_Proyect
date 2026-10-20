package com.example.gestioneventos.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestioneventos.data.local.OportunidadEntity
import com.example.gestioneventos.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Conteos para los paneles de cada rol (se calculan desde Room, no son datos fijos). */
data class ResumenOportunidades(
    val total: Int = 0,
    val enSeguimiento: Int = 0, // contacto pendiente/realizado, propuesta enviada, en espera
    val sinContacto: Int = 0,   // evento identificado o contacto pendiente
    val confirmadas: Int = 0,   // participación confirmada
    val descartadas: Int = 0,
    val enProceso: Int = 0      // ni confirmadas ni descartadas
)

class OportunidadViewModel(
    private val repository: OportunidadRepository
) : ViewModel() {

    /** Lista reactiva: Room emite de nuevo cada vez que se inserta o modifica algo. */
    val oportunidades: StateFlow<List<OportunidadEntity>> = repository.oportunidades
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val resumen: StateFlow<ResumenOportunidades> = oportunidades
        .map { lista ->
            val confirmadas = lista.count { it.estado == "Participación confirmada" }
            val descartadas = lista.count { it.estado == OportunidadOpciones.ESTADO_DESCARTADA }
            ResumenOportunidades(
                total = lista.size,
                enSeguimiento = lista.count {
                    it.estado in listOf(
                        "Contacto pendiente", "Contacto realizado",
                        "Propuesta enviada", "En espera de respuesta"
                    )
                },
                sinContacto = lista.count {
                    it.estado == "Evento identificado" || it.estado == "Contacto pendiente"
                },
                confirmadas = confirmadas,
                descartadas = descartadas,
                enProceso = lista.size - confirmadas - descartadas
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ResumenOportunidades()
        )

    private val _uiState = MutableStateFlow(OportunidadUiState())
    val uiState: StateFlow<OportunidadUiState> = _uiState.asStateFlow()

    // ---------- Carga del formulario (crear / editar) ----------

    /** id == 0 -> formulario vacío (nueva). id > 0 -> carga desde Room (edición). */
    fun cargar(id: Int) {
        _uiState.value = OportunidadUiState()
        if (id == 0) return
        viewModelScope.launch {
            val e = repository.obtenerPorId(id) ?: return@launch
            _uiState.value = OportunidadUiState(
                id = e.id,
                nombre = e.nombre,
                tipo = e.tipo,
                descripcion = e.descripcion,
                fuente = e.fuente,
                prioridad = e.prioridad,
                potencial = e.potencial,
                estado = e.estado,
                observaciones = e.observaciones,
                latitud = e.latitud,
                longitud = e.longitud,
                fotoPath = e.fotoPath
            )
        }
    }

    // ---------- Cambios en los campos (limpian su error) ----------

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, nombreError = null) }
    fun onTipoChange(v: String) = _uiState.update { it.copy(tipo = v, tipoError = null) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v, descripcionError = null) }
    fun onFuenteChange(v: String) = _uiState.update { it.copy(fuente = v, fuenteError = null) }
    fun onPrioridadChange(v: String) = _uiState.update { it.copy(prioridad = v, prioridadError = null) }
    fun onPotencialChange(v: String) = _uiState.update { it.copy(potencial = v, potencialError = null) }
    fun onEstadoChange(v: String) = _uiState.update { it.copy(estado = v, estadoError = null) }
    fun onObservacionesChange(v: String) = _uiState.update { it.copy(observaciones = v) }

    // ---------- Recursos nativos (cámara y ubicación) ----------

    fun onFotoCapturada(ruta: String) =
        _uiState.update { it.copy(fotoPath = ruta, mensajeRecurso = null) }

    fun onQuitarFoto() = _uiState.update { it.copy(fotoPath = null) }

    fun onUbicacionObteniendose() =
        _uiState.update { it.copy(obteniendoUbicacion = true, mensajeRecurso = null) }

    fun onUbicacionObtenida(latitud: Double, longitud: Double) = _uiState.update {
        it.copy(latitud = latitud, longitud = longitud, obteniendoUbicacion = false, mensajeRecurso = null)
    }

    fun onUbicacionError(mensaje: String) =
        _uiState.update { it.copy(obteniendoUbicacion = false, mensajeRecurso = mensaje) }

    fun onQuitarUbicacion() = _uiState.update { it.copy(latitud = null, longitud = null) }

    fun onMensajeRecurso(mensaje: String?) = _uiState.update { it.copy(mensajeRecurso = mensaje) }

    // ---------- Guardar ----------

    fun guardar() {
        val validado = validar(_uiState.value)
        if (validado.hayErrores()) {
            _uiState.value = validado
            return
        }
        _uiState.value = validado.copy(guardando = true)
        viewModelScope.launch {
            repository.guardar(validado.toEntity())
            _uiState.update { it.copy(guardando = false, guardadoExitosamente = true) }
        }
    }

    /** La pantalla llama a esto después de navegar de vuelta, para no repetir el evento. */
    fun guardadoConsumido() = _uiState.update { it.copy(guardadoExitosamente = false) }

    // ---------- Acción rápida desde la lista (supervisor) ----------

    fun cambiarEstado(oportunidad: OportunidadEntity, nuevoEstado: String) {
        viewModelScope.launch { repository.guardar(oportunidad.copy(estado = nuevoEstado)) }
    }

    // ---------- Validaciones (toda la lógica vive aquí, no en la UI) ----------

    private fun validar(s: OportunidadUiState): OportunidadUiState {
        val obligatorio = "Este campo es obligatorio"
        val opcionInvalida = "Seleccione una opción válida"
        return s.copy(
            nombreError = if (s.nombre.isBlank()) obligatorio else null,
            tipoError = when {
                s.tipo.isBlank() -> obligatorio
                s.tipo !in OportunidadOpciones.tipos -> opcionInvalida
                else -> null
            },
            descripcionError = if (s.descripcion.isBlank()) obligatorio else null,
            fuenteError = if (s.fuente.isBlank()) obligatorio else null,
            prioridadError = when {
                s.prioridad.isBlank() -> obligatorio
                s.prioridad !in OportunidadOpciones.prioridades -> opcionInvalida
                else -> null
            },
            potencialError = when {
                s.potencial.isBlank() -> obligatorio
                s.potencial !in OportunidadOpciones.potenciales -> opcionInvalida
                else -> null
            },
            estadoError = when {
                s.estado.isBlank() -> obligatorio
                s.estado !in OportunidadOpciones.estados -> opcionInvalida
                else -> null
            }
        )
    }

    private fun OportunidadUiState.hayErrores() = listOf(
        nombreError, tipoError, descripcionError, fuenteError,
        prioridadError, potencialError, estadoError
    ).any { it != null }

    private fun OportunidadUiState.toEntity() = OportunidadEntity(
        id = id,
        nombre = nombre.trim(),
        tipo = tipo,
        descripcion = descripcion.trim(),
        fuente = fuente.trim(),
        prioridad = prioridad,
        potencial = potencial,
        estado = estado,
        observaciones = observaciones.trim(),
        latitud = latitud,
        longitud = longitud,
        fotoPath = fotoPath
    )

    // ---------- Factory (el ViewModel necesita el Repository) ----------

    class Factory(private val repository: OportunidadRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OportunidadViewModel(repository) as T
    }
}