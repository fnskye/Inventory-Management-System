package User;

//includes action listener for buttons
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

//includes the java swing gui
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Database.InitializeDatabase;

public class RemoveAccountMenu extends JDialog {

	private static final Logger logger = LogManager.getLogger(RemoveAccountMenu.class);

	// Add default serial version ID
	private static final long serialVersionUID = 1L;

	// Initialization of variables
	private JTextField usernameField;
	private JTextField confirmUsernameField;
	private JLabel textLabel;

	// Custom Green Color
	Color darkGray = new Color(0, 102, 102);
	Color lightGreen = new Color(0, 204, 102);

	public RemoveAccountMenu(String currentUsername, JFrame parentFrame) {
		super(parentFrame, "Remove Account", true);
		setSize(400, 320);
		setLocationRelativeTo(parentFrame);
		setLayout(new GridBagLayout());

		String username = currentUsername;

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// --- GUI Components ---
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Remove Account", SwingConstants.CENTER);
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
		add(new JLabel("Confirm Username:"), gbc);
		confirmUsernameField = new JTextField(15);
		gbc.gridx = 1;
		add(confirmUsernameField, gbc);

		// Label for Account Creation
		textLabel = new JLabel(" ");
		textLabel.setForeground(null); // sets to green for success and red for warning
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		add(textLabel, gbc);

		// Update the Remove Account Button and save on row 5
		gbc.gridy = 4;
		JButton removeButton = new JButton("Remove Account");
		removeButton.setBackground(darkGray);
		removeButton.setForeground(Color.WHITE);
		add(removeButton, gbc);

		// Real-Time Username Matcher
		DocumentListener usernameListener = new DocumentListener() {
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
				removeButton.setBackground(darkGray);
				textLabel.setForeground(null);
				String username1 = new String(usernameField.getText());
				String username2 = new String(confirmUsernameField.getText());

				if (username2.length() > 0 && !username1.equals(username2)) {
					textLabel.setForeground(Color.RED);
					textLabel.setText("Username do not match!");
				} else if (!username1.isEmpty() && !username2.isEmpty() && username1.equals(username2)) {
					textLabel.setForeground(lightGreen);
					textLabel.setText("Username matched.");
					removeButton.setBackground(lightGreen);
				} else {
					removeButton.setBackground(darkGray);
					textLabel.setText(" ");
				}
			}
		};

		// Attach the listener to both password fields
		usernameField.getDocument().addDocumentListener(usernameListener);
		confirmUsernameField.getDocument().addDocumentListener(usernameListener);

		// --- Register the user when clicking the register button ---
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Calling remove user...");
				removeUser(username);
			}
		});
	}

	private void removeUser(String username) {
		String oldUsername = usernameField.getText();
		String confirmOldUsername = new String(confirmUsernameField.getText());

		// Checks if Username and Confirm Username Field are empty
		if (oldUsername.isEmpty() || confirmOldUsername.isEmpty()) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Please fill in all fields.");
			logger.info("Please fill in all fields.");
			return;
		}

		// Checks if Username is not equal to Confirm Username
		if (!oldUsername.equals(confirmOldUsername)) {
			return;
		}

		// Checks for Field if user tries to delete 'current user'
		if (oldUsername.equals(username) && !oldUsername.equalsIgnoreCase("admin")) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Cannot Delete Current Logged In User.");
			logger.info("Please fill in all fields.");
			return;
		}

		// Checks for Field if user tries to delete 'admin'
		if (oldUsername.equalsIgnoreCase("admin")) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Cannot delete the admin account.");
			logger.info("Cannot delete the admin account.");
			return;
		}

		String sql = "DELETE FROM Users WHERE username = ?";

		logger.info("Connecting to Database");
		try (java.sql.Connection connection = InitializeDatabase.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			// Initialize the show message dialog
			boolean showDialog = false;

			// Replace the first '?' with the value of oldUsername
			preparedStatement.setString(1, oldUsername);

			// Checks for rows if the TABLE got affected
			int rowsAffected = preparedStatement.executeUpdate();

			// Condition for update status for console logs
			String updateStatus = (rowsAffected > 0) ? "success" : "failed";

			if (updateStatus.equals("success")) {
				showDialog = true;
			}
			if (updateStatus.equals("failed")) {
				showDialog = false;
			}

			// Prints the update for console logs
			logger.info("Remove Update " + "for " + oldUsername + " " + updateStatus);

			// Condition to show message dialog
			if (showDialog) {
				JOptionPane.showMessageDialog(RemoveAccountMenu.this, "Removed: " + oldUsername);
				logger.info("Successfully Removed " + oldUsername);
				dispose();
			} else {
				JOptionPane.showMessageDialog(RemoveAccountMenu.this, oldUsername + " Account Not Found.");
				logger.info(oldUsername + " Account Not Found");
			}
		} catch (java.sql.SQLException ex) {
			logger.error("Database deletion error: " + ex.getMessage());
		}
	}
}
