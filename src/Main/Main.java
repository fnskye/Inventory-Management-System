package Main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.InitializeDatabase;
import Module.InventoryMenu;
import Module.LoginMenu;
import Module.MainMenu;
import Module.OrderMenu;

public class Main {
	// Global User Tracker
	public static String currentUser = null;

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
}
