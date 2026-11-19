package com.example.bitacoragamer.modelo;

import java.util.Date;

public class JuegoGuardado {

    public int id;
    public String nombre;
    public String portada;
    public String estado;
    public Date agregado;

    public JuegoGuardado(int id, String nombre, String portada, String estado, Date agregado) {
        this.id = id;
        this.nombre = nombre;
        this.portada = portada;
        this.estado = estado;
        this.agregado = agregado;
    }
}
