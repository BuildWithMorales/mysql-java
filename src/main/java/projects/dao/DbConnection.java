package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {
	
	// Constants for database connection
    private static String HOST = "localhost";
    private static String PASSWORD = "MOrales_2025"; // or whatever password you set in MySQL
    private static int PORT = 3306;
    private static String SCHEMA = "projects";
    private static String USER = "projects"; // or whatever username you created
    
 // Method to get a JDBC connection
    public static Connection getConnection() {
        String uri = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true",
                                    HOST, PORT, SCHEMA);
        try {
            Connection conn = DriverManager.getConnection(uri, USER, PASSWORD);
            System.out.println("Connection to database successful!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            throw new DbException("Unable to connect to the database.", e);
        }

    }
}
