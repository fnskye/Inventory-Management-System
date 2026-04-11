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

import Database.InitializeDatabase;

public class RemoveAccountMenu extends JDialog {
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
				removeUser(username);
			}
		});
	}

	private void removeUser(String username) {
		String oldUsername = usernameField.getText();
		String confirmOldUsername = new String(confirmUsernameField.getText());

		if (oldUsername.isEmpty() || confirmOldUsername.isEmpty()) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Please fill in all fields.");
			return;
		}

		if (!oldUsername.equals(confirmOldUsername)) {
			return;
		}

		if (oldUsername.equals(username) && !oldUsername.equalsIgnoreCase("admin")) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Cannot Delete Current Logged In User.");
			return;
		}

		if (oldUsername.equalsIgnoreCase("admin")) {
			textLabel.setForeground(Color.RED);
			textLabel.setText("Cannot delete the admin account.");
			System.out.println("\nCannot delete the admin account.");
			return;
		}

		String sql = "DELETE FROM Users WHERE username = ?";

		try (java.sql.Connection connection = InitializeDatabase.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, oldUsername);

			int rowsAffected = preparedStatement.executeUpdate();

			String updateStatus = (rowsAffected > 0) ? "success" : "failed";

			System.out.println("Remove Update " + "for " + oldUsername + " " + updateStatus);

			textLabel.setForeground(lightGreen);
			JOptionPane.showMessageDialog(RemoveAccountMenu.this, "Removed: " + oldUsername);
			dispose();

		} catch (java.sql.SQLException ex) {
			System.out.println("Database deletion error: " + ex.getMessage());
		}
	}
}
