package com.example.bitacoragamer;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorEntradas;
    private EditText etEntrada;

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

        agregarEntrada(getString(R.string.ejemplo_1_fecha), getString(R.string.ejemplo_1_texto));
        agregarEntrada(getString(R.string.ejemplo_2_fecha), getString(R.string.ejemplo_2_texto));

        configurarBotonAgregar();
    }

    private void configurarBotonAgregar() {
        Button btnAgregar = findViewById(R.id.btn_agregar);
        btnAgregar.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String texto = etEntrada.getText().toString().trim();
               agregarEntrada(fechaDeHoy(), texto);
               etEntrada.setText("");
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

        contenedorEntradas.addView(tarjeta, 0);
    }

    private String fechaDeHoy() {
        SimpleDateFormat formato = new SimpleDateFormat(getString(R.string.formato_fecha), Locale.getDefault());
        return formato.format(new Date());
    }
}