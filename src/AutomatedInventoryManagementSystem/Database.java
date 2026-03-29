package AutomatedInventoryManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    
    // create a database connection, and the path of database file
    private static final String URL = "jdbc:sqlite:database.db";

    // create a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Run this once when the application starts to build the tables
    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            if (connection != null) {
                // creating users table
                String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                        + "username TEXT UNIQUE NOT NULL, "
                                        + "password TEXT NOT NULL)";
                statement.execute(createUsersTable);
                
                // inserting a default admin account
                String insertAdmin = "INSERT OR IGNORE INTO Users (username, password) VALUES ('admin', 'admin')";
                statement.execute(insertAdmin);
                
                System.out.println("Database Initialization Successful.");
            }
        } catch (SQLException e) {
            e.printStackTrace(System.err);
            System.out.println("Database Setup Error: " + e.getMessage());
        }
    }
}

