package db;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    // Database Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/NEXT?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // CHANGE THIS to your MySQL username
    private static final String PASSWORD = "password"; // CHANGE THIS to your MySQL password

    // Singleton instance
    private static java.sql.Connection instance = null;

    // Private constructor to prevent instantiation
    private Connection() {}

    /**
     * Returns the singleton connection instance.
     * If the connection is closed or null, it reconnects.
     */
    public static java.sql.Connection getInstance() {
        try {
            if (instance == null || instance.isClosed()) {
                // Load the driver explicitly (optional in newer JDBC versions but good practice)
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL JDBC Driver not found. Add the JAR to your library path!");
                    e.printStackTrace();
                    return null;
                }

                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Safely closes the connection.
     */
    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
        System.out.println("Testing database connection...");
        java.sql.Connection conn = Connection.getInstance();
        if (conn != null) {
            System.out.println("Test passed: Connection established.");
            Connection.close();
        } else {
            System.err.println("Test failed: Could not establish connection.");
        }
    }
}