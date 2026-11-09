# Pokédex Fire

App Android en Kotlin con Jetpack Compose para explorar Pokémon de tipo fuego. Este repositorio corresponde al Laboratorio 11-12 de Programación en Móviles.

## Estado actual

La interfaz incluye login, catálogo con búsqueda, detalle, favoritos y navegación. Por ahora funciona en modo demostración con datos locales de muestra. El inicio de sesión con Firebase, la carga desde PokeAPI, la persistencia Room y el panel de administración están pendientes de la integración de datos.

## Tecnologías previstas

- Kotlin y Jetpack Compose con Material 3
- Arquitectura MVVM
- PokeAPI mediante Retrofit
- Room para caché local y favoritos
- Firebase Authentication y Cloud Firestore para roles
- Notificación local al sincronizar

## API del tema

- Lista por tipo: `https://pokeapi.co/api/v2/type/fire`
- Detalle: `https://pokeapi.co/api/v2/pokemon/{nombre}`

La API es pública y no requiere clave. La respuesta de tipo incluye cada Pokémon dentro de `pokemon[].pokemon.name`.

## Abrir el proyecto

Abre esta carpeta en Android Studio. Usa un emulador Android con API 24 o superior y sincroniza el proyecto Gradle. El paquete es `com.tecsup.pokemon`.

## Firebase

Al integrar Firebase, activa Email/Password en Authentication y crea los perfiles de prueba en Firestore en `usuarios/{uid}` con el campo `rol` (`admin` o `usuario`). No subas `google-services.json`; debe permanecer local y fuera de Git.

## Reparto

- Usuario 1: tema visual, componentes de estado, pantallas de login, catálogo, detalle y favoritos, navegación y documentación.
- Usuario 2: dependencias e integración de PokeAPI, Room, repositorios, autenticación y roles Firebase, modo sin conexión, panel administrador y notificaciones.

## Seguridad

El repositorio ignora `local.properties`, carpetas de compilación, `.idea` y `google-services.json`. No guardes contraseñas, tokens ni claves en el código.
