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

    // 100: Login Genérico
    public User login(String identifier, String password) {
        // Fetch user and all their roles in one query
        String sql = "SELECT u.id_usuario, u.nombre, u.correo, u.contraseña, u.salt, u.estado, u.fecha_registro, " +
                     "GROUP_CONCAT(r.nombre) as roles " +
                     "FROM usuario u " +
                     "LEFT JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario " +
                     "LEFT JOIN rol r ON ur.id_rol = r.id_rol " +
                     "WHERE u.nombre = ? OR u.correo = ? " +
                     "GROUP BY u.id_usuario";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("contraseña");
                    String salt = rs.getString("salt");
                    
                    if (common.PasswordUtils.verifyPassword(password, storedHash, salt)) {
                        String rolesObj = rs.getString("roles");
                        String roles = rolesObj == null ? "" : rolesObj;
                        
                        boolean isAdmin = roles.contains("ADMIN");
                        boolean isDriver = roles.contains("CONDUCTOR");

                        return new User(
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getBoolean("estado"),
                            String.valueOf(rs.getLong("fecha_registro")),
                            isAdmin,
                            isDriver
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
        
        String insertUser = "INSERT INTO usuario (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        String insertRole = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";

        try {
            // Start transaction-like sequence
            PreparedStatement stmtUser = this.conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, nombre);
            stmtUser.setString(2, correo);
            stmtUser.setString(3, secureHash);
            stmtUser.setString(4, salt);
            stmtUser.setBoolean(5, true);    
            stmtUser.setLong(6, System.currentTimeMillis());
            
            int affected = stmtUser.executeUpdate();
            if (affected == 0) return false;

            int userId = -1;
            try (ResultSet generatedKeys = stmtUser.getGeneratedKeys()) {
                if (generatedKeys.next()) userId = generatedKeys.getInt(1);
            }
            stmtUser.close();

            if (userId != -1) {
                // Assign 'PASAJERO' role (ID 1)
                PreparedStatement stmtRole = this.conn.prepareStatement(insertRole);
                stmtRole.setInt(1, userId);
                stmtRole.setInt(2, 1); 
                stmtRole.executeUpdate();

                // If driver, also assign 'CONDUCTOR' role (ID 2)
                if (isDriver) {
                    stmtRole.setInt(1, userId);
                    stmtRole.setInt(2, 2);
                    stmtRole.executeUpdate();
                }
                stmtRole.close();
                return true;
            }
        } catch (SQLException e) {
            // Duplicate entry errors will be caught here
            e.printStackTrace(); 
        }
        return false;
    }

    // 105: Registro Administrador
    public boolean registerAdmin(String nombre, String correo, String password) {
        if (this.permissionLevel < 2) return false;
        
        String salt = common.PasswordUtils.getSalt();
        String secureHash = common.PasswordUtils.hashPassword(password, salt);

        String insertUser = "INSERT INTO usuario (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        String insertRole = "INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (?, ?)";
        
        try {
            PreparedStatement stmtUser = this.conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, nombre);
            stmtUser.setString(2, correo);
            stmtUser.setString(3, secureHash);
            stmtUser.setString(4, salt);
            stmtUser.setBoolean(5, true);    
            stmtUser.setLong(6, System.currentTimeMillis());

            int affected = stmtUser.executeUpdate();
            if (affected == 0) return false;

            int userId = -1;
            try (ResultSet generatedKeys = stmtUser.getGeneratedKeys()) {
                if (generatedKeys.next()) userId = generatedKeys.getInt(1);
            }
            stmtUser.close();

            if (userId != -1) {
                // Assign 'ADMIN' role (ID 3)
                PreparedStatement stmtRole = this.conn.prepareStatement(insertRole);
                stmtRole.setInt(1, userId);
                stmtRole.setInt(2, 3);
                stmtRole.executeUpdate();
                stmtRole.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return false;
    }

    // --- 2xx: CLIENTE / PASAJERO ---

    // 200: Datos Usuario
    public User getUserData(String username) {
        String sql = "SELECT u.nombre, u.correo, u.estado, u.fecha_registro, GROUP_CONCAT(r.nombre) as roles " +
                     "FROM usuario u " +
                     "LEFT JOIN usuario_rol ur ON u.id_usuario = ur.id_usuario " +
                     "LEFT JOIN rol r ON ur.id_rol = r.id_rol " +
                     "WHERE u.nombre = ? GROUP BY u.id_usuario";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    if (roles == null) roles = "";
                    return new User(
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBoolean("estado"),
                        String.valueOf(rs.getLong("fecha_registro")),
                        roles.contains("ADMIN"),
                        roles.contains("CONDUCTOR")
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
        String sql = "SELECT nombre, distrito, direccion, latitud, longitud FROM paradero";
        
        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stops.add(new Stop(
                    rs.getString("nombre"),
                    rs.getString("direccion"),
                    rs.getString("distrito"), 
                    rs.getDouble("latitud"),
                    rs.getDouble("longitud")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stops;
    }

    // 202: Obtener Mapa - Rutas
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT p1.nombre as origen, p2.nombre as destino, r.distancia, r.tiempo, r.estado " +
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
                    rs.getDouble("distancia"),
                    rs.getInt("tiempo")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return routes;
    }

    // 205: Solicitar Viaje
    public int requestRide(String username, int originId, int destId, double price, double distance) {
        int userId = getUserId(username);
        if (userId == -1) return -1;

        // FIXED: Added 'duracion' to the SQL Insert because the table requires it.
        // Also estimating duration based on distance (2 mins per unit) to satisfy constraint.
        String sql = "INSERT INTO viaje (id_usuario, origen, destino, fecha, precio, distancia, estado, duracion) VALUES (?, ?, ?, ?, ?, ?, 'PENDIENTE', ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, originId);
            stmt.setInt(3, destId);
            stmt.setLong(4, System.currentTimeMillis());
            stmt.setDouble(5, price);
            stmt.setDouble(6, distance);
            stmt.setInt(7, (int)(distance * 2)); // Placeholder duration: 2 units of time per distance unit
            
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); }
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
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // 208: Cancelar Viaje
    public boolean cancelRide(int rideId, String username) {
        int userId = getUserId(username);
        String sql = "UPDATE viaje SET estado = 'CANCELADO' WHERE id_viaje = ? AND id_usuario = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, rideId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
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
        String sql = "INSERT INTO paradero (nombre, distrito, direccion, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, nombre); stmt.setString(2, distrito); stmt.setString(3, direccion);
            stmt.setDouble(4, lat); stmt.setDouble(5, lon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removeStop(int stopId) {
        if (this.permissionLevel < 2) return false;
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

    public boolean acceptRide(int rideId, String driverUsername) {
        if (this.permissionLevel < 1) return false;
        String sql = "UPDATE viaje SET estado = 'ACEPTADO', conductor = ? WHERE id_viaje = ? AND estado = 'PENDIENTE'";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, driverUsername);
            stmt.setInt(2, rideId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

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