package com.example.gestioneventos.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestioneventos.data.repository.repo

class LoginViewModel(
    private val repo1 : repo= repo()
): ViewModel(){

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun  onUserChange(value:String){
        uiState=uiState.copy(user= value,error = null)
    }

    fun onPasswd(value: String){
        uiState=uiState.copy(passwd = value, error = null)
    }

    fun ok (onSucess: (String) -> Unit ){
        uiState=uiState.copy(isLoadin = true, error = null)

        val okey =repo1.login(uiState.user.trim(),uiState.passwd)

        uiState=uiState.copy(isLoadin = false )

        if(okey)onSucess(uiState.user.trim())
        else uiState=uiState.copy(error="Credenciales invalidas")


    }


}

