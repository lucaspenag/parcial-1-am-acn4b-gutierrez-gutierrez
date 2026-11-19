package com.example.bitacoragamer;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bitacoragamer.datos.Repositorio;
import com.example.bitacoragamer.modelo.Entrada;
import com.example.bitacoragamer.modelo.Estado;
import com.example.bitacoragamer.modelo.JuegoGuardado;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class EstadisticasActivity extends AppCompatActivity {

    private List<JuegoGuardado> juegos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });
        Navegacion.configurar(this, Navegacion.ESTADISTICAS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }


    private void cargarDatos() {
        Repositorio.obtenerJuegos(new Repositorio.Resultado<List<JuegoGuardado>>() {
            @Override
            public void alTerminar(List<JuegoGuardado> datos) {
                juegos = datos;
                Repositorio.obtenerEntradas(new Repositorio.Resultado<List<Entrada>>() {
                    @Override
                    public void alTerminar(List<Entrada> entradas) {

                        if (isDestroyed()) {
                            return;
                        }
                        mostrarEstadisticas(entradas);
                    }

                    @Override
                    public void alFallar(Exception error) {
                        mostrarError();
                    }
                });
            }

            @Override
            public void alFallar(Exception error) {
                mostrarError();
            }
        });
    }

    private void mostrarError() {
        Toast.makeText(this, R.string.error_cargar, Toast.LENGTH_SHORT).show();
    }

    private void mostrarEstadisticas(List<Entrada> entradas) {
        int jugando = 0;
        int completados = 0;
        int pausados = 0;
        for (JuegoGuardado juego : juegos) {
            if (Estado.COMPLETADO.equals(juego.estado)) {
                completados++;
            } else if (Estado.PAUSADO.equals(juego.estado)) {
                pausados++;
            } else {
                jugando++;
            }
        }

        TextView tvJuegos = findViewById(R.id.tv_juegos);
        TextView tvCompletados = findViewById(R.id.tv_completados);
        TextView tvEntradas = findViewById(R.id.tv_entradas);
        TextView tvRacha = findViewById(R.id.tv_racha);
        tvJuegos.setText(String.valueOf(juegos.size()));
        tvCompletados.setText(String.valueOf(completados));
        tvEntradas.setText(String.valueOf(entradas.size()));
        int racha = calcularRacha(entradas);
        tvRacha.setText(racha == 1 ? getString(R.string.racha_un_dia) : getString(R.string.racha_dias, racha));

        LinearLayout contenedorEstados = findViewById(R.id.contenedor_estados);
        contenedorEstados.removeAllViews();
        contenedorEstados.addView(crearFilaEstado(Estado.JUGANDO, jugando, juegos.size()));
        contenedorEstados.addView(crearFilaEstado(Estado.COMPLETADO, completados, juegos.size()));
        contenedorEstados.addView(crearFilaEstado(Estado.PAUSADO, pausados, juegos.size()));

        mostrarMasRegistrado(entradas);
    }


    private int calcularRacha(List<Entrada> entradas) {
        SimpleDateFormat formatoDia = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Set<String> diasConEntradas = new HashSet<>();
        for (Entrada entrada : entradas) {
            diasConEntradas.add(formatoDia.format(entrada.fecha));
        }

        Calendar dia = Calendar.getInstance();
        if (!diasConEntradas.contains(formatoDia.format(dia.getTime()))) {
            dia.add(Calendar.DAY_OF_YEAR, -1);
        }
        int racha = 0;
        while (diasConEntradas.contains(formatoDia.format(dia.getTime()))) {
            racha++;
            dia.add(Calendar.DAY_OF_YEAR, -1);
        }
        return racha;
    }


    private View crearFilaEstado(String estado, int cantidad, int total) {
        int espacio = getResources().getDimensionPixelSize(R.dimen.espacio_medio);
        int color = getColor(Estado.color(estado));

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(0, espacio, 0, espacio);

        TextView tvEstado = new TextView(this);
        tvEstado.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.ancho_etiqueta_estado),
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tvEstado.setText(Estado.texto(estado));
        tvEstado.setTextColor(color);
        tvEstado.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));

        LinearLayout barra = new LinearLayout(this);
        barra.setOrientation(LinearLayout.HORIZONTAL);
        barra.setBackgroundResource(R.drawable.fondo_barra);
        barra.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.superficie)));
        barra.setLayoutParams(new LinearLayout.LayoutParams(
                0, getResources().getDimensionPixelSize(R.dimen.alto_barra_estado), 1));

        barra.setWeightSum(total == 0 ? 1 : total);

        View parteLlena = new View(this);
        parteLlena.setBackgroundResource(R.drawable.fondo_barra);
        parteLlena.setBackgroundTintList(ColorStateList.valueOf(color));
        parteLlena.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, cantidad));
        barra.addView(parteLlena);

        TextView tvCantidad = new TextView(this);
        LinearLayout.LayoutParams paramsCantidad = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsCantidad.setMarginStart(espacio);
        tvCantidad.setLayoutParams(paramsCantidad);
        tvCantidad.setText(String.valueOf(cantidad));
        tvCantidad.setTextColor(getColor(R.color.texto_primario));
        tvCantidad.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));

        fila.addView(tvEstado);
        fila.addView(barra);
        fila.addView(tvCantidad);
        return fila;
    }


    private void mostrarMasRegistrado(List<Entrada> entradas) {
        LinearLayout tarjeta = findViewById(R.id.tarjeta_mas_registrado);
        TextView tvSinDatos = findViewById(R.id.tv_sin_mas_registrado);
        if (entradas.isEmpty()) {
            tarjeta.setVisibility(View.GONE);
            tvSinDatos.setVisibility(View.VISIBLE);
            return;
        }

        Map<Integer, Integer> entradasPorJuego = new HashMap<>();
        Entrada ganadora = null;
        int maximo = 0;
        for (Entrada entrada : entradas) {
            Integer anterior = entradasPorJuego.get(entrada.juegoId);
            int cantidad = (anterior == null ? 0 : anterior) + 1;
            entradasPorJuego.put(entrada.juegoId, cantidad);
            if (cantidad > maximo) {
                maximo = cantidad;
                ganadora = entrada;
            }
        }

        TextView tvNombre = findViewById(R.id.tv_mas_registrado_nombre);
        TextView tvCantidad = findViewById(R.id.tv_mas_registrado_cantidad);
        ImageView imgPortada = findViewById(R.id.img_mas_registrado);
        tvNombre.setText(ganadora.juegoNombre);
        tvCantidad.setText(maximo == 1 ? getString(R.string.una_entrada) : getString(R.string.cantidad_entradas, maximo));
        Glide.with(this)
                .load(ganadora.juegoPortada)
                .placeholder(R.drawable.portada_placeholder)
                .error(R.drawable.portada_placeholder)
                .into(imgPortada);

        tvSinDatos.setVisibility(View.GONE);
        tarjeta.setVisibility(View.VISIBLE);
    }
}
