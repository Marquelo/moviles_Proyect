package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCatalogoExterno(
    navController: NavController,
    viewModel: OportunidadViewModel
) {
    val estado by viewModel.catalogoExterno.collectAsState()

    LaunchedEffect(Unit) {
        if (estado.eventos.isEmpty()) {
            viewModel.cargarCatalogoExterno()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Eventos Externos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarCatalogoExterno() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                estado.cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                estado.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = estado.error ?: "Error de conexión",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.cargarCatalogoExterno() }) {
                            Text("Reintentar")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.eventos, key = { it.idEvento }) { evento ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${evento.idEvento} • ${evento.nombreEvento}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("Organización: ${evento.organizacion}", style = MaterialTheme.typography.bodyMedium)
                                    Text("Fecha: ${evento.fecha} | Comuna: ${evento.comunaUbicacion}", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.prepararImportacionDesdeApi(evento)
                                            navController.navigate("oportunidad_form/-1")
                                        }
                                    ) {
                                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Importar como Oportunidad")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}