package Module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderMenu extends JFrame {

	// 50% AI

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(OrderMenu.class);

	// Initialization of variables
	private String loggedInUser;

	// Left Side:
	public JPanel leftPanel;

	// Right Side: Cart / Order Summary
	public JTable cartTable;
	public DefaultTableModel cartModel;
	public JLabel totalAmountLabel;

	// Bottom Buttons
	public JButton buttonCheckout, buttonCancel;

	// Custom Colors
	Color darkGray = new Color(0, 102, 102);
	Color slateGray = new Color(105, 115, 132);

	public OrderMenu(String username) {
		logger.info("Initializing Order Menu for user: " + username);
		this.loggedInUser = username;

		setTitle("Order Menu");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(20, 20));

		logger.debug("Setting up top menu bar...");

		setJMenuBar(Dropdown.createTopMenu(true, this.loggedInUser, this));

		// --- Modular UI ---
		logger.debug("Assembling modular UI panels...");
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createCenterSplitPanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		// Add padding around the entire window edges
		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		setLocationRelativeTo(null);

		// Wire up the flow
		setupFlow();

		logger.info("Order Menu initialized successfully.");
	}

	private JPanel createHeaderPanel() {
		logger.debug("Building Header Panel...");
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Order Menu", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
		topPanel.add(titleLabel, BorderLayout.CENTER);
		return topPanel;
	}

	private JPanel createCenterSplitPanel() {
		logger.debug("Building Center Split Panel (Drop Zone & Cart)...");

		// Splits the screen perfectly in half with a 20px gap in the middle
		JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));

		leftPanel = new JPanel(new BorderLayout());

		// Add a dashed border just so you can see where their workspace is while
		// testing.

		leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 2),
				"Product Selection Workspace (Backend Team)"));

		// --- Order Summary Cart ---
		JPanel rightPanel = new JPanel(new BorderLayout(0, 10));

		JLabel summaryTitle = new JLabel("Order Summary");
		summaryTitle.setFont(new Font("Arial", Font.BOLD, 18));

		String[] cartCols = { "Remove", "Item", "Qty", "Price", "Total" };
		cartModel = new DefaultTableModel(cartCols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		cartTable = new JTable(cartModel);

		// Style the table
		cartTable.setRowHeight(35);
		cartTable.setFont(new Font("Arial", Font.PLAIN, 14));
		cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		cartTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		cartTable.getTableHeader().setReorderingAllowed(false);

		// Center the text in the table
		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < cartTable.getColumnCount(); i++) {
			cartTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		cartTable.setFillsViewportHeight(true);

		totalAmountLabel = new JLabel("Total: ₱ 0.00", SwingConstants.RIGHT);
		totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 22));
		totalAmountLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

		rightPanel.add(summaryTitle, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
		rightPanel.add(totalAmountLabel, BorderLayout.SOUTH);

		// Add both sides to the split panel
		splitPanel.add(leftPanel);
		splitPanel.add(rightPanel);

		return splitPanel;
	}

	private JPanel createBottomPanel() {
		logger.debug("Building Bottom Panel (Action Buttons)...");

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));

		buttonCancel = new JButton("Cancel");
		buttonCancel.setPreferredSize(new Dimension(150, 40));
		buttonCancel.setBackground(slateGray);
		buttonCancel.setForeground(Color.WHITE);
		buttonCancel.setFocusPainted(false);

		buttonCheckout = new JButton("Checkout");
		buttonCheckout.setPreferredSize(new Dimension(150, 40));
		buttonCheckout.setBackground(darkGray);
		buttonCheckout.setForeground(Color.WHITE);
		buttonCheckout.setFocusPainted(false);

		bottomPanel.add(buttonCheckout);
		bottomPanel.add(buttonCancel);

		return bottomPanel;
	}

	private void setupFlow() {
		// When Checkout is clicked, open the Details dialog
		buttonCheckout.addActionListener(e -> {
			logger.info("User clicked Checkout button.");
			showCheckoutDetailsPopup();
		});

		// add UI to leftPanel and connect
		// cart model here
	}

	// Wireframe Popup 1: Checkout Details
	private void showCheckoutDetailsPopup() {
		logger.debug("Opening Checkout Details popup...");
		javax.swing.JDialog detailsDialog = new javax.swing.JDialog(this, "Checkout Details", true);
		detailsDialog.setSize(400, 250);
		detailsDialog.setLocationRelativeTo(this);
		detailsDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Please enter the following details:", SwingConstants.CENTER);
		detailsDialog.add(titleLabel, gbc);

		gbc.gridy = 1;
		gbc.gridwidth = 1;
		detailsDialog.add(new JLabel("Customer Name:"), gbc);
		JTextField nameField = new JTextField(15);
		gbc.gridx = 1;
		detailsDialog.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		detailsDialog.add(new JLabel("Telephone Number:"), gbc);
		JTextField phoneField = new JTextField(15);
		gbc.gridx = 1;
		detailsDialog.add(phoneField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		JButton proceedButton = new JButton("Proceed to Checkout");
		proceedButton.setBackground(darkGray);
		proceedButton.setForeground(Color.WHITE);
		proceedButton.setFocusPainted(false);

		proceedButton.addActionListener(e -> {
			logger.info("Proceeding with checkout for customer: " + nameField.getText());

			// process the final order to the database here
			// generate and show the receipt here

			detailsDialog.dispose();
		});

		detailsDialog.add(proceedButton, gbc);
		detailsDialog.setVisible(true);
	}
}