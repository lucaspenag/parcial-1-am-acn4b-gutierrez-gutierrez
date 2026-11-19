package com.example.bitacoragamer.api;

import com.example.bitacoragamer.modelo.JuegoRawg;
import com.example.bitacoragamer.modelo.RespuestaBusqueda;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RawgService {


    @GET("games")
    Call<RespuestaBusqueda> buscarJuegos(
            @Query("key") String apiKey,
            @Query("search") String texto,
            @Query("page_size") int cantidad);

    @GET("games/{id}")
    Call<JuegoRawg> obtenerJuego(
            @Path("id") int id,
            @Query("key") String apiKey);
}
