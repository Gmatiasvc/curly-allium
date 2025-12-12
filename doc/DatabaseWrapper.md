# Documentación de DatabaseWrapper

La clase `DatabaseWrapper` actúa como la capa de acceso a datos (DAL) para la aplicación TravelPlus. Encapsula todas las consultas JDBC y la lógica de seguridad (hashing de contraseñas) para interactuar con la base de datos MySQL.

## Instanciación

```
DatabaseWrapper db = new DatabaseWrapper(int permissionLevel, Connection conn);

```

* **permissionLevel**: Un entero que representa los privilegios de la sesión actual; `0` para Usuario y `1` para Administrador.

* **conn**: La conexión activa `java.sql.Connection`.

## 1xx: Login y Conexiones

Estas llamadas gestionan la autenticación y el registro de nuevas cuentas.

### `loginUser`

* **Código/ID:** 100

* **Descripción:** Verifica las credenciales de un usuario estándar. Comprueba si el usuario existe, si su estado es activo (`true`) y si el hash de la contraseña coincide.

* **Permisos:** Público (Cualquier nivel).

* **Uso:** `boolean success = db.loginUser("usuario123", "contraseñaPlana");`

* **Interpretación:**

  * `true`: Credenciales correctas y cuenta activa.

  * `false`: Usuario no encontrado, contraseña incorrecta, cuenta suspendida o error de base de datos.

### `loginAdmin`

* **Código/ID:** 101

* **Descripción:** Similar a `loginUser`, pero consulta la tabla `administrador` y utiliza el correo electrónico como identificador.

* **Permisos:** Público (Cualquier nivel).

* **Uso:** `boolean` success` = db.loginAdmin("admin@travelplus.com", "contraseñaPlana");`

* **Interpretación:**

  * `true`: Acceso de administrador concedido.

  * `false`: Credenciales inválidas.

### `registerUser`

* **Código/ID:** 104

* **Descripción:** Crea un nuevo registro en la tabla `usuario`. Genera automáticamente un *salt* único y hashea la contraseña antes de guardarla.

* **Permisos:** Público.

* **Uso:** `boolean` success = db.registerUser("Nombre` Real", "usuario", "email@test.com", "pass");`

* **Interpretación:**

  * `true`: Usuario registrado exitosamente.

  * `false`: Fallo en el registro (probablemente el nombre de usuario o correo ya existen).

### `registerAdmin`

* **Código/ID:** 105

* **Descripción:** Crea un nuevo administrador. Requiere que la sesión actual tenga permisos elevados.

* **Permisos:** Requiere `permissionLevel >= 0` (Autenticado).

* **Uso:** `boolean success = db.registerAdmin("Nombre Admin", "admin@test.com", "pass");`

* **Interpretación:**

  * `true`: Administrador creado.

  * `false`: Permisos insuficientes o duplicidad de correo.

## 2xx: Peticiones de Usuario (User Requests)

Funcionalidades disponibles para usuarios autenticados.

### `getUserData`

* **Código/ID:** 200

* **Descripción:** Recupera la información del perfil público de un usuario.

* **Permisos:** Público/Usuario.

* **Uso:** `User user = db.getUserData("nombreUsuario");`

* **Interpretación:** Devuelve un objeto `User` si se encuentra; `null` si no existe o hay error.

### `addFriend`

* **Código/ID:** 202

* **Descripción:** Crea una relación en la tabla `amigo` entre el usuario actual y otro usuario objetivo.

* **Permisos:** Usuario.

* **Uso:** `boolean success = db.addFriend("miUsuario", "usuarioAmigo");`

* **Interpretación:** `true` si se agregó el amigo correctamente.

### `removeFriend`

* **Código/ID:** 203

* **Descripción:** Elimina la relación de amistad en la tabla `amigo`.

* **Permisos:** Usuario.

* **Uso:** `boolean` success = db.removeFriend("miUsuario", "usuarioAmigo");

* **Interpretación:** `true` si se eliminó correctamente.

### `registerTrip`

* **Código/ID:** 205

* **Descripción:** Guarda un registro de un viaje realizado en la tabla `viaje`.

* **Permisos:** Usuario.

* **Uso:** `db.registerTrip("usuario", idOrigen, idDestino, precio, duracion, distancia);`

* **Interpretación:** `true` si el viaje se guardó en el historial.

### `changePassword`

* **Código/ID:** 206

* **Descripción:** Genera un nuevo salt y hash para la nueva contraseña y actualiza el registro del usuario.

* **Permisos:** Usuario (debe conocer su usuario).

* **Uso:** `boolean success = db.changePassword("usuario", "nuevaPass");`

* **Interpretación:** `true` si la contraseña se actualizó.

### `updateUserProfile`

* **Código/ID:** 207

* **Descripción:** Actualiza el nombre real y el correo electrónico del usuario.

* **Permisos:** Usuario.

* **Uso:** `boolean success = db.updateUserProfile("usuarioActual", "Nuevo Nombre", "nuevo@email.com");`

* **Interpretación:** `true` si los datos se actualizaron correctamente.

## 5xx: Peticiones de Administrador (Admin Requests)

Estas operaciones modifican la estructura de la red de transporte o el estado de los usuarios. Todas requieren `permissionLevel >= 0` (idealmente validado como nivel Admin en la lógica de negocio).

### `addStop`

* **Código/ID:** 500

* **Descripción:** Inserta un nuevo registro en la tabla `paradero`.

* **Permisos:** Admin.

* **Uso:** `db.addStop("Central", "Lima", "Av. Arequipa 123", -12.04, -77.03);`

* **Interpretación:** `true` si el paradero se creó.

### `removeStop`

* **Código/ID:** 501

* **Descripción:** Elimina un paradero dado su ID. **Nota:** Incluye una verificación de seguridad; no eliminará el paradero si este está siendo usado en rutas, viajes o historial.

* **Permisos:** Admin.

* **Uso:** `boolean success = db.removeStop(15);`

* **Interpretación:**

  * `true`: Paradero eliminado.

  * `false`: Error SQL o el paradero está en uso (violación de integridad referencial prevenida).

### `addRoute`

* **Código/ID:** 502

* **Descripción:** Crea una conexión dirigida entre dos paraderos existentes en la tabla `ruta`.

* **Permisos:** Admin.

* **Uso:** `db.addRoute(idOrigen, idDestino, tiempoMinutos, distanciaKm, true);`

* **Interpretación:** `true` si la ruta se creó.

### `removeRoute`

* **Código/ID:** 503

* **Descripción:** Elimina una ruta específica entre dos puntos.

* **Permisos:** Admin.

* **Uso:** `boolean success = db.removeRoute(idOrigen, idDestino);`

* **Interpretación:** `true` si la ruta fue eliminada.

### `modifyStop`

* **Código/ID:** 504

* **Descripción:** Actualiza todos los campos de un paradero existente.

* **Permisos:** Admin.

* **Uso:** `db.modifyStop(id, "Nuevo Nombre", "Distrito", "Dir", lat, lon);`

* **Interpretación:** `true` si la actualización fue exitosa.

### `modifyRoute`

* **Código/ID:** 505

* **Descripción:** Actualiza el tiempo, distancia y estado de una ruta existente.

* **Permisos:** Admin.

* **Uso:** `db.modifyRoute(idOrigen, idDestino, nuevoTiempo, nuevaDistancia, estado);`

* **Interpretación:** `true` si la ruta se actualizó.

### `suspendUser`

* **Código/ID:** 508

* **Descripción:** Cambia el campo `estado` de un usuario a `false` (bloqueado/inactivo).

* **Permisos:** Admin.

* **Uso:** `boolean success = db.suspendUser("usuarioMalicioso");`

* **Interpretación:** `true` si el usuario fue suspendido.

## Métodos Auxiliares (Helpers)

Estos métodos se utilizan para poblar interfaces gráficas, generar reportes o como utilidades internas.

### `getAllStops`

* **Categoría:** Datos generales (utilizado para mapas o listas).

* **Descripción:** Devuelve una lista de todos los objetos `Stop` en la base de datos.

* **Retorno:** `List<Stop>`.

### `getAllRoutes`

* **Categoría:** Datos generales (utilizado para grafos).

* **Descripción:** Devuelve una lista de todos los objetos `Route` activos.

* **Retorno:** `List<Route>`.

### `getUserHistory`

* **Categoría:** Datos de usuario.

* **Descripción:** Recupera el historial de búsquedas del usuario desde `historial_busqueda`. Realiza un JOIN para obtener los nombres legibles de los paraderos.

* **Permisos:** Requiere `permissionLevel >= 0`.

* **Retorno:** `ArrayList<String>` con desc