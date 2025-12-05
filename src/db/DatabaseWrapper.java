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

    
    
    public boolean registerAdmin(String nombre, String usuario, String correo, String password) {
        
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

}