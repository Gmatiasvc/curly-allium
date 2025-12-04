package db;

import common.LinkedList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import objects.User;

public class DatabaseWrapper {

    private final int permissionLevel;
    private final java.sql.Connection conn;

    public DatabaseWrapper(int permissionLevel, java.sql.Connection conn) {
        this.permissionLevel = permissionLevel;
        this.conn = conn;
    }


    public LinkedList<User> getAllUsers() {
        LinkedList<User> users = new LinkedList<>();
        String sql = "SELECT nombre, correo, estado, fecha_registro FROM usuario";
        
        if (this.conn == null) return users;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("nombre");
                String email = rs.getString("correo");
                boolean isActive = rs.getBoolean("estado");
                long createdAt = rs.getLong("fecha_registro");

                users.add(new User(name, email, isActive, String.valueOf(createdAt), false));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }

    public boolean addUser(String nombre, String usuario, String correo, String password) {
        if (this.permissionLevel < 1) {
            System.err.println("Insufficient permissions to add user.");
            return false;
        }

        String sql = "INSERT INTO usuario (nombre, nombre_usuario, correo, contraseña, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?)";
        
        if (this.conn == null) return false;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            stmt.setString(2, usuario);
            stmt.setString(3, correo);
            stmt.setString(4, password);
            stmt.setBoolean(5, true);    
            stmt.setLong(6, System.currentTimeMillis());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
}