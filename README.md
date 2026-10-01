# Bitácora Gamer

**Aplicaciones Móviles - Escuela Da Vinci**

Profesor: Sergio Medina

| Integrantes                    |
|--------------------------------|
| Peña Gutierrez, Lucas Rafael   |
| Peña Gutierrez, Matias Eduardo |

---

## 1. La IDEA

**Bitácora Gamer** es una app para llevar un diario de lo que vas haciendo en cada videojuego. Después
de cada sesión anotas que hiciste ("derrote a tal jefe", "llegue a tal zona", "me trabe en tal parte").
Asi, cuando retomas un juego semanas después, sabes exactamente donde estabas.

---

## 2. Pantallas de la app

La app completa va a tener 5 pantallas.

| # | Pantalla           | Para qué sirve                                        |
|---|--------------------|-------------------------------------------------------|
| 1 | Biblioteca         | Ver todos mis juegos y su estado                      |
| 2 | Buscar juego       | Buscar en la API y agregar un juego                   |
| 3 | Detalle + bitacora | Ver un juego y registrar lo que hice                  |
| 4 | Diario             | Ver todas las entradas de todos los juegos, por fecha |
| 5 | Estadisticas       | Resumen de horas, completados y rachas                |

### 2.3 Detalle + bitácora 

**Objetivo:** ver la información de un juego y registrar lo que se hizo en cada sesión.

**Funcionalidades esperadas:**
- Encabezado con portada, título, desarrollador, año, género, plataformas, horas jugadas y estado actual.
- Sinopsis que se puede expandir y contraer.
- Tres botones para cambiar el estado del juego.
- Bitácora: lista de entradas (fecha + texto) de la más nueva a las más vieja.
- Campo de texto y botón **Agregar** para sumar una entrada nueva.

---

## 3. Flujo general de la app

- **Biblioteca**, **Diario**, **Estadísticas** son las tres secciones principales y se cambia entre ellas con la barra inferior.
- Desde la biblioteca, **+** lleva a **Buscar juego**. Al agregar un juego, se vuelve a la Biblioteca.
- Tocando un juego (en la biblioteca) o una entrada (en el Diario) se llega al **Detalle**.
- **Cómo se mueven los datos:**
    - La API se usa solo en **Buscar juego** para traer la información del juego.
    - Lo que el usuario escribe en el **Detalle** (Bitácora y estado) se guarda en el celular.
    - **Diario** y **Estadísticas** solo leen lo que ya está guardado.

---

## 4. Pantalla elegida para el parcial: Detalle + bitácora

### ¿Por qué esta pantalla?

- **Es el corazón de la app.** Las otras pantallas muestran o resumen lo que se carga acá.
- **Funciona sola:** no necesita navegación entre pantalla ni la API. Los datos del juego están en `strings.xml`

### Interacciones
1. **Agregar una entrada:** el usuario escribe qué hizo y toca **Agregar**. Se crea una tarjeta nueva con la fecha de hoy, arriba de todo en la lista, el campo se limpia y aparece el mensaje "Entrada agregada".
2. **Validación:** si toca **Agregar** con el campo vacío, no se agrega nada y aparece el mensaje "Escribí algo antes de agregar".
3. **Contador:** el título muestra "Bitácora (N)" con la cantidad de entradas. Si no queda ninguna, aparece un mensaje invitando a escribir la primera.
4. **Cambiar el estado:** los botones Jugando, Completado y Pausado cambian el texto y el color del estado. Cada cambio queda registrado automáticamente en la bitácora ("Cambié el estado a Completado"). Si se elige el estado que ya tiene, se avisa y no se repite.
5. **Borrar una entrada:** tocar una tarjeta de la bitácora la elimina y el contador se actualiza.
6. **Sinopsis expandible:** tocar la portada, la sinopsis o **Ver más** muestra la sinopsis completa. Tocando de nuevo se vuelve a 3 líneas.

### Diseño y colores

Se eligió un **tema oscuro** porque la app se usa justo después de jugar, muchas veces de noche. El violeta es el color de marca y cada estado del juego tiene su propio color para reconocerlo de un vistazo.

| Uso                              | Nombre en `colors.xml` | Color     |
|----------------------------------|------------------------|-----------|
| Fondo                            | `fondo`                | `#121218` |
| Tarjetas y botones               | `superficie`           | `#23232F` |
| Color de marca / botón principal | `primario`             | `#7C4DFF` |
| Detalles (fechas, horas, links)  | `acento`               | `#B388FF` |
| Texto principal                  | `texto_primario`       | `#F5F5F7` |
| Texto secundario                 | `texto_secundario`     | `#A8A8BC` |
| Estado Jugando                   | `estado_jugando`       | `#4FC3F7` |
| Estado Completado                | `estado_completado`    | `#66BB6A` |
| Estado Pausado                   | `estado_pausado`       | `#FFB74D` |

---

## 5. Cómo correr el proyecto

1. Clonar el repositorio.
2. Abrirlo con Android Studio y esperar a que termine de sincronizar Gradle.
3. Ejecutar la app en un emulador o en un celular con Android 7.0 o superior.

---
