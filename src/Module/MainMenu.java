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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.Main;

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

		logger.info("Main Menu User: " + this.loggedInUser);
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

		// .exe Safe loader
		ImageIcon inventoryIcon = loadIconFromResource("/Module/images/inventory.png", 32, 32);
		ImageIcon orderIcon = loadIconFromResource("/Module/images/order.png", 32, 32);
		ImageIcon salesIcon = loadIconFromResource("/Module/images/sales.png", 32, 32);

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
			private String loggedInUser = username;

			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Inventory Menu...");
				// simulateLoading("Inventory");
				Main.openInventoryMenu(this.loggedInUser, MainMenu.this);
			}
		});

		orderButton.addActionListener(new ActionListener() {
			private String loggedInUser = username;

			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Order Menu...");
				// simulateLoading("Order");
				Main.openOrderMenu(this.loggedInUser, MainMenu.this);
			}
		});

		salesReportButton.addActionListener(new ActionListener() {
			private String loggedInUser;

			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Clicked Sales Report...");
				// simulateLoading("Sales Report");
				Main.openInventoryMenu(this.loggedInUser, MainMenu.this);
			}
		});
	}
	/*
	 * // Simulate the Loading (tentative) private void simulateLoading(String
	 * moduleName) {
	 * 
	 * javax.swing.JDialog loadingDialog = new javax.swing.JDialog(this, "Opening "
	 * + moduleName + " Module...", true); loadingDialog.setSize(250, 100);
	 * loadingDialog.setLocationRelativeTo(this);
	 * loadingDialog.setDefaultCloseOperation(javax.swing.JDialog.
	 * DO_NOTHING_ON_CLOSE);
	 * 
	 * javax.swing.JLabel loadingLabel = new javax.swing.JLabel("Opening " +
	 * moduleName + " Module...", SwingConstants.CENTER);
	 * loadingDialog.add(loadingLabel, BorderLayout.CENTER);
	 * 
	 * javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {
	 * 
	 * @Override public void actionPerformed(ActionEvent evt) {
	 * logger.info("Opening " + moduleName + " Module..."); loadingDialog.dispose();
	 * } });
	 * 
	 * timer.setRepeats(false); timer.start(); loadingDialog.setVisible(true); }
	 */

	// Create custom button with decoupled Icon and Text
	private JButton createCustomButton(String text, ImageIcon icon, Color bgColor, Dimension size) {

		// We override the built-in paint method to bypass Java's layout rules
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(java.awt.Graphics graphics) {
				super.paintComponent(graphics); // Makes the button background and perfectly centered text

				// Manually stamp the icon directly onto the screen
				if (icon != null) {
					// Calculate exact vertical center for the image
					int iconHeight = (getHeight() - icon.getIconHeight()) / 2;

					// Draw the icon exactly 25 pixels from the left edge
					icon.paintIcon(this, graphics, 25, iconHeight);
				}
			}
		};

		button.setPreferredSize(size);
		button.setBackground(bgColor);
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Arial", Font.PLAIN, 15));
		button.setFocusPainted(false);
		button.setContentAreaFilled(true);

		// Centers the text perfectly
		button.setHorizontalAlignment(SwingConstants.CENTER);

		// Standard Slate Border
		button.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(80, 90, 110), 2));

		return button;
	}

	// --- .exe Safe Image Loader ---
	private ImageIcon loadIconFromResource(String resourcePath, int width, int height) {
		try {
			// This looks inside compiled .exe or .jar file
			java.net.URL imgURL = getClass().getResource(resourcePath);

			if (imgURL == null) {
				logger.error("CRITICAL: Image missing inside .exe file -> " + resourcePath);
				return null;
			}

			java.awt.Image img = javax.imageio.ImageIO.read(imgURL);
			java.awt.Image scaled = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
			return new ImageIcon(scaled);

		} catch (Exception e) {
			logger.error("Error loading resource: " + resourcePath, e);
			return null;
		}
	}
}
