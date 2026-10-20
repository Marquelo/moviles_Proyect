package com.example.gestioneventos.data.repository

class repo {
    fun login(username: String, password: String): Boolean {
        return Credential.todosLosUsuarios.any {
            it.user == username && it.passwd == password
        }
    }
}