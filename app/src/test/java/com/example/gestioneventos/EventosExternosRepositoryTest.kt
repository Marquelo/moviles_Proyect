package com.example.gestioneventos

import com.example.gestioneventos.data.remote.EventosExternosApi
import com.example.gestioneventos.data.remote.PostExternoDto
import com.example.gestioneventos.data.repository.EventosExternosRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class EventosExternosRepositoryTest {

    @Test
    fun `obtenerCatalogoEventos transforma respuesta de API en eventos externos`() = runTest {
        val apiMock = mockk<EventosExternosApi>()
        coEvery { apiMock.obtenerEventosPublicos() } returns listOf(
            PostExternoDto(id = 1, userId = 1, title = "encuentro empresarial", body = "detalle")
        )

        val repo = EventosExternosRepository(apiMock)
        val resultado = repo.obtenerCatalogoEventos()

        assertTrue(resultado.isSuccess)
        val evento = resultado.getOrThrow().first()
        assertEquals("EXT-101", evento.idEvento)
        assertEquals("Encuentro empresarial", evento.nombreEvento)
    }

    @Test
    fun `obtenerCatalogoEventos captura excepciones de red sin lanzar crash`() = runTest {
        val apiMock = mockk<EventosExternosApi>()
        coEvery { apiMock.obtenerEventosPublicos() } throws IOException("Sin conexión")

        val repo = EventosExternosRepository(apiMock)
        val resultado = repo.obtenerCatalogoEventos()

        assertTrue(resultado.isFailure)
    }
}