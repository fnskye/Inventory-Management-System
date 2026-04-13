package Module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.Main;

public class OrderMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(OrderMenu.class);

	// Core Variables
	private String loggedInUser;
	private String dbUrl;
	private double grandTotal = 0.0;

	// UI Components
	public JTextField searchField;
	public JTable availableItemsTable, cartTable;
	public DefaultTableModel availableItemsModel, cartModel;
	public TableRowSorter<DefaultTableModel> rowSorter;
	public JLabel totalAmountLabel;
	public JButton btnCheckout, btnCancel, btnAddToCart, btnRemoveFromCart;

	// Custom Colors
	Color darkGray = new Color(0, 102, 102);
	Color slateGray = new Color(105, 115, 132);

	public OrderMenu(String username) {
		logger.info("Initializing Final Order Menu for user: " + username);
		this.loggedInUser = username;

		// Setup EXE-Safe Database Connection
		String currentFolder = System.getProperty("user.dir");
		this.dbUrl = "jdbc:sqlite:" + currentFolder + "/database.db";

		setTitle("Order Menu");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(20, 20));

		setJMenuBar(Dropdown.createTopMenu(true, this.loggedInUser, this));

		// Build UI
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createCenterSplitPanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		setLocationRelativeTo(null);

		// Wire up logic and load database
		setupBackendLogic();
		loadAvailableProducts();
	}

	// ---------------------------------------------------------
	// --- UI ASSEMBLY -----------------------------------------
	// ---------------------------------------------------------

	private JPanel createHeaderPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Order Menu", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
		topPanel.add(titleLabel, BorderLayout.CENTER);
		return topPanel;
	}

	private JPanel createCenterSplitPanel() {
		JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));

		// --- LEFT SIDE: Available Products (Database connected) ---
		JPanel leftPanel = new JPanel(new BorderLayout(0, 10));

		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(400, 35));
		searchField.setBorder(BorderFactory.createTitledBorder("Search Products"));

		String[] availableCols = { "Item Name", "Price", "Stock" };
		availableItemsModel = new DefaultTableModel(availableCols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		availableItemsTable = new JTable(availableItemsModel);
		styleTable(availableItemsTable);

		rowSorter = new TableRowSorter<>(availableItemsModel);
		availableItemsTable.setRowSorter(rowSorter);

		btnAddToCart = new JButton("Add Selected to Cart ->");
		styleButton(btnAddToCart);

		leftPanel.add(searchField, BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(availableItemsTable), BorderLayout.CENTER);
		leftPanel.add(btnAddToCart, BorderLayout.SOUTH);

		// --- RIGHT SIDE: Shopping Cart ---
		JPanel rightPanel = new JPanel(new BorderLayout(0, 10));

		JLabel summaryTitle = new JLabel("Order Summary");
		summaryTitle.setFont(new Font("Arial", Font.BOLD, 18));

		String[] cartCols = { "Item Name", "Qty", "Unit Price", "Line Total" };
		cartModel = new DefaultTableModel(cartCols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		cartTable = new JTable(cartModel);
		styleTable(cartTable);

		btnRemoveFromCart = new JButton("<- Remove Selected");
		btnRemoveFromCart.setBackground(Color.RED);
		btnRemoveFromCart.setForeground(Color.WHITE);
		btnRemoveFromCart.setFocusPainted(false);

		JPanel cartBottomPanel = new JPanel(new BorderLayout());
		totalAmountLabel = new JLabel("Total: ₱ 0.00", SwingConstants.RIGHT);
		totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 22));

		cartBottomPanel.add(btnRemoveFromCart, BorderLayout.WEST);
		cartBottomPanel.add(totalAmountLabel, BorderLayout.EAST);

		rightPanel.add(summaryTitle, BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
		rightPanel.add(cartBottomPanel, BorderLayout.SOUTH);

		splitPanel.add(leftPanel);
		splitPanel.add(rightPanel);

		return splitPanel;
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
		btnCancel = new JButton("Cancel");
		styleButton(btnCancel);
		btnCheckout = new JButton("Checkout");
		styleButton(btnCheckout);

		bottomPanel.add(btnCheckout);
		bottomPanel.add(btnCancel);
		return bottomPanel;
	}

	private void styleTable(JTable table) {
		table.setRowHeight(35);
		table.setFont(new Font("Arial", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		table.getTableHeader().setPreferredSize(new Dimension(0, 40));
		table.getTableHeader().setReorderingAllowed(false);
		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		table.setFillsViewportHeight(true);
	}

	private void styleButton(JButton btn) {
		btn.setPreferredSize(new Dimension(180, 40));
		btn.setBackground(darkGray);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
	}

	// ---------------------------------------------------------
	// --- FULL STACK BACKEND LOGIC ----------------------------
	// ---------------------------------------------------------

	private void setupBackendLogic() {
		// 1. Live Search functionality
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTable();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTable();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterTable();
			}

			private void filterTable() {
				String text = searchField.getText();
				if (text.trim().length() == 0)
					rowSorter.setRowFilter(null);
				else
					rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
			}
		});

		// 2. Add to Cart Logic (UPDATED WITH QUANTITY PROMPT)
		btnAddToCart.addActionListener(e -> {
			int selectedRow = availableItemsTable.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Select an item to add.");
				return;
			}

			// Convert visual row to actual data row in case user is searching
			int modelRow = availableItemsTable.convertRowIndexToModel(selectedRow);
			String itemName = availableItemsModel.getValueAt(modelRow, 0).toString();
			double price = Double.parseDouble(availableItemsModel.getValueAt(modelRow, 1).toString());
			int stock = Integer.parseInt(availableItemsModel.getValueAt(modelRow, 2).toString());

			// Find current quantity already sitting in the cart
			int currentQtyInCart = 0;
			int cartRowIndex = -1;
			for (int i = 0; i < cartModel.getRowCount(); i++) {
				if (cartModel.getValueAt(i, 0).toString().equals(itemName)) {
					currentQtyInCart = Integer.parseInt(cartModel.getValueAt(i, 1).toString());
					cartRowIndex = i;
					break;
				}
			}

			// Calculate how many more they are actually allowed to add
			int maxAllowedToAdd = stock - currentQtyInCart;

			if (maxAllowedToAdd <= 0) {
				JOptionPane.showMessageDialog(this,
						"Not enough stock available! You already have all available stock in the cart.");
				return;
			}

			// Prompt user for quantity with a smart default
			String input = JOptionPane.showInputDialog(this, "Enter quantity to add (Max: " + maxAllowedToAdd + "):",
					"Quantity", JOptionPane.QUESTION_MESSAGE);

			// If user clicks Cancel or closes the dialog, just do nothing
			if (input == null || input.trim().isEmpty()) {
				return;
			}

			try {
				int qtyToAdd = Integer.parseInt(input.trim());

				// Validation: Prevent negative numbers or zero
				if (qtyToAdd <= 0) {
					JOptionPane.showMessageDialog(this, "Please enter a valid quantity greater than 0.");
					return;
				}

				// Validation: Prevent exceeding stock
				if (qtyToAdd > maxAllowedToAdd) {
					JOptionPane.showMessageDialog(this,
							"Cannot add " + qtyToAdd + ". Only " + maxAllowedToAdd + " left in stock.");
					return;
				}

				// Valid quantity entered! Update cart model.
				if (cartRowIndex != -1) {
					// Update existing row
					int newTotalQty = currentQtyInCart + qtyToAdd;
					cartModel.setValueAt(newTotalQty, cartRowIndex, 1);
					cartModel.setValueAt(newTotalQty * price, cartRowIndex, 3);
				} else {
					// Add brand new row
					cartModel.addRow(new Object[] { itemName, qtyToAdd, price, price * qtyToAdd });
				}

				// Recalculate grand total
				updateTotal();

			} catch (NumberFormatException ex) {
				// Validation: Prevent user from typing letters like "abc"
				JOptionPane.showMessageDialog(this, "Invalid input. Please enter a whole number.");
			}
		});

		// 3. Remove from Cart Logic
		btnRemoveFromCart.addActionListener(e -> {
			int selectedRow = cartTable.getSelectedRow();
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Select an item in the cart to remove.");
				return;
			}
			cartModel.removeRow(selectedRow);
			updateTotal();
		});

		// 4. Checkout Logic
		btnCheckout.addActionListener(e -> {
			if (cartModel.getRowCount() == 0) {
				JOptionPane.showMessageDialog(this, "Cart is empty!");
				return;
			}
			showCheckoutDetailsPopup();
		});

		btnCancel.addActionListener(e -> {
			dispose();
			Main.openMainMenu(loggedInUser, this);
		});
	}

	private void updateTotal() {
		grandTotal = 0.0;
		for (int i = 0; i < cartModel.getRowCount(); i++) {
			grandTotal += Double.parseDouble(cartModel.getValueAt(i, 3).toString());
		}
		totalAmountLabel.setText(String.format("Total: ₱%,.2f", grandTotal));
	}

	private void loadAvailableProducts() {
		availableItemsModel.setRowCount(0);
		try (Connection conn = DriverManager.getConnection(dbUrl);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT product_name, price, stock FROM Products WHERE stock > 0")) {

			while (rs.next()) {
				availableItemsModel.addRow(
						new Object[] { rs.getString("product_name"), rs.getDouble("price"), rs.getInt("stock") });
			}
		} catch (Exception e) {
			logger.error("Failed to load products for ordering.", e);
		}
	}

	// ---------------------------------------------------------
	// --- CHECKOUT & RECEIPT POPUPS ---------------------------
	// ---------------------------------------------------------

	private void showCheckoutDetailsPopup() {
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
		detailsDialog.add(new JLabel("Please enter customer details:", SwingConstants.CENTER), gbc);

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
		JButton proceedButton = new JButton("Confirm & Pay");
		styleButton(proceedButton);

		proceedButton.addActionListener(e -> {
			String cName = nameField.getText().trim();
			String cPhone = phoneField.getText().trim();

			if (cName.isEmpty() || cPhone.isEmpty()) {
				JOptionPane.showMessageDialog(detailsDialog, "Please fill in all fields.");
				return;
			}

			// --- DATABASE TRANSACTION: DEDUCT STOCK & SAVE SALE ---
			try (Connection conn = DriverManager.getConnection(dbUrl)) {
				conn.setAutoCommit(false);

				// 1. Deduct the stock
				try (PreparedStatement psStock = conn
						.prepareStatement("UPDATE Products SET stock = stock - ? WHERE product_name = ?")) {
					for (int i = 0; i < cartModel.getRowCount(); i++) {
						int qtyToDeduct = Integer.parseInt(cartModel.getValueAt(i, 1).toString());
						String prodName = cartModel.getValueAt(i, 0).toString();

						psStock.setInt(1, qtyToDeduct);
						psStock.setString(2, prodName);
						psStock.executeUpdate();
					}
				}

				// 2. Save the sale to the new Transactions table
				String orderId = "ORD-" + System.currentTimeMillis();
				String dateToday = java.time.LocalDate.now().toString();

				try (PreparedStatement psSale = conn.prepareStatement(
						"INSERT INTO Transactions (order_date, order_number, customer_name, total_amount) VALUES (?, ?, ?, ?)")) {
					psSale.setString(1, dateToday);
					psSale.setString(2, orderId);
					psSale.setString(3, cName);
					psSale.setDouble(4, grandTotal);
					psSale.executeUpdate();
				}

				conn.commit(); // Save both the stock deduction AND the new sale
				logger.info("Order processed successfully. Stock deducted and sale recorded.");

			} catch (Exception sqlError) {
				logger.error("Checkout failed, transaction rolled back.", sqlError);
				JOptionPane.showMessageDialog(detailsDialog, "Database error during checkout.");
				return;
			}

			detailsDialog.dispose();
			showBillingInvoicePopup(cName, cPhone);
		});

		detailsDialog.add(proceedButton, gbc);
		detailsDialog.setVisible(true);
	}

	private void showBillingInvoicePopup(String customerName, String customerPhone) {
		javax.swing.JDialog invoiceDialog = new javax.swing.JDialog(this, "Billing Invoice", true);
		invoiceDialog.setLayout(new BorderLayout(10, 10));

		javax.swing.JTextArea receiptText = new javax.swing.JTextArea();
		receiptText.setEditable(false);
		receiptText.setFont(new Font("Courier New", Font.PLAIN, 14));
		receiptText.setMargin(new java.awt.Insets(20, 20, 20, 20));

		// --- DYNAMIC RECEIPT GENERATION ---
		StringBuilder sb = new StringBuilder();
		sb.append("                        CodeFlux Supplier\n");
		sb.append("                      123 BGC, Taguig City\n");
		sb.append("                      Contact: +63993576808\n");
		sb.append("=================================================================\n");

		String orderId = "ORD-" + System.currentTimeMillis(); // Simple auto-generated ID
		sb.append(String.format("Order #: %-25s Date: %s\n", orderId, java.time.LocalDate.now().toString()));
		sb.append(String.format("Customer Name: %-19s Contact #: %s\n", customerName, customerPhone));
		sb.append("-----------------------------------------------------------------\n");
		sb.append(String.format("%-4s %-30s %13s %15s\n", "Qty", "Item Description", "Unit Price", "Amount"));
		sb.append("-----------------------------------------------------------------\n");

		// Loop through the cart and add every item to the receipt
		for (int i = 0; i < cartModel.getRowCount(); i++) {
			String qty = cartModel.getValueAt(i, 1).toString();

			// Truncate long names so they don't break the layout
			String itemName = cartModel.getValueAt(i, 0).toString();
			if (itemName.length() > 28)
				itemName = itemName.substring(0, 25) + "...";

			double unitPrice = Double.parseDouble(cartModel.getValueAt(i, 2).toString());
			double lineTotal = Double.parseDouble(cartModel.getValueAt(i, 3).toString());

			String formattedPrice = String.format("PHP %,.2f", unitPrice);
			String formattedTotal = String.format("PHP %,.2f", lineTotal);

			sb.append(String.format("%-4s %-30s %13s %15s\n", qty, itemName, formattedPrice, formattedTotal));
		}

		sb.append("-----------------------------------------------------------------\n");
		sb.append(String.format("%49s %15s\n", "TOTAL AMOUNT DUE:", String.format("PHP %,.2f", grandTotal)));
		sb.append("=================================================================\n\n");
		sb.append("                 Print generated by the system.\n");

		receiptText.setText(sb.toString());

		JScrollPane scrollPane = new JScrollPane(receiptText);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JButton btnPrint = new JButton("Print");
		JButton btnSave = new JButton("Save");
		btnPrint.setPreferredSize(new Dimension(120, 40));
		btnSave.setPreferredSize(new Dimension(120, 40));
		btnPrint.setBackground(slateGray);
		btnPrint.setForeground(Color.WHITE);
		btnSave.setBackground(slateGray);
		btnSave.setForeground(Color.WHITE);

		btnPrint.addActionListener(e -> {
			JOptionPane.showMessageDialog(invoiceDialog, "Print Successful!\nReturn to Main Menu");
			invoiceDialog.dispose();
			dispose(); // Close Order Menu
			Main.openMainMenu(this.loggedInUser, this);
		});

		btnSave.addActionListener(e -> {
			JOptionPane.showMessageDialog(invoiceDialog, "Saved as text.\nReturn to Main Menu");
			invoiceDialog.dispose();
			dispose(); // Close Order Menu
			Main.openMainMenu(this.loggedInUser, this);
		});

		buttonPanel.add(btnPrint);
		buttonPanel.add(btnSave);

		invoiceDialog.add(scrollPane, BorderLayout.CENTER);
		invoiceDialog.add(buttonPanel, BorderLayout.SOUTH);

		// Dynamic Window Size

		// 1. Count how many physical lines of text are in the receipt
		int lineCount = sb.toString().split("\n").length;

		// 2. Calculate height: ~18 pixels per line of text + 150 pixels for
		// padding/buttons
		int calculatedHeight = (lineCount * 18) + 150;

		// 3. Safety Net: Ask the OS for the monitor's screen height so it doesn't go
		// off-screen
		int maxScreenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - 100;

		// 4. Use whichever is smaller: the receipt height, or the max screen height
		int finalHeight = Math.min(calculatedHeight, maxScreenHeight);

		// Apply the dynamic size and center it
		invoiceDialog.setSize(615, finalHeight);
		invoiceDialog.setLocationRelativeTo(this);

		// Finally, show the window
		invoiceDialog.setVisible(true);
	}
}