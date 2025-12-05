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

        String sql = "INSERT INTO usuario (nombre, correo, contraseña, salt, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        
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

}