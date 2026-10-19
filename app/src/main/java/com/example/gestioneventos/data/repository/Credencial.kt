package com.example.gestioneventos.data.repository

data class Credential (val user:String, val passwd:String){
    companion object{
        val Admin = Credential(user="admin", passwd="123")
    }

}