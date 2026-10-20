package com.example.gestioneventos.ui.oportunidad

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.gestioneventos.util.CamaraHelper
import com.example.gestioneventos.util.UbicacionHelper
import java.io.File

/**
 * Recursos nativos del formulario: cámara (foto de evidencia) y ubicación (referencia general).
 * Los permisos se piden solo cuando el usuario presiona el botón; la ubicación es opcional.
 */
@Composable
fun SeccionRecursosNativos(
    uiState: OportunidadUiState,
    viewModel: OportunidadViewModel
) {
    val context = LocalContext.current

    // Sobrevive a un cambio de configuración mientras la app de cámara está abierta
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    // ---------- Cámara ----------
    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val ruta = rutaPendiente
        rutaPendiente = null
        if (ok && ruta != null) {
            viewModel.onFotoCapturada(ruta)
        } else {
            ruta?.let { File(it).delete() } // cancelada: no dejar archivos vacíos
        }
    }

    fun lanzarCamara() {
        val archivo = CamaraHelper.crearArchivoFoto(context)
        rutaPendiente = archivo.absolutePath
        tomarFoto.launch(CamaraHelper.uriDe(context, archivo))
    }

    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) lanzarCamara()
        else viewModel.onMensajeRecurso("Permiso de cámara denegado. No se puede tomar la foto.")
    }

    // ---------- Ubicación ----------
    fun obtenerUbicacion() {
        viewModel.onUbicacionObteniendose()
        UbicacionHelper.obtenerUbicacionUnica(context) { lat, lon, error ->
            if (lat != null && lon != null) viewModel.onUbicacionObtenida(lat, lon)
            else viewModel.onUbicacionError(error ?: "No se pudo obtener la ubicación.")
        }
    }

    val permisoUbicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (permisos.values.any { it }) obtenerUbicacion()
        else viewModel.onMensajeRecurso("Permiso de ubicación denegado. Puede continuar sin ubicación.")
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Text("Recursos del dispositivo (opcional)", style = MaterialTheme.typography.titleMedium)

        // --- Foto de evidencia ---
        uiState.fotoPath?.let { ruta ->
            FotoPreview(
                ruta = ruta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) lanzarCamara() else permisoCamara.launch(Manifest.permission.CAMERA)
            }) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Text(if (uiState.fotoPath == null) "  Tomar foto" else "  Cambiar foto")
            }
            if (uiState.fotoPath != null) {
                TextButton(onClick = viewModel::onQuitarFoto) { Text("Quitar") }
            }
        }

        // --- Ubicación de referencia ---
        if (uiState.latitud != null && uiState.longitud != null) {
            Text(
                "Ubicación de referencia: ${uiState.latitud}, ${uiState.longitud}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                enabled = !uiState.obteniendoUbicacion,
                onClick = {
                    if (UbicacionHelper.tienePermiso(context)) obtenerUbicacion()
                    else permisoUbicacion.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            ) {
                if (uiState.obteniendoUbicacion) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text("  Obteniendo...")
                } else {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(if (uiState.latitud == null) "  Obtener ubicación" else "  Actualizar ubicación")
                }
            }
            if (uiState.latitud != null) {
                TextButton(onClick = viewModel::onQuitarUbicacion) { Text("Quitar") }
            }
        }
        Text(
            "Se guarda solo una referencia general (aprox. 1 km) al presionar el botón. No se registra su recorrido.",
            style = MaterialTheme.typography.bodySmall
        )

        uiState.mensajeRecurso?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}