package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val filtradas by viewModel.oportunidadesFiltradas.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val filtroEstado by viewModel.filtroEstado.collectAsState()

    val lista = when (usuario.cargo) {
        "MARKETING" -> filtradas.filter {
            it.estado == OportunidadOpciones.ESTADO_CONFIRMADA ||
                    it.estado == OportunidadOpciones.ESTADO_CERRADA
        }
        else -> filtradas
    }

    val puedeCrear = usuario.cargo == "EJECUTIVO" || usuario.cargo == "SUPERVISOR"
    val puedeEditar = usuario.cargo == "EJECUTIVO" || usuario.cargo == "SUPERVISOR" || usuario.cargo == "MARKETING"
    val esSupervisor = usuario.cargo == "SUPERVISOR"
    val puedeEliminar = usuario.cargo == "EJECUTIVO"

    var oportunidadAEliminar by remember { mutableStateOf<OportunidadEntity?>(null) }

    val opcionesFiltro = listOf(
        "TODAS" to "Todas",
        "SIN_CONTACTO" to "Sin contacto",
        "EN_SEGUIMIENTO" to "En seguimiento",
        "CONFIRMADAS" to "Confirmadas",
        "DESCARTADAS" to "Descartadas"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (usuario.cargo) {
                            "EJECUTIVO" -> "Mis oportunidades"
                            "MARKETING" -> "Gestión de activaciones"
                            "DIRECTIVO" -> "Reporte general"
                            else -> "Supervisión de oportunidades"
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
            if (puedeCrear) {
                FloatingActionButton(onClick = { navController.navigate("oportunidad_form/0") }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar oportunidad")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Barra de búsqueda por evento, comuna, organización o responsable
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = viewModel::onBusquedaChange,
                placeholder = { Text("Buscar por evento, comuna, org. o responsable") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Filtros por estado (ocultos para Marketing ya que solo ve confirmadas/cerradas)
            if (usuario.cargo != "MARKETING") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(opcionesFiltro) { (clave, etiqueta) ->
                        FilterChip(
                            selected = filtroEstado == clave,
                            onClick = { viewModel.onFiltroEstadoChange(clave) },
                            label = { Text(etiqueta) }
                        )
                    }
                }
            }

            if (lista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay oportunidades que coincidan con el filtro.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.id }) { oportunidad ->
                        TarjetaOportunidad(
                            oportunidad = oportunidad,
                            cargo = usuario.cargo,
                            esSupervisor = esSupervisor,
                            puedeEliminar = puedeEliminar,
                            onClick = if (puedeEditar) {
                                { navController.navigate("oportunidad_form/${oportunidad.id}") }
                            } else null,
                            onReasignar = { nuevoResp ->
                                viewModel.reasignarResponsable(oportunidad, nuevoResp)
                            },
                            onCambiarPrioridad = { nuevaPrio ->
                                viewModel.cambiarPrioridad(oportunidad, nuevaPrio)
                            },
                            onDescartar = {
                                viewModel.cambiarEstado(oportunidad, OportunidadOpciones.ESTADO_DESCARTADA)
                            },
                            onEliminar = { oportunidadAEliminar = oportunidad }
                        )
                    }
                }
            }
        }
    }

    oportunidadAEliminar?.let { aEliminar ->
        AlertDialog(
            onDismissRequest = { oportunidadAEliminar = null },
            title = { Text("Eliminar oportunidad") },
            text = { Text("¿Seguro que quieres eliminar \"${aEliminar.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(aEliminar)
                    oportunidadAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { oportunidadAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TarjetaOportunidad(
    oportunidad: OportunidadEntity,
    cargo: String,
    esSupervisor: Boolean,
    puedeEliminar: Boolean,
    onClick: (() -> Unit)?,
    onReasignar: (String) -> Unit,
    onCambiarPrioridad: (String) -> Unit,
    onDescartar: () -> Unit,
    onEliminar: () -> Unit
) {
    val descartada = oportunidad.estado == OportunidadOpciones.ESTADO_DESCARTADA
    var menuResponsableAbierto by remember { mutableStateOf(false) }
    var menuPrioridadAbierto by remember { mutableStateOf(false) }

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
            Text(
                text = "${oportunidad.tipo} • ${oportunidad.organizacion.ifBlank { "Sin organización" }}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (oportunidad.fechaEvento.isNotBlank() || oportunidad.comuna.isNotBlank()) {
                Text(
                    text = "Fecha: ${oportunidad.fechaEvento.ifBlank { "Por definir" }} | Comuna: ${oportunidad.comuna.ifBlank { "N/A" }}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Responsable: ${oportunidad.responsable}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
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
            if (oportunidad.condicionesActivacion.isNotBlank()) {
                Text(
                    text = "Condiciones: ${oportunidad.condicionesActivacion}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (oportunidad.resultadoActivacion.isNotBlank()) {
                Text(
                    text = "Resultados: ${oportunidad.resultadoActivacion}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
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

            // Controles rápidos según el rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (esSupervisor) {
                    Box {
                        TextButton(onClick = { menuResponsableAbierto = true }) {
                            Text("Reasignar")
                        }
                        DropdownMenu(
                            expanded = menuResponsableAbierto,
                            onDismissRequest = { menuResponsableAbierto = false }
                        ) {
                            OportunidadOpciones.responsables.forEach { resp ->
                                DropdownMenuItem(
                                    text = { Text(resp) },
                                    onClick = {
                                        onReasignar(resp)
                                        menuResponsableAbierto = false
                                    }
                                )
                            }
                        }
                    }

                    Box {
                        TextButton(onClick = { menuPrioridadAbierto = true }) {
                            Text("Prioridad")
                        }
                        DropdownMenu(
                            expanded = menuPrioridadAbierto,
                            onDismissRequest = { menuPrioridadAbierto = false }
                        ) {
                            OportunidadOpciones.prioridades.forEach { prio ->
                                DropdownMenuItem(
                                    text = { Text(prio) },
                                    onClick = {
                                        onCambiarPrioridad(prio)
                                        menuPrioridadAbierto = false
                                    }
                                )
                            }
                        }
                    }

                    if (!descartada) {
                        TextButton(onClick = onDescartar) {
                            Text("Descartar", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                if (cargo == "MARKETING" && onClick != null) {
                    TextButton(onClick = onClick) {
                        Text("Registrar condiciones / resultados")
                    }
                }

                if (puedeEliminar) {
                    TextButton(onClick = onEliminar) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}