package com.example.bitacoragamer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bitacoragamer.datos.Repositorio;
import com.example.bitacoragamer.modelo.Entrada;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DiarioActivity extends AppCompatActivity {

    private LinearLayout contenedorDiario;
    private TextView tvVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        contenedorDiario = findViewById(R.id.contenedor_diario);
        tvVacio = findViewById(R.id.tv_vacio);
        Navegacion.configurar(this, Navegacion.DIARIO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Repositorio.obtenerEntradas(new Repositorio.Resultado<List<Entrada>>() {
            @Override
            public void alTerminar(List<Entrada> entradas) {
                if (isDestroyed()) {
                    return;
                }
                mostrarEntradas(entradas);
            }

            @Override
            public void alFallar(Exception error) {
                tvVacio.setText(R.string.error_cargar);
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }

    private void mostrarEntradas(List<Entrada> entradas) {
        contenedorDiario.removeAllViews();
        if (entradas.isEmpty()) {
            tvVacio.setText(R.string.diario_vacio);
            tvVacio.setVisibility(View.VISIBLE);
            return;
        }
        tvVacio.setVisibility(View.GONE);

        String diaAnterior = null;
        for (Entrada entrada : entradas) {
            String dia = nombreDelDia(entrada.fecha);
            if (!dia.equals(diaAnterior)) {
                contenedorDiario.addView(crearTituloDia(dia));
                diaAnterior = dia;
            }
            contenedorDiario.addView(crearTarjeta(entrada));
        }
    }

    private String nombreDelDia(Date fecha) {
        Calendar dia = Calendar.getInstance();
        dia.setTime(fecha);
        Calendar hoy = Calendar.getInstance();
        if (mismoDia(dia, hoy)) {
            return getString(R.string.hoy);
        }
        hoy.add(Calendar.DAY_OF_YEAR, -1);
        if (mismoDia(dia, hoy)) {
            return getString(R.string.ayer);
        }
        return new SimpleDateFormat(getString(R.string.formato_fecha), Locale.getDefault()).format(fecha);
    }

    private boolean mismoDia(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private TextView crearTituloDia(String dia) {
        TextView titulo = new TextView(this);
        titulo.setText(dia);
        titulo.setTextColor(getColor(R.color.acento));
        titulo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));
        titulo.setTypeface(null, Typeface.BOLD);
        int espacio = getResources().getDimensionPixelSize(R.dimen.espacio_medio);
        titulo.setPadding(0, espacio, 0, espacio);
        return titulo;
    }

    private View crearTarjeta(final Entrada entrada) {
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_entrada);

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setPadding(padding, padding, padding, padding);
        tarjeta.setBackgroundResource(R.drawable.fondo_entrada);
        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTarjeta.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.espacio_medio));
        tarjeta.setLayoutParams(paramsTarjeta);

        ImageView portada = new ImageView(this);
        portada.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.portada_mini_ancho),
                getResources().getDimensionPixelSize(R.dimen.portada_mini_alto)));
        portada.setScaleType(ImageView.ScaleType.CENTER_CROP);
        portada.setContentDescription(getString(R.string.desc_portada_juego));
        Glide.with(this)
                .load(entrada.juegoPortada)
                .placeholder(R.drawable.portada_placeholder)
                .error(R.drawable.portada_placeholder)
                .into(portada);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paramsTextos = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsTextos.setMarginStart(getResources().getDimensionPixelSize(R.dimen.espacio_grande));
        textos.setLayoutParams(paramsTextos);

        String hora = new SimpleDateFormat(getString(R.string.formato_hora), Locale.getDefault()).format(entrada.fecha);
        TextView tvJuego = new TextView(this);
        tvJuego.setText(getString(R.string.dos_datos, entrada.juegoNombre, hora));
        tvJuego.setTextColor(getColor(R.color.texto_secundario));
        tvJuego.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_chico));
        tvJuego.setTypeface(null, Typeface.BOLD);

        TextView tvTexto = new TextView(this);
        tvTexto.setText(entrada.texto);
        tvTexto.setTextColor(getColor(R.color.texto_primario));
        tvTexto.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));

        textos.addView(tvJuego);
        textos.addView(tvTexto);
        tarjeta.addView(portada);
        tarjeta.addView(textos);

        tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DetalleActivity.crearIntent(
                        DiarioActivity.this, entrada.juegoId, entrada.juegoNombre, entrada.juegoPortada));
            }
        });
        return tarjeta;
    }
}