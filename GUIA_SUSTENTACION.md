# Guía rápida para la sustentación

Esta guía describe la versión actual de la rama `mejora-ia`. Está pensada para preguntas sobre qué hace cada función y dónde se guarda cada dato.

## Preguntas probables y respuestas

### Catálogo y navegación

**1. ¿Dónde se guarda el catálogo de Pokémon?**

En la base de datos local Room del dispositivo, llamada `pokemon.db`. Los registros están en la tabla `pokemon`; la tabla `pokemon_type_catalog` relaciona cada Pokémon con el tipo del catálogo. El catálogo visible se observa desde Room, no directamente desde la API. Está definido en `data/local/PokemonDatabase.kt` y `PokemonDao.kt`.

**2. ¿De dónde salen los Pokémon la primera vez?**

De PokeAPI. Retrofit consulta `type/{type}` para el tipo seleccionado (fuego, planta o agua); cada resultado contiene nombre y URL. La app extrae el ID, descarta IDs fuera del rango de especies nacionales 1–1025 y guarda los resultados en Room. La lógica está en `PokemonRepositoryImpl.synchronize()`.

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

Descargar el catálogo del tipo seleccionado desde PokeAPI y guardarlo en Room. Retrofit obtiene los datos; el repositorio coordina API y base local. Después, el flujo de Room actualiza automáticamente las pantallas que observan esos datos.

**9. ¿La app muestra el catálogo si no hay internet?**

Si se sincronizó antes, sí: la lista se alimenta desde Room. Si no hay datos guardados, se muestra un error indicando que no existe catálogo local y se ofrece reintentar.

**10. ¿Al sincronizar se pierden mis favoritos?**

No. `PokemonDao.replaceCatalog()` revisa cuáles Pokémon ya estaban marcados y conserva `isFavorite` al reemplazar los datos del catálogo.

**11. ¿Qué hace Limpiar datos locales?**

El panel administrador llama `clearCache()`, que ejecuta `PokemonDao.clearAll()`. Elimina del dispositivo el catálogo y los favoritos locales; después se pueden volver a descargar. No borra cuentas de Firebase.

**12. ¿Cómo cambio de colección o tipo?**

En la parte superior del catálogo hay filtros para fuego, planta y agua. `PokemonListViewModel.selectType()` valida y guarda la selección; si hay conexión, sincroniza ese catálogo. La tabla `pokemon_type_catalog` permite que un mismo Pokémon pertenezca a catálogos por tipo sin duplicar su fila principal en Room.

**13. ¿Qué API y endpoints usa?**

PokeAPI, sin clave: `type/{type}` lista Pokémon por tipo; `pokemon/{name}` obtiene sus datos y tipos; `pokemon-species/{id}` permite leer la generación. El `BASE_URL` está en `data/remote/PokemonApiService.kt`.

### Detalle del Pokémon

**14. ¿Cómo obtiene el juego de debut y la temporada de anime?**

Se obtiene la generación desde `pokemon-species/{id}`. La app convierte `generation-i`, `generation-ii`, etc. en un número y muestra el primer juego principal de esa generación y una temporada de anime de referencia. La conversión está en `data/mapper/PokemonMappers.kt`. Es una referencia por generación, no la fecha exacta de primera aparición individual de cada especie.

**15. ¿De dónde sale la imagen de cada Pokémon?**

Del campo `sprites.other.official-artwork.front_default` de PokeAPI. Si falta, el mapper construye la URL del arte oficial usando el ID. El mapeo está en `PokemonMappers.kt` y su presentación en `ui/components/PokemonArtwork.kt`.

### Usuarios, Firebase y perfiles

**16. ¿Para qué se usa Firebase en esta app?**

Firebase Authentication inicia sesión o crea cuentas con correo y contraseña. Cloud Firestore guarda el perfil bajo `usuarios/{uid}`, con nombre y rol. El catálogo y los favoritos se guardan en Room, no en Firebase.

**17. ¿Qué rol tiene alguien que se registra desde la app?**

`usuario`. La app escribe ese rol junto al nombre al crear la cuenta. No ofrece una opción para registrarse como administrador.

**18. ¿Cómo entra un administrador?**

Su cuenta se crea y configura desde Firebase. Firestore debe tener `usuarios/{uid}` con `rol: "admin"`. La sesión lee ese perfil; el valor no está fijado en la pantalla ni se deduce del correo.

**19. ¿Qué ve el usuario normal y qué añade el administrador?**

El usuario normal ve catálogo, detalle, favoritos y perfil. El administrador tiene además el panel para revisar totales, sincronizar y limpiar los datos locales. La navegación verifica el rol antes de abrir la ruta `admin` (`ui/navigation/PokemonNavigation.kt`).

**20. ¿Qué pasa si Firebase no está configurado?**

La app puede compilar, pero autenticación y registro no podrán completarse. Se necesita el archivo local `app/google-services.json`, habilitar correo/contraseña en Firebase Authentication y publicar `firestore.rules`. Ese JSON no debe subirse al repositorio.

### Arquitectura y sistema Android

**21. ¿Qué hace MVVM aquí?**

La pantalla Compose presenta la interfaz; el ViewModel mantiene el estado y responde a acciones; el repositorio decide cómo leer o guardar datos en Room o PokeAPI. Así, la pantalla no llama directamente a Retrofit ni a la base de datos.

**22. ¿Qué es `UiState` y para qué sirve?**

Representa estados de la interfaz como carga, éxito o error. Por ejemplo, el catálogo puede enseñar un indicador de carga, una lista, un estado vacío o un mensaje con reintento. Los ViewModels exponen estados como `StateFlow`.

**23. ¿Qué hace la notificación de sincronización?**

Al terminar una sincronización, `AndroidSyncNotifier` crea el canal `pokemon_sync` y muestra cuántos Pokémon se guardaron. En Android 13 o posterior requiere permiso de notificaciones; si se deniega, la app sigue funcionando y solo omite el aviso.

**24. ¿Cómo se conectan las partes de la app?**

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

## Más preguntas posibles del profesor

### Interfaz, acciones y flujo

**25. ¿Cómo funciona la búsqueda?**

El texto se guarda temporalmente en el estado de `PokemonListScreen` y filtra la lista ya cargada por nombre, ignorando mayúsculas. No hace otra consulta a Firebase ni a PokeAPI. Ruta: `app/src/main/java/com/tecsup/pokemon/ui/catalog/PokemonListScreen.kt`.

**26. ¿Cómo cambia la app entre fuego, planta y agua?**

Los chips llaman `onSelectType`; la navegación lo conecta con `PokemonListViewModel.selectType()`. El ViewModel cambia un `StateFlow` y `flatMapLatest` observa el catálogo Room del tipo elegido. Rutas: `ui/catalog/PokemonListScreen.kt`, `ui/navigation/PokemonNavigation.kt`, `ui/viewmodel/PokemonListViewModel.kt`.

**27. ¿Por qué existe una tabla de catálogo por tipo aparte de la tabla Pokémon?**

Porque un Pokémon puede pertenecer a más de un tipo. `pokemon` guarda sus datos una vez, y `pokemon_type_catalog` asocia su ID con fuego, planta o agua. Así no se duplica la ficha principal. Ruta: `data/local/PokemonTypeCatalogEntity.kt` y consultas en `PokemonDao.kt`.

**28. ¿Qué pasa si busco un nombre que no existe en la lista actual?**

La lista filtrada queda vacía y `PokemonListScreen` muestra “No encontramos coincidencias”. Ese estado no significa que la API haya fallado; solo que el texto no coincide con Pokémon de la colección cargada. Ruta: `ui/catalog/PokemonListScreen.kt`.

**29. ¿Cómo se abre el perfil y cómo se cierra sesión?**

La navegación abre la ruta `profile`; el botón de salir llama a `AuthViewModel.signOut()`, que delega en el repositorio Firebase y devuelve el flujo a login. Rutas: `ui/navigation/PokemonNavigation.kt`, `ui/profile/ProfileScreen.kt`, `ui/viewmodel/AuthViewModel.kt`.

**30. ¿Cerrar sesión borra el catálogo o los favoritos?**

No. Cerrar sesión sale de Firebase; los datos de Pokémon siguen en Room en ese dispositivo. La limpieza local es una acción aparte del panel administrador.

**31. ¿Qué pasa al quitar de favoritos desde la lista, el detalle o Favoritos?**

Las tres pantallas llaman la misma acción del ViewModel. El valor `isFavorite` cambia en Room y los flujos actualizan las pantallas observadoras. Rutas: `ui/navigation/PokemonNavigation.kt`, `ui/viewmodel/PokemonListViewModel.kt`, `data/local/PokemonDao.kt`.

**32. ¿Qué muestra la pantalla si la lista está cargando, falla o no tiene resultados?**

Usa paneles distintos de carga, error y vacío. El error incluye un botón para reintentar. Los componentes están en `app/src/main/java/com/tecsup/pokemon/ui/components/StatePanels.kt`; se usan en `ui/catalog/PokemonListScreen.kt` y `ui/detail/PokemonDetailScreen.kt`.

**33. ¿Qué hace el botón volver del detalle?**

Ejecuta `navController.popBackStack()` para volver a la pantalla anterior. La ruta se define en `PokemonNavigation.kt` y el botón visual está en `PokemonDetailScreen.kt`.

### API, modelos y conversiones

**34. ¿Qué diferencia hay entre DTO, Entity y modelo `Pokemon`?**

El DTO representa el JSON que llega de PokeAPI; la Entity representa la fila guardada en Room; `Pokemon` es el modelo que usan dominio e interfaz. Los mappers hacen las conversiones. Rutas: `data/remote/PokemonDtos.kt`, `data/local/PokemonEntity.kt`, `domain/model/Pokemon.kt`, `data/mapper/PokemonMappers.kt`.

**35. ¿Cómo sabe la app el ID de un Pokémon de la respuesta de tipo?**

La referencia de PokeAPI incluye una URL cuyo último segmento es el ID. `PokemonReferenceDto.toEntity()` extrae ese segmento. Ruta: `data/mapper/PokemonMappers.kt`.

**36. ¿Por qué se consulta también `pokemon-species/{id}`?**

La ficha `/pokemon/{name}` trae altura, peso, imagen y tipos; la especie entrega la generación. Esa generación se usa para mostrar el juego y el anime de referencia. Rutas: `data/remote/PokemonApiService.kt`, `data/repository/PokemonRepositoryImpl.kt`.

**37. ¿Cómo se convierten altura y peso?**

PokeAPI expresa altura en decímetros y peso en hectogramos. La pantalla divide entre 10 para mostrarlos en metros y kilogramos. Ruta: `ui/detail/PokemonDetailScreen.kt`.

**38. ¿Cómo se almacena la lista de tipos en una columna Room?**

`PokemonEntity.types` es una lista; Room usa `PokemonConverters` para convertirla a texto al guardar y recuperarla como lista al leer. Ruta: `data/local/PokemonConverters.kt`, conectado con `@TypeConverters` en `PokemonDatabase.kt`.

**39. ¿Por qué se usa Retrofit si los datos terminan en Room?**

Retrofit realiza las solicitudes HTTP a PokeAPI y convierte el JSON a DTO. Después el repositorio mapea esos DTOs a Entities para guardarlos en Room. Retrofit no reemplaza la base local.

**40. ¿Qué hace `generationNumber()`?**

Convierte nombres como `generation-ix` en el número 9. `Pokemon.debut()` usa ese número para elegir el juego y la temporada de referencia. Rutas: `data/mapper/PokemonMappers.kt` y `domain/model/Pokemon.kt`.

**41. ¿Qué diferencia hay entre ID y número nacional de Pokédex?**

`id` identifica el recurso Pokémon de PokeAPI; `dexNumber` representa el número de especie de la Pokédex nacional y se obtiene de la URL de la especie. La Entity y los mappers guardan ambos. Rutas: `data/local/PokemonEntity.kt`, `data/mapper/PokemonMappers.kt`.

### Base local y sincronización

**42. ¿Qué hace `observeCatalog()`?**

Es una consulta Room que cruza `pokemon` con `pokemon_type_catalog`, filtra por el tipo elegido y emite cambios como `Flow`. Ruta: `data/local/PokemonDao.kt`.

**43. ¿Por qué Room usa `Flow`?**

Para emitir de nuevo los datos cuando cambia la base. Por ejemplo, al marcar favorito o sincronizar, el ViewModel recibe el cambio y Compose redibuja la parte necesaria.

**44. ¿Qué hace `replaceCatalog()` paso a paso?**

En una transacción obtiene las marcas de favorito existentes, reemplaza las asociaciones del tipo, inserta o actualiza las Entities conservando los favoritos y quita Pokémon no referenciados que no sean favoritos. Ruta: `data/local/PokemonDao.kt`.

**45. ¿Qué es una transacción y por qué se usa aquí?**

Agrupa varias operaciones de base de datos para que el reemplazo del catálogo no quede a medias si ocurre un error. `replaceCatalog()` está anotado con `@Transaction` en `PokemonDao.kt`.

**46. ¿Qué significan las migraciones de Room?**

Son cambios controlados al esquema de la base entre versiones para conservar los datos existentes. `PokemonDatabase` agrega columnas y la tabla de tipos de la versión 1 a la 3. Ruta: `data/local/PokemonDatabase.kt`.

**47. ¿Cómo se avisa que no hay internet?**

`AndroidNetworkMonitor` observa la conectividad y expone `isOnline` como flujo; el ViewModel actualiza `isOffline` y decide si sincronizar o mostrar los datos guardados. Rutas: `data/network/AndroidNetworkMonitor.kt`, `domain/repository/NetworkMonitor.kt`, `ui/viewmodel/PokemonListViewModel.kt`.

**48. ¿Dónde está la acción de sincronizar del administrador?**

El botón está en `ui/admin/AdminPanelScreen.kt`; llama a `AdminViewModel.synchronize()`, que pide la sincronización al repositorio y actualiza el mensaje de resultado. El repositorio llama a PokeAPI y Room.

**49. ¿Qué cuentan las métricas de administrador?**

La cantidad total de filas Pokémon guardadas y la cantidad con `isFavorite = 1`. Las consultas son `observeCount()` y `observeFavoriteCount()` en `PokemonDao`; `observeStats()` las combina en `PokemonRepositoryImpl`.

**50. ¿El administrador borra los datos de todos los usuarios?**

No. `clearCache()` elimina los datos Room del dispositivo actual. No borra datos del proyecto Firebase ni las cuentas de otras personas.

**51. ¿Qué pasa si la sincronización falla pero ya había datos?**

Los datos locales siguen siendo la fuente de la lista. Se informa el error y se puede reintentar; no se necesita reemplazar Room con una pantalla vacía.

### Firebase y seguridad

**52. ¿Qué guarda Firestore exactamente?**

El perfil del usuario bajo `usuarios/{uid}`, con `nombre` y `rol`. No guarda el catálogo ni la marca de favoritos. Rutas: `data/repository/FirebaseAuthRepository.kt`, `firestore.rules`.

**53. ¿Cómo impide la app que alguien se registre como admin?**

El registro de cliente siempre escribe `rol: "usuario"`; las reglas de Firestore permiten crear el propio perfil solo con ese rol y bloquean actualización y eliminación desde el cliente. Las cuentas admin se preparan desde Firebase Console. Rutas: `FirebaseAuthRepository.kt`, `firestore.rules`.

**54. ¿Qué pasa si Authentication crea la cuenta, pero Firestore no puede guardar el perfil?**

El repositorio intenta eliminar la cuenta recién creada, cierra la sesión y devuelve el error para no dejar al entrenador en un estado parcial. Ruta: `data/repository/FirebaseAuthRepository.kt`, función `signUp()`.

**55. ¿Cómo valida la app el correo y la contraseña al registrarse?**

La pantalla revisa campos vacíos, formato del correo, longitud mínima de seis caracteres y coincidencia de contraseñas antes de llamar al ViewModel. Firebase vuelve a validar al crear la cuenta. Rutas: `ui/login/LoginScreen.kt`, `data/repository/FirebaseAuthRepository.kt`.

**56. ¿Qué ocurre si un usuario autenticado no tiene rol en Firestore?**

La lectura de perfil falla, la sesión se cierra y se informa el error. La app no adivina ni asigna rol por correo. Ruta: `data/repository/FirebaseAuthRepository.kt`, función `toSession()`.

**57. ¿Dónde está la configuración privada de Firebase?**

`app/google-services.json` debe estar solo localmente y excluido de Git. Las reglas que se publican están en el archivo raíz `firestore.rules`. Nunca se deben exponer contraseñas de prueba.

### Android, arquitectura y pruebas

**58. ¿Quién crea Room, Retrofit y los repositorios?**

`PokemonAppContainer` reúne las dependencias y crea la base, el servicio Retrofit, repositorios, monitor de red y notificador. Ruta: `app/src/main/java/com/tecsup/pokemon/data/PokemonAppContainer.kt`.

**59. ¿Qué hace `MainActivity`?**

Es el punto de entrada Android. Crea el contenedor y muestra el tema y la navegación Compose. Ruta: `app/src/main/java/com/tecsup/pokemon/MainActivity.kt`.

**60. ¿Dónde se conectan las rutas de pantallas?**

En `PokemonNavigation.kt`: login, catálogo, detalle, favoritos, perfil y panel admin. Allí se conectan acciones de interfaz con ViewModels.

**61. ¿Por qué las pantallas no llaman directamente a PokeAPI o Room?**

Para separar presentación y datos. La UI reporta acciones; ViewModels administran estado; repositorios encapsulan acceso a fuentes. Eso facilita cambiar o probar componentes.

**62. ¿Qué significa `collectAsStateWithLifecycle()`?**

Convierte un `Flow`/`StateFlow` del ViewModel en estado que Compose puede observar, respetando el ciclo de vida de la pantalla para no recolectar cuando no está activa. Se usa en `PokemonNavigation.kt`.

**63. ¿Qué hace `PokemonPreviewViewModel`?**

Contiene datos de muestra para previsualizar pantallas. No es el repositorio real ni el origen del catálogo en ejecución; el flujo de la app usa las dependencias de `PokemonAppContainer`. Ruta: `ui/viewmodel/PokemonPreviewViewModel.kt`.

**64. ¿Qué pruebas automáticas hay?**

Hay pruebas del debut por generación, del ViewModel de autenticación y del ViewModel de catálogo. Rutas: `app/src/test/java/com/tecsup/pokemon/data/mapper/PokemonDebutTest.kt`, `ui/viewmodel/AuthViewModelTest.kt`, `ui/viewmodel/PokemonListViewModelTest.kt`.

**65. ¿Dónde se pide permiso para notificaciones?**

La navegación solicita `POST_NOTIFICATIONS` al detectar una sesión iniciada en Android 13 o posterior; el notificador revisa el permiso antes de publicar. Rutas: `ui/navigation/PokemonNavigation.kt`, `data/notification/AndroidSyncNotifier.kt`, `app/src/main/AndroidManifest.xml`.

## Mapa ampliado de rutas

Todas estas rutas son relativas a la carpeta del proyecto `C:\Users\Nikolai\StudioProjects\lab12_grupal_programacion_moviles_Rau_Suarez`.

- **Inicio y tema:** `app/src/main/java/com/tecsup/pokemon/MainActivity.kt`, `ui/theme/PokemonTheme.kt`.
- **Navegación y permisos:** `app/src/main/java/com/tecsup/pokemon/ui/navigation/PokemonNavigation.kt`, `app/src/main/AndroidManifest.xml`.
- **Login, registro y perfil:** `app/src/main/java/com/tecsup/pokemon/ui/login/LoginScreen.kt`, `ui/profile/ProfileScreen.kt`, `ui/viewmodel/AuthViewModel.kt`.
- **Firebase y roles:** `app/src/main/java/com/tecsup/pokemon/data/repository/FirebaseAuthRepository.kt`, `app/src/main/java/com/tecsup/pokemon/domain/repository/AuthRepository.kt`, `app/src/main/java/com/tecsup/pokemon/domain/model/UserSession.kt`, `firestore.rules`.
- **Catálogo y búsqueda:** `app/src/main/java/com/tecsup/pokemon/ui/catalog/PokemonListScreen.kt`, `app/src/main/java/com/tecsup/pokemon/ui/viewmodel/PokemonListViewModel.kt`.
- **Detalle y debut:** `app/src/main/java/com/tecsup/pokemon/ui/detail/PokemonDetailScreen.kt`, `app/src/main/java/com/tecsup/pokemon/domain/model/Pokemon.kt`, `app/src/main/java/com/tecsup/pokemon/data/mapper/PokemonMappers.kt`.
- **Favoritos:** `app/src/main/java/com/tecsup/pokemon/ui/favorites/FavoritesScreen.kt`, `PokemonListViewModel.kt`, `app/src/main/java/com/tecsup/pokemon/data/local/PokemonDao.kt`, `PokemonEntity.kt`.
- **API y JSON:** `app/src/main/java/com/tecsup/pokemon/data/remote/PokemonApiService.kt`, `PokemonDtos.kt`.
- **Room y conversiones:** `app/src/main/java/com/tecsup/pokemon/data/local/PokemonDatabase.kt`, `PokemonDao.kt`, `PokemonEntity.kt`, `PokemonTypeCatalogEntity.kt`, `PokemonConverters.kt`.
- **Sincronización y datos:** `app/src/main/java/com/tecsup/pokemon/data/repository/PokemonRepositoryImpl.kt`, `app/src/main/java/com/tecsup/pokemon/domain/repository/PokemonRepository.kt`.
- **Panel administrador:** `app/src/main/java/com/tecsup/pokemon/ui/admin/AdminPanelScreen.kt`, `app/src/main/java/com/tecsup/pokemon/ui/viewmodel/AdminViewModel.kt`.
- **Estados visuales e imágenes:** `app/src/main/java/com/tecsup/pokemon/ui/components/StatePanels.kt`, `PokemonArtwork.kt`, portada en `app/src/main/res/drawable-nodpi/pokemon_starters_banner.png`.
- **Red y notificaciones:** `app/src/main/java/com/tecsup/pokemon/data/network/AndroidNetworkMonitor.kt`, `app/src/main/java/com/tecsup/pokemon/domain/repository/NetworkMonitor.kt`, `app/src/main/java/com/tecsup/pokemon/data/notification/AndroidSyncNotifier.kt`, `app/src/main/java/com/tecsup/pokemon/domain/repository/SyncNotifier.kt`.
- **Pruebas:** `app/src/test/java/com/tecsup/pokemon/data/mapper/PokemonDebutTest.kt`, `app/src/test/java/com/tecsup/pokemon/ui/viewmodel/AuthViewModelTest.kt`, `PokemonListViewModelTest.kt`.
- **Dependencias y compilación:** `app/build.gradle.kts`, `build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`.