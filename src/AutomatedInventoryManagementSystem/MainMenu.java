package AutomatedInventoryManagementSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenu extends JFrame {

	// --- Tentative Main Menu (might change in the future) ---

	// Add default serial version ID
	private static final long serialVersionUID = 1L;

	// Initialization of variables
	private String loggedInUser;

	public MainMenu(String username) {
		this.loggedInUser = username;

		// Setup the Main Window
		setTitle("Main Menu");
		setSize(600, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		setJMenuBar(Dropdown.createTopMenu(true, this));

		// Center Panel Setup
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 15, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		// Title & Welcome Message
		JLabel titleLabel = new JLabel("Main Menu", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
		gbc.gridy = 0;
		centerPanel.add(titleLabel, gbc);

		JLabel welcomeLabel = new JLabel("Welcome " + loggedInUser + "!", SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		gbc.gridy = 1;
		centerPanel.add(welcomeLabel, gbc);

		// Button Format
		Dimension buttonSize = new Dimension(250, 45);
		Color buttonColor = new Color(105, 115, 132);

		// --- Create 3 Buttons ---
		JButton inventoryButton = new JButton("Inventory");
		inventoryButton.setPreferredSize(buttonSize);
		inventoryButton.setBackground(buttonColor);
		inventoryButton.setForeground(Color.WHITE);
		gbc.gridy = 2;
		centerPanel.add(inventoryButton, gbc);

		JButton orderButton = new JButton("Order");
		orderButton.setPreferredSize(buttonSize);
		orderButton.setBackground(buttonColor);
		orderButton.setForeground(Color.WHITE);
		gbc.gridy = 3;
		centerPanel.add(orderButton, gbc);

		JButton salesReportButton = new JButton("Sales Report");
		salesReportButton.setPreferredSize(buttonSize);
		salesReportButton.setBackground(buttonColor);
		salesReportButton.setForeground(Color.WHITE);
		gbc.gridy = 4;
		centerPanel.add(salesReportButton, gbc);

		add(centerPanel, BorderLayout.CENTER);

		// --- Add Action Listeners for 3 buttons ---
		inventoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simulateLoading("Inventory");
			}
		});

		orderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simulateLoading("Order");
			}
		});

		salesReportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				simulateLoading("Sales Report");
			}
		});
	}

	// Simulate the Loading (tentative)
	private void simulateLoading(String moduleName) {

		javax.swing.JDialog loadingDialog = new javax.swing.JDialog(this, "Opening " + moduleName + " Module...", true);
		loadingDialog.setSize(250, 100);
		loadingDialog.setLocationRelativeTo(this);
		loadingDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

		System.out.println("\nOpening " + moduleName + " Module...");
		javax.swing.JLabel loadingLabel = new javax.swing.JLabel("Opening " + moduleName + " Module...",
				SwingConstants.CENTER);
		loadingDialog.add(loadingLabel, BorderLayout.CENTER);

		javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				loadingDialog.dispose();

				// --- Might Improve Soon when there is Inventory, Order, Sales Report ----

				// OverallSystem.openInventoryMenu(MainMenu.this);
				// OverallSystem.openOrderMenu(MainMenu.this);
				// OverallSystem.openSalesReport(MainMenu.this);
			}
		});

		timer.setRepeats(false);
		timer.start();
		loadingDialog.setVisible(true);
	}
}