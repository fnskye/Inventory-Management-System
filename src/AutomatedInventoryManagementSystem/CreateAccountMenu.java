package AutomatedInventoryManagementSystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//includes the java swing gui
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CreateAccountMenu extends JDialog {
	// Add default serial version ID
	private static final long serialVersionUID = 1L;

	// Initialization of variables
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JLabel textLabel;

	// Custom Green Color
	Color darkGray = new Color(0, 102, 102);
	Color lightGreen = new Color(0, 204, 102);

	public CreateAccountMenu(JFrame parentFrame) {
		super(parentFrame, "Create new Account", true);
		setSize(400, 320);
		setLocationRelativeTo(parentFrame);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// --- GUI Components ---
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		add(titleLabel, gbc);

		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(new JLabel("Username:"), gbc);
		usernameField = new JTextField(15);
		gbc.gridx = 1;
		add(usernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Password:"), gbc);
		passwordField = new JPasswordField(15);
		gbc.gridx = 1;
		add(passwordField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel("Confirm Password:"), gbc);
		confirmPasswordField = new JPasswordField(15);
		gbc.gridx = 1;
		add(confirmPasswordField, gbc);

		// Label for Account Creation
		textLabel = new JLabel(" ");
		textLabel.setForeground(null); // sets to green for success and red for warning
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(textLabel, gbc);

		// Update the Register Button and save on row 5
		gbc.gridy = 5;
		JButton registerButton = new JButton("Register Account");
		registerButton.setBackground(darkGray);
		registerButton.setForeground(Color.WHITE);
		add(registerButton, gbc);

		// Real-Time Password Matcher
		DocumentListener passwordListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				checkMatch();
			}

			public void insertUpdate(DocumentEvent e) {
				checkMatch();
			}

			public void removeUpdate(DocumentEvent e) {
				checkMatch();
			}

			private void checkMatch() {
				registerButton.setBackground(darkGray);
				textLabel.setForeground(null);
				String password1 = new String(passwordField.getPassword());
				String password2 = new String(confirmPasswordField.getPassword());

				if (password2.length() > 0 && !password1.equals(password2)) {
					textLabel.setForeground(Color.RED);
					textLabel.setText("Passwords do not match!");
				} else if (!password1.isEmpty() && !password2.isEmpty() && password1.equals(password2)) {
					textLabel.setForeground(lightGreen);
					textLabel.setText("Passwords matched.");
					registerButton.setBackground(lightGreen);
				} else {
					registerButton.setBackground(darkGray);
					textLabel.setText(" ");
				}
			}
		};

		// Attach the listener to both password fields
		passwordField.getDocument().addDocumentListener(passwordListener);
		confirmPasswordField.getDocument().addDocumentListener(passwordListener);

		// --- Register the user when clicking the register button ---
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				registerNewUser();
			}
		});
	}

	private void registerNewUser() {
		String newUsername = usernameField.getText();
		String newPassword = new String(passwordField.getPassword());
		String confirmPassword = new String(confirmPasswordField.getPassword());

		if (newUsername.isEmpty() || newPassword.isEmpty()) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Please fill in all fields.");
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			return;
		}

		String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";

		try (Connection connection = Database.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, newUsername);
			preparedStatement.setString(2, newPassword);
			preparedStatement.executeUpdate();

			textLabel.setForeground(lightGreen);
			JOptionPane.showMessageDialog(CreateAccountMenu.this, "New Account Created: " + newUsername);
			dispose();

		} catch (SQLException ex) {
			// Catch both types of SQLite constraint errors
			if (ex.getMessage().contains("UNIQUE constraint failed")
					|| ex.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				textLabel.setForeground(Color.RED);
				textLabel.setText("Username already exists. Choose another.");
			} else {
				JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
