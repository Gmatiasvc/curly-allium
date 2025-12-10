package db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
public class DatabaseWrapper {

    private final int permissionLevel;
    private final java.sql.Connection conn;

    public DatabaseWrapper(int permissionLevel, java.sql.Connection conn) {
        this.permissionLevel = permissionLevel;
        this.conn = conn;
    }




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

    
    
    public boolean registerAdmin(String nombre, String correo, String password) {

        if (this.permissionLevel < 0) {
            return false;
        }
        
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

    public boolean loginUser(String usuario, String password) {
        String sql = "SELECT contraseña, salt FROM usuario WHERE nombre_usuario = ? AND estado = true";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("contraseña");
                String salt = rs.getString("salt");
                return common.PasswordUtils.verifyPassword(password, storedHash, salt);
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loginAdmin(String correo, String password) {
        String sql = "SELECT contraseña, salt FROM usuario WHERE correo = ? AND estado = true";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("contraseña");
                String salt = rs.getString("salt");
                return common.PasswordUtils.verifyPassword(password, storedHash, salt);
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addStop(String nombre, String distrito, String direccion, double latitud, double longitud) {

        if (this.permissionLevel < 0) {
            return false;
        }

        String sql = "INSERT INTO usuario (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addRoute(int origen, int destino, int tiempo, double distancia, boolean estado){
        if (this.permissionLevel < 0) {
            return false;
        }

        String sql = "INSERT INTO ruta (id_origen, id_destino, tiempo_estimado, distancia, estado) VALUES (?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setInt(1, origen);
            stmt.setInt(2, destino);
            stmt.setInt(3, tiempo);
            stmt.setDouble(4, distancia);
            stmt.setBoolean(5, estado);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeRout(int origen, int destino) {

        if (this.permissionLevel < 0) {
            return false;
        }

        String sql = "DELETE FROM ruta WHERE id_origen = ? AND id_destino = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setInt(1, origen);
            stmt.setInt(2, destino);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeStop(int stopId) {

        if (this.permissionLevel < 0) {
            return false;
        }

        String sql = "DELETE FROM parada WHERE id_parada = ?";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stopId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}