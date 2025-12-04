package db;

import java.sql.DriverManager;
import java.sql.SQLException;

@SuppressWarnings("CallToPrintStackTrace")
public class Connection {

    private static final String URL = "jdbc:mysql://localhost:3306/NEXT";
    private static final String USER = "NEXT_SUDO";
    private static final String PASSWORD = "VerySecurePassword";

    private static java.sql.Connection instance = null;

    private Connection() {
    }

    public static java.sql.Connection getInstance() {
        try {
            if (instance == null || instance.isClosed()) {
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

    // public static void main(String[] args) {
    //     System.out.println("Testing database connection...");
    //     java.sql.Connection conn = Connection.getInstance();
    //     if (conn != null) {
    //         System.out.println("Test passed: Connection established.");
    //         Connection.close();
    //     } else {
    //         System.err.println("Test failed: Could not establish connection.");
    //     }
    // }
}
