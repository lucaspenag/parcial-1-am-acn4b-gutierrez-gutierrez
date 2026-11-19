package com.example.bitacoragamer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.core.widget.TextViewCompat;

public class Navegacion {

    public static final int BIBLIOTECA = 0;
    public static final int DIARIO = 1;
    public static final int ESTADISTICAS = 2;

    public static void configurar(final Activity actividad, final int seccionActual) {
        TextView navBiblioteca = actividad.findViewById(R.id.nav_biblioteca);
        TextView navDiario = actividad.findViewById(R.id.nav_diario);
        TextView navEstadisticas = actividad.findViewById(R.id.nav_estadisticas);

        marcar(actividad, navBiblioteca, seccionActual == BIBLIOTECA);
        marcar(actividad, navDiario, seccionActual == DIARIO);
        marcar(actividad, navEstadisticas, seccionActual == ESTADISTICAS);

        navBiblioteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seccionActual != BIBLIOTECA) {
                    actividad.finish();
                }
            }
        });
        navDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrir(actividad, seccionActual, DIARIO, DiarioActivity.class);
            }
        });
        navEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrir(actividad, seccionActual, ESTADISTICAS, EstadisticasActivity.class);
            }
        });
    }

    private static void abrir(Activity actividad, int seccionActual, int seccionDestino, Class<?> pantalla) {
        if (seccionActual == seccionDestino) {
            return;
        }
        actividad.startActivity(new Intent(actividad, pantalla));
        if (seccionActual != BIBLIOTECA) {
            actividad.finish();
        }
    }


    private static void marcar(Activity actividad, TextView item, boolean actual) {
        int color = actividad.getColor(actual ? R.color.acento : R.color.texto_secundario);
        item.setTextColor(color);
        TextViewCompat.setCompoundDrawableTintList(item, ColorStateList.valueOf(color));
    }
}
