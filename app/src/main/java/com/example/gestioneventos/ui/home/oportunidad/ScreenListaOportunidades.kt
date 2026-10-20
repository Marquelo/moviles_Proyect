package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gestioneventos.data.model.Usuario
import com.example.gestioneventos.model.Oportunidad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenListaOportunidades(
    navController: NavController,
    usuario: Usuario
) {
    val todasLasOportunidades = remember {
        mutableStateListOf(
            Oportunidad(
                id = "1", nombre = "Feria Municipal", descripcion = "Actividad municipal",
                nivelPrioridad = "Alta", responsableId = "Juan Ejecutivo", estadoGestion = "Participación confirmada",
                tipoOportunidad = "Feria", fuenteInformacion = "Llamada", fechaDeteccion = "15/05/2026",
                potencialComercial = "Alto", observaciones = "El alcalde está interesado."
            ),
            Oportunidad(
                id = "2", nombre = "Feria Dental", descripcion = "Evento corporativo",
                nivelPrioridad = "Media", responsableId = "", estadoGestion = "Contacto pendiente", // Sin asignar
                tipoOportunidad = "Evento", fuenteInformacion = "Web", fechaDeteccion = "10/05/2026",
                potencialComercial = "Medio", observaciones = "Llamar después de las 15:00."
            )
        )
    }

    val listaFiltrada = when (usuario.cargo) {
        "EJECUTIVO" -> todasLasOportunidades.filter { it.responsableId == usuario.nombre }
        "MARKETING" -> todasLasOportunidades.filter { it.estadoGestion == "Participación confirmada" }
        else -> todasLasOportunidades
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titulo = when (usuario.cargo) {
                        "EJECUTIVO" -> "Mis Oportunidades"
                        "MARKETING" -> "Eventos Confirmados"
                        "DIRECTIVO" -> "Reporte General"
                        else -> "Todas las Oportunidades"
                    }
                    Text(titulo)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No hay oportunidades para mostrar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(listaFiltrada, key = { it.id }) { oportunidad ->
                    TarjetaOportunidad(
                        oportunidad = oportunidad,
                        cargoUsuario = usuario.cargo,
                        // AQUÍ ESTÁ LA LÓGICA: Buscamos la oportunidad y la actualizamos
                        onAsignar = { nuevoEjecutivo ->
                            val index = todasLasOportunidades.indexOfFirst { it.id == oportunidad.id }
                            if (index != -1) {
                                todasLasOportunidades[index] = oportunidad.copy(responsableId = nuevoEjecutivo)
                            }
                        },
                        onDescartar = {
                            val index = todasLasOportunidades.indexOfFirst { it.id == oportunidad.id }
                            if (index != -1) {
                                todasLasOportunidades[index] = oportunidad.copy(estadoGestion = "Descartada")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaOportunidad(
    oportunidad: Oportunidad,
    cargoUsuario: String,
    onAsignar: (String) -> Unit,
    onDescartar: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (oportunidad.estadoGestion == "Descartada")
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = oportunidad.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (oportunidad.estadoGestion == "Descartada") Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = oportunidad.tipoOportunidad,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (cargoUsuario == "SUPERVISOR") {
                    Box {
                        IconButton(onClick = { expandedMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Asignar a Juan Ejecutivo") },
                                onClick = {
                                    onAsignar("Juan Ejecutivo")
                                    expandedMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Asignar a María Ventas") },
                                onClick = {
                                    onAsignar("María Ventas")
                                    expandedMenu = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Descartar Oportunidad", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    onDescartar()
                                    expandedMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = oportunidad.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = oportunidad.estadoGestion,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (oportunidad.estadoGestion == "Descartada") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Prioridad: ${oportunidad.nivelPrioridad}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = when (oportunidad.nivelPrioridad) {
                        "Alta" -> MaterialTheme.colorScheme.error
                        "Media" -> Color(0xFFFF9800)
                        else -> Color.Gray
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Fuente: ${oportunidad.fuenteInformacion}", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Potencial: ${oportunidad.potencialComercial}", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Detectada: ${oportunidad.fechaDeteccion}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (oportunidad.responsableId.isNotEmpty()) oportunidad.responsableId else "Sin asignar",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (oportunidad.responsableId.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (oportunidad.observaciones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Obs: ${oportunidad.observaciones}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }

            if (cargoUsuario == "MARKETING") {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Agregar requerimiento de material")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Lista Supervisor Interactiva")
@Composable
fun PreviewListaSupervisorInteractiva() {
    val usuarioFalso = Usuario(nombre = "Ana Supervisora", cargo = "SUPERVISOR")
    MaterialTheme { ScreenListaOportunidades(navController = rememberNavController(), usuario = usuarioFalso) }
}