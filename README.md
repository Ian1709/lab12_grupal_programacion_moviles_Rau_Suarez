# Pokédex Fire

Aplicación Android para explorar catálogos Pokémon de tipo fuego, planta y agua. Proyecto en Kotlin con Jetpack Compose, Material 3, MVVM, Retrofit, Room y Firebase Authentication/Cloud Firestore.

## Funciones

- Inicio de sesión con correo y contraseña de Firebase.
- Registro de entrenadores con nombre, correo y contraseña; las cuentas nuevas reciben el rol `usuario` desde Firestore.
- Roles `usuario` y `admin` leídos desde `usuarios/{uid}` en Firestore.
- Catálogos PokeAPI de fuego, planta y agua, con búsqueda, ilustraciones, detalle y favoritos.
- Ficha con altura, peso, juego original de su generación y referencia de temporada del anime.
- Favoritos persistentes y catálogo disponible sin conexión cuando ya se sincronizó.
- Panel administrador con totales, sincronización y limpieza confirmada de los datos locales.
- Notificación de sincronización en Android 13 o posterior cuando el permiso se concede. Si se rechaza, las funciones principales siguen disponibles.

El juego mostrado es el primer juego principal de la generación en que se introdujo la especie. La temporada de anime es una referencia de esa generación, no una afirmación de la primera escena exacta en la que apareció cada Pokémon.

## Tecnologías y arquitectura

- Kotlin, Jetpack Compose y Material 3; no se usan layouts XML.
- MVVM: pantallas Compose observan `StateFlow<UiState<T>>`; los ViewModels dependen de interfaces de repositorio.
- Retrofit consulta `type/fire`, `pokemon/{name}` y `pokemon-species/{id}`.
- Room es la fuente del catálogo visible y conserva los favoritos al sincronizar. La base migra de la versión 1 a la 2 para guardar la generación.
- Firebase Authentication valida credenciales; el rol proviene de Firestore, no de valores fijados en la pantalla.
- `SyncNotifier` separa el ViewModel de la implementación Android de notificaciones.

Paquete Android: `com.tecsup.pokemon`.

## Abrir y compilar

1. Abre la carpeta del proyecto en Android Studio.
2. Usa JDK 17 como Gradle JDK.
3. Usa un emulador con Google Play o un dispositivo con Android 7.0 (API 24) o posterior.
4. Configura Firebase con los pasos de abajo antes de probar el inicio de sesión.

En PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

APK de depuración generado en `app/build/outputs/apk/debug/app-debug.apk`.

## Configurar Firebase localmente

El repositorio no contiene el archivo de configuración ni credenciales. Una persona del equipo debe configurarlos en la consola Firebase:

1. Registra una app Android con el paquete exacto `com.tecsup.pokemon`.
2. Activa el método **Correo electrónico/contraseña** en Authentication.
3. Crea el usuario administrador desde Authentication y Firestore. Su perfil debe estar en `usuarios/{uid}` con `rol: "admin"` y `nombre`.
4. Publica las reglas de [firestore.rules](firestore.rules). Permiten que la app cree su propio perfil solo con rol `usuario`; el cliente no puede asignarse rol admin.
5. Descarga `google-services.json` y colócalo localmente en `app/google-services.json`.
6. Sincroniza Gradle y ejecuta la app. Los entrenadores pueden registrarse desde el enlace **¿Nuevo entrenador? Crear cuenta**; la app crea la cuenta de Authentication y el perfil de usuario en Firestore.

No subas `google-services.json`, contraseñas ni claves. `.gitignore` excluye el archivo. Sin esta configuración, la app compila, pero el acceso Firebase no puede completarse.

El registro necesita las reglas de `firestore.rules`: si se mantienen reglas que deniegan toda escritura, Firebase Authentication podrá crear la cuenta, pero la app no podrá guardar el perfil y cancelará el alta.

## API de Pokémon

- Tipo fuego: <https://pokeapi.co/api/v2/type/fire>
- Detalle: <https://pokeapi.co/api/v2/pokemon/{nombre}>
- Generación de especie: <https://pokeapi.co/api/v2/pokemon-species/{id}>

PokeAPI es pública y no requiere clave. La lista se encuentra en `pokemon[].pokemon.name`; la especie indica su `generation` (por ejemplo, `generation-ix`).

## Pruebas

Las pruebas unitarias no necesitan conexión a Firebase ni a PokeAPI. Cubren el estado del catálogo, el error sin conexión, la acción de favoritos y la conversión del debut generacional:

```powershell
.\gradlew.bat testDebugUnitTest
```

Antes de entregar, probar manualmente en emulador con las dos cuentas: credenciales válidas e inválidas, rol admin y usuario, detalle, favorito tras reabrir, modo avión, sincronización, limpieza de caché y permiso de notificaciones aceptado y rechazado. Las capturas finales del login, catálogo, detalle, favoritos, panel y notificación deben tomarse con las cuentas y el proyecto Firebase del grupo.

## Reparto

- **Usuario 1:** tema visual, componentes de estados, login, catálogo, detalle, favoritos, navegación, perfil y README.
- **Usuario 2:** Retrofit/PokeAPI, Room, repositorio, Firebase Authentication/Firestore, sesión y roles, modo sin conexión, panel admin y notificaciones.
- **Integración de Fase 2:** pruebas, mejoras visuales, ficha de debut y documentación de consultas IA en `PROMPTS.md`.

## Fases y Git

- `main`: versión integrada de Fase 1.
- `mejora-ia`: mejoras documentadas de Fase 2; abrir un Pull Request hacia `main` después de revisar las pruebas y la configuración Firebase.
- El historial actual de `mejora-ia` incluye más de 10 commits de mejora.
- La entrega final del curso requiere tag `v1.0` con APK de depuración y capturas del emulador.

## Seguridad

No guardes archivos de Firebase, contraseñas, tokens ni datos de las cuentas de prueba en Git. Las credenciales de demostración deben compartirse solo por el canal privado del grupo.
