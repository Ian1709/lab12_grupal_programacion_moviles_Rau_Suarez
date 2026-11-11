# Bitácora de mejora con IA

## Consulta 1
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Revisar la guía del laboratorio y comparar los requisitos faltantes con el estado actual del repositorio.
- Qué respondió (resumen): Identificó pruebas unitarias, documentación de Fase 2, permiso de notificaciones, configuración de Firebase y entregables de GitHub pendientes.
- Qué usamos/cambiamos y por qué: Se creó esta rama de mejora y se priorizaron pruebas automatizadas, permisos y documentación verificable.
- Qué aprendimos: Un inventario de archivos no demuestra que una función esté configurada en Firebase o probada en un dispositivo.

## Consulta 2
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Añadir al detalle de cada Pokémon el juego de debut de su generación y una referencia a la temporada de anime correspondiente.
- Qué respondió (resumen): Propuso obtener la generación desde el recurso `pokemon-species` de PokeAPI y separar esa información del modelo de interfaz.
- Qué usamos/cambiamos y por qué: Se agregó el DTO y endpoint de especie, el campo de generación en Room y un mapper hacia el modelo Pokémon.
- Qué aprendimos: El endpoint `/pokemon/{nombre}` no incluye directamente la generación de la especie.

## Consulta 3
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex con documentación oficial de PokeAPI
- Prompt: Confirmar qué campo expone PokeAPI para saber en qué generación se introdujo una especie.
- Qué respondió (resumen): La documentación describe `generation` en el recurso PokemonSpecies.
- Qué usamos/cambiamos y por qué: Se añadió la consulta `/pokemon-species/{id}` y se guardó la generación con una migración Room.
- Qué aprendimos: La generación identifica el debut generacional, pero no garantiza la fecha exacta de la primera aparición individual en el anime.

## Consulta 4
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Mostrar el juego de debut y la temporada asociada cuando se abre la ficha de un Pokémon.
- Qué respondió (resumen): Recomendó mostrar los datos en secciones legibles dentro de una ficha desplazable.
- Qué usamos/cambiamos y por qué: Se incorporaron tarjetas de debut a la pantalla de detalle, identificadas como referencia de generación.
- Qué aprendimos: Conviene etiquetar con claridad los datos generacionales para no presentarlos como una fecha exacta de episodio.

## Consulta 5
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Crear un perfil para cada rol, con acceso administrativo solo cuando Firestore indique `admin`.
- Qué respondió (resumen): Propuso reutilizar una pantalla de perfil que cambia el contenido según la sesión autenticada.
- Qué usamos/cambiamos y por qué: Se añadió perfil con correo, rol, identificador abreviado y acceso al panel únicamente para administración.
- Qué aprendimos: Ocultar un botón mejora la navegación, pero la ruta admin también debe comprobar el rol.

## Consulta 6
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Mejorar visualmente el catálogo y mantener la búsqueda, imágenes, favoritos y estados de carga.
- Qué respondió (resumen): Sugirió una cabecera visual, jerarquía tipográfica y tarjetas con ilustración y datos compactos.
- Qué usamos/cambiamos y por qué: Se rediseñó el encabezado y las tarjetas; la imagen sigue procediendo de la ilustración oficial de PokeAPI.
- Qué aprendimos: El identificador y la generación sirven como contexto visual y mantienen la lista fácil de recorrer.

## Consulta 7
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Corregir la notificación local para que diga cuántos elementos se guardaron al sincronizar.
- Qué respondió (resumen): Hizo que el repositorio devuelva el total sincronizado y que la interfaz de notificación reciba esa cantidad.
- Qué usamos/cambiamos y por qué: La sincronización ahora informa el conteo en el panel y en la notificación.
- Qué aprendimos: Devolver un resultado desde el repositorio evita recalcular el conteo dentro de la vista.

## Consulta 8
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Solicitar el permiso de notificaciones de forma contextual y permitir el uso normal si se rechaza.
- Qué respondió (resumen): Propuso pedir el permiso después de iniciar sesión y comprobarlo antes de publicar una notificación.
- Qué usamos/cambiamos y por qué: Se agregó la solicitud para Android 13 o posterior y la sincronización no depende de que se conceda.
- Qué aprendimos: Los permisos son opcionales; la app debe gestionar la negativa como un estado normal.

## Consulta 9
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Evitar que una lista vacía aparezca antes de que termine la primera sincronización y cumplir el resultado de limpieza de caché.
- Qué respondió (resumen): Separó carga inicial de estado vacío y señaló que la prueba de aceptación espera limpiar también favoritos locales.
- Qué usamos/cambiamos y por qué: El ViewModel conserva Cargando hasta el primer resultado; limpiar caché confirma la acción y borra los datos locales.
- Qué aprendimos: Una lista vacía al inicio puede ser un estado transitorio y no debe confundirse con una descarga terminada.

## Consulta 10
- Fecha: 2026-11-11
- Herramienta: OpenAI Codex
- Prompt: Añadir pruebas del ViewModel y de los mappers de debut sin usar Firebase ni la red real.
- Qué respondió (resumen): Recomendó un repositorio falso, un monitor de red falso y pruebas locales de conversión.
- Qué usamos/cambiamos y por qué: Se agregaron pruebas del catálogo, favoritos, error sin conexión y conversión de generaciones.
- Qué aprendimos: Las interfaces permiten verificar estados y reglas de datos sin depender de servicios externos.
