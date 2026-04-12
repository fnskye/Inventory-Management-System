package Module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainMenu extends JFrame {

	private static final Logger logger = LogManager.getLogger(MainMenu.class);

	// --- Tentative Main Menu (might change in the future) ---

	// Add default serial version ID
	private static final long serialVersionUID = 1L;

	// Initialization of variables
	private String loggedInUser;

	public MainMenu(String username) {
		logger.info("Checking User...");
		this.loggedInUser = username;

		logger.info("User Identified: " + this.loggedInUser);
		// Setup the Main Window
		setTitle("Main Menu");
		setSize(600, 450);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		setJMenuBar(Dropdown.createTopMenu(true, username, this));

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

		// Load images from Module/images folder
		ImageIcon inventoryIcon = loadImageFromFile("src/Module/images/inventory.png", 32, 32);
		ImageIcon orderIcon = loadImageFromFile("src/Module/images/order.png", 32, 32);
		ImageIcon salesIcon = loadImageFromFile("src/Module/images/sales.png", 32, 32);

		// --- Create 3 Buttons with custom panel layout ---
		JButton inventoryButton = createCustomButton("Inventory", inventoryIcon, buttonColor, buttonSize);
		gbc.gridy = 2;
		centerPanel.add(inventoryButton, gbc);

		JButton orderButton = createCustomButton("Order", orderIcon, buttonColor, buttonSize);
		gbc.gridy = 3;
		centerPanel.add(orderButton, gbc);

		JButton salesReportButton = createCustomButton("Sales Report", salesIcon, buttonColor, buttonSize);
		gbc.gridy = 4;
		centerPanel.add(salesReportButton, gbc);

		add(centerPanel, BorderLayout.CENTER);

		// --- Add Action Listeners for 3 buttons ---
		inventoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Inventory Menu...");
				simulateLoading("Inventory");
			}
		});

		orderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Order Menu...");
				simulateLoading("Order");
			}
		});

		salesReportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Sales Report...");
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

		javax.swing.JLabel loadingLabel = new javax.swing.JLabel("Opening " + moduleName + " Module...",
				SwingConstants.CENTER);
		loadingDialog.add(loadingLabel, BorderLayout.CENTER);

		javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				logger.info("Opening " + moduleName + " Module...");
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

	// Style button with icon
	private void styleButton(JButton button, Dimension size, Color bgColor) {
		button.setPreferredSize(size);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Arial", Font.PLAIN, 14));
		button.setFocusPainted(false);
		button.setBorderPainted(true);
		button.setContentAreaFilled(true);
		button.setIconTextGap(15);
		button.setMargin(new Insets(0, 10, 0, 10));  // Left and right padding
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		button.setHorizontalTextPosition(SwingConstants.RIGHT);
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setBorder(javax.swing.BorderFactory.createLineBorder(
			new Color(80, 90, 110), 2));  // Consistent border
	}

	// Create custom button with perfectly aligned icon
	private JButton createCustomButton(String text, ImageIcon icon, Color bgColor, Dimension size) {
		JButton button = new JButton(text);

		int iconWidth = 0;
		int gap = 15; 

		if (icon != null) {
			button.setIcon(icon);
			iconWidth = icon.getIconWidth(); // Measure the icon dynamically
		}

		button.setPreferredSize(size);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Arial", Font.PLAIN, 14));
		button.setFocusPainted(false);
		button.setContentAreaFilled(true);

		// Group them together and put the text to the right of the icon
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setVerticalAlignment(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		button.setHorizontalTextPosition(SwingConstants.RIGHT);
		button.setIconTextGap(gap);

		// Create original gray line border
		javax.swing.border.Border line = javax.swing.BorderFactory.createLineBorder(new Color(80, 90, 110), 2);
		button.setBorder(line);

		button.setMargin(new java.awt.Insets(0, 105, 0, 0));

		return button;
	}

	// Load image from file path
	private ImageIcon loadImageFromFile(String filePath, int width, int height) {
		try {
			java.io.File file = new java.io.File(filePath);
			System.out.println("Attempting to load image: " + file.getAbsolutePath());
			if (!file.exists()) {
				System.err.println("Image file not found: " + file.getAbsolutePath());
				logger.error("Image file not found: " + file.getAbsolutePath());
				return null;
			}
			Image img = javax.imageio.ImageIO.read(file);
			if (img == null) {
				System.err.println("Failed to read image: " + filePath);
				logger.error("Failed to read image: " + filePath);
				return null;
			}
			Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			System.out.println("Successfully loaded image: " + filePath);
			logger.info("Successfully loaded image: " + filePath);
			return new ImageIcon(scaled);
		} catch (Exception e) {
			System.err.println("Error loading image: " + filePath);
			logger.error("Error loading image: " + filePath, e);
			e.printStackTrace();
			return null;
		}
	}
}
