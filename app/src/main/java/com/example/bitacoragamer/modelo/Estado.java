package com.example.bitacoragamer.modelo;

import com.example.bitacoragamer.R;


public class Estado {

    public static final String JUGANDO = "jugando";
    public static final String COMPLETADO = "completado";
    public static final String PAUSADO = "pausado";


    public static int texto(String estado) {
        if (COMPLETADO.equals(estado)) {
            return R.string.estado_completado;
        }
        if (PAUSADO.equals(estado)) {
            return R.string.estado_pausado;
        }
        return R.string.estado_jugando;
    }


    public static int color(String estado) {
        if (COMPLETADO.equals(estado)) {
            return R.color.estado_completado;
        }
        if (PAUSADO.equals(estado)) {
            return R.color.estado_pausado;
        }
        return R.color.estado_jugando;
    }
}
