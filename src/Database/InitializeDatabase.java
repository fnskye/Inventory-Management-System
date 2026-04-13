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

				// Ask the OS where the app is currently running from
				String currentFolder = System.getProperty("user.dir");

				// Build the absolute path to the external database file
				String dbUrl = "jdbc:sqlite:" + currentFolder + "/database.db";

				// Connect to the database
				try (java.sql.Connection connectdbUrl = java.sql.DriverManager.getConnection(dbUrl)) {
					logger.info("Successfully connected to external database at: " + dbUrl);
					// Creating user table for Login Menu
					String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
							+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "username TEXT UNIQUE NOT NULL, "
							+ "password TEXT NOT NULL)";
					// Execute statement
					statement.execute(createUsersTable);

					// Creating product table for Inventory Menu
					String createProductsTable = "CREATE TABLE IF NOT EXISTS Products ("
							+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "product_name TEXT NOT NULL UNIQUE,"
							+ "category TEXT NOT NULL," + "price REAL NOT NULL," + "stock INTEGER NOT NULL" + ");";

					// Execute statement
					statement.execute(createProductsTable);
					logger.info("Products table created successfully.");

					String createTransactionTable = "CREATE TABLE IF NOT EXISTS Transactions ("
							+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "order_date TEXT NOT NULL,"
							+ "order_number TEXT NOT NULL," + "customer_name TEXT NOT NULL,"
							+ "total_amount REAL NOT NULL" + ");";

					// Execute statement
					statement.execute(createTransactionTable);
					logger.info("Transaction table created successfully.");

					// Inserting a default admin account
					String insertAdmin = "INSERT OR IGNORE INTO Users (username,  password) VALUES ('admin', 'admin')";
					statement.execute(insertAdmin);
					logger.info("Database connected.");
				} catch (java.sql.SQLException e) {
					logger.error("Database connection failed!", e);
				}
			}
			// Close connection
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			logger.error("Database Setup Error: " + e.getMessage());
		}
	}
}
