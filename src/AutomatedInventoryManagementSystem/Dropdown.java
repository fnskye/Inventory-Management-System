package AutomatedInventoryManagementSystem;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class Dropdown {

	// Universal Exit Button
	public static JMenuItem createExitMenuItem() {
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(e -> {
			int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (confirmation == JOptionPane.YES_OPTION) {
				System.out.println("\nExiting...");
				System.exit(0);
			}
		});
		return exitItem;
	}

	// Dropdown Menu System (might improve in the future)
	public static JMenuBar createTopMenu(boolean isLoggedIn, JFrame currentScreen) {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionsMenu = new JMenu("≡ Menu");

		if (isLoggedIn) {
			// --- Create Account Button ---
			JMenuItem createAccountItem = new JMenuItem("Create Account");
			createAccountItem.addActionListener(e -> {
				// Opens the Create Account window
				CreateAccountMenu create = new CreateAccountMenu(currentScreen);
				create.setVisible(true);
			});
			optionsMenu.add(createAccountItem);

			// --- Logout Button ---
			JMenuItem logoutItem = new JMenuItem("Logout");
			logoutItem.addActionListener(e -> {
				// Tells the System Manager to handle the logout!
				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (confirmation == JOptionPane.YES_OPTION) {
					OverallSystem.logout(currentScreen);
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