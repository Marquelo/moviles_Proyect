package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gestioneventos.data.local.OportunidadEntity
import com.example.gestioneventos.data.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenListaOportunidades(
    navController: NavController,
    usuario: Usuario,
    viewModel: OportunidadViewModel
) {
    // Viene de Room (Flow): se actualiza sola al crear o editar.
    val todas by viewModel.oportunidades.collectAsState()

    val lista = when (usuario.cargo) {
        "MARKETING" -> todas.filter { it.estado == "Participación confirmada" }
        else -> todas
    }
    val puedeEditar = usuario.cargo == "EJECUTIVO" || usuario.cargo == "SUPERVISOR"
    val puedeDescartar = usuario.cargo == "SUPERVISOR"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (usuario.cargo) {
                            "EJECUTIVO" -> "Mis oportunidades"
                            "MARKETING" -> "Eventos confirmados"
                            "DIRECTIVO" -> "Reporte general"
                            else -> "Todas las oportunidades"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (puedeEditar) {
                FloatingActionButton(onClick = { navController.navigate("oportunidad_form/0") }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar oportunidad")
                }
            }
        }
    ) { innerPadding ->
        if (lista.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay oportunidades para mostrar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lista, key = { it.id }) { oportunidad ->
                    TarjetaOportunidad(
                        oportunidad = oportunidad,
                        puedeDescartar = puedeDescartar,
                        onClick = if (puedeEditar) {
                            { navController.navigate("oportunidad_form/${oportunidad.id}") }
                        } else null,
                        onDescartar = {
                            viewModel.cambiarEstado(oportunidad, OportunidadOpciones.ESTADO_DESCARTADA)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaOportunidad(
    oportunidad: OportunidadEntity,
    puedeDescartar: Boolean,
    onClick: (() -> Unit)?,
    onDescartar: () -> Unit
) {
    val descartada = oportunidad.estado == OportunidadOpciones.ESTADO_DESCARTADA

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (descartada)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = oportunidad.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(oportunidad.tipo, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Prioridad: ${oportunidad.prioridad}", style = MaterialTheme.typography.bodySmall)
                Text("Potencial: ${oportunidad.potencial}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "Estado: ${oportunidad.estado}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Fuente: ${oportunidad.fuente}", style = MaterialTheme.typography.bodySmall)
            if (oportunidad.observaciones.isNotBlank()) {
                Text("Obs: ${oportunidad.observaciones}", style = MaterialTheme.typography.bodySmall)
            }
            if (oportunidad.latitud != null && oportunidad.longitud != null) {
                Text(
                    "Ubicación: ${oportunidad.latitud}, ${oportunidad.longitud}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            oportunidad.fotoPath?.let { ruta ->
                FotoPreview(
                    ruta = ruta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            if (puedeDescartar && !descartada) {
                TextButton(onClick = onDescartar) { Text("Descartar") }
            }
        }
    }
}