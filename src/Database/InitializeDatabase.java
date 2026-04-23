package Database;

// includes the database connection
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class InitializeDatabase {
	// Create instance
	Argon2 argon2 = Argon2Factory.create();

	private static final Logger logger = LogManager.getLogger(InitializeDatabase.class);

	// Create a database connection, and the path of database file
	private static final String currentFolder = System.getProperty("user.dir");

	// Path to the current directory
	private static final String dBUrl = "jdbc:sqlite:" + currentFolder + "/database.db";;

	// Create a database connection
	public static Connection getConnection() throws SQLException {

		return DriverManager.getConnection(dBUrl);
	}

	// Run this once when the application starts to build the tables
	public static void initializeDatabase() {
		// Create connection
		try (Connection connection = getConnection();
				// Create statement
				Statement statement = connection.createStatement()) {

			if (connection != null) {
				logger.info("Successfully connected to external database at: " + dBUrl);

				// Creating user table for Login Menu
				String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "username TEXT UNIQUE NOT NULL, "
						+ "password TEXT NOT NULL)";
				statement.execute(createUsersTable);
				logger.info("User table loaded successfully.");

				String createSettingsTable = "CREATE TABLE IF NOT EXISTS Settings (" + "setting_key TEXT PRIMARY KEY, "
						+ "setting_value TEXT NOT NULL)";
				statement.execute(createSettingsTable);
				logger.info("Glbal Settings table loaded successfully.");

				// Insert a default currency if it doesn't exist yet (for Philippines)
				String defaultCurrency = "INSERT OR IGNORE INTO Settings (setting_key, setting_value) VALUES ('currency', '₱')";
				statement.execute(defaultCurrency);
				logger.info("Default currency loaded successfully.");

				// Creating product table for Inventory Menu
				String createProductsTable = "CREATE TABLE IF NOT EXISTS Products ("
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "product_name TEXT NOT NULL UNIQUE,"
						+ "category TEXT NOT NULL," + "price REAL NOT NULL," + "stock INTEGER NOT NULL,"
						+ "unit_type TEXT NOT NULL" + ");";
				statement.execute(createProductsTable);
				logger.info("Products table loaded successfully.");

				String createTransactionTable = "CREATE TABLE IF NOT EXISTS Transactions ("
						+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "order_date TEXT NOT NULL,"
						+ "order_number TEXT NOT NULL," + "customer_name TEXT NOT NULL," + "total_amount REAL NOT NULL"
						+ ");";
				statement.execute(createTransactionTable);
				logger.info("Transaction table loaded successfully.");

				// Secure Admin Insertion
				String defaultPassword = "admin";
				Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

				// Hashing the password with 3 iterations, 64MB memory, and 1 thread
				String secureHash = argon2.hash(3, 65536, 1, defaultPassword.toCharArray());

				// Inserting a default admin account
				String insertAdmin = "INSERT OR IGNORE INTO Users (username,  password) VALUES ('admin', '" + secureHash
						+ "')";
				statement.execute(insertAdmin);

				// Wipe the plain text password from memory so hackers can't access anything
				// from memory
				argon2.wipeArray(defaultPassword.toCharArray());

				logger.info("Database connected.");

				// Close connection
				connection.close();
			}
		} catch (java.sql.SQLException e) {
			e.printStackTrace(System.err);
			logger.error("Database Setup Error: " + e.getMessage());
		}

	}

}
