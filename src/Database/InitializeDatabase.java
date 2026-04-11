package Database;

// includes the database connection
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InitializeDatabase {

	private static final Logger logger = LogManager.getLogger(InitializeDatabase.class);

	// Create a database connection, and the path of database file
	private static final String URL = "jdbc:sqlite:database.db";

	// Create a database connection
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL);
	}

	// Run this once when the application starts to build the tables
	public static void initializeDatabase() {
		// Create connection
		try (Connection connection = getConnection();
				// Create statement
				Statement statement = connection.createStatement()) {

			if (connection != null) {
				// Creating user table
				String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "username TEXT UNIQUE NOT NULL, "
						+ "password TEXT NOT NULL)";
				// Execute statement
				statement.execute(createUsersTable);

				// Inserting a default admin account
				String insertAdmin = "INSERT OR IGNORE INTO Users (username, password) VALUES ('admin', 'admin')";
				statement.execute(insertAdmin);
				logger.info("Database connected.");
			}
			// Close connection
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			logger.error("Database Setup Error: " + e.getMessage());
		}
	}
}
