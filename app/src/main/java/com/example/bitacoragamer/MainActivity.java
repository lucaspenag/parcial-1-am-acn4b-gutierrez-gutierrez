package com.example.bitacoragamer;

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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorEntradas;
    private EditText etEntrada;

    private TextView tvTituloBitacora;
    private TextView tvVacio;
    private int cantidadEntradas = 0;

    private TextView tvEstado;
    private int estadoActual = R.string.estado_jugando;

    private static final int LINEA_SINOPSIS = 3;
    private TextView tvSinopsis;
    private TextView tvVerMas;
    private boolean sinopsisExpandida = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        contenedorEntradas = findViewById(R.id.contenedor_entradas);
        etEntrada = findViewById(R.id.et_entrada);
        tvTituloBitacora = findViewById(R.id.tv_titulo_bitacora);
        tvVacio = findViewById(R.id.tv_vacio);

        agregarEntrada(getString(R.string.ejemplo_1_fecha), getString(R.string.ejemplo_1_texto));
        agregarEntrada(getString(R.string.ejemplo_2_fecha), getString(R.string.ejemplo_2_texto));

        configurarBotonAgregar();
        configurarBotonesEstado();
        configurarSinopsis();
    }

    private void configurarBotonAgregar() {
        Button btnAgregar = findViewById(R.id.btn_agregar);
        btnAgregar.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String texto = etEntrada.getText().toString().trim();

               if (texto.isEmpty()) {
                   Toast.makeText(MainActivity.this, R.string.error_entrada_vacia, Toast.LENGTH_SHORT).show();
                   return;
               }
               agregarEntrada(fechaDeHoy(), texto);
               etEntrada.setText("");
               Toast.makeText(MainActivity.this, R.string.entrada_agregada, Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void agregarEntrada(String fecha, String texto) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_entrada);
        tarjeta.setPadding(padding, padding, padding, padding);
        tarjeta.setBackgroundColor(getColor(R.color.superficie));

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

        tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contenedorEntradas.removeView(v);
                cantidadEntradas--;
                actualizarContador();
                Toast.makeText(MainActivity.this, R.string.entrada_eliminada, Toast.LENGTH_SHORT).show();
            }
        });

        contenedorEntradas.addView(tarjeta, 0);

        cantidadEntradas++;
        actualizarContador();
    }

    private String fechaDeHoy() {
        SimpleDateFormat formato = new SimpleDateFormat(getString(R.string.formato_fecha), Locale.getDefault());
        return formato.format(new Date());
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
        tvEstado = findViewById(R.id.tv_estado);
        Button btnJugando = findViewById(R.id.btn_jugando);
        Button btnCompletado = findViewById(R.id.btn_completado);
        Button btnPausado = findViewById(R.id.btn_pausado);

        btnJugando.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cambiarEstado(R.string.estado_jugando, R.color.estado_jugando);
            }
        });
        btnCompletado.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cambiarEstado(R.string.estado_completado, R.color.estado_completado);
            }
        });
        btnPausado.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cambiarEstado(R.string.estado_pausado, R.color.estado_pausado);
            }
        });
    }
    private void cambiarEstado(int textoEstado, int colorEstado) {
        if (textoEstado == estadoActual) {
            Toast.makeText(this, R.string.estado_sin_cambios, Toast.LENGTH_SHORT).show();
            return;
        }
        estadoActual = textoEstado;
        tvEstado.setText(textoEstado);
        tvEstado.setTextColor(getColor(colorEstado));
        agregarEntrada(fechaDeHoy(), getString(R.string.entrada_cambio_estado, getString(textoEstado)));
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
                    tvSinopsis.setMaxLines(LINEA_SINOPSIS);
                    tvVerMas.setText(R.string.ver_mas);
                }
            }
        };

        imgPortada.setOnClickListener(alternarSinopsis);
        tvSinopsis.setOnClickListener(alternarSinopsis);
        tvVerMas.setOnClickListener(alternarSinopsis);
    }
}