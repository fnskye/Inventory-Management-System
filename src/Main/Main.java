package Main;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.InitializeDatabase;
import Module.InventoryMenu;
import Module.LoginMenu;
import Module.MainMenu;
import Module.OrderMenu;
import Module.SalesReport;

public class Main {
	// Global User Tracker
	public static String currentUser = null;

	// Global Currency Tracker
	public static String currencySymbol = "₱";

	// Initialize the logger for this specific class
	private static final Logger logger = LogManager.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException {

		// --- Main Module + Logger Module ---
		Thread.sleep(1500);
		logger.info("---------------------------------------------");
		logger.info("|   Inventory System Started Successfully   |");
		logger.info("---------------------------------------------");

		try {
			Thread.sleep(1500);
			logger.info("Attempting to connect to the database...");
			Thread.sleep(300);
			logger.info("Initializing...");
			InitializeDatabase.initializeDatabase();
			Thread.sleep(50);
			logger.info("Loading Global Settings...");
			loadGlobalSettings();
		} catch (Exception e) {
			logger.error("Failed to load database.", e);
		}

		// Initialize and read the database

		Thread.sleep(1500);

		logger.info("Calling Login Module...");
		Thread.sleep(1500);

		// Launch the Login Menu without interruption with thread sleep
		SwingUtilities.invokeLater(() -> {
			logger.info("Initializing...");
			openLoginMenu();
		});
	}

	// --- Global Open Login Menu ---
	public static void openLoginMenu() {
		// --- Open Login Menu when called ---
		LoginMenu login = new LoginMenu();

		logger.info("Opening Login Module...");
		login.setVisible(true); // Make it visible

		login.setAlwaysOnTop(true); // Temporarily pin then unpin it
		login.setAlwaysOnTop(false);

		login.toFront(); // The window come to the front
		login.requestFocus(); // Grab the keyboard focus
		logger.debug("Success Opening Login Menu.");
	}

	// --- Global Open Main Menu ---
	public static void openMainMenu(String username, JFrame currentScreen) {
		// --- Open Main Menu when called ---

		// Save the user as global user when reaching main menu
		currentUser = username;

		// Close whatever screen the user is currently on
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		// Open the Main Menu and pass the logged in username
		MainMenu mainMenu = new MainMenu(username);
		mainMenu.setVisible(true);
		logger.info("Success Opening Main Menu.");
	}

	// --- Global Open Inventory Menu ---
	public static void openInventoryMenu(String username, JFrame currentScreen) {
		// --- Open Inventory when called ---

		// Save the user as global user when reaching inventory menu
		currentUser = username;

		// Close whatever screen the user is currently on
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		// Open the Inventory Menu and pass the logged in username
		InventoryMenu inventoryMenu = new InventoryMenu(currentUser);
		inventoryMenu.setVisible(true);
		logger.info("Success Opening Inventory Menu.");
	}

	// --- Global Open Order Menu ---
	public static void openOrderMenu(String username, JFrame currentScreen) {
		// --- Open Order when called ---

		// Save the user as global user when reaching ordder menu
		currentUser = username;

		// Close whatever screen the user is currently on
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		// Open the Order Menu and pass the logged in username
		OrderMenu orderMenu = new OrderMenu(currentUser);
		orderMenu.setVisible(true);
		logger.info("Success Opening Order Menu.");
	}

	// --- Global Open Sales Report Menu ---
	public static void openSalesReport(String username, JFrame currentScreen) {
		// --- Open Order when called ---

		// Save the user as global user when reaching ordder menu
		currentUser = username;

		// Close whatever screen the user is currently on
		if (currentScreen != null) {
			currentScreen.dispose();
		}

		// Open the SalesReport and pass the logged in username
		SalesReport salesReport = new SalesReport(currentUser);
		salesReport.setVisible(true);
		logger.info("Success Opening Sales Menu.");
	}

	// --- Global Logout ---
	public static void logout(JFrame currentScreen) {

		currentUser = null; // Wipe the Data (important when logging out)
		logger.info("Logging Out...");

		// Show the Logging Out... Dialog
		javax.swing.JDialog logoutDialog = new javax.swing.JDialog(currentScreen, "Logging Out", true);
		logoutDialog.setSize(250, 100);
		logoutDialog.setLayout(new java.awt.BorderLayout());
		logoutDialog.setLocationRelativeTo(currentScreen);
		logoutDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

		javax.swing.JLabel logoutLabel = new javax.swing.JLabel("Logging out...", javax.swing.SwingConstants.CENTER);
		logoutDialog.add(logoutLabel, java.awt.BorderLayout.CENTER);

		// Setting up the timer variables
		javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {

			logoutDialog.dispose(); // Calls the code after 1.5 seconds clicking logout button

			if (currentScreen != null) {
				currentScreen.dispose(); // Close the Current Menu
				logger.info("Success Logging Out.");
			}

			logger.info("Calling Login Module...");
			openLoginMenu(); // Reopen the Login Menu after logging out
			logger.info("Opened Login Module.");
		});

		timer.setRepeats(false);
		timer.start(); // Calls the timer for 1.5 seconds then run the logout dispose window

		logoutDialog.setVisible(true); // Pauses the rest of the code from running until the dialog is closed
	}

	// Currency Changer
	public static void openCurrencyWindow(JFrame parent, String username) throws InterruptedException {

		Color lightGreen = new Color(0, 204, 102);

		javax.swing.JDialog changeCurrencyDialog = new javax.swing.JDialog(parent, "Change Currency", true);
		changeCurrencyDialog.setSize(300, 200);
		changeCurrencyDialog.setLocationRelativeTo(parent);
		changeCurrencyDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		changeCurrencyDialog.add(new javax.swing.JLabel("Select Currency:"), gbc);

		// Currencies
		String[] currencies = { "₱", "$", "€", "£", "¥" };
		javax.swing.JComboBox<String> currencyBox = new javax.swing.JComboBox<>(currencies);

		currencyBox.setSelectedItem(Main.currencySymbol);
		gbc.gridy = 1;
		changeCurrencyDialog.add(currencyBox, gbc);

		javax.swing.JButton saveButton = new javax.swing.JButton("Save Settings");
		saveButton.setBackground(lightGreen);
		saveButton.setForeground(java.awt.Color.WHITE);
		saveButton.setFocusPainted(false);
		gbc.gridy = 2;
		changeCurrencyDialog.add(saveButton, gbc);

		saveButton.addActionListener(e -> {
			String selected = currencyBox.getSelectedItem().toString();

			// Update the Database so it remembers it forever
			String dbUrl = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database.db";
			try (java.sql.Connection connection = java.sql.DriverManager.getConnection(dbUrl);
					java.sql.PreparedStatement preparedstatement = connection
							.prepareStatement("UPDATE Settings SET setting_value = ? WHERE setting_key = 'currency'")) {

				preparedstatement.setString(1, selected);
				preparedstatement.executeUpdate();

				// Update the Global Variable in memory so it changes instantly
				if (!(Main.currencySymbol.equals(selected))) {
					Main.currencySymbol = selected;
					javax.swing.JOptionPane.showMessageDialog(changeCurrencyDialog, "Currency updated to " + selected);
					changeCurrencyDialog.dispose();

					// Soft Reload Logic
					String currentTitle = parent.getTitle(); // Check what menu we are currently looking at
					parent.dispose(); // Destroy the old, outdated window

					// Instantly boot up a fresh version of the exact same window
					if (currentTitle.contains("Inventory")) {
						new InventoryMenu(username).setVisible(true);
					} else if (currentTitle.contains("Order")) {
						new OrderMenu(username).setVisible(true);
					} else if (currentTitle.contains("Sales")) {
						new SalesReport(username).setVisible(true);
					} else {
						new MainMenu(username).setVisible(true);
					}
				} else {
					logger.error(selected, " is already the Global Currency Symbol.");
					javax.swing.JOptionPane.showMessageDialog(changeCurrencyDialog,
							selected + " is already the Global Currency Symbol.");
					return;
				}

			} catch (Exception ex) {
				logger.error("Error saving settings.", ex);
				javax.swing.JOptionPane.showMessageDialog(changeCurrencyDialog, "Error saving settings.");
			}
		});
		changeCurrencyDialog.setVisible(true);
	}

	public static void loadGlobalSettings() {
		String currentFolder = System.getProperty("user.dir");
		String dbUrl = "jdbc:sqlite:" + currentFolder + "/database.db";

		try (java.sql.Connection connection = java.sql.DriverManager.getConnection(dbUrl);
				java.sql.Statement statement = connection.createStatement();
				java.sql.ResultSet resultset = statement
						.executeQuery("SELECT setting_value FROM Settings WHERE setting_key = 'currency'")) {

			if (resultset.next()) {
				currencySymbol = resultset.getString("setting_value");
				logger.info("Loaded global currency symbol: " + currencySymbol);
			}
		} catch (Exception e) {
			logger.error("Failed to load settings.", e);
		}
	}
}
