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

    public DatabaseWrapper(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        this.conn = Connection.getInstance();
    }

    
    // 1xx: LOGIN Y CONEXIONES

    // 100: Login Usuario
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
            e.printStackTrace(); 
            return false;
        }
    }
    
    // 101: Login Admin
    public boolean loginAdmin(String correo, String password) {
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
            e.printStackTrace(); 
            return false;
        }
    }

    // 104: Registrar Usuario
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
            e.printStackTrace(); 
            return false;
        }
    }

    // 105: Registrar Admin
    public boolean registerAdmin(String nombre, String correo, String password) {
        if (this.permissionLevel < 0) return false; 
        
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
            e.printStackTrace(); 
            return false;
        }
    }



    // 2xx: REQUESTS USER

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
                        String.valueOf(rs.getLong("fecha_registro")), 
                        false 
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
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
            e.printStackTrace(); 
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
            e.printStackTrace(); 
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
            e.printStackTrace(); 
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
            e.printStackTrace(); 
            return false;
        }
    }

    // 207: Cambio otros datos 
    public boolean updateUserProfile(String currentUsername, String newName, String newEmail) {
        String sql = "UPDATE usuario SET nombre = ?, correo = ? WHERE nombre_usuario = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setString(3, currentUsername);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }


    
    // 5xx: REQUESTS ADMIN
    

    // 500: Añadir paradero
    public boolean addStop(String nombre, String distrito, String direccion, double latitud, double longitud) {
        if (this.permissionLevel < 0) return false;

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
            e.printStackTrace(); 
            return false;
        }
    }

    // 501: Eliminar paradero (CORREGIDO: Integridad referencial)
    public boolean removeStop(int stopId) {
        if (this.permissionLevel < 0) return false;
        if (this.conn == null) return false;

        if (isStopInUse(stopId)) {
            System.err.println("No se puede eliminar el paradero " + stopId + ": está en uso por rutas o viajes.");
            return false;
        }

        String sql = "DELETE FROM paradero WHERE id_paradero = ?";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, stopId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    private boolean isStopInUse(int stopId) {
        String[] checks = {
            "SELECT 1 FROM ruta WHERE origen = ? OR destino = ?",
            "SELECT 1 FROM viaje WHERE origen = ? OR destino = ?",
            "SELECT 1 FROM historial_busqueda WHERE origen = ? OR destino = ?"
        };

        try {
            for (String sql : checks) {
                try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
                    stmt.setInt(1, stopId);
                    stmt.setInt(2, stopId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) return true; 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; 
        }
        return false;
    }

    // 502: Añadir ruta
    public boolean addRoute(int origen, int destino, int tiempo, double distancia, boolean estado) {
        if (this.permissionLevel < 0) return false;

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
            e.printStackTrace(); 
            return false;
        }
    }

    // 503: Eliminar ruta
    public boolean removeRoute(int origen, int destino) {
        if (this.permissionLevel < 0) return false;

        String sql = "DELETE FROM ruta WHERE origen = ? AND destino = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, origen);
            stmt.setInt(2, destino);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    // 504: Modificar paradero
    public boolean modifyStop(int stopId, String nombre, String distrito, String direccion, double latitud, double longitud) {
        if (this.permissionLevel < 0) return false;

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
            e.printStackTrace(); 
            return false;
        }
    }

    // 505: Modificar ruta
    public boolean modifyRoute(int origen, int destino, int tiempo, double distancia, boolean estado) {
        if (this.permissionLevel < 0) return false;

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
            e.printStackTrace(); 
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
            e.printStackTrace(); 
            return false;
        }
    }

    // 506 / 200 Helper: Obtener todos los paraderos 
    public List<Stop> getAllStops() {
        List<Stop> stops = new ArrayList<>();
        String sql = "SELECT nombre, distrito, dirección, latitud, longitud FROM paradero";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                stops.add(new Stop(
                    rs.getString("nombre"),
                    rs.getString("dirección"),
                    rs.getString("distrito"), 
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return stops;
    }

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT p1.nombre as origen, p2.nombre as destino, r.distancia, r.tiempo " +
                     "FROM ruta r " +
                     "JOIN paradero p1 ON r.origen = p1.id_paradero " +
                     "JOIN paradero p2 ON r.destino = p2.id_paradero " +
                     "WHERE r.estado = true";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                double dist = rs.getDouble("distancia");
                
                routes.add(new Route(
                    rs.getString("origen"),
                    rs.getString("destino"),
                    dist,
                    rs.getInt("tiempo")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return routes;
    }

    public ArrayList<String> getUserHistory(int userId) {
        ArrayList<String> history = new ArrayList<>();
        
        if (this.permissionLevel < 0) {
            return null;
        }

        String sql = "SELECT p1.nombre as origen, p2.nombre as destino, h.fecha " +
                     "FROM historial_busqueda h " +
                     "LEFT JOIN paradero p1 ON h.origen = p1.id_paradero " +
                     "LEFT JOIN paradero p2 ON h.destino = p2.id_paradero " +
                     "WHERE h.id_usuario = ? " +
                     "ORDER BY h.fecha DESC";

        if (this.conn == null) return history;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String origen = rs.getString("origen");
                    String destino = rs.getString("destino");
                    long fecha = rs.getLong("fecha");
                    
                    if (origen == null) origen = "Desconocido";
                    if (destino == null) destino = "Desconocido";

                    String fechaStr = new java.util.Date(fecha).toString();
                    
                    history.add("Búsqueda: " + origen + " -> " + destino + " [" + fechaStr + "]");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        
        return history;
    }


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
            e.printStackTrace(); 
        }
        return -1;
    }
}