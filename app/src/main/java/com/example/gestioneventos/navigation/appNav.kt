package com.example.gestioneventos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestioneventos.data.local.AppDatabase
import com.example.gestioneventos.data.model.Usuario
import com.example.gestioneventos.data.repository.OportunidadRepository
import com.example.gestioneventos.ui.home.directivo.HomeDirectivoScreen
import com.example.gestioneventos.ui.home.ejecutivo.HomeEjecutivoScreen
import com.example.gestioneventos.ui.home.marketing.ScreenHomeMarketing
import com.example.gestioneventos.ui.home.supervisor.ScreenHomeSupervisor
import com.example.gestioneventos.ui.login.LoginScreenView
import com.example.gestioneventos.ui.oportunidad.OportunidadViewModel
import com.example.gestioneventos.ui.oportunidad.ScreenFormularioOportunidad
import com.example.gestioneventos.ui.oportunidad.ScreenListaOportunidades

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Room -> DAO -> Repository -> ViewModel (un solo ViewModel compartido por lista y formulario)
    val context = LocalContext.current.applicationContext
    val repository = remember {
        OportunidadRepository(AppDatabase.getDatabase(context).oportunidadDao())
    }
    val oportunidadViewModel: OportunidadViewModel = viewModel(
        factory = OportunidadViewModel.Factory(repository)
    )

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
                viewModel = oportunidadViewModel,
                onCrearOportunidadClick = { navController.navigate("oportunidad_form/0") },
                onVerMisOportunidadesClick = { navController.navigate("lista_oportunidades/EJECUTIVO") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_ejecutivo") { inclusive = true } }
                }
            )
        }

        composable("home_supervisor") {
            ScreenHomeSupervisor(
                viewModel = oportunidadViewModel,
                onVerTodasClick = { navController.navigate("lista_oportunidades/SUPERVISOR") },
                onVerAtrasadasClick = { navController.navigate("lista_oportunidades/SUPERVISOR") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_supervisor") { inclusive = true } }
                }
            )
        }

        composable("home_marketing") {
            ScreenHomeMarketing(
                viewModel = oportunidadViewModel,
                onVerEventosClick = { navController.navigate("lista_oportunidades/MARKETING") },
                onCerrarSesionClick = {
                    navController.navigate("login") { popUpTo("home_marketing") { inclusive = true } }
                }
            )
        }

        composable("home_directivo") {
            HomeDirectivoScreen(
                navController = navController,
                viewModel = oportunidadViewModel,
                onVerOportunidadesClick = { navController.navigate("lista_oportunidades/DIRECTIVO") }
            )
        }

        // id = 0 -> crear; id > 0 -> editar
        composable(
            route = "oportunidad_form/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ScreenFormularioOportunidad(
                navController = navController,
                oportunidadId = id,
                viewModel = oportunidadViewModel
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
                usuario = usuarioFalso,
                viewModel = oportunidadViewModel
            )
        }
    }
}