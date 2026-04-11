package Module;

// includes action listener for buttons
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// includes the java swing gui
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// include classes outside the package
import Database.InitializeDatabase;
import Main.Main;

public class LoginMenu extends JFrame {

	private static final Logger logger = LogManager.getLogger(LoginMenu.class);

	private static final long serialVersionUID = 1L; // Add default serial version ID

	// Initialization of variables
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;

	public LoginMenu() {
		// Setup the Main Window with JFrame
		setTitle("Login");
		setSize(500, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // Centers the window on the screen
		setLayout(new BorderLayout());

		// Sets to false because no one is logged in yet
		setJMenuBar(Dropdown.createTopMenu(false, null, this));

		// Center the Form Panel
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new GridBagLayout()); // Centers the components
		GridBagConstraints gbc = new GridBagConstraints();

		// Single Column Layout
		gbc.gridx = 0;
		gbc.gridwidth = 1;

		// --- Title ---
		JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 38));
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 5, 0); // Space below title
		formPanel.add(titleLabel, gbc);

		// --- Subtitle ---
		JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
		subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, 30, 0); // Space before the form starts
		formPanel.add(subtitleLabel, gbc);

		// --- Username Label ---
		JLabel userLabel = new JLabel("USERNAME");
		userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST; // Pushes the text to the left!
		gbc.insets = new Insets(0, 0, 5, 0); // Space between label and text box
		formPanel.add(userLabel, gbc);

		// --- Username Field ---
		usernameField = new JTextField();
		usernameField.setPreferredSize(new Dimension(280, 40));
		usernameField.setBackground(Color.lightGray); // Color of the Username Field
		usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Field Border Color
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 15, 0); // Space before password section
		formPanel.add(usernameField, gbc);

		// --- Password Label ---
		JLabel passLabel = new JLabel("PASSWORD");
		passLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST; // Pushes the text to the left
		gbc.insets = new Insets(0, 0, 5, 0);
		formPanel.add(passLabel, gbc);

		// --- Password Field ---
		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(280, 40));
		passwordField.setBackground(Color.lightGray); // Color of the Password Field
		passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 30, 0); // Space before the login button
		formPanel.add(passwordField, gbc);

		// --- Login Button ---
		loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(160, 40));
		loginButton.setBackground(Color.darkGray); // Color of the login button
		loginButton.setForeground(Color.WHITE);
		loginButton.setFont(new Font("Arial", Font.BOLD, 14));
		loginButton.setFocusPainted(false); // Removes the focus border when clicked
		gbc.gridy = 6;
		gbc.fill = GridBagConstraints.NONE; // Keep button its preferred size
		gbc.anchor = GridBagConstraints.CENTER; // Make the button center
		formPanel.add(loginButton, gbc);

		add(formPanel, BorderLayout.CENTER); // Add the entire panel to the center of the window

		// Add Action Listeners in the Login Button Logic
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());

				// 1. Create a simple loading popup
				javax.swing.JDialog loadingDialog = new javax.swing.JDialog(LoginMenu.this, "Authenticating", true);
				loadingDialog.setSize(250, 100);
				loadingDialog.setLocationRelativeTo(LoginMenu.this);
				loadingDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

				logger.info("Logging in...");
				javax.swing.JLabel loadingLabel = new javax.swing.JLabel("Logging in, please wait...",
						SwingConstants.CENTER);
				loadingDialog.add(loadingLabel, BorderLayout.CENTER);

				// Waits 1.5 seconds then runs the code inside
				javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						logger.info("Opened Login Module.");
						loadingDialog.dispose(); // Close the loading popup after 1.5 seconds

						// Checks the user in the database
						if (authenticateUser(username, password)) {
							logger.info("Login Successful.");
							JOptionPane.showMessageDialog(LoginMenu.this, "Login Successful!", "Login",
									JOptionPane.INFORMATION_MESSAGE);

							logger.info("Calling Main Menu Module...");
							Main.openMainMenu(username, LoginMenu.this);
						} else {
							logger.error("Login Error.");
							JOptionPane.showMessageDialog(LoginMenu.this, "Invalid Username or Password.", "Login",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				});

				timer.setRepeats(false); // Makes sure it only runs once
				timer.start(); // Call and start the timer
				loadingDialog.setVisible(true); // Show the loading dialog
			}

			// Checking the user in the database
			private boolean authenticateUser(String enteredUsername, String enteredPassword) {
				String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
				try (java.sql.Connection connection = InitializeDatabase.getConnection();
						java.sql.PreparedStatement preparedstatement = connection.prepareStatement(sql)) {

					preparedstatement.setString(1, enteredUsername);
					preparedstatement.setString(2, enteredPassword);

					java.sql.ResultSet resultset = preparedstatement.executeQuery();
					return resultset.next();

				} catch (java.sql.SQLException ex) {
					System.out.println("Database login error: " + ex.getMessage());
					return false;
				}
			}
		});
	}
}