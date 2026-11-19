package com.example.bitacoragamer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.bitacoragamer.api.RawgClient;
import com.example.bitacoragamer.modelo.Estado;
import com.example.bitacoragamer.modelo.JuegoRawg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleActivity extends AppCompatActivity {

    // Claves de los extras que recibe esta pantalla
    public static final String EXTRA_ID = "com.example.bitacoragamer.extra.ID";
    public static final String EXTRA_NOMBRE = "com.example.bitacoragamer.extra.NOMBRE";
    public static final String EXTRA_PORTADA = "com.example.bitacoragamer.extra.PORTADA";

    public static Intent crearIntent(Context contexto, int id, String nombre, String portada) {
        Bundle datos = new Bundle();
        datos.putInt(EXTRA_ID, id);
        datos.putString(EXTRA_NOMBRE, nombre);
        datos.putString(EXTRA_PORTADA, portada);

        Intent intent = new Intent(contexto, DetalleActivity.class);
        intent.putExtras(datos);
        return intent;
    }

    private int idJuego;
    private String nombreJuego;
    private String portadaJuego;

    private LinearLayout contenedorEntradas;
    private EditText etEntrada;

    private TextView tvTituloBitacora;
    private TextView tvVacio;
    private int cantidadEntradas = 0;

    private TextView tvEstado;
    private String estadoActual = Estado.JUGANDO;

    private TextView tvSinopsis;
    private TextView tvVerMas;
    private boolean sinopsisExpandida = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_ID)) {
            finish();
            return;
        }
        idJuego = extras.getInt(EXTRA_ID);
        nombreJuego = extras.getString(EXTRA_NOMBRE);
        portadaJuego = extras.getString(EXTRA_PORTADA);

        contenedorEntradas = findViewById(R.id.contenedor_entradas);
        etEntrada = findViewById(R.id.et_entrada);
        tvTituloBitacora = findViewById(R.id.tv_titulo_bitacora);
        tvVacio = findViewById(R.id.tv_vacio);
        tvEstado = findViewById(R.id.tv_estado);

        TextView tvTitulo = findViewById(R.id.tv_titulo_juego);
        tvTitulo.setText(nombreJuego);
        mostrarPortada(portadaJuego);
        mostrarEstado();
        actualizarContador();

        findViewById(R.id.tv_volver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        configurarBotonAgregar();
        configurarBotonesEstado();
        configurarSinopsis();

        cargarDetallesDeRawg();
    }

    private void mostrarPortada(String url) {
        ImageView imgPortada = findViewById(R.id.img_portada);
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.portada_placeholder)
                .error(R.drawable.portada_placeholder)
                .into(imgPortada);
    }

    private void cargarDetallesDeRawg() {
        RawgClient.getServicio()
                .obtenerJuego(idJuego, getString(R.string.rawg_api_key))
                .enqueue(new Callback<JuegoRawg>() {
                    @Override
                    public void onResponse(Call<JuegoRawg> call, Response<JuegoRawg> response) {
                        if (isDestroyed()) {
                            return;
                        }
                        if (response.isSuccessful() && response.body() != null) {
                            mostrarDetalles(response.body());
                        } else {
                            mostrarErrorDetalles();
                        }
                    }

                    @Override
                    public void onFailure(Call<JuegoRawg> call, Throwable t) {
                        mostrarErrorDetalles();
                    }
                });
    }

    private void mostrarDetalles(JuegoRawg juego) {
        TextView tvDesarrolladorAnio = findViewById(R.id.tv_desarrollador_anio);
        TextView tvGenero = findViewById(R.id.tv_genero);
        TextView tvPlataformas = findViewById(R.id.tv_plataformas);
        TextView tvPuntaje = findViewById(R.id.tv_puntaje);

        tvDesarrolladorAnio.setText(unir(juego.getDesarrolladores(), juego.getAnio()));
        tvGenero.setText(textoODefault(juego.getGeneros()));
        tvPlataformas.setText(textoODefault(juego.getPlataformas(4)));
        if (juego.puntaje > 0) {
            tvPuntaje.setText(getString(R.string.puntaje_rawg, juego.puntaje));
            tvPuntaje.setVisibility(View.VISIBLE);
        }

        if (juego.descripcion == null || juego.descripcion.trim().isEmpty()) {
            tvSinopsis.setText(R.string.sin_sinopsis);
        } else {
            tvSinopsis.setText(juego.descripcion.trim());
        }

        if (portadaJuego == null && juego.urlPortada != null) {
            portadaJuego = juego.urlPortada;
            mostrarPortada(portadaJuego);
        }
    }

    private void mostrarErrorDetalles() {
        TextView tvDesarrolladorAnio = findViewById(R.id.tv_desarrollador_anio);
        tvDesarrolladorAnio.setText(R.string.sin_datos);
        tvSinopsis.setText(R.string.error_detalle);
    }

    private String unir(String desarrollador, String anio) {
        if (desarrollador != null && anio != null) {
            return getString(R.string.dos_datos, desarrollador, anio);
        }
        return textoODefault(desarrollador != null ? desarrollador : anio);
    }

    private String textoODefault(String texto) {
        return texto != null ? texto : getString(R.string.sin_datos);
    }

    private void configurarBotonAgregar() {
        Button btnAgregar = findViewById(R.id.btn_agregar);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = etEntrada.getText().toString().trim();

                if (texto.isEmpty()) {
                    Toast.makeText(DetalleActivity.this, R.string.error_entrada_vacia, Toast.LENGTH_SHORT).show();
                    return;
                }

                agregarEntrada(fechaDeHoy(), texto);
                Toast.makeText(DetalleActivity.this, R.string.entrada_agregada, Toast.LENGTH_SHORT).show();
                etEntrada.setText("");
            }
        });
    }

    private void agregarEntrada(String fecha, String texto) {
        LinearLayout tarjeta = crearTarjeta(fecha, texto);

        tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenedorEntradas.removeView(v);
                cantidadEntradas--;
                actualizarContador();
                Toast.makeText(DetalleActivity.this, R.string.entrada_eliminada, Toast.LENGTH_SHORT).show();
            }
        });

        contenedorEntradas.addView(tarjeta, 0);
        cantidadEntradas++;
        actualizarContador();
    }

    private LinearLayout crearTarjeta(String fecha, String texto) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_entrada);
        tarjeta.setPadding(padding, padding, padding, padding);
        tarjeta.setBackgroundResource(R.drawable.fondo_entrada);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.espacio_medio));
        tarjeta.setLayoutParams(params);

        TextView tvFecha = new TextView(this);
        tvFecha.setText(fecha);
        tvFecha.setTextColor(getColor(R.color.acento));
        tvFecha.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_chico));

        TextView tvTexto = new TextView(this);
        tvTexto.setText(texto);
        tvTexto.setTextColor(getColor(R.color.texto_primario));
        tvTexto.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.texto_cuerpo));

        tarjeta.addView(tvFecha);
        tarjeta.addView(tvTexto);
        return tarjeta;
    }

    private String fechaDeHoy() {
        return formatearFecha(new Date());
    }

    private String formatearFecha(Date fecha) {
        SimpleDateFormat formato = new SimpleDateFormat(getString(R.string.formato_fecha), Locale.getDefault());
        return formato.format(fecha);
    }

    private void actualizarContador() {
        tvTituloBitacora.setText(getString(R.string.titulo_bitacora_contador, cantidadEntradas));
        if (cantidadEntradas == 0) {
            tvVacio.setVisibility(View.VISIBLE);
        } else {
            tvVacio.setVisibility(View.GONE);
        }
    }

    private void configurarBotonesEstado() {
        Button btnJugando = findViewById(R.id.btn_jugando);
        Button btnCompletado = findViewById(R.id.btn_completado);
        Button btnPausado = findViewById(R.id.btn_pausado);

        btnJugando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado(Estado.JUGANDO);
            }
        });
        btnCompletado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado(Estado.COMPLETADO);
            }
        });
        btnPausado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarEstado(Estado.PAUSADO);
            }
        });
    }

    private void cambiarEstado(final String nuevoEstado) {
        if (nuevoEstado.equals(estadoActual)) {
            Toast.makeText(this, R.string.estado_sin_cambios, Toast.LENGTH_SHORT).show();
            return;
        }
        final String textoCambio = getString(R.string.entrada_cambio_estado, getString(Estado.texto(nuevoEstado)));
        estadoActual = nuevoEstado;
        mostrarEstado();
        agregarEntrada(fechaDeHoy(), textoCambio);
    }

    private void mostrarEstado() {
        tvEstado.setText(Estado.texto(estadoActual));
        tvEstado.setTextColor(getColor(Estado.color(estadoActual)));
    }

    private void configurarSinopsis() {
        tvSinopsis = findViewById(R.id.tv_sinopsis);
        tvVerMas = findViewById(R.id.tv_ver_mas);
        ImageView imgPortada = findViewById(R.id.img_portada);

        View.OnClickListener alternarSinopsis = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinopsisExpandida = !sinopsisExpandida;
                if (sinopsisExpandida) {
                    tvSinopsis.setMaxLines(Integer.MAX_VALUE);
                    tvVerMas.setText(R.string.ver_menos);
                } else {
                    tvSinopsis.setMaxLines(getResources().getInteger(R.integer.lineas_sinopsis));
                    tvVerMas.setText(R.string.ver_mas);
                }
            }
        };

        imgPortada.setOnClickListener(alternarSinopsis);
        tvSinopsis.setOnClickListener(alternarSinopsis);
        tvVerMas.setOnClickListener(alternarSinopsis);
    }
}