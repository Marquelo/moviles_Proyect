package com.example.gestioneventos.ui.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestioneventos.data.local.OportunidadEntity
import com.example.gestioneventos.data.remote.EventoExterno
import com.example.gestioneventos.data.repository.EventosExternosRepository
import com.example.gestioneventos.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResumenOportunidades(
    val total: Int = 0,
    val enSeguimiento: Int = 0,
    val sinContacto: Int = 0,
    val confirmadas: Int = 0,
    val cerradas: Int = 0,
    val descartadas: Int = 0,
    val enProceso: Int = 0
)

class OportunidadViewModel(
    private val repository: OportunidadRepository,
    private val externosRepository: EventosExternosRepository = EventosExternosRepository()
) : ViewModel() {

    val oportunidades: StateFlow<List<OportunidadEntity>> = repository.oportunidades
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // Búsqueda y filtro rápido para Supervisor y Ejecutivos
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _filtroEstado = MutableStateFlow("TODAS")
    val filtroEstado: StateFlow<String> = _filtroEstado.asStateFlow()

    val oportunidadesFiltradas: StateFlow<List<OportunidadEntity>> = combine(
        oportunidades,
        _textoBusqueda,
        _filtroEstado
    ) { lista, query, filtro ->
        lista.filter { item ->
            val coincideTexto = query.isBlank() ||
                    item.nombre.contains(query, ignoreCase = true) ||
                    item.organizacion.contains(query, ignoreCase = true) ||
                    item.comuna.contains(query, ignoreCase = true) ||
                    item.responsable.contains(query, ignoreCase = true)

            val coincideFiltro = when (filtro) {
                "SIN_CONTACTO" -> item.estado == "Evento identificado" || item.estado == "Contacto pendiente"
                "EN_SEGUIMIENTO" -> item.estado in listOf("Contacto realizado", "Propuesta enviada", "En espera de respuesta")
                "CONFIRMADAS" -> item.estado == OportunidadOpciones.ESTADO_CONFIRMADA || item.estado == OportunidadOpciones.ESTADO_CERRADA
                "DESCARTADAS" -> item.estado == OportunidadOpciones.ESTADO_DESCARTADA
                else -> true
            }
            coincideTexto && coincideFiltro
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val resumen: StateFlow<ResumenOportunidades> = oportunidades
        .map { lista ->
            val confirmadas = lista.count { it.estado == OportunidadOpciones.ESTADO_CONFIRMADA }
            val cerradas = lista.count { it.estado == OportunidadOpciones.ESTADO_CERRADA }
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
                cerradas = cerradas,
                descartadas = descartadas,
                enProceso = lista.size - confirmadas - cerradas - descartadas
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ResumenOportunidades()
        )

    private val _uiState = MutableStateFlow(OportunidadUiState())
    val uiState: StateFlow<OportunidadUiState> = _uiState.asStateFlow()

    private val _catalogoExterno = MutableStateFlow(CatalogoExternoUiState())
    val catalogoExterno: StateFlow<CatalogoExternoUiState> = _catalogoExterno.asStateFlow()

    fun onBusquedaChange(texto: String) {
        _textoBusqueda.value = texto
    }

    fun onFiltroEstadoChange(filtro: String) {
        _filtroEstado.value = filtro
    }

    // ---------- Carga del formulario ----------

    fun cargar(id: Int) {
        if (id == -1) return // -1 indica datos ya precargados desde API externa
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
                fechaEvento = e.fechaEvento,
                comuna = e.comuna,
                organizacion = e.organizacion,
                contactoNombre = e.contactoNombre,
                contactoMedio = e.contactoMedio,
                publicoEsperado = e.publicoEsperado,
                responsable = e.responsable,
                proximaAccion = e.proximaAccion,
                condicionesActivacion = e.condicionesActivacion,
                resultadoActivacion = e.resultadoActivacion,
                historialCambios = e.historialCambios,
                latitud = e.latitud,
                longitud = e.longitud,
                fotoPath = e.fotoPath
            )
        }
    }

    // ---------- API Externa (Retrofit) ----------

    fun cargarCatalogoExterno() {
        _catalogoExterno.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            externosRepository.obtenerCatalogoEventos()
                .onSuccess { lista ->
                    _catalogoExterno.update { it.copy(cargando = false, eventos = lista, error = null) }
                }
                .onFailure { err ->
                    _catalogoExterno.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo consultar el catálogo externo: ${err.localizedMessage ?: "Error de red"}"
                        )
                    }
                }
        }
    }

    /** Precarga datos de la API externa en el formulario sin grabar automáticamente en Room. */
    fun prepararImportacionDesdeApi(evento: EventoExterno) {
        _uiState.value = OportunidadUiState(
            id = 0,
            nombre = evento.nombreEvento,
            tipo = "Evento corporativo",
            descripcion = "Evento importado desde catálogo externo (${evento.idEvento}).",
            fuente = "API Externa (${evento.idEvento})",
            fechaEvento = evento.fecha,
            comuna = evento.comunaUbicacion,
            organizacion = evento.organizacion,
            prioridad = "Media",
            potencial = "Medio",
            estado = OportunidadOpciones.ESTADO_INICIAL
        )
    }

    // ---------- Cambios de campos ----------

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, nombreError = null) }
    fun onTipoChange(v: String) = _uiState.update { it.copy(tipo = v, tipoError = null) }
    fun onDescripcionChange(v: String) = _uiState.update { it.copy(descripcion = v, descripcionError = null) }
    fun onFuenteChange(v: String) = _uiState.update { it.copy(fuente = v, fuenteError = null) }
    fun onPrioridadChange(v: String) = _uiState.update { it.copy(prioridad = v, prioridadError = null) }
    fun onPotencialChange(v: String) = _uiState.update { it.copy(potencial = v, potencialError = null) }
    fun onEstadoChange(v: String) = _uiState.update { it.copy(estado = v, estadoError = null) }
    fun onObservacionesChange(v: String) = _uiState.update { it.copy(observaciones = v) }
    fun onFechaEventoChange(v: String) = _uiState.update { it.copy(fechaEvento = v, fechaEventoError = null) }
    fun onComunaChange(v: String) = _uiState.update { it.copy(comuna = v, comunaError = null) }
    fun onOrganizacionChange(v: String) = _uiState.update { it.copy(organizacion = v, organizacionError = null) }
    fun onContactoNombreChange(v: String) = _uiState.update { it.copy(contactoNombre = v) }
    fun onContactoMedioChange(v: String) = _uiState.update { it.copy(contactoMedio = v) }
    fun onPublicoEsperadoChange(v: String) = _uiState.update { it.copy(publicoEsperado = v) }
    fun onResponsableChange(v: String) = _uiState.update { it.copy(responsable = v) }
    fun onProximaAccionChange(v: String) = _uiState.update { it.copy(proximaAccion = v) }
    fun onCondicionesActivacionChange(v: String) = _uiState.update { it.copy(condicionesActivacion = v) }
    fun onResultadoActivacionChange(v: String) = _uiState.update { it.copy(resultadoActivacion = v) }

    // ---------- Recursos nativos ----------

    fun onFotoCapturada(ruta: String) = _uiState.update { it.copy(fotoPath = ruta, mensajeRecurso = null) }
    fun onQuitarFoto() = _uiState.update { it.copy(fotoPath = null) }
    fun onUbicacionObteniendose() = _uiState.update { it.copy(obteniendoUbicacion = true, mensajeRecurso = null) }
    fun onUbicacionObtenida(lat: Double, lon: Double) = _uiState.update {
        it.copy(latitud = lat, longitud = lon, obteniendoUbicacion = false, mensajeRecurso = null)
    }
    fun onUbicacionError(msg: String) = _uiState.update { it.copy(obteniendoUbicacion = false, mensajeRecurso = msg) }
    fun onQuitarUbicacion() = _uiState.update { it.copy(latitud = null, longitud = null) }
    fun onMensajeRecurso(msg: String?) = _uiState.update { it.copy(mensajeRecurso = msg) }

    // ---------- Guardar + Bitácora de historial ----------

    fun guardar() {
        val validado = validar(_uiState.value)
        if (validado.hayErrores()) {
            _uiState.value = validado
            return
        }
        val traza = if (validado.id == 0) {
            "• Creada en estado '${validado.estado}' (Resp: ${validado.responsable})"
        } else {
            "• Actualizada -> Estado: '${validado.estado}', Prioridad: ${validado.prioridad}, Resp: ${validado.responsable}"
        }
        val nuevoHistorial = listOf(validado.historialCambios, traza)
            .filter { it.isNotBlank() }
            .joinToString("\n")

        _uiState.value = validado.copy(guardando = true, historialCambios = nuevoHistorial)
        viewModelScope.launch {
            repository.guardar(_uiState.value.toEntity())
            _uiState.update { it.copy(guardando = false, guardadoExitosamente = true) }
        }
    }

    fun guardadoConsumido() = _uiState.update { it.copy(guardadoExitosamente = false) }

    // ---------- Acciones rápidas de Supervisión / Gestión ----------

    fun cambiarEstado(oportunidad: OportunidadEntity, nuevoEstado: String) {
        val log = "${oportunidad.historialCambios}\n• Cambio rápido de estado a '$nuevoEstado'".trim()
        viewModelScope.launch {
            repository.guardar(oportunidad.copy(estado = nuevoEstado, historialCambios = log))
        }
    }

    fun reasignarResponsable(oportunidad: OportunidadEntity, nuevoResponsable: String) {
        val log = "${oportunidad.historialCambios}\n• Reasignada a '$nuevoResponsable'".trim()
        viewModelScope.launch {
            repository.guardar(oportunidad.copy(responsable = nuevoResponsable, historialCambios = log))
        }
    }

    fun cambiarPrioridad(oportunidad: OportunidadEntity, nuevaPrioridad: String) {
        val log = "${oportunidad.historialCambios}\n• Prioridad ajustada a '$nuevaPrioridad'".trim()
        viewModelScope.launch {
            repository.guardar(oportunidad.copy(prioridad = nuevaPrioridad, historialCambios = log))
        }
    }

    fun eliminar(oportunidad: OportunidadEntity) {
        viewModelScope.launch { repository.eliminar(oportunidad) }
    }

    // ---------- Validaciones ----------

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
            fechaEventoError = if (s.fechaEvento.isBlank()) obligatorio else null,
            comunaError = if (s.comuna.isBlank()) obligatorio else null,
            organizacionError = if (s.organizacion.isBlank()) obligatorio else null,
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
        nombreError, tipoError, fechaEventoError, comunaError, organizacionError,
        descripcionError, fuenteError, prioridadError, potencialError, estadoError
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
        fechaEvento = fechaEvento.trim(),
        comuna = comuna.trim(),
        organizacion = organizacion.trim(),
        contactoNombre = contactoNombre.trim(),
        contactoMedio = contactoMedio.trim(),
        publicoEsperado = publicoEsperado.trim(),
        responsable = responsable.trim(),
        proximaAccion = proximaAccion.trim(),
        condicionesActivacion = condicionesActivacion.trim(),
        resultadoActivacion = resultadoActivacion.trim(),
        historialCambios = historialCambios.trim(),
        latitud = latitud,
        longitud = longitud,
        fotoPath = fotoPath
    )

    class Factory(
        private val repository: OportunidadRepository,
        private val externosRepository: EventosExternosRepository = EventosExternosRepository()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OportunidadViewModel(repository, externosRepository) as T
    }
}