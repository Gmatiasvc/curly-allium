package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import objects.Route;
import objects.Stop;
import objects.User;

public class DatabaseWrapper {

    private final int permissionLevel;
    private final java.sql.Connection conn;

    public DatabaseWrapper(int permissionLevel, java.sql.Connection conn) {
        this.permissionLevel = permissionLevel;
        this.conn = conn;
    }

    // =========================================================================
    // 1xx: LOGIN Y CONEXIONES
    // =========================================================================

    public boolean registerUser(String nombre, String usuario, String correo, String password) {
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(password, salt);

        String sql = "INSERT INTO usuario (nombre, nombre_usuario, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, usuario);
            stmt.setString(3, correo);
            stmt.setString(4, secureHash);
            stmt.setString(5, salt);
            stmt.setBoolean(6, true);    
            stmt.setLong(7, System.currentTimeMillis());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    public boolean registerAdmin(String nombre, String correo, String password) {
        if (this.permissionLevel < 0) return false; // Solo admins pueden crear admins, supongo
        
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(password, salt);

        String sql = "INSERT INTO administrador (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, secureHash);
            stmt.setString(4, salt);
            stmt.setBoolean(5, true);    
            stmt.setLong(6, System.currentTimeMillis());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    public boolean loginUser(String usuario, String password) {
        String sql = "SELECT contraseña, salt FROM usuario WHERE nombre_usuario = ? AND estado = true";
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contraseña");
                    String salt = rs.getString("salt");
                    return common.PasswordUtils.verifyPassword(password, storedHash, salt);
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    public boolean loginAdmin(String correo, String password) {
        // CORRECCION: Consultaba tabla 'usuario' en lugar de 'administrador'
        String sql = "SELECT contraseña, salt FROM administrador WHERE correo = ? AND estado = true";
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contraseña");
                    String salt = rs.getString("salt");
                    return common.PasswordUtils.verifyPassword(password, storedHash, salt);
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // =========================================================================
    // 2xx: REQUESTS USER
    // =========================================================================

    // 200: Datos Usuario
    public User getUserData(String username) {
        String sql = "SELECT nombre, correo, estado, fecha_registro FROM usuario WHERE nombre_usuario = ?";
        if (this.conn == null) return null;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBoolean("estado"),
                        String.valueOf(rs.getLong("fecha_registro")), // Convertir long a String para el objeto User
                        false // No es admin si viene de tabla usuario
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
        }
        return null;
    }

    // 202: Añadir amigo
    public boolean addFriend(String username, String friendUsername) {
        int userId = getUserId(username);
        int friendId = getUserId(friendUsername);
        
        if (userId == -1 || friendId == -1) return false;

        String sql = "INSERT INTO amigo (id_usuario, id_amigo) VALUES (?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 203: Eliminar amigo
    public boolean removeFriend(String username, String friendUsername) {
        int userId = getUserId(username);
        int friendId = getUserId(friendUsername);

        if (userId == -1 || friendId == -1) return false;

        String sql = "DELETE FROM amigo WHERE id_usuario = ? AND id_amigo = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 205: Registrar viaje
    public boolean registerTrip(String username, int origenId, int destinoId, double precio, int duracion, double distancia) {
        int userId = getUserId(username);
        if (userId == -1) return false;

        String sql = "INSERT INTO viaje (id_usuario, origen, destino, fecha, precio, duración, distancia) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, origenId);
            stmt.setInt(3, destinoId);
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setDouble(5, precio);
            stmt.setInt(6, duracion);
            stmt.setDouble(7, distancia);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 206: Cambio contraseña
    public boolean changePassword(String username, String newPassword) {
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(newPassword, salt);

        String sql = "UPDATE usuario SET contraseña = ?, salt = ? WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, secureHash);
            stmt.setString(2, salt);
            stmt.setString(3, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 207: Cambio otros datos (Perfil)
    public boolean updateUserProfile(String currentUsername, String newName, String newEmail) {
        String sql = "UPDATE usuario SET nombre = ?, correo = ? WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setString(3, currentUsername);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // =========================================================================
    // 5xx: REQUESTS ADMIN
    // =========================================================================

    // 500: Añadir paradero
    public boolean addStop(String nombre, String distrito, String direccion, double latitud, double longitud) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Estaba insertando en 'usuario' en vez de 'paradero'
        String sql = "INSERT INTO paradero (nombre, distrito, dirección, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, distrito);
            stmt.setString(3, direccion);
            stmt.setDouble(4, latitud);
            stmt.setDouble(5, longitud);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 501: Eliminar paradero
    public boolean removeStop(int stopId) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Nombre de tabla 'paradero' y columna 'id_paradero'
        String sql = "DELETE FROM paradero WHERE id_paradero = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, stopId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 502: Añadir ruta
    public boolean addRoute(int origen, int destino, int tiempo, double distancia, boolean estado) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Nombres de columnas según DB.sql son 'origen' y 'destino'
        String sql = "INSERT INTO ruta (origen, destino, tiempo, distancia, estado) VALUES (?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, origen);
            stmt.setInt(2, destino);
            stmt.setInt(3, tiempo);
            stmt.setDouble(4, distancia);
            stmt.setBoolean(5, estado);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 503: Eliminar ruta
    public boolean removeRoute(int origen, int destino) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Columnas 'origen' y 'destino'
        String sql = "DELETE FROM ruta WHERE origen = ? AND destino = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, origen);
            stmt.setInt(2, destino);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 504: Modificar paradero
    public boolean modifyStop(int stopId, String nombre, String distrito, String direccion, double latitud, double longitud) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Tabla 'paradero' e 'id_paradero'
        String sql = "UPDATE paradero SET nombre = ?, distrito = ?, dirección = ?, latitud = ?, longitud = ? WHERE id_paradero = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, distrito);
            stmt.setString(3, direccion);
            stmt.setDouble(4, latitud);
            stmt.setDouble(5, longitud);
            stmt.setInt(6, stopId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 505: Modificar ruta
    public boolean modifyRoute(int origen, int destino, int tiempo, double distancia, boolean estado) {
        if (this.permissionLevel < 0) return false;

        // CORRECCION: Columnas 'origen' y 'destino' y columna 'tiempo' en vez de 'tiempo_estimado' (verificando DB.sql)
        // Nota: DB.sql dice `tiempo` INT NOT NULL.
        String sql = "UPDATE ruta SET tiempo = ?, distancia = ?, estado = ? WHERE origen = ? AND destino = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, tiempo);
            stmt.setDouble(2, distancia);
            stmt.setBoolean(3, estado);
            stmt.setInt(4, origen);
            stmt.setInt(5, destino);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 508: Suspender usuario
    public boolean suspendUser(String username) {
        if (this.permissionLevel < 0) return false;

        String sql = "UPDATE usuario SET estado = false WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
            return false;
        }
    }

    // 506 / 200 Helper: Obtener todos los paraderos (útil para mapas)
    public List<Stop> getAllStops() {
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT nombre, distrito, dirección, latitud, longitud FROM paradero";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                stops.add(new Stop(
                    rs.getString("nombre"),
                    rs.getString("distrito") + ", " + rs.getString("dirección"), // Location combinada
                    String.valueOf(rs.getDouble("latitud")),
                    String.valueOf(rs.getDouble("longitud"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
        }
        return stops;
    }

    // Helper: Obtener rutas para analítica o mapas
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        // Un join para obtener nombres de paraderos sería ideal aquí, pero mantendré IDs por simplicidad o nombres si existen
        String sql = "SELECT p1.nombre as origen, p2.nombre as destino, r.distancia, r.tiempo " +
                     "FROM ruta r " +
                     "JOIN paradero p1 ON r.origen = p1.id_paradero " +
                     "JOIN paradero p2 ON r.destino = p2.id_paradero " +
                     "WHERE r.estado = true";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                routes.add(new Route(
                    rs.getString("origen"),
                    rs.getString("destino"),
                    (int) rs.getDouble("distancia"), // Route objeto usa int distance? Ajustar si necesario.
                    rs.getInt("tiempo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
        }
        return routes;
    }

    // =========================================================================
    // UTILS / HELPERS
    // =========================================================================

    public ArrayList<String> getUserHistory(int userId) {
        // TODO: Implementar consulta a tabla historial_busqueda o viaje
        if (this.permissionLevel < 0) {
            return null;
        }
        return new ArrayList<>();
    }

    /**
     * Helper privado para obtener ID de usuario dado su nombre de usuario.
     */
    private int getUserId(String username) {
        String sql = "SELECT id_usuario FROM usuario WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // TODO: Logger
        }
        return -1;
    }
}