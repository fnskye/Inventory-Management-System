package Module;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.Main;
import User.CreateAccountMenu;
import User.RemoveAccountMenu;

public class Dropdown {

	private static final Logger logger = LogManager.getLogger(Dropdown.class);

	// Universal Exit Button
	public static JMenuItem createExitMenuItem() {
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(e -> {
			int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.YES_OPTION) {
				logger.info("\nExiting...");
				System.exit(0);
			}
		});
		return exitItem;
	}

	// Dropdown Menu
	public static JMenuBar createTopMenu(boolean isLoggedIn, String username, JFrame currentScreen) {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionsMenu = new JMenu("≡ Options");

		if (isLoggedIn) {
			// --- Account Creation and Removal Button (for admin only) ---
			if (username.equalsIgnoreCase("admin") && ("Main Menu".equals(currentScreen.getTitle()))) {
				// --- Create Account Button ---
				JMenuItem createAccountItem = new JMenuItem("Create Account");
				createAccountItem.addActionListener(e -> {
					// Opens the Create Account window
					CreateAccountMenu createacc = new CreateAccountMenu(currentScreen);
					logger.info("Opening Create Account Menu...");
					createacc.setVisible(true);
				});
				optionsMenu.add(createAccountItem);
				optionsMenu.addSeparator();
			}

			if (username.equalsIgnoreCase("admin") && ("Main Menu".equals(currentScreen.getTitle()))) {
				// --- Remove Account Button ---
				JMenuItem removeAccountItem = new JMenuItem("Remove Account");
				removeAccountItem.addActionListener(e -> {
					// Opens the Remove Account Window
					RemoveAccountMenu removeacc = new RemoveAccountMenu(username, currentScreen);
					logger.info("Opening Remove Account Menu...");
					removeacc.setVisible(true);
				});
				optionsMenu.add(removeAccountItem);
				optionsMenu.addSeparator();

				// --- System Settings Button ---
				JMenuItem settingsItem = new JMenuItem("Change Currency");
				settingsItem.addActionListener(e -> {
					logger.info("Opening System Settings...");
					openSettingsWindow(currentScreen);
				});
				optionsMenu.add(settingsItem);
				optionsMenu.addSeparator();
			}
		}

		if (!("Main Menu".equals(currentScreen.getTitle()))) {
			// --- Back to Main Menu Button (if only you are not in Main Menu) ---
			JMenuItem backButton = new JMenuItem("Back");
			backButton.addActionListener(e -> {
				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back?", "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (confirmation == JOptionPane.YES_OPTION) {
					logger.info("Returning to Main Menu...");
					currentScreen.dispose(); // Close current screen
					MainMenu back = new MainMenu(username);
					back.setVisible(true); // Open new main menu
				}
			});
			optionsMenu.add(backButton);

			optionsMenu.addSeparator();
		}

		// --- Logout Button ---
		JMenuItem logoutItem = new JMenuItem("Logout");
		logoutItem.addActionListener(e -> {
			// Tells the System Manager to handle the logout
			int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.YES_OPTION) {
				logger.debug("Disposing Current Screen");
				Main.logout(currentScreen);
			}
		});
		optionsMenu.add(logoutItem);

		optionsMenu.addSeparator();

		// All class have exit button
		optionsMenu.add(createExitMenuItem());

		menuBar.add(optionsMenu);
		return menuBar;
	}

	// Currency Changer
	private static void openSettingsWindow(JFrame parent) {

		Color lightGreen = new Color(0, 204, 102);

		javax.swing.JDialog settingsDialog = new javax.swing.JDialog(parent, "Change Currency", true);
		settingsDialog.setSize(300, 200);
		settingsDialog.setLocationRelativeTo(parent);
		settingsDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		settingsDialog.add(new javax.swing.JLabel("Select Currency:"), gbc);

		// Currencies
		String[] currencies = { "₱", "$", "€", "£", "¥" };
		javax.swing.JComboBox<String> currencyBox = new javax.swing.JComboBox<>(currencies);

		currencyBox.setSelectedItem(Main.currencySymbol);
		gbc.gridy = 1;
		settingsDialog.add(currencyBox, gbc);

		javax.swing.JButton saveButton = new javax.swing.JButton("Save Settings");
		saveButton.setBackground(lightGreen);
		saveButton.setForeground(java.awt.Color.WHITE);
		saveButton.setFocusPainted(false);
		gbc.gridy = 2;
		settingsDialog.add(saveButton, gbc);

		saveButton.addActionListener(e -> {
			String selected = currencyBox.getSelectedItem().toString();

			// Update the Database so it remembers it forever
			String dbUrl = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database.db";
			try (java.sql.Connection conn = java.sql.DriverManager.getConnection(dbUrl);
					java.sql.PreparedStatement ps = conn
							.prepareStatement("UPDATE Settings SET setting_value = ? WHERE setting_key = 'currency'")) {

				ps.setString(1, selected);
				ps.executeUpdate();

				// Update the Global Variable in memory so it changes instantly
				Main.currencySymbol = selected;

				javax.swing.JOptionPane.showMessageDialog(settingsDialog, "Currency updated to " + selected);
				settingsDialog.dispose();

			} catch (Exception ex) {
				logger.error("Error saving settings.", ex);
				javax.swing.JOptionPane.showMessageDialog(settingsDialog, "Error saving settings.");
			}
		});

		settingsDialog.setVisible(true);
	}
}