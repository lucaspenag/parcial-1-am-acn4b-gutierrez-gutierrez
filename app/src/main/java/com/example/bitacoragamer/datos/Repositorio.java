package com.example.bitacoragamer.datos;

import com.example.bitacoragamer.modelo.Entrada;
import com.example.bitacoragamer.modelo.JuegoGuardado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Repositorio {

    public interface Resultado<T> {
        void alTerminar(T datos);

        void alFallar(Exception error);
    }

    private static CollectionReference coleccionDelUsuario(String nombre) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .collection(nombre);
    }



    public static void obtenerJuegos(final Resultado<List<JuegoGuardado>> resultado) {
        coleccionDelUsuario("juegos")
                .orderBy("agregado", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(documentos -> {
                    List<JuegoGuardado> juegos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentos) {
                        juegos.add(leerJuego(doc));
                    }
                    resultado.alTerminar(juegos);
                })
                .addOnFailureListener(resultado::alFallar);
    }


    public static void obtenerJuego(int id, final Resultado<JuegoGuardado> resultado) {
        coleccionDelUsuario("juegos")
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(doc -> resultado.alTerminar(doc.exists() ? leerJuego(doc) : null))
                .addOnFailureListener(resultado::alFallar);
    }

    public static void agregarJuego(JuegoGuardado juego, final Resultado<Void> resultado) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", juego.id);
        datos.put("nombre", juego.nombre);
        datos.put("portada", juego.portada);
        datos.put("estado", juego.estado);
        datos.put("agregado", juego.agregado);


        coleccionDelUsuario("juegos")
                .document(String.valueOf(juego.id))
                .set(datos)
                .addOnSuccessListener(nada -> resultado.alTerminar(null))
                .addOnFailureListener(resultado::alFallar);
    }

    public static void cambiarEstado(int idJuego, String estado, final Resultado<Void> resultado) {
        coleccionDelUsuario("juegos")
                .document(String.valueOf(idJuego))
                .update("estado", estado)
                .addOnSuccessListener(nada -> resultado.alTerminar(null))
                .addOnFailureListener(resultado::alFallar);
    }

    private static JuegoGuardado leerJuego(DocumentSnapshot doc) {
        Long id = doc.getLong("id");
        return new JuegoGuardado(
                id == null ? 0 : id.intValue(),
                doc.getString("nombre"),
                doc.getString("portada"),
                doc.getString("estado"),
                doc.getDate("agregado"));
    }


    public static void obtenerEntradas(final Resultado<List<Entrada>> resultado) {
        coleccionDelUsuario("entradas")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(documentos -> {
                    List<Entrada> entradas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentos) {
                        entradas.add(leerEntrada(doc));
                    }
                    resultado.alTerminar(entradas);
                })
                .addOnFailureListener(resultado::alFallar);
    }


    public static void obtenerEntradasDeJuego(int idJuego, final Resultado<List<Entrada>> resultado) {
        coleccionDelUsuario("entradas")
                .whereEqualTo("juegoId", idJuego)
                .get()
                .addOnSuccessListener(documentos -> {
                    List<Entrada> entradas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : documentos) {
                        entradas.add(leerEntrada(doc));
                    }

                    Collections.sort(entradas, new Comparator<Entrada>() {
                        @Override
                        public int compare(Entrada a, Entrada b) {
                            return b.fecha.compareTo(a.fecha);
                        }
                    });
                    resultado.alTerminar(entradas);
                })
                .addOnFailureListener(resultado::alFallar);
    }


    public static void agregarEntrada(final Entrada entrada, final Resultado<Entrada> resultado) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("juegoId", entrada.juegoId);
        datos.put("juegoNombre", entrada.juegoNombre);
        datos.put("juegoPortada", entrada.juegoPortada);
        datos.put("texto", entrada.texto);
        datos.put("fecha", entrada.fecha);

        coleccionDelUsuario("entradas")
                .add(datos)
                .addOnSuccessListener(referencia -> {
                    entrada.idDocumento = referencia.getId();
                    resultado.alTerminar(entrada);
                })
                .addOnFailureListener(resultado::alFallar);
    }

    public static void eliminarEntrada(String idDocumento, final Resultado<Void> resultado) {
        coleccionDelUsuario("entradas")
                .document(idDocumento)
                .delete()
                .addOnSuccessListener(nada -> resultado.alTerminar(null))
                .addOnFailureListener(resultado::alFallar);
    }

    private static Entrada leerEntrada(DocumentSnapshot doc) {
        Long juegoId = doc.getLong("juegoId");
        Date fecha = doc.getDate("fecha");
        return new Entrada(
                doc.getId(),
                juegoId == null ? 0 : juegoId.intValue(),
                doc.getString("juegoNombre"),
                doc.getString("juegoPortada"),
                doc.getString("texto"),
                fecha == null ? new Date() : fecha);
    }
}
