package com.example.gestioneventos

import com.example.gestioneventos.ui.login.LoginViewModel
import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `login exitoso con credenciales validas de cada rol`() {
        val vm = LoginViewModel()
        var usuarioAutenticado = ""

        vm.onUserChange("ejecutivo@agencia.cl")
        vm.onPasswd("1234")
        vm.ok { usuarioAutenticado = it }

        assertEquals("ejecutivo@agencia.cl", usuarioAutenticado)
        assertNull(vm.uiState.error)
    }

    @Test
    fun `login fallido con contrasena incorrecta muestra mensaje de error`() {
        val vm = LoginViewModel()
        var llamado = false

        vm.onUserChange("supervisor@agencia.cl")
        vm.onPasswd("9999")
        vm.ok { llamado = true }

        assertFalse(llamado)
        assertEquals("Credenciales invalidas", vm.uiState.error)
    }
}