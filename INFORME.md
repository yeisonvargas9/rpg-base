# Informe: Arena Segura (JDBC + MVC)

## 1. Flujo del login

```
LoginUI (View) -> LoginController -> JugadorDAO -> BD (rpg_arena_db)
      ^                                                  |
      +------------------ Resultado <-------------------+
```

- La View pide usuario y clave con Consola y llama al Controller.
- El Controller valida los datos y cuenta los intentos (maximo 3).
- El DAO ejecuta el SELECT con PreparedStatement y devuelve un Jugador o null.
- La respuesta vuelve en sentido contrario como un Resultado, sin exponer excepciones ni JDBC a la View.

## 2. Statement vs PreparedStatement

| Aspecto | Statement | PreparedStatement |
|---|---|---|
| Seguridad | Vulnerable: `admin' OR 1=1 -- ` entro sin conocer la clave | Seguro: la entrada viaja como dato, no como SQL |
| Reutilizacion | Se arma un texto SQL nuevo en cada ejecucion | Una sola consulta con `?`; solo cambian los valores |

**Tiempos de 1000 inserciones:**

| Prueba | Statement | PreparedStatement |
|---|---|---|
| 1 | 1373 ms | 1362 ms |
| 2 | 1345 ms | 1286 ms |
| 3 | 1357 ms | 1261 ms |
| Promedio | 1358 ms | 1303 ms |

PreparedStatement fue algo mas rapido en las 3 pruebas (unos 55 ms de promedio, cerca de un 4 %), pero la diferencia es pequena. El driver de MySQL prepara las consultas en el cliente por defecto, asi que la ventaja real aqui es la seguridad, no la velocidad.

**Evidencia de la inyeccion SQL** (salida de `LoginInseguroDemo`):

 " === LOGIN INSEGURO (Statement) ===
SQL generado: SELECT * FROM jugadores WHERE usuario = 'admin' OR 1=1 -- ' AND clave = 'cualquiera'
Resultado: ENTRO SIN CONOCER LA CLAVE

=== LOGIN SEGURO (PreparedStatement) ===
Resultado: rechazado "

**Por que falla la inyeccion con PreparedStatement:** la consulta se envia con su estructura ya fijada (`usuario = ? AND clave = ?`) y los valores viajan aparte, como datos. La base de datos nunca interpreta lo escrito como SQL, asi que busca un usuario llamado literalmente `admin' OR 1=1 -- ` y no existe.

## 3. executeQuery vs executeUpdate

| Metodo | Se usa con | Devuelve |
|---|---|---|
| `executeQuery` | SELECT | Un `ResultSet` con las filas |
| `executeUpdate` | INSERT, UPDATE, DELETE | Un `int` con las filas afectadas |

## 4. Error encontrado y como lo resolvi

**ERROR: "No se pudo conectar a la base de datos: No suitable driver found for jdbc:mysql://localhost:3306/rpg_arena_db"**

**SOLUCION: Agregar la dependencia al pom.xml y recargar Maven.**