package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // Datos de prueba
    val todasLasOportunidades = listOf(
        Oportunidad("1", "Feria Municipal", "Actividad municipal", "Alta", "Juan Ejecutivo", "Participación confirmada"),
        Oportunidad("2", "Feria Dental", "Evento corporativo", "Media", "María Ventas", "Contacto pendiente"),
        Oportunidad("3", "Jornada Beneficios", "Jornada de beneficios", "Baja", "Juan Ejecutivo", "Evento identificado"),
        Oportunidad("4", "Feria Institucional", "Feria institucional", "Alta", "María Ventas", "Participación confirmada")
    )

    // Filtro por perfil
    val listaFiltrada = when (usuario.cargo) {
        "EJECUTIVO" -> todasLasOportunidades.filter { it.responsableId == usuario.nombre }
        "MARKETING" -> todasLasOportunidades.filter { it.estadoGestion == "Participación confirmada" }
        else -> todasLasOportunidades // SUPERVISOR y DIRECTIVO ven todo
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFiltrada) { oportunidad ->
                    TarjetaOportunidad(oportunidad = oportunidad, cargoUsuario = usuario.cargo)
                }
            }
        }
    }
}

@Composable
fun TarjetaOportunidad(oportunidad: Oportunidad, cargoUsuario: String) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = oportunidad.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Solo el supervisor ve los 3 puntitos
                if (cargoUsuario == "SUPERVISOR") {
                    Box {
                        IconButton(onClick = { expandedMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(text = { Text("Asignar a Ejecutivo") }, onClick = { expandedMenu = false })
                            DropdownMenuItem(text = { Text("Descartar Oportunidad", color = MaterialTheme.colorScheme.error) }, onClick = { expandedMenu = false })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Responsable: ${oportunidad.responsableId ?: "Sin asignar"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${oportunidad.estadoGestion}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Prioridad: ", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = oportunidad.nivelPrioridad,
                    fontWeight = FontWeight.Bold,
                    color = when (oportunidad.nivelPrioridad) {
                        "Alta" -> MaterialTheme.colorScheme.error
                        "Media" -> Color(0xFFFF9800)
                        else -> Color.Gray
                    }
                )
            }

            // Solo marketing ve el botón de requerimientos
            if (cargoUsuario == "MARKETING") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { /* Lógica */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Agregar requerimiento de material")
                }
            }
        }
    }
}

// ==========================================
// PREVIEWS PARA VER LA LISTA SEGÚN EL CARGO
// ==========================================

@Preview(showBackground = true, name = "1. Lista Ejecutivo")
@Composable
fun PreviewListaEjecutivo() {
    val usuarioFalso = Usuario(
        nombre = "Juan Ejecutivo",
        cargo = "EJECUTIVO"
    )
    MaterialTheme {
        ScreenListaOportunidades(
            navController = rememberNavController(),
            usuario = usuarioFalso
        )
    }
}

@Preview(showBackground = true, name = "2. Lista Supervisor")
@Composable
fun PreviewListaSupervisor() {
    val usuarioFalso = Usuario(
        nombre = "Ana Supervisora",
        cargo = "SUPERVISOR"
    )
    MaterialTheme {
        ScreenListaOportunidades(
            navController = rememberNavController(),
            usuario = usuarioFalso
        )
    }
}

@Preview(showBackground = true, name = "3. Lista Marketing")
@Composable
fun PreviewListaMarketing() {
    val usuarioFalso = Usuario(
        nombre = "Carlos Marketing",
        cargo = "MARKETING"
    )
    MaterialTheme {
        ScreenListaOportunidades(
            navController = rememberNavController(),
            usuario = usuarioFalso
        )
    }
}

@Preview(showBackground = true, name = "4. Lista Directivo")
@Composable
fun PreviewListaDirectivo() {
    val usuarioFalso = Usuario(
        nombre = "Director General",
        cargo = "DIRECTIVO"
    )
    MaterialTheme {
        ScreenListaOportunidades(
            navController = rememberNavController(),
            usuario = usuarioFalso
        )
    }
}