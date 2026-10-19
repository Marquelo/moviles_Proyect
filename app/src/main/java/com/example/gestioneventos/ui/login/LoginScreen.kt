package com.example.gestioneventos.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gestioneventos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenView(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    val state = vm.uiState
    var showPass by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Inicio De Sesion")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Campo Usuario
            OutlinedTextField(
                value = state.user,
                onValueChange = vm::onUserChange,
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            // Campo Contraseña
            OutlinedTextField(
                value = state.passwd,
                onValueChange = vm::onPasswd,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass = !showPass }) {
                        Text(if (showPass) "Ocultar" else "Ver")
                    }
                },
                modifier = Modifier.fillMaxWidth(0.95f)
            )

            // Mensaje de error
            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Iniciar sesión
            Button(
                onClick = {
                    vm.ok { user ->
                        // 🔹 TODO: aquí irá la navegación cuando tengas el menú
                        // navController.navigate("DrawerMenu/$user") { ... }
                    }
                },
                enabled = !state.isLoadin,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(if (state.isLoadin) "Validando..." else "Iniciar Sesión")
            }
        }
    }
}