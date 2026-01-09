package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import objects.Route;
import objects.Stop;
import objects.User;

public class DatabaseWrapper {

    private final int permissionLevel; // 0: User, 1: Conductor, 2: Admin
    private final java.sql.Connection conn;

    public DatabaseWrapper(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        this.conn = Connection.getInstance();
    }

    // --- 1xx: AUTENTICACIÓN Y REGISTRO ---

    // 100: Login Genérico (Retorna User object con rol)
    public User login(String identifier, String password) {
        // Primero intentar como Usuario (Pasajero o Conductor)
        String sqlUser = "SELECT nombre, correo, contraseña, salt, estado, fecha_registro, es_conductor FROM usuario WHERE nombre = ? OR correo = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sqlUser)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contraseña");
                    String salt = rs.getString("salt");
                    if (common.PasswordUtils.verifyPassword(password, storedHash, salt)) {
                        return new User(
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getBoolean("estado"),
                            String.valueOf(rs.getLong("fecha_registro")),
                            false, // IsAdmin
                            rs.getBoolean("es_conductor") // IsDriver
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si falla, intentar como Administrador
        String sqlAdmin = "SELECT nombre, correo, contraseña, salt, estado, fecha_registro FROM administrador WHERE nombre = ? OR correo = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sqlAdmin)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contraseña");
                    String salt = rs.getString("salt");
                    if (common.PasswordUtils.verifyPassword(password, storedHash, salt)) {
                        return new User(
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getBoolean("estado"),
                            String.valueOf(rs.getLong("fecha_registro")),
                            true, // IsAdmin
                            false // IsDriver
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 104: Registro Pasajero
    public boolean registerUser(String nombre, String correo, String password) {
        return registerGenericUser(nombre, correo, password, false);
    }

    // 106: Registro Conductor
    public boolean registerConductor(String nombre, String correo, String password) {
        return registerGenericUser(nombre, correo, password, true);
    }

    private boolean registerGenericUser(String nombre, String correo, String password, boolean isDriver) {
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(password, salt);
        // Assuming table 'usuario' has 'es_conductor' column
        String sql = "INSERT INTO usuario (nombre, correo, contraseña, salt, estado, fecha_registro, es_conductor) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, secureHash);
            stmt.setString(4, salt);
            stmt.setBoolean(5, true);    
            stmt.setLong(6, System.currentTimeMillis());
            stmt.setBoolean(7, isDriver);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    // 105: Registro Administrador (Requiere Permiso 2)
    public boolean registerAdmin(String nombre, String correo, String password) {
        if (this.permissionLevel < 2) return false;
        
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(password, salt);

        String sql = "INSERT INTO administrador (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, secureHash);
            stmt.setString(4, salt);
            stmt.setBoolean(5, true);    
            stmt.setLong(6, System.currentTimeMillis());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

    // --- 2xx: CLIENTE / PASAJERO ---

    // 200: Datos Usuario
    public User getUserData(String username) {
        // Reuse logic or query specific table based on known context
        // Simplified for 'usuario' table primarily
        String sql = "SELECT nombre, correo, estado, fecha_registro, es_conductor FROM usuario WHERE nombre = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBoolean("estado"),
                        String.valueOf(rs.getLong("fecha_registro")),
                        false,
                        rs.getBoolean("es_conductor")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 201: Obtener Mapa - Paraderos
    public List<Stop> getAllStops() {
        List<Stop> stops = new ArrayList<>();
        // Assuming table 'paradero'
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

    // 202: Obtener Mapa - Rutas
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        // Assuming table 'ruta' with joins for names if needed, or just IDs. 
        // Objects/Route.java uses names for origin/dest, but DB uses IDs.
        // We will fetch names to match the object.
        String sql = "SELECT p1.nombre as origen, p2.nombre as destino, r.distancia, r.tiempo, r.estado " +
                     "FROM ruta r " +
                     "JOIN paradero p1 ON r.origen = p1.id_paradero " +
                     "JOIN paradero p2 ON r.destino = p2.id_paradero " +
                     "WHERE r.estado = true";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Route(String origin, String destiny, double distance, int duration)
                routes.add(new Route(
                    rs.getString("origen"),
                    rs.getString("destino"),
                    rs.getDouble("distancia"),
                    rs.getInt("tiempo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return routes;
    }

    // 205: Solicitar Viaje
    public int requestRide(String username, int originId, int destId, double price, double distance) {
        int userId = getUserId(username);
        if (userId == -1) return -1;

        String sql = "INSERT INTO viaje (id_usuario, origen, destino, fecha, precio, distancia, estado) VALUES (?, ?, ?, ?, ?, ?, 'PENDIENTE')";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, originId);
            stmt.setInt(3, destId);
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setDouble(5, price);
            stmt.setDouble(6, distance);
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return -1;
    }

    // 206: Consultar Estado de Viaje
    public String getRideStatus(int rideId) {
        String sql = "SELECT estado FROM viaje WHERE id_viaje = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("estado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 207: Actualizar Perfil
    public boolean updateProfile(String username, String newEmail, String newName) {
        String sql = "UPDATE usuario SET correo = ?, nombre = ? WHERE nombre = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, newName);
            stmt.setString(3, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 208: Cancelar Viaje
    public boolean cancelRide(int rideId, String username) {
        int userId = getUserId(username);
        // Ensure user owns the ride and it's pending
        String sql = "UPDATE viaje SET estado = 'CANCELADO' WHERE id_viaje = ? AND id_usuario = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 209: Historial Pasajero
    public List<String> getPassengerHistory(String username) {
        int userId = getUserId(username);
        List<String> history = new ArrayList<>();
        String sql = "SELECT p1.nombre as ori, p2.nombre as des, v.fecha, v.estado, v.precio " +
                     "FROM viaje v " +
                     "JOIN paradero p1 ON v.origen = p1.id_paradero " +
                     "JOIN paradero p2 ON v.destino = p2.id_paradero " +
                     "WHERE v.id_usuario = ? ORDER BY v.fecha DESC";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(rs.getString("ori") + "->" + rs.getString("des") + " [" + rs.getString("estado") + "]");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return history;
    }

    // --- 5xx: ADMIN (Permiso >= 2) ---

    public boolean addStop(String nombre, String distrito, String direccion, double lat, double lon) {
        if (this.permissionLevel < 2) return false;
        String sql = "INSERT INTO paradero (nombre, distrito, dirección, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre); stmt.setString(2, distrito); stmt.setString(3, direccion);
            stmt.setDouble(4, lat); stmt.setDouble(5, lon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removeStop(int stopId) {
        if (this.permissionLevel < 2) return false;
        // Basic check skipped for brevity, real app should check FKs
        String sql = "DELETE FROM paradero WHERE id_paradero = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, stopId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean addRoute(int originId, int destId, int time, double dist) {
        if (this.permissionLevel < 2) return false;
        String sql = "INSERT INTO ruta (origen, destino, tiempo, distancia, estado) VALUES (?, ?, ?, ?, true)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, originId); stmt.setInt(2, destId);
            stmt.setInt(3, time); stmt.setDouble(4, dist);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean modifyStop(int id, String nombre, double lat, double lon) {
        if (this.permissionLevel < 2) return false;
        String sql = "UPDATE paradero SET nombre = ?, latitud = ?, longitud = ? WHERE id_paradero = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre); stmt.setDouble(2, lat); stmt.setDouble(3, lon); stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean suspendUser(String username) {
        if (this.permissionLevel < 2) return false;
        String sql = "UPDATE usuario SET estado = false WHERE nombre = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- 7xx: CONDUCTOR (Permiso >= 1) ---

    // 700: Obtener Solicitudes Pendientes
    // Returns serialized list items: "ViajeID¶OrigenNombre¶DestinoNombre¶Distancia"
    public List<String> getPendingRides() {
        if (this.permissionLevel < 1) return null;
        List<String> rides = new ArrayList<>();
        String sql = "SELECT v.id_viaje, p1.nombre as ori, p2.nombre as des, v.distancia " +
                     "FROM viaje v " +
                     "JOIN paradero p1 ON v.origen = p1.id_paradero " +
                     "JOIN paradero p2 ON v.destino = p2.id_paradero " +
                     "WHERE v.estado = 'PENDIENTE'";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rides.add(rs.getInt("id_viaje") + "¶" + rs.getString("ori") + "¶" + rs.getString("des") + "¶" + rs.getDouble("distancia"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rides;
    }

    // 701: Aceptar Viaje
    public boolean acceptRide(int rideId, String driverUsername) {
        if (this.permissionLevel < 1) return false;
        // Optionally get driver ID from username, but simplified here
        String sql = "UPDATE viaje SET estado = 'ACEPTADO', conductor = ? WHERE id_viaje = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, driverUsername);
            stmt.setInt(2, rideId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 702: Actualizar Estado Viaje (EN_CURSO, FINALIZADO)
    public boolean updateRideStatus(int rideId, String newStatus) {
        if (this.permissionLevel < 1) return false;
        String sql = "UPDATE viaje SET estado = ? WHERE id_viaje = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, rideId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // --- UTILS ---
    private int getUserId(String username) {
        String sql = "SELECT id_usuario FROM usuario WHERE nombre = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuario");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
}