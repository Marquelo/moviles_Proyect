package com.example.gestioneventos.ui.login

data class LoginUiState(

    val user: String="",
    val passwd : String ="",
    val isLoadin : Boolean=false,
    val error: String?=null
)