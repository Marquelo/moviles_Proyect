package com.example.gestioneventos.ui.home.ejecutivo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeEjecutivoScreen(
    navController: NavController,
    nombreEjecutivo: String = "Juan Ejecutivo",
    onCrearOportunidadClick: () -> Unit,
    onVerMisOportunidadesClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    val misOportunidades = 12
    val pendientesSeguimiento = 4
    val eventosProximos = 2

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Ejecutivo") },
                actions = {
                    IconButton(onClick = onCerrarSesionClick) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Saludo
            Text(
                text = "¡Hola, $nombreEjecutivo!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gestiona tus oportunidades y revisa tus gestiones pendientes.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$misOportunidades", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Mis Oportunidades", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                    // Pendientes
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$pendientesSeguimiento", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text("Por Seguir", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                    // Próximos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$eventosProximos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Próximos", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCrearOportunidadClick,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Nueva Oportunidad", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedButton(
                onClick = onVerMisOportunidadesClick,
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Mis Oportunidades", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true, name = "Home Ejecutivo")
@Composable
fun HomeEjecutivoScreenPreview() {
    HomeEjecutivoScreen(
        navController = rememberNavController(),
        onCrearOportunidadClick = {},
        onVerMisOportunidadesClick = {},
        onCerrarSesionClick = {}
    )
}