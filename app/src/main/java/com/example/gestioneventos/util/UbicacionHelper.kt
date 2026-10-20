package com.example.gestioneventos.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal

/**
 * Obtiene UNA sola referencia geográfica (cuando el usuario presiona el botón).
 * No hay seguimiento permanente ni se guardan recorridos.
 * Se redondea a 2 decimales (~1 km) para guardar solo una ubicación general.
 */
object UbicacionHelper {

    fun tienePermiso(context: Context): Boolean =
        concedido(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                concedido(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    fun obtenerUbicacionUnica(
        context: Context,
        onResultado: (latitud: Double?, longitud: Double?, error: String?) -> Unit
    ) {
        val fina = concedido(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximada = concedido(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!fina && !aproximada) {
            onResultado(null, null, "Permiso de ubicación denegado.")
            return
        }

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val proveedor = when {
            fina && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        }
        if (proveedor == null) {
            onResultado(null, null, "Active la ubicación del dispositivo e intente de nuevo.")
            return
        }

        try {
            LocationManagerCompat.getCurrentLocation(
                manager,
                proveedor,
                CancellationSignal(),
                ContextCompat.getMainExecutor(context)
            ) { ubicacion: Location? ->
                val resultado = ubicacion ?: ultimaConocida(manager, proveedor)
                if (resultado != null) {
                    onResultado(redondear(resultado.latitude), redondear(resultado.longitude), null)
                } else {
                    onResultado(null, null, "No se pudo obtener la ubicación. Intente nuevamente.")
                }
            }
        } catch (e: SecurityException) {
            onResultado(null, null, "Permiso de ubicación denegado.")
        }
    }

    private fun ultimaConocida(manager: LocationManager, proveedor: String): Location? =
        try {
            manager.getLastKnownLocation(proveedor)
        } catch (e: SecurityException) {
            null
        }

    private fun redondear(valor: Double): Double = Math.round(valor * 100.0) / 100.0

    private fun concedido(context: Context, permiso: String) =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
}