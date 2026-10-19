package com.example.gestioneventos.data.repository

data class Credential(val user: String, val passwd: String) {
    companion object {
        val Ejecutivo = Credential(
            user = "ejecutivo@agencia.cl",
            passwd = "1234"
        )

        val Supervisor = Credential(
            user = "supervisor@agencia.cl",
            passwd = "1234"
        )

        val Marketing = Credential(
            user = "marketing@agencia.cl",
            passwd = "1234"
        )

        val Admin = Credential(
            user = "admin",
            passwd = "123"
        )
        val todosLosUsuarios = listOf(Ejecutivo, Supervisor, Marketing, Admin)
    }
}