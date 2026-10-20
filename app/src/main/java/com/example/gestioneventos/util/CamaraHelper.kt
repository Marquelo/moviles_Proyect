package com.example.gestioneventos.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Utilidades para la captura de fotos de evidencia (no tiene lógica de negocio). */
object CamaraHelper {

    /** Crea un archivo vacío en la carpeta privada de la app (filesDir/fotos). */
    fun crearArchivoFoto(context: Context): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        return File.createTempFile("oportunidad_", ".jpg", carpeta)
    }

    /** URI segura (FileProvider) que se le entrega a la app de cámara. */
    fun uriDe(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Carga la imagen reducida (para no gastar memoria) y corrige la rotación EXIF. */
    fun cargarBitmap(ruta: String, maxLado: Int): Bitmap? {
        val archivo = File(ruta)
        if (!archivo.exists() || archivo.length() == 0L) return null

        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, limites)

        var muestra = 1
        while (maxOf(limites.outWidth, limites.outHeight) / muestra > maxLado) muestra *= 2

        val bitmap = BitmapFactory.decodeFile(
            ruta,
            BitmapFactory.Options().apply { inSampleSize = muestra }
        ) ?: return null

        val grados = try {
            when (ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: Exception) {
            0f
        }
        if (grados == 0f) return bitmap

        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }
}