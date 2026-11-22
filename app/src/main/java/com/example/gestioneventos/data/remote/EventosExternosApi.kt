package com.example.gestioneventos.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class PostExternoDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

data class EventoExterno(
    val idEvento: String,
    val nombreEvento: String,
    val fecha: String,
    val comunaUbicacion: String,
    val organizacion: String
)

interface EventosExternosApi {
    @GET("posts")
    suspend fun obtenerEventosPublicos(): List<PostExternoDto>

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun crear(): EventosExternosApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EventosExternosApi::class.java)
    }
}