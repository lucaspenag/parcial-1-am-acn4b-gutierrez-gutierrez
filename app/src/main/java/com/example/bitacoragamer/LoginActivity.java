package com.example.bitacoragamer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class LoginActivity extends AppCompatActivity {

    private static final int LARGO_MINIMO_CONTRASENIA = 6;

    private FirebaseAuth auth;
    private EditText etEmail;
    private EditText etContrasenia;
    private Button btnIngresar;
    private Button btnCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null) {
            irALaApp();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etContrasenia = findViewById(R.id.et_contrasenia);
        btnIngresar = findViewById(R.id.btn_ingresar);
        btnCrearCuenta = findViewById(R.id.btn_crear_cuenta);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCuenta();
            }
        });
    }

    private void ingresar() {
        if (!datosValidos()) {
            return;
        }
        habilitarBotones(false);
        auth.signInWithEmailAndPassword(email(), contrasenia())
                .addOnCompleteListener(this, tarea -> {
                    habilitarBotones(true);
                    if (tarea.isSuccessful()) {
                        irALaApp();
                    } else {
                        Toast.makeText(this, R.string.error_login, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void crearCuenta() {
        if (!datosValidos()) {
            return;
        }
        habilitarBotones(false);
        auth.createUserWithEmailAndPassword(email(), contrasenia())
                .addOnCompleteListener(this, tarea -> {
                    habilitarBotones(true);
                    if (tarea.isSuccessful()) {
                        irALaApp();
                    } else if (tarea.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, R.string.error_cuenta_existente, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.error_registro, Toast.LENGTH_LONG).show();
                    }
                });
    }


    private boolean datosValidos() {
        if (email().isEmpty() || contrasenia().isEmpty()) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (contrasenia().length() < LARGO_MINIMO_CONTRASENIA) {
            Toast.makeText(this, R.string.error_contrasenia_corta, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String email() {
        return etEmail.getText().toString().trim();
    }

    private String contrasenia() {
        return etContrasenia.getText().toString();
    }


    private void habilitarBotones(boolean habilitados) {
        btnIngresar.setEnabled(habilitados);
        btnCrearCuenta.setEnabled(habilitados);
    }


    private void irALaApp() {
        String email = auth.getCurrentUser().getEmail();
        Toast.makeText(this, getString(R.string.bienvenida, email), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
