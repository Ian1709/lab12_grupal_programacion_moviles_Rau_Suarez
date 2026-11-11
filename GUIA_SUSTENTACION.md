# Guía rápida para la sustentación

Esta guía describe la versión actual de la rama `mejora-ia`. Está pensada para preguntas sobre qué hace cada función y dónde se guarda cada dato.

## Preguntas probables y respuestas

### Catálogo y navegación

**1. ¿Dónde se guarda el catálogo de Pokémon?**  
En la base de datos local Room del dispositivo, llamada `pokemon.db`. Los registros están en la tabla `pokemon`; la tabla `pokemon_type_catalog` relaciona cada Pokémon con el tipo del catálogo. El catálogo visible se observa desde Room, no directamente desde la API. Está definido en `data/local/PokemonDatabase.kt` y `PokemonDao.kt`.

**2. ¿De dónde salen los Pokémon la primera vez?**  
De PokeAPI. Retrofit consulta `type/fire`; cada resultado contiene nombre y URL. La app extrae el ID, descarta IDs fuera del rango de especies nacionales 1–1025 y guarda los resultados en Room. La lógica está en `PokemonRepositoryImpl.synchronize()`.

**3. ¿Qué pasa cuando toco una tarjeta de Pokémon?**  
La navegación abre `detail/{pokemonId}/{pokemonName}`. `PokemonDetailViewModel` observa el Pokémon guardado en Room y carga o actualiza sus datos desde PokeAPI. La pantalla de detalle muestra la información y permite cambiar el favorito.

**4. ¿Dónde está la acción equivalente al carrito del aplicativo de café?**  
Aquí la acción principal equivalente es **marcar como favorito**: el botón de corazón en la tarjeta o en el detalle. `PokemonListViewModel.toggleFavorite()` invierte el valor y el repositorio lo guarda en Room con `PokemonDao.setFavorite()`.

**5. ¿Dónde encuentro los Pokémon que marqué?**  
En la pantalla Favoritos. Room los busca con `observeFavorites()`, filtrando `isFavorite = 1`. Quitar uno vuelve a cambiar esa misma marca; no crea otra copia del Pokémon.

**6. ¿Los favoritos siguen después de cerrar la app?**  
Sí. `isFavorite` forma parte de la entidad Room y se guarda en `pokemon.db`; no depende de que la pantalla siga abierta. La persistencia es local a ese dispositivo, no se sincroniza entre cuentas.

**7. ¿Qué hace el botón Reintentar del catálogo?**  
Llama a `PokemonListViewModel.synchronize()`. Si hay conexión, consulta PokeAPI y actualiza Room; si falla, conserva lo que ya estaba guardado y presenta el estado de error o el catálogo disponible.

### Sincronización y modo sin conexión

**8. ¿Qué significa sincronizar?**  
Descargar el catálogo desde PokeAPI y guardarlo en Room. Retrofit obtiene los datos; el repositorio coordina API y base local. Después, el flujo de Room actualiza automáticamente las pantallas que observan esos datos.

**9. ¿La app muestra el catálogo si no hay internet?**  
Si se sincronizó antes, sí: la lista se alimenta desde Room. Si no hay datos guardados, se muestra un error indicando que no existe catálogo local y se ofrece reintentar.

**10. ¿Al sincronizar se pierden mis favoritos?**  
No. `PokemonDao.replaceCatalog()` revisa cuáles Pokémon ya estaban marcados y conserva `isFavorite` al reemplazar los datos del catálogo.

**11. ¿Qué hace Limpiar datos locales?**  
El panel administrador llama `clearCache()`, que ejecuta `PokemonDao.clearAll()`. Elimina del dispositivo el catálogo y los favoritos locales; después se pueden volver a descargar. No borra cuentas de Firebase.

**12. ¿Qué API y endpoints usa?**  
PokeAPI, sin clave: `type/{type}` lista Pokémon por tipo; `pokemon/{name}` obtiene sus datos y tipos; `pokemon-species/{id}` permite leer la generación. El `BASE_URL` está en `data/remote/PokemonApiService.kt`.

### Detalle del Pokémon

**13. ¿Cómo obtiene el juego de debut y la temporada de anime?**  
Se obtiene la generación desde `pokemon-species/{id}`. La app convierte `generation-i`, `generation-ii`, etc. en un número y muestra el primer juego principal de esa generación y una temporada de anime de referencia. La conversión está en `data/mapper/PokemonMappers.kt`. Es una referencia por generación, no la fecha exacta de primera aparición individual de cada especie.

**14. ¿De dónde sale la imagen de cada Pokémon?**  
Del campo `sprites.other.official-artwork.front_default` de PokeAPI. Si falta, el mapper construye la URL del arte oficial usando el ID. El mapeo está en `PokemonMappers.kt` y su presentación en `ui/components/PokemonArtwork.kt`.

### Usuarios, Firebase y perfiles

**15. ¿Para qué se usa Firebase en esta app?**  
Firebase Authentication inicia sesión o crea cuentas con correo y contraseña. Cloud Firestore guarda el perfil bajo `usuarios/{uid}`, con nombre y rol. El catálogo y los favoritos se guardan en Room, no en Firebase.

**16. ¿Qué rol tiene alguien que se registra desde la app?**  
`usuario`. La app escribe ese rol junto al nombre al crear la cuenta. No ofrece una opción para registrarse como administrador.

**17. ¿Cómo entra un administrador?**  
Su cuenta se crea y configura desde Firebase. Firestore debe tener `usuarios/{uid}` con `rol: "admin"`. La sesión lee ese perfil; el valor no está fijado en la pantalla ni se deduce del correo.

**18. ¿Qué ve el usuario normal y qué añade el administrador?**  
El usuario normal ve catálogo, detalle, favoritos y perfil. El administrador tiene además el panel para revisar totales, sincronizar y limpiar los datos locales. La navegación verifica el rol antes de abrir la ruta `admin` (`ui/navigation/PokemonNavigation.kt`).

**19. ¿Qué pasa si Firebase no está configurado?**  
La app puede compilar, pero autenticación y registro no podrán completarse. Se necesita el archivo local `app/google-services.json`, habilitar correo/contraseña en Firebase Authentication y publicar `firestore.rules`. Ese JSON no debe subirse al repositorio.

### Arquitectura y sistema Android

**20. ¿Qué hace MVVM aquí?**  
La pantalla Compose presenta la interfaz; el ViewModel mantiene el estado y responde a acciones; el repositorio decide cómo leer o guardar datos en Room o PokeAPI. Así, la pantalla no llama directamente a Retrofit ni a la base de datos.

**21. ¿Qué es `UiState` y para qué sirve?**  
Representa estados de la interfaz como carga, éxito o error. Por ejemplo, el catálogo puede enseñar un indicador de carga, una lista, un estado vacío o un mensaje con reintento. Los ViewModels exponen estados como `StateFlow`.

**22. ¿Qué hace la notificación de sincronización?**  
Al terminar una sincronización, `AndroidSyncNotifier` crea el canal `pokemon_sync` y muestra cuántos Pokémon se guardaron. En Android 13 o posterior requiere permiso de notificaciones; si se deniega, la app sigue funcionando y solo omite el aviso.

**23. ¿Cómo se conectan las partes de la app?**  
`PokemonAppContainer` crea Room, Retrofit, repositorios, monitor de red y notificador. `MainActivity` entrega estas dependencias a `PokemonNavigation`, que conecta pantallas y ViewModels.

## Ubicación rápida en el proyecto

| Tema | Archivo principal |
| --- | --- |
| Catálogo local, tabla y consultas | `app/src/main/java/com/tecsup/pokemon/data/local/PokemonDatabase.kt`, `PokemonDao.kt` |
| Entidad persistida | `app/src/main/java/com/tecsup/pokemon/data/local/PokemonEntity.kt` |
| Endpoints PokeAPI | `app/src/main/java/com/tecsup/pokemon/data/remote/PokemonApiService.kt` |
| Coordinación entre Room y API | `app/src/main/java/com/tecsup/pokemon/data/repository/PokemonRepositoryImpl.kt` |
| Modelo y conversiones | `app/src/main/java/com/tecsup/pokemon/data/mapper/PokemonMappers.kt` |
| Estado y acciones del catálogo | `app/src/main/java/com/tecsup/pokemon/ui/viewmodel/PokemonListViewModel.kt` |
| Login y alta | `app/src/main/java/com/tecsup/pokemon/data/repository/FirebaseAuthRepository.kt`, `ui/login/LoginScreen.kt` |
| Roles y rutas | `app/src/main/java/com/tecsup/pokemon/ui/navigation/PokemonNavigation.kt` |
| Panel administrador | `app/src/main/java/com/tecsup/pokemon/ui/admin/AdminPanelScreen.kt`, `ui/viewmodel/AdminViewModel.kt` |
| Aviso de sincronización | `app/src/main/java/com/tecsup/pokemon/data/notification/AndroidSyncNotifier.kt` |

## Resumen para memorizar

- **PokeAPI** aporta datos remotos.
- **Room** guarda catálogo y favoritos en el teléfono.
- **Retrofit** realiza las solicitudes HTTP.
- **Repository** coordina API y Room.
- **ViewModel** expone estados y acciones a Compose.
- **Firebase Authentication y Firestore** manejan cuentas, nombres y roles.
- **Favorito** es una marca local `isFavorite` del Pokémon.
