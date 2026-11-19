package com.example.bitacoragamer.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RawgClient {

    private static final String URL_BASE = "https://api.rawg.io/api/";

    private static RawgService servicio;

    public static RawgService getServicio() {
        if (servicio == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            servicio = retrofit.create(RawgService.class);
        }
        return servicio;
    }
}
