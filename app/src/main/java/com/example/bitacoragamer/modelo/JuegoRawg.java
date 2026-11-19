package com.example.bitacoragamer.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class JuegoRawg {

    public int id;

    @SerializedName("name")
    public String nombre;

    @SerializedName("released")
    public String fechaSalida;

    @SerializedName("background_image")
    public String urlPortada;

    @SerializedName("rating")
    public double puntaje;

    @SerializedName("description_raw")
    public String descripcion;

    @SerializedName("genres")
    public List<ItemConNombre> generos;

    @SerializedName("developers")
    public List<ItemConNombre> desarrolladores;

    @SerializedName("platforms")
    public List<PlataformaRawg> plataformas;

    public static class ItemConNombre {
        @SerializedName("name")
        public String nombre;
    }

    public static class PlataformaRawg {
        @SerializedName("platform")
        public ItemConNombre plataforma;
    }

    public String getAnio() {
        if (fechaSalida == null || fechaSalida.length() < 4) {
            return null;
        }
        return fechaSalida.substring(0, 4);
    }

    public String getGeneros() {
        return unirNombres(generos, 3);
    }

    public String getDesarrolladores() {
        return unirNombres(desarrolladores, 2);
    }

    public String getPlataformas(int maximo) {
        if (plataformas == null) {
            return null;
        }
        StringBuilder texto = new StringBuilder();
        int agregadas = 0;
        for (PlataformaRawg p : plataformas) {
            if (agregadas == maximo) {
                break;
            }
            if (p.plataforma != null && p.plataforma.nombre != null) {
                if (agregadas > 0) {
                    texto.append(" · ");
                }
                texto.append(p.plataforma.nombre);
                agregadas++;
            }
        }
        return agregadas == 0 ? null : texto.toString();
    }

    private static String unirNombres(List<ItemConNombre> lista, int maximo) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < lista.size() && i < maximo; i++) {
            if (i > 0) {
                texto.append(" · ");
            }
            texto.append(lista.get(i).nombre);
        }
        return texto.toString();
    }
}
