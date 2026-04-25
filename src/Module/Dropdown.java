package Module;

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
			}

			if (!("Main Menu".equals(currentScreen.getTitle()))) {
				// --- Back to Main Menu Button (if only you are not in Main Menu) ---
				JMenuItem backButton = new JMenuItem("Back");
				backButton.addActionListener(e -> {
					int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back?",
							"Confirm", JOptionPane.YES_NO_OPTION);
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

			if ("Main Menu".equals(currentScreen.getTitle()) || !("Login".equals(currentScreen.getTitle()))) {
				// --- Currency Changer Button ---
				JMenuItem currencyChangerItem = new JMenuItem("Change Currency");
				currencyChangerItem.addActionListener(e -> {
					logger.info("Opening Currency Changer Menu...");
					try {
						Main.openCurrencyWindow(currentScreen, username);
					} catch (InterruptedException ex) {
						logger.error("Login Error.", ex);
					}
				});
				optionsMenu.add(currencyChangerItem);
				optionsMenu.addSeparator();

				// --- Logout Button ---
				JMenuItem logoutItem = new JMenuItem("Logout");
				logoutItem.addActionListener(e -> {
					// Tells the System Manager to handle the logout
					int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?",
							"Confirm", JOptionPane.YES_NO_OPTION);
					if (confirmation == JOptionPane.YES_OPTION) {
						logger.debug("Disposing Current Screen");
						Main.logout(currentScreen);
					}
				});
				optionsMenu.add(logoutItem);

				optionsMenu.addSeparator();
			}
		}

		// All class have exit button
		optionsMenu.add(createExitMenuItem());

		menuBar.add(optionsMenu);
		return menuBar;
	}
}