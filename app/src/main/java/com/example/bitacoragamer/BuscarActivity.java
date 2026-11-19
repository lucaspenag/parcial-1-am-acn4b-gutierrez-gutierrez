package com.example.bitacoragamer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bitacoragamer.api.RawgClient;
import com.example.bitacoragamer.datos.Repositorio;
import com.example.bitacoragamer.modelo.Estado;
import com.example.bitacoragamer.modelo.JuegoGuardado;
import com.example.bitacoragamer.modelo.JuegoRawg;
import com.example.bitacoragamer.modelo.RespuestaBusqueda;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BuscarActivity extends AppCompatActivity {

    private static final int ERROR_KEY_INVALIDA = 401;

    private EditText etBusqueda;
    private ProgressBar progreso;
    private TextView tvMensaje;
    private LinearLayout contenedorResultados;


    private final Set<Integer> idsEnBiblioteca = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        etBusqueda = findViewById(R.id.et_busqueda);
        progreso = findViewById(R.id.progreso);
        tvMensaje = findViewById(R.id.tv_mensaje);
        contenedorResultados = findViewById(R.id.contenedor_resultados);


        findViewById(R.id.tv_volver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_buscar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });


        etBusqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int accion, KeyEvent evento) {
                if (accion == EditorInfo.IME_ACTION_SEARCH) {
                    buscar();
                    return true;
                }
                return false;
            }
        });

        cargarIdsDeLaBiblioteca();
    }

    private void buscar() {
        String texto = etBusqueda.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, R.string.error_buscar_vacio, Toast.LENGTH_SHORT).show();
            return;
        }
        ocultarTeclado();
        contenedorResultados.removeAllViews();
        tvMensaje.setVisibility(View.GONE);
        progreso.setVisibility(View.VISIBLE);


        RawgClient.getServicio()
                .buscarJuegos(getString(R.string.rawg_api_key), texto,
                        getResources().getInteger(R.integer.resultados_busqueda))
                .enqueue(new Callback<RespuestaBusqueda>() {
                    @Override
                    public void onResponse(Call<RespuestaBusqueda> call, Response<RespuestaBusqueda> response) {

                        if (isDestroyed()) {
                            return;
                        }
                        progreso.setVisibility(View.GONE);
                        if (response.code() == ERROR_KEY_INVALIDA) {
                            mostrarMensaje(R.string.error_api_key);
                        } else if (!response.isSuccessful() || response.body() == null) {
                            mostrarMensaje(R.string.error_api);
                        } else if (response.body().resultados == null || response.body().resultados.isEmpty()) {
                            mostrarMensaje(R.string.sin_resultados);
                        } else {
                            for (JuegoRawg juego : response.body().resultados) {
                                contenedorResultados.addView(crearTarjetaResultado(juego));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaBusqueda> call, Throwable t) {

                        progreso.setVisibility(View.GONE);
                        mostrarMensaje(R.string.error_api);
                    }
                });
    }

    private void mostrarMensaje(int idTexto) {
        tvMensaje.setText(idTexto);
        tvMensaje.setVisibility(View.VISIBLE);
    }


    private View crearTarjetaResultado(final JuegoRawg juego) {
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
                .load(juego.urlPortada)
                .placeholder(R.drawable.portada_placeholder)
                .error(R.drawable.portada_placeholder)
                .into(portada);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams paramsTextos = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsTextos.setMarginStart(getResources().getDimensionPixelSize(R.dimen.espacio_grande));
        paramsTextos.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.espacio_medio));
        textos.setLayoutParams(paramsTextos);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(juego.nombre);
        tvNombre.setTextColor(getColor(R.color.texto_primario));
        tvNombre.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));
        tvNombre.setTypeface(null, Typeface.BOLD);

        String anio = juego.getAnio() != null ? juego.getAnio() : getString(R.string.sin_fecha);
        String plataformas = juego.getPlataformas(3) != null ? juego.getPlataformas(3) : getString(R.string.sin_datos);
        TextView tvInfo = new TextView(this);
        tvInfo.setText(getString(R.string.dos_datos, anio, plataformas));
        tvInfo.setTextColor(getColor(R.color.texto_secundario));
        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_chico));

        textos.addView(tvNombre);
        textos.addView(tvInfo);
        tarjeta.addView(portada);
        tarjeta.addView(textos);
        tarjeta.addView(crearBotonResultado(juego));
        return tarjeta;
    }


    private Button crearBotonResultado(final JuegoRawg juego) {
        final Button boton = new Button(this);
        boton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_boton));
        if (idsEnBiblioteca.contains(juego.id)) {
            marcarComoAgregado(boton, juego);
        } else {
            boton.setText(R.string.agregar_juego);
            boton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregarALaBiblioteca(juego, boton);
                }
            });
        }
        return boton;
    }

    private void agregarALaBiblioteca(final JuegoRawg juego, final Button boton) {
        boton.setEnabled(false);
        JuegoGuardado nuevo = new JuegoGuardado(juego.id, juego.nombre, juego.urlPortada, Estado.JUGANDO, new Date());
        Repositorio.agregarJuego(nuevo, new Repositorio.Resultado<Void>() {
            @Override
            public void alTerminar(Void datos) {
                idsEnBiblioteca.add(juego.id);
                Toast.makeText(BuscarActivity.this, getString(R.string.juego_agregado, juego.nombre),
                        Toast.LENGTH_SHORT).show();
                marcarComoAgregado(boton, juego);
            }

            @Override
            public void alFallar(Exception error) {
                boton.setEnabled(true);
                Toast.makeText(BuscarActivity.this, R.string.error_guardar, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void marcarComoAgregado(Button boton, final JuegoRawg juego) {
        boton.setText(R.string.ver_juego);

        boton.setEnabled(false);
    }


    private void cargarIdsDeLaBiblioteca() {
        Repositorio.obtenerJuegos(new Repositorio.Resultado<List<JuegoGuardado>>() {
            @Override
            public void alTerminar(List<JuegoGuardado> juegos) {
                for (JuegoGuardado juego : juegos) {
                    idsEnBiblioteca.add(juego.id);
                }
            }

            @Override
            public void alFallar(Exception error) {
            }
        });
    }

    private void ocultarTeclado() {
        InputMethodManager teclado = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(etBusqueda.getWindowToken(), 0);
    }
}
