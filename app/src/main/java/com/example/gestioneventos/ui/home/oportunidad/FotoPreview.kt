package com.example.gestioneventos.ui.oportunidad

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.gestioneventos.util.CamaraHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Muestra la foto guardada en [ruta]. La carga se hace fuera del hilo principal. */
@Composable
fun FotoPreview(ruta: String, modifier: Modifier = Modifier) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = ruta) {
        value = withContext(Dispatchers.IO) {
            CamaraHelper.cargarBitmap(ruta, 800)?.asImageBitmap()
        }
    }
    imagen?.let {
        Image(
            bitmap = it,
            contentDescription = "Foto de evidencia",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}