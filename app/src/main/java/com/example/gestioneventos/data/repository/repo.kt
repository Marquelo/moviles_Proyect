package com.example.gestioneventos.data.repository



class repo(
private val validCredential: Credential = Credential.Admin
){
    fun login(username: String, password:String): Boolean{
        return username == validCredential.user && password==validCredential.passwd

    }

}