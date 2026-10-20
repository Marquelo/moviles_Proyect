package com.example.gestioneventos.ui.oportunidad

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gestioneventos.model.Oportunidad
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenFormularioOportunidad(
    navController: NavController,
    nombreEjecutivo: String = "Ejecutivo Comercial" // El nombre de quien se logueó
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fuente by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("") }
    var potencial by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    // Variables para mostrar errores en rojo
    var errorNombre by remember { mutableStateOf("") }
    var errorFecha by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Oportunidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .verticalScroll(rememberScrollState()), // Para que se pueda hacer scroll
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // CAMPO: NOMBRE
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = "" },
                label = { Text("Nombre del Evento *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorNombre.isNotEmpty()
            )
            if (errorNombre.isNotEmpty()) {
                Text(errorNombre, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // CAMPO: TIPO DE OPORTUNIDAD
            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (Ej: Feria, Municipal...)") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO: FECHA (Con calendario nativo)
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Detección *") },
                trailingIcon = {
                    IconButton(onClick = {
                        val calendario = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                fecha = "$day/${month + 1}/$year"
                                errorFecha = ""
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorFecha.isNotEmpty()
            )
            if (errorFecha.isNotEmpty()) {
                Text(errorFecha, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // CAMPO: PRIORIDAD
            OutlinedTextField(
                value = prioridad,
                onValueChange = { prioridad = it },
                label = { Text("Prioridad (Alta, Media, Baja)") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO: POTENCIAL
            OutlinedTextField(
                value = potencial,
                onValueChange = { potencial = it },
                label = { Text("Potencial Comercial") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO: FUENTE
            OutlinedTextField(
                value = fuente,
                onValueChange = { fuente = it },
                label = { Text("Fuente (Ej: Instagram)") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO: DESCRIPCIÓN
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            // CAMPO: OBSERVACIONES
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = {
                    // Validamos que lo obligatorio esté lleno
                    if (nombre.isEmpty() || fecha.isEmpty()) {
                        if (nombre.isEmpty()) errorNombre = "Este campo es obligatorio"
                        if (fecha.isEmpty()) errorFecha = "Este campo es obligatorio"
                    } else {
                        // 2. Creamos el objeto usando la data class exacta que me pasaste
                        val nuevaOportunidad = Oportunidad(
                            id = System.currentTimeMillis().toString(),
                            nombre = nombre,
                            tipoOportunidad = tipo,
                            descripcion = descripcion,
                            fuenteInformacion = fuente,
                            fechaDeteccion = fecha,
                            responsableId = nombreEjecutivo, // Se llena automáticamente
                            nivelPrioridad = prioridad,
                            potencialComercial = potencial,
                            estadoGestion = "Evento identificado", // Por defecto
                            fechaUltimaActualizacion = fecha,
                            observaciones = observaciones
                        )

                        // Aquí luego puedes guardar esto en una lista o base de datos
                        println("Se creó la oportunidad: $nuevaOportunidad")

                        // Volvemos a la pantalla anterior
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Oportunidad", fontWeight = FontWeight.Bold)
            }
        }
    }
}// --- PREVIEW PARA VER EL FORMULARIO EN ANDROID STUDIO ---
@Preview(showBackground = true, name = "Vista Formulario")
@Composable
fun PreviewFormularioOportunidad() {
    MaterialTheme {
        ScreenFormularioOportunidad(
            navController = rememberNavController(),
            nombreEjecutivo = "Juan Ejecutivo"
        )
    }
}