package com.example.bitacoragamer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.example.bitacoragamer.modelo.Estado;
import com.example.bitacoragamer.modelo.JuegoGuardado;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class BibliotecaActivity extends AppCompatActivity {


    private String filtroActual = null;


    private List<JuegoGuardado> juegos = new ArrayList<>();

    private LinearLayout contenedorJuegos;
    private TextView tvVacio;
    private Button btnTodos;
    private Button btnJugando;
    private Button btnCompletados;
    private Button btnPausados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_biblioteca);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        contenedorJuegos = findViewById(R.id.contenedor_juegos);
        tvVacio = findViewById(R.id.tv_vacio);

        configurarFiltros();
        configurarSalir();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cargarJuegos();
    }

    private void cargarJuegos() {
        Repositorio.obtenerJuegos(new Repositorio.Resultado<List<JuegoGuardado>>() {
            @Override
            public void alTerminar(List<JuegoGuardado> datos) {

                if (isDestroyed()) {
                    return;
                }
                juegos = datos;
                mostrarJuegos();
            }

            @Override
            public void alFallar(Exception error) {
                tvVacio.setText(R.string.error_cargar);
                tvVacio.setVisibility(View.VISIBLE);
            }
        });
    }


    private void mostrarJuegos() {
        contenedorJuegos.removeAllViews();
        int mostrados = 0;
        for (JuegoGuardado juego : juegos) {
            if (filtroActual == null || filtroActual.equals(juego.estado)) {
                contenedorJuegos.addView(crearTarjetaJuego(juego));
                mostrados++;
            }
        }

        if (mostrados > 0) {
            tvVacio.setVisibility(View.GONE);
        } else {
            tvVacio.setText(juegos.isEmpty() ? R.string.biblioteca_vacia : R.string.biblioteca_vacia_filtro);
            tvVacio.setVisibility(View.VISIBLE);
        }
    }


    private View crearTarjetaJuego(final JuegoGuardado juego) {
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_entrada);

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setGravity(Gravity.CENTER_VERTICAL);
        tarjeta.setPadding(padding, padding, padding, padding);
        tarjeta.setBackgroundResource(R.drawable.fondo_entrada);
        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTarjeta.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.espacio_medio));
        tarjeta.setLayoutParams(paramsTarjeta);

        ImageView portada = new ImageView(this);
        portada.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.portada_chica_ancho),
                getResources().getDimensionPixelSize(R.dimen.portada_chica_alto)));
        portada.setScaleType(ImageView.ScaleType.CENTER_CROP);
        portada.setContentDescription(getString(R.string.desc_portada_juego));

        Glide.with(this)
                .load(juego.portada)
                .placeholder(R.drawable.portada_placeholder)
                .error(R.drawable.portada_placeholder)
                .into(portada);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paramsTextos = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsTextos.setMarginStart(getResources().getDimensionPixelSize(R.dimen.espacio_grande));
        textos.setLayoutParams(paramsTextos);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(juego.nombre);
        tvNombre.setTextColor(getColor(R.color.texto_primario));
        tvNombre.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_subtitulo));
        tvNombre.setTypeface(null, Typeface.BOLD);

        TextView tvEstado = new TextView(this);
        tvEstado.setText(Estado.texto(juego.estado));
        tvEstado.setTextColor(getColor(Estado.color(juego.estado)));
        tvEstado.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));

        textos.addView(tvNombre);
        textos.addView(tvEstado);
        tarjeta.addView(portada);
        tarjeta.addView(textos);
        return tarjeta;
    }


    private void configurarFiltros() {
        btnTodos = findViewById(R.id.btn_filtro_todos);
        btnJugando = findViewById(R.id.btn_filtro_jugando);
        btnCompletados = findViewById(R.id.btn_filtro_completados);
        btnPausados = findViewById(R.id.btn_filtro_pausados);

        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFiltro(null, btnTodos);
            }
        });
        btnJugando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFiltro(Estado.JUGANDO, btnJugando);
            }
        });
        btnCompletados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFiltro(Estado.COMPLETADO, btnCompletados);
            }
        });
        btnPausados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elegirFiltro(Estado.PAUSADO, btnPausados);
            }
        });
    }


    private void elegirFiltro(String filtro, Button botonElegido) {
        filtroActual = filtro;
        Button[] botones = {btnTodos, btnJugando, btnCompletados, btnPausados};
        for (Button boton : botones) {
            int fondo = boton == botonElegido ? R.color.primario : R.color.superficie;
            boton.setBackgroundTintList(ColorStateList.valueOf(getColor(fondo)));
        }
        mostrarJuegos();
    }


    private void configurarSalir() {
        findViewById(R.id.tv_salir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(BibliotecaActivity.this, R.string.sesion_cerrada, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BibliotecaActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
