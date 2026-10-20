package com.example.gestioneventos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestioneventos.data.model.Usuario
import com.example.gestioneventos.ui.home.directivo.HomeDirectivoScreen
import com.example.gestioneventos.ui.home.ejecutivo.HomeEjecutivoScreen
import com.example.gestioneventos.ui.home.marketing.ScreenHomeMarketing
import com.example.gestioneventos.ui.home.supervisor.ScreenHomeSupervisor
import com.example.gestioneventos.ui.login.LoginScreenView
import com.example.gestioneventos.ui.oportunidad.ScreenFormularioOportunidad
import com.example.gestioneventos.ui.oportunidad.ScreenListaOportunidades

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        composable("login") {
            LoginScreenView(navController = navController)
        }

        composable("home_ejecutivo") {
            HomeEjecutivoScreen(
                navController = navController,
                nombreEjecutivo = "Juan Ejecutivo",
                onCrearOportunidadClick = { navController.navigate("crear_oportunidad") },
                onVerMisOportunidadesClick = { navController.navigate("lista_oportunidades/EJECUTIVO") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_ejecutivo") { inclusive = true } }
                }
            )
        }

        composable("home_supervisor") {
            ScreenHomeSupervisor(
                onVerTodasClick = { navController.navigate("lista_oportunidades/SUPERVISOR") },
                onVerAtrasadasClick = { navController.navigate("lista_oportunidades/SUPERVISOR") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_supervisor") { inclusive = true } }
                }
            )
        }

        composable("home_marketing") {
            ScreenHomeMarketing(
                onVerEventosClick = { navController.navigate("lista_oportunidades/MARKETING") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_marketing") { inclusive = true } }
                }
            )
        }

        composable("home_directivo") {
            HomeDirectivoScreen(
                navController = navController,
                onVerOportunidadesClick = { navController.navigate("lista_oportunidades/DIRECTIVO") }
            )
        }

        composable("crear_oportunidad") {
            ScreenFormularioOportunidad(
                navController = navController,
                nombreEjecutivo = "Juan Ejecutivo"
            )
        }

        composable(
            route = "lista_oportunidades/{cargo}",
            arguments = listOf(navArgument("cargo") { type = NavType.StringType })
        ) { backStackEntry ->
            val cargoRecibido = backStackEntry.arguments?.getString("cargo") ?: "SUPERVISOR"

            val usuarioFalso = Usuario(
                id = "0",
                nombre = "Juan Ejecutivo",
                cargo = cargoRecibido
            )

            ScreenListaOportunidades(
                navController = navController,
                usuario = usuarioFalso
            )
        }
    }
}