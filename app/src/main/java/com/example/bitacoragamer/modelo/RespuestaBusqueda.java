package com.example.bitacoragamer.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class RespuestaBusqueda {

    @SerializedName("count")
    public int cantidad;

    @SerializedName("results")
    public List<JuegoRawg> resultados;
}
