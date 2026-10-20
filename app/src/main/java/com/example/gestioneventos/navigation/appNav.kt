package com.example.gestioneventos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestioneventos.data.model.Usuario
import com.example.gestioneventos.ui.login.LoginScreenView
import com.example.gestioneventos.ui.home.ejecutivo.HomeEjecutivoScreen
import com.example.gestioneventos.ui.home.supervisor.ScreenHomeSupervisor
import com.example.gestioneventos.ui.home.marketing.ScreenHomeMarketing
import com.example.gestioneventos.ui.home.directivo.HomeDirectivoScreen
import com.example.gestioneventos.ui.oportunidad.ScreenFormularioOportunidad
import com.example.gestioneventos.ui.oportunidad.ScreenListaOportunidades

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // --- USUARIOS DE PRUEBA PARA LA LISTA ---
    val userEjecutivo = Usuario(id = "1", nombre = "Juan Ejecutivo", cargo = "EJECUTIVO")
    val userSupervisor = Usuario(id = "2", nombre = "Ana Supervisora", cargo = "SUPERVISOR")
    val userMarketing = Usuario(id = "3", nombre = "Carlos Marketing", cargo = "MARKETING")
    val userDirectivo = Usuario(id = "4", nombre = "Director General", cargo = "DIRECTIVO")

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        // --- 1. LOGIN ---
        composable("login") {
            LoginScreenView(navController = navController)
        }

        // --- 2. HOME EJECUTIVO ---
        composable("home_ejecutivo") {
            HomeEjecutivoScreen(
                navController = navController,
                nombreEjecutivo = "Juan Ejecutivo",
                onCrearOportunidadClick = { navController.navigate("crear_oportunidad") },
                onVerMisOportunidadesClick = { navController.navigate("lista_oportunidades") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_ejecutivo") { inclusive = true } }
                }
            )
        }

        // --- 3. HOME SUPERVISOR ---
        composable("home_supervisor") {
            ScreenHomeSupervisor(
                onVerTodasClick = { navController.navigate("lista_oportunidades") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_supervisor") { inclusive = true } }
                }
            )
        }

        // --- 4. HOME MARKETING ---
        composable("home_marketing") {
            ScreenHomeMarketing(
                onVerEventosClick = { navController.navigate("lista_oportunidades") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_marketing") { inclusive = true } }
                }
            )
        }

        // --- 5. HOME DIRECTIVO ---
        composable("home_directivo") {
            HomeDirectivoScreen(
                navController = navController,
                onVerOportunidadesClick = { navController.navigate("lista_oportunidades") }
            )
        }

        // --- 6. FORMULARIO ---
        composable("crear_oportunidad") {
            ScreenFormularioOportunidad(
                navController = navController,
                nombreEjecutivo = "Juan Ejecutivo"
            )
        }

        // --- 7. LISTA DE OPORTUNIDADES ---
        composable("lista_oportunidades") {
            // Como la lista necesita un usuario para saber qué mostrar,
            // le pasamos uno de los de prueba (por ahora verá todo como Supervisor).
            ScreenListaOportunidades(
                navController = navController,
                usuario = userSupervisor
            )
        }
    }
}