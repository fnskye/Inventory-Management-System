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

	// Dropdown Menu System (might improve in the future)
	public static JMenuBar createTopMenu(boolean isLoggedIn, String username, JFrame currentScreen) {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionsMenu = new JMenu("≡ Menu");

		if (isLoggedIn) {
			// --- Create Account Button ---
			JMenuItem createAccountItem = new JMenuItem("Create Account");
			createAccountItem.addActionListener(e -> {
				// Opens the Create Account window
				CreateAccountMenu createacc = new CreateAccountMenu(currentScreen);
				logger.info("Opening Create Account Menu...");
				createacc.setVisible(true);
			});
			optionsMenu.add(createAccountItem);

			// --- Remove Account Button (for admin only) ---
			if (username.equalsIgnoreCase("admin")) {
				// --- Remove Account Button ---
				JMenuItem removeAccountItem = new JMenuItem("Remove Account");
				removeAccountItem.addActionListener(e -> {
					// Opens the Remove Account Window
					RemoveAccountMenu removeacc = new RemoveAccountMenu(username, currentScreen);
					logger.info("Opening Remove Account Menu...");
					removeacc.setVisible(true);
				});
				optionsMenu.add(removeAccountItem);
				;
			}

			// --- Logout Button ---
			JMenuItem logoutItem = new JMenuItem("Logout");
			logoutItem.addActionListener(e -> {
				// Tells the System Manager to handle the logout!
				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (confirmation == JOptionPane.YES_OPTION) {
					logger.debug("Disposing Current Screen");
					Main.logout(currentScreen);
				}
			});
			optionsMenu.add(logoutItem);

			optionsMenu.addSeparator();
		}

		// All class have exit button
		optionsMenu.add(createExitMenuItem());

		menuBar.add(optionsMenu);
		return menuBar;
	}
}