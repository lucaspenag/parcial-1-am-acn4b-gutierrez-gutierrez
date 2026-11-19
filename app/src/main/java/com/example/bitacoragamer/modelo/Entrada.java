package com.example.bitacoragamer.modelo;

import java.util.Date;

public class Entrada {

    public String idDocumento;
    public int juegoId;
    public String juegoNombre;
    public String juegoPortada;
    public String texto;
    public Date fecha;

    public Entrada(String idDocumento, int juegoId, String juegoNombre, String juegoPortada,
                   String texto, Date fecha) {
        this.idDocumento = idDocumento;
        this.juegoId = juegoId;
        this.juegoNombre = juegoNombre;
        this.juegoPortada = juegoPortada;
        this.texto = texto;
        this.fecha = fecha;
    }
}
