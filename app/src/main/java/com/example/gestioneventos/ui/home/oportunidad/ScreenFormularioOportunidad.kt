package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Sirve para crear (oportunidadId = 0) y para editar (oportunidadId > 0).
 * No tiene lógica de negocio: lee el uiState y avisa los cambios al ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFormularioOportunidad(
    navController: NavController,
    oportunidadId: Int,
    viewModel: OportunidadViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Carga una sola vez por visita al formulario (no se repite al rotar la pantalla
    // ni al volver de la app de cámara, para no perder lo que el usuario ya escribió)
    var cargado by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(oportunidadId) {
        if (!cargado) {
            viewModel.cargar(oportunidadId)
            cargado = true
        }
    }

    LaunchedEffect(uiState.guardadoExitosamente) {
        if (uiState.guardadoExitosamente) {
            viewModel.guardadoConsumido()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (oportunidadId == 0) "Nueva oportunidad" else "Editar oportunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CampoTexto(
                etiqueta = "Nombre de la oportunidad *",
                valor = uiState.nombre,
                error = uiState.nombreError,
                onCambio = viewModel::onNombreChange
            )

            CampoDropdown(
                etiqueta = "Tipo de oportunidad *",
                valor = uiState.tipo,
                opciones = OportunidadOpciones.tipos,
                error = uiState.tipoError,
                onSeleccion = viewModel::onTipoChange
            )

            CampoTexto(
                etiqueta = "Descripción *",
                valor = uiState.descripcion,
                error = uiState.descripcionError,
                onCambio = viewModel::onDescripcionChange,
                lineas = 3
            )

            CampoTexto(
                etiqueta = "Fuente de información *",
                valor = uiState.fuente,
                error = uiState.fuenteError,
                onCambio = viewModel::onFuenteChange
            )

            CampoDropdown(
                etiqueta = "Prioridad *",
                valor = uiState.prioridad,
                opciones = OportunidadOpciones.prioridades,
                error = uiState.prioridadError,
                onSeleccion = viewModel::onPrioridadChange
            )

            CampoDropdown(
                etiqueta = "Potencial comercial *",
                valor = uiState.potencial,
                opciones = OportunidadOpciones.potenciales,
                error = uiState.potencialError,
                onSeleccion = viewModel::onPotencialChange
            )

            CampoDropdown(
                etiqueta = "Estado de gestión *",
                valor = uiState.estado,
                opciones = OportunidadOpciones.estados,
                error = uiState.estadoError,
                onSeleccion = viewModel::onEstadoChange
            )

            CampoTexto(
                etiqueta = "Observaciones",
                valor = uiState.observaciones,
                error = null,
                onCambio = viewModel::onObservacionesChange,
                lineas = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            SeccionRecursosNativos(uiState = uiState, viewModel = viewModel)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::guardar,
                enabled = !uiState.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (oportunidadId == 0) "Guardar oportunidad" else "Guardar cambios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    error: String?,
    onCambio: (String) -> Unit,
    lineas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { Text(error ?: "") },
        minLines = lineas,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoDropdown(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    error: String?,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            isError = error != null,
            supportingText = { Text(error ?: "") },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}