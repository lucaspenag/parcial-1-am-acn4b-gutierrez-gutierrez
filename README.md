# Bitácora Gamer

**Parcial 2 - Aplicaciones Móviles - Escuela Da Vinci**
Profesor: Sergio Medina

| Integrantes |
|---|
| Peña Gutierrez, Lucas Rafael|
| Peña Gutierrez, Matias Eduardo|

---

## 1. La idea

**Bitácora Gamer** es una app para llevar un diario de lo que vas haciendo en cada videojuego. Después de cada sesión anotás qué hiciste ("derroté a tal jefe", "llegué a tal zona", "me trabé en tal parte"). Así, cuando retomás un juego semanas después, sabés exactamente dónde estabas.

- **Para quién es:** gente que juega varios juegos a la vez, o que deja un juego y lo retoma más adelante.
- **Datos de los juegos (reales):** nombre, portada, desarrollador, año, géneros, plataformas, puntaje y sinopsis se traen de la API pública de [RAWG](https://rawg.io/apidocs).
- **Datos del usuario:** cada usuario entra con su cuenta (Firebase Auth) y su biblioteca, el estado de cada juego y su bitácora se guardan en la nube (Firestore). Si cambia de celular, sus datos siguen ahí.

---

## 2. Pantallas de la app

La app tiene 6 pantallas. **En este parcial están todas implementadas.**

| # | Pantalla | Para qué sirve | Activity |
|---|---|---|---|
| 0 | Login | Ingresar o crear una cuenta | `LoginActivity` |
| 1 | Biblioteca | Ver todos mis juegos y su estado | `BibliotecaActivity` |
| 2 | Buscar juego | Buscar en RAWG y agregar un juego | `BuscarActivity` |
| 3 | Detalle + bitácora | Ver un juego y registrar lo que hice | `DetalleActivity` |
| 4 | Diario | Ver todas las entradas de todos los juegos, por fecha | `DiarioActivity` |
| 5 | Estadísticas | Resumen de juegos, completados, entradas y racha | `EstadisticasActivity` |

### 2.0 Login

**Objetivo:** que cada usuario tenga su propia biblioteca y bitácora.

**Funcionalidades:**
- Campos de mail y contraseña.
- Botón **Ingresar** (cuenta existente) y botón **Crear cuenta** (cuenta nueva), con Firebase Auth.
- Validaciones antes de llamar a Firebase: campos vacíos y contraseña de menos de 6 caracteres.
- Mensajes claros si la contraseña es incorrecta o si el mail ya tiene una cuenta.
- Si el usuario ya había ingresado antes, Firebase recuerda la sesión y la app pasa directo a la Biblioteca.

**Flujo de uso:** es la primera pantalla que se abre. Al ingresar se abre la **Biblioteca** y el Login se cierra (`finish()`), así el botón Atrás no vuelve a esta pantalla.

### 2.1 Biblioteca (inicio)

**Objetivo:** mostrar todos los juegos que el usuario agregó.

**Funcionalidades:**
- Lista de juegos con portada (descargada con Glide), nombre y estado (Jugando, Completado o Pausado), cada estado con su color. Las tarjetas se crean desde Java.
- Filtros por estado: Todos, Jugando, Completados y Pausados. El filtro elegido se marca en violeta.
- Mensaje cuando la biblioteca está vacía o cuando ningún juego tiene el estado elegido.
- Botón redondo **+** para agregar un juego nuevo.
- **Salir** cierra la sesión y vuelve al Login.
- Barra de navegación inferior: Biblioteca, Diario y Estadísticas.

**Flujo de uso:** el usuario toca un filtro para ver solo los juegos que está jugando, toca un juego para ir a su **Detalle**, o toca **+** para ir a **Buscar juego**. Al volver de otra pantalla, la lista se recarga (`onResume`), así se ven los estados actualizados.

### 2.2 Buscar juego

**Objetivo:** encontrar un juego en RAWG y sumarlo a la biblioteca.

**Funcionalidades:**
- Campo de búsqueda por nombre, con el botón **Buscar** o la tecla de búsqueda del teclado.
- Pedido a la API de RAWG con **Retrofit**. Mientras espera, se ve una rueda de carga.
- Resultados creados desde Java, con portada (Glide), nombre, año y plataformas.
- Botón **Agregar** en cada resultado. Si el juego ya está en la biblioteca, el botón dice **Ver**.
- Mensajes para búsqueda vacía, sin resultados, sin conexión o API key inválida.

**Flujo de uso:** desde la Biblioteca, el usuario toca **+**, escribe el nombre del juego y toca **Buscar**. Al tocar **Agregar**, el juego se guarda en Firestore con estado "Jugando" y se abre su **Detalle**. **Volver** regresa a la Biblioteca.

### 2.3 Detalle + bitácora

**Objetivo:** ver la información de un juego y registrar lo que se hizo en cada sesión.

**Funcionalidades:**
- Recibe por **extras** (un `Bundle` dentro del `Intent`) el id, el nombre y la portada del juego. Con eso muestra enseguida el título y la portada.
- Con el id pide a RAWG el resto: desarrollador, año, géneros, plataformas, puntaje y sinopsis.
- Sinopsis que se puede expandir y contraer (tocando la portada, la sinopsis o **Ver más**).
- Tres botones para cambiar el estado del juego. El cambio se guarda en Firestore y queda anotado en la bitácora.
- Bitácora: lista de entradas (fecha + texto) de la más nueva a la más vieja, con el contador "Bitácora (N)".
- Campo de texto y botón **Agregar** para sumar una entrada nueva, que se guarda en Firestore.
- Tocar una entrada la borra.

**Flujo de uso:** se llega tocando un juego en la Biblioteca, agregándolo desde Buscar o tocando una entrada del Diario. El usuario anota qué hizo y toca **Agregar**. **Volver** regresa a la pantalla anterior.

### 2.4 Diario

**Objetivo:** ver en un solo lugar todo lo que el usuario jugó, ordenado por fecha.

**Funcionalidades:**
- Entradas de todos los juegos, de la más nueva a la más vieja, agrupadas por día ("Hoy", "Ayer" o la fecha).
- Cada entrada muestra la portada mini, el nombre del juego, la hora y el texto.

**Flujo de uso:** el usuario entra desde la barra inferior y recorre sus últimas sesiones. Si toca una entrada, va al **Detalle** de ese juego.

### 2.5 Estadísticas

**Objetivo:** mostrar un resumen de la actividad del usuario.

**Funcionalidades:**
- Tarjetas con la cantidad de juegos, juegos completados, entradas y racha de días seguidos con al menos una entrada.
- Barras con la cantidad de juegos en cada estado, creadas desde Java (el largo de la barra se calcula con `layout_weight`).
- El juego con más entradas, con su portada.

**Flujo de uso:** el usuario entra desde la barra inferior. Es una pantalla de solo lectura: se calcula en el momento con lo que ya está guardado.

---

## 3. Flujo general de la app

- La app abre en el **Login**. Si ya hay una sesión abierta, pasa directo a la **Biblioteca**.
- **Biblioteca**, **Diario** y **Estadísticas** son las tres secciones principales y se cambia entre ellas con la barra inferior. La Biblioteca queda siempre abajo en la pila de pantallas, así que Atrás desde Diario o Estadísticas vuelve a ella.
- Desde la Biblioteca, **+** lleva a **Buscar juego**. Al agregar un juego se abre su **Detalle**.
- Tocando un juego (en la Biblioteca) o una entrada (en el Diario) se llega al **Detalle**.
- **Salir** (en la Biblioteca) cierra la sesión y vuelve al Login.

**Cómo se mueven los datos:**

| Desde | Hacia | Cómo |
|---|---|---|
| RAWG | Buscar y Detalle | Retrofit (`RawgService`) devuelve JSON que Gson convierte en `JuegoRawg` |
| Internet | Todas las portadas | Glide descarga la imagen desde la URL |
| Biblioteca, Buscar o Diario | Detalle | `Intent` con un `Bundle` de extras: id, nombre y portada del juego |
| Detalle y Buscar | Firestore | `Repositorio` guarda juegos, estados y entradas |
| Firestore | Biblioteca, Diario y Estadísticas | `Repositorio` los lee cada vez que la pantalla vuelve a verse |

Estructura en Firestore (cada usuario solo puede leer y escribir lo suyo, ver `docs/firestore.rules`):

```
usuarios/{uid}/juegos/{idRawg}      nombre, portada, estado, agregado
usuarios/{uid}/entradas/{idAuto}    juegoId, juegoNombre, juegoPortada, texto, fecha
```

---

## 4. Cómo correr el proyecto

1. Clonar el repositorio y abrirlo con Android Studio. Esperar a que termine de sincronizar Gradle.
2. El archivo `app/google-services.json` (configuración de Firebase) ya está en el repo.
3. Ejecutar la app en un emulador o en un celular con Android 7.0 o superior y con internet.
4. Crear una cuenta desde el Login (cualquier mail y una contraseña de 6 caracteres o más).

La API key de RAWG está en `app/src/main/res/values/api.xml`. 

### Bibliotecas usadas

| Biblioteca | Para qué |
|---|---|
| Retrofit + converter-gson | Pedidos a la API de RAWG y conversión del JSON |
| Glide | Descargar y mostrar las portadas |
| Firebase Auth | Login con mail y contraseña |
| Firebase Firestore | Guardar biblioteca y bitácora en la nube |

---

