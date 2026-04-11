package Main;

import Module.*;
import Database.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class Main {
	// Global User Tracker
	public static String currentUser = null;

	public static void main(String[] args) throws InterruptedException {
		// --- Main Module ---
		System.out.println("--- Logs will be monitored at the console ---");
		Thread.sleep(2500);

		System.out.println("\nInitializing...\n");
		Thread.sleep(1500);

		// Initialize and read the database
		InitializeDatabase.initializeDatabase();
		Thread.sleep(1500);

		System.out.println("\nCalling Login Module...");
		Thread.sleep(1500);

		// Launch the Login Menu without interruption with thread sleep
		SwingUtilities.invokeLater(() -> {
			openLoginMenu();
		});
	}

	// --- Global Open Login Menu ---
	public static void openLoginMenu() {
		// --- Open Login Menu when called ---
		LoginMenu login = new LoginMenu();

		login.setVisible(true); // Make it visible

		login.setAlwaysOnTop(true); // Temporarily pin then unpin it
		login.setAlwaysOnTop(false);

		login.toFront(); // The window come to the front
		login.requestFocus(); // Grab the keyboard focus
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
	}

	// --- Global Logout ---
	public static void logout(JFrame currentScreen) {

		currentUser = null; // Wipe the Data (important when logging out)
		System.out.println("\nLogging Out...");

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
			}

			System.out.println("\nCalling Login Module...");
			openLoginMenu(); // Reopen the Login Menu after logging out
		});

		timer.setRepeats(false);
		timer.start(); // Calls the timer for 1.5 seconds then run the logout dispose window

		logoutDialog.setVisible(true); // Pauses the rest of the code from running until the dialog is closed
	}
}
