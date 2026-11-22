package com.example.gestioneventos

import com.example.gestioneventos.data.local.OportunidadEntity
import com.example.gestioneventos.data.remote.EventoExterno
import com.example.gestioneventos.data.repository.EventosExternosRepository
import com.example.gestioneventos.data.repository.OportunidadRepository
import com.example.gestioneventos.ui.oportunidad.OportunidadOpciones
import com.example.gestioneventos.ui.oportunidad.OportunidadViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OportunidadViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: OportunidadRepository
    private lateinit var externosRepository: EventosExternosRepository
    private lateinit var flujoOportunidades: MutableStateFlow<List<OportunidadEntity>>
    private lateinit var viewModel: OportunidadViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        flujoOportunidades = MutableStateFlow(emptyList())
        repository = mockk(relaxed = true)
        externosRepository = mockk()
        every { repository.oportunidades } returns flujoOportunidades
        viewModel = OportunidadViewModel(repository, externosRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guardar con campos vacios marca errores de validacion y no persiste`() = runTest {
        viewModel.guardar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.nombreError)
        assertNotNull(state.tipoError)
        assertNotNull(state.fechaEventoError)
        assertNotNull(state.comunaError)
        assertNotNull(state.organizacionError)
        assertFalse(state.guardadoExitosamente)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    @Test
    fun `guardar oportunidad valida persiste en Room con historial inicial`() = runTest {
        viewModel.onNombreChange("Feria PyME Santiago")
        viewModel.onTipoChange("Feria empresarial")
        viewModel.onFechaEventoChange("2026-12-10")
        viewModel.onComunaChange("Santiago Centro")
        viewModel.onOrganizacionChange("Cámara PyME")
        viewModel.onDescripcionChange("Stand corporativo")
        viewModel.onFuenteChange("Portal Municipal")
        viewModel.onPrioridadChange("Alta")
        viewModel.onPotencialChange("Alto")
        viewModel.onEstadoChange("Evento identificado")

        viewModel.guardar()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.guardadoExitosamente)
        coVerify(exactly = 1) {
            repository.guardar(match {
                it.nombre == "Feria PyME Santiago" &&
                        it.comuna == "Santiago Centro" &&
                        it.historialCambios.contains("Creada en estado")
            })
        }
    }

    @Test
    fun `resumen calcula correctamente totales por estado`() = runTest {
        val job = backgroundScope.launch { viewModel.resumen.collect {} }

        flujoOportunidades.value = listOf(
            crearEntidad(1, "Evento identificado"),
            crearEntidad(2, "Contacto pendiente"),
            crearEntidad(3, "Propuesta enviada"),
            crearEntidad(4, "Participación confirmada"),
            crearEntidad(5, "Oportunidad descartada")
        )
        advanceUntilIdle()

        val res = viewModel.resumen.first()
        assertEquals(5, res.total)
        assertEquals(2, res.sinContacto)
        assertEquals(2, res.enSeguimiento)
        assertEquals(1, res.confirmadas)
        assertEquals(1, res.descartadas)
        job.cancel()
    }

    @Test
    fun `filtros por texto y estado retornan oportunidades esperadas`() = runTest {
        val job = backgroundScope.launch { viewModel.oportunidadesFiltradas.collect {} }

        flujoOportunidades.value = listOf(
            crearEntidad(1, "Evento identificado", nombre = "Expo Salud", comuna = "Providencia"),
            crearEntidad(2, "Participación confirmada", nombre = "Feria Tech", comuna = "Las Condes")
        )
        advanceUntilIdle()

        viewModel.onFiltroEstadoChange("CONFIRMADAS")
        viewModel.onBusquedaChange("Las Condes")
        advanceUntilIdle()

        val filtradas = viewModel.oportunidadesFiltradas.first()
        assertEquals(1, filtradas.size)
        assertEquals("Feria Tech", filtradas.first().nombre)
        job.cancel()
    }

    @Test
    fun `supervisor puede cambiar estado y reasignar responsable`() = runTest {
        val entidad = crearEntidad(10, "Evento identificado")

        viewModel.cambiarEstado(entidad, OportunidadOpciones.ESTADO_CONFIRMADA)
        viewModel.reasignarResponsable(entidad, "María Ventas")
        advanceUntilIdle()

        coVerify { repository.guardar(match { it.estado == OportunidadOpciones.ESTADO_CONFIRMADA }) }
        coVerify { repository.guardar(match { it.responsable == "María Ventas" }) }
    }

    @Test
    fun `cargarCatalogoExterno actualiza lista al responder con exito`() = runTest {
        val listaMock = listOf(
            EventoExterno("EXT-101", "Expo Innovación", "2026-12-05", "Providencia", "Corfo")
        )
        coEvery { externosRepository.obtenerCatalogoEventos() } returns Result.success(listaMock)

        viewModel.cargarCatalogoExterno()
        advanceUntilIdle()

        assertFalse(viewModel.catalogoExterno.value.cargando)
        assertEquals(1, viewModel.catalogoExterno.value.eventos.size)
        assertNull(viewModel.catalogoExterno.value.error)
    }

    @Test
    fun `prepararImportacionDesdeApi precarga uiState sin grabar en Room`() {
        val externo = EventoExterno("EXT-105", "Cumbre Verde", "2026-12-20", "Ñuñoa", "Fundación Eco")
        viewModel.prepararImportacionDesdeApi(externo)

        val state = viewModel.uiState.value
        assertEquals(0, state.id)
        assertEquals("Cumbre Verde", state.nombre)
        assertEquals("Ñuñoa", state.comuna)
        assertEquals("Fundación Eco", state.organizacion)
        coVerify(exactly = 0) { repository.guardar(any()) }
    }

    private fun crearEntidad(
        id: Int,
        estado: String,
        nombre: String = "Evento $id",
        comuna: String = "Santiago"
    ) = OportunidadEntity(
        id = id,
        nombre = nombre,
        tipo = "Evento corporativo",
        descripcion = "Desc",
        fuente = "Web",
        prioridad = "Media",
        potencial = "Medio",
        estado = estado,
        observaciones = "",
        fechaEvento = "2026-12-01",
        comuna = comuna,
        organizacion = "Org Test"
    )
}