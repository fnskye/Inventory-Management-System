package Module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class InventoryMenu extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InventoryMenu.class);

	// Initialization of variables
	private String loggedInUser;
	private String dbUrl; // Centralized Exe-Safe Database URL

	// UI Components
	private JTextField searchField;
	private JTable inventoryTable; // UNCOMMENTED
	private DefaultTableModel tableModel; // UNCOMMENTED
	private TableRowSorter<DefaultTableModel> rowSorter; // ADDED FOR LIVE SEARCH
	private JButton btnAdd, btnRestock, btnEdit, btnDelete;

	// Custom Colors
	Color darkGray = new Color(0, 102, 102);
	Color lightGreen = new Color(0, 204, 102);

	public InventoryMenu(String username) {
		logger.info("Checking User...");
		this.loggedInUser = username;

		// --- EXE-SAFE DATABASE URL SETUP ---
		String currentFolder = System.getProperty("user.dir");
		this.dbUrl = "jdbc:sqlite:" + currentFolder + "/database.db";

		setTitle("Inventory Management");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		setJMenuBar(Dropdown.createTopMenu(true, this.loggedInUser, this));

		// --- Modular UI Assembly ---
		add(createTopPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		// --- LIVE SEARCH LOGIC ---
		setupLiveSearch();

		setLocationRelativeTo(null);
		loadInventoryData();
	}

	// --- Modular Methods for UI ---

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout(5, 5));
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

		// Create the text field
		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(800, 35));

		// Create the Search Button
		JButton searchBtn = new JButton("Search");
		searchBtn.setBackground(lightGreen);
		searchBtn.setForeground(Color.WHITE);
		searchBtn.setPreferredSize(new Dimension(100, 35));
		searchBtn.setFocusPainted(false);

		// Wrap them together in a horizontal container
		JPanel searchContainer = new JPanel(new BorderLayout(10, 0));
		searchContainer.add(searchField, BorderLayout.CENTER);
		searchContainer.add(searchBtn, BorderLayout.EAST);

		topPanel.add(titleLabel, BorderLayout.NORTH);
		topPanel.add(searchContainer, BorderLayout.SOUTH);

		return topPanel;
	}

	// Fixed Formatting!
	private JScrollPane createTablePanel() {
		String[] columns = { "Product Name", "Category", "Price", "Stock", "Status" };

		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		inventoryTable = new JTable(tableModel);

		// --- LIVE SEARCH SORTER ATTACHED ---
		rowSorter = new TableRowSorter<>(tableModel);
		inventoryTable.setRowSorter(rowSorter);

		// Styling
		inventoryTable.setRowHeight(35);
		inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
		inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		inventoryTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		inventoryTable.getTableHeader().setReorderingAllowed(false);

		// Centering
		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
		for (int i = 0; i < 5; i++) {
			inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		inventoryTable.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(inventoryTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		return scrollPane;
	}

	// Fixed Formatting!
	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

		btnAdd = new JButton("Add Product");
		btnRestock = new JButton("Restock");
		btnEdit = new JButton("Edit");
		btnDelete = new JButton("Delete");

		// Delete Button Specific Style
		btnDelete.setPreferredSize(new Dimension(150, 40));
		btnDelete.setBackground(Color.red);
		btnDelete.setForeground(Color.WHITE);
		btnDelete.setFocusPainted(false);

		// General Button Styling
		styleButton(btnAdd);
		styleButton(btnRestock);
		styleButton(btnEdit);

		// Wiring up the Actions
		btnAdd.addActionListener(e -> openAddProductWindow());
		btnRestock.addActionListener(e -> restockProduct());
		btnEdit.addActionListener(e -> openEditProductWindow());
		btnDelete.addActionListener(e -> deleteSelectedProducts());

		bottomPanel.add(btnAdd);
		bottomPanel.add(btnRestock);
		bottomPanel.add(btnEdit);
		bottomPanel.add(btnDelete);

		return bottomPanel;
	}

	private void styleButton(JButton btn) {
		btn.setPreferredSize(new Dimension(150, 40));
		btn.setBackground(darkGray);
		btn.setForeground(Color.WHITE);
		btn.setFocusPainted(false);
	}

	// --- Live Search Implementation ---
	private void setupLiveSearch() {
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
				if (text.trim().length() == 0) {
					rowSorter.setRowFilter(null);
				} else {
					rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
				}
			}
		});
	}

	// --- Database Operations (Now using central dbUrl) ---

	private void loadInventoryData() {
		tableModel.setRowCount(0);

		// Using the central dbUrl
		try (java.sql.Connection connection = java.sql.DriverManager.getConnection(this.dbUrl);
				java.sql.Statement statement = connection.createStatement();
				java.sql.ResultSet resultset = statement.executeQuery("SELECT * FROM Products")) {

			while (resultset.next()) {
				String name = resultset.getString("product_name");
				String category = resultset.getString("category");
				double price = resultset.getDouble("price");
				int stock = resultset.getInt("stock");

				String status;
				if (stock == 0) {
					status = "Out of Stock";
				} else if (stock <= 10) {
					status = "Low Stock";
				} else {
					status = "In Stock";
				}

				Object[] add = new Object[] { name, category, price, stock, status };
				tableModel.addRow(add);
			}
			logger.info("Inventory data loaded successfully.");
		} catch (Exception e) {
			logger.error("Failed to load inventory data.", e);
		}
	}

	private void openAddProductWindow() {
		javax.swing.JDialog addDialog = new javax.swing.JDialog(this, "Add New Product", true);
		addDialog.setSize(400, 350);
		addDialog.setLocationRelativeTo(this);
		addDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Add Product", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		addDialog.add(titleLabel, gbc);

		gbc.gridy = 1;
		gbc.gridwidth = 1;
		addDialog.add(new JLabel("Product Name:"), gbc);
		JTextField nameField = new JTextField(15);
		gbc.gridx = 1;
		addDialog.add(nameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		addDialog.add(new JLabel("Category:"), gbc);
		JTextField categoryField = new JTextField(15);
		gbc.gridx = 1;
		addDialog.add(categoryField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		addDialog.add(new JLabel("Price (₱):"), gbc);
		JTextField priceField = new JTextField(15);
		gbc.gridx = 1;
		addDialog.add(priceField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		addDialog.add(new JLabel("Initial Stock:"), gbc);
		JTextField stockField = new JTextField(15);
		gbc.gridx = 1;
		addDialog.add(stockField, gbc);

		JLabel textLabel = new JLabel(" ");
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		addDialog.add(textLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 2;
		JButton saveProductButton = new JButton("Save Product");
		saveProductButton.setBackground(darkGray);
		saveProductButton.setForeground(Color.WHITE);
		saveProductButton.setFocusPainted(false);

		stockField.getDocument().addDocumentListener(new DocumentListener() {
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
				String name = nameField.getText().trim();
				String category = categoryField.getText().trim();
				String stock = stockField.getText().trim();
				String price = priceField.getText().trim();

				if (stock.isEmpty()) {
					saveProductButton.setBackground(darkGray);
					textLabel.setText(" ");
				} else {
					try {
						int val = Integer.parseInt(stock);
						if (val > 0 && !price.isEmpty() && !stock.isEmpty() && !category.isEmpty() && !name.isEmpty()) {
							saveProductButton.setBackground(lightGreen);
						}
						if (val <= 0) {
							saveProductButton.setBackground(darkGray);
							textLabel.setForeground(Color.RED);
							textLabel.setText("Initial stock must be greater than 0.");
						}
					} catch (NumberFormatException ex) {
						saveProductButton.setBackground(darkGray);
						textLabel.setForeground(Color.RED);
						textLabel.setText("Invalid number format.");
					}
				}
			}
		});

		saveProductButton.addActionListener(e ->

		{
			try {
				String name = nameField.getText();
				String category = categoryField.getText();
				double price = Double.parseDouble(priceField.getText());

				int stock = Integer.parseInt(stockField.getText());
				if (stock <= 0) {
					return;
				}

				// Using the central dbUrl
				try (java.sql.Connection connection = java.sql.DriverManager.getConnection(this.dbUrl);
						java.sql.PreparedStatement preparedStatement = connection.prepareStatement(
								"INSERT INTO Products (product_name, category, price, stock) VALUES (?, ?, ?, ?)")) {

					preparedStatement.setString(1, name);
					preparedStatement.setString(2, category);
					preparedStatement.setDouble(3, price);
					preparedStatement.setInt(4, stock);
					preparedStatement.executeUpdate();

					logger.info("New product added: " + name);
					javax.swing.JOptionPane.showMessageDialog(addDialog, "Product added successfully.");
					addDialog.dispose();
					loadInventoryData();
				} catch (Exception sqlException) {
					logger.error("Database error while adding product", sqlException);
					javax.swing.JOptionPane.showMessageDialog(addDialog, "Error: Product name might already exist.");
				}
			} catch (NumberFormatException numError) {
				javax.swing.JOptionPane.showMessageDialog(addDialog,
						"Please enter a valid numbers for Price and Stock.");
			}
		});

		addDialog.add(saveProductButton, gbc);
		addDialog.setVisible(true);
	}

	private void restockProduct() {
		int selectedRow = inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			javax.swing.JOptionPane.showMessageDialog(this, "Please select a product to restock.");
			return;
		}

		String productName = tableModel.getValueAt(selectedRow, 0).toString();

		javax.swing.JDialog restockDialog = new javax.swing.JDialog(this, "Restock Product", true);
		restockDialog.setSize(400, 320);
		restockDialog.setLocationRelativeTo(this);
		restockDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Restock Product", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		restockDialog.add(titleLabel, gbc);

		gbc.gridy = 1;
		gbc.gridwidth = 1;
		restockDialog.add(new JLabel("Selected Item:"), gbc);
		gbc.gridx = 1;
		restockDialog.add(new JLabel(productName), gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		restockDialog.add(new JLabel("Quantity:"), gbc);
		JTextField stockField = new JTextField(15);
		gbc.gridx = 1;
		restockDialog.add(stockField, gbc);

		JLabel textLabel = new JLabel(" ");
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		restockDialog.add(textLabel, gbc);

		gbc.gridy = 4;
		JButton confirmRestockButton = new JButton("Confirm Restock");
		confirmRestockButton.setBackground(darkGray);
		confirmRestockButton.setForeground(Color.WHITE);
		confirmRestockButton.setFocusPainted(false);
		restockDialog.add(confirmRestockButton, gbc);

		stockField.getDocument().addDocumentListener(new DocumentListener() {
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
				String stock = stockField.getText().trim();
				if (stock.isEmpty()) {
					confirmRestockButton.setBackground(darkGray);
					textLabel.setText(" ");
				} else {
					try {
						int val = Integer.parseInt(stock);
						if (val > 0) {
							confirmRestockButton.setBackground(lightGreen);
							textLabel.setForeground(lightGreen);
						} else {
							confirmRestockButton.setBackground(darkGray);
							textLabel.setForeground(Color.RED);
							textLabel.setText("Quantity must be greater than 0.");
						}
					} catch (NumberFormatException ex) {
						confirmRestockButton.setBackground(darkGray);
						textLabel.setForeground(Color.RED);
						textLabel.setText("Invalid number format.");
					}
				}
			}
		});

		confirmRestockButton.addActionListener(e -> {
			try {
				int addedStock = Integer.parseInt(stockField.getText().trim());
				if (addedStock <= 0)
					return;

				// Using the central dbUrl
				try (java.sql.Connection connection = java.sql.DriverManager.getConnection(this.dbUrl);
						java.sql.PreparedStatement preparedStatement = connection
								.prepareStatement("UPDATE Products SET stock = stock + ? WHERE product_name = ?")) {

					preparedStatement.setInt(1, addedStock);
					preparedStatement.setString(2, productName);
					preparedStatement.executeUpdate();

					javax.swing.JOptionPane.showMessageDialog(restockDialog,
							"Successfully added " + addedStock + " units!");
					restockDialog.dispose();
					loadInventoryData();
				} catch (Exception sqlEx) {
					logger.error("Database error during restock", sqlEx);
				}

			} catch (Exception ex) {
				logger.error("Please Input a number, Error updating quantity in database.", ex);
				javax.swing.JOptionPane.showMessageDialog(this, "Please input a number.");
				return;
			}
		});
		restockDialog.setVisible(true);
	}

	private void openEditProductWindow() {
		int selectedRow = inventoryTable.getSelectedRow();
		if (selectedRow == -1) {
			javax.swing.JOptionPane.showMessageDialog(this, "Please select a product to edit!");
			return;
		}

		String oldName = tableModel.getValueAt(selectedRow, 0).toString();
		String oldCategory = tableModel.getValueAt(selectedRow, 1).toString();
		String oldPrice = tableModel.getValueAt(selectedRow, 2).toString();
		String oldStock = tableModel.getValueAt(selectedRow, 3).toString();

		javax.swing.JDialog editDialog = new javax.swing.JDialog(this, "Edit Product", true);
		editDialog.setSize(400, 380);
		editDialog.setLocationRelativeTo(this);
		editDialog.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
		gbc.insets = new java.awt.Insets(10, 10, 10, 10);
		gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titleLabel = new JLabel("Edit Product", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		editDialog.add(titleLabel, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 1;
		gbc.gridx = 0;
		editDialog.add(new JLabel("Product Name:"), gbc);
		JTextField nameField = new JTextField(oldName, 15);
		gbc.gridx = 1;
		editDialog.add(nameField, gbc);

		gbc.gridy = 2;
		gbc.gridx = 0;
		editDialog.add(new JLabel("Category:"), gbc);
		JTextField categoryField = new JTextField(oldCategory, 15);
		gbc.gridx = 1;
		editDialog.add(categoryField, gbc);

		gbc.gridy = 3;
		gbc.gridx = 0;
		editDialog.add(new JLabel("Price (₱):"), gbc);
		JTextField priceField = new JTextField(oldPrice, 15);
		gbc.gridx = 1;
		editDialog.add(priceField, gbc);

		gbc.gridy = 4;
		gbc.gridx = 0;
		editDialog.add(new JLabel("Stock:"), gbc);
		JTextField stockField = new JTextField(oldStock, 15);
		gbc.gridx = 1;
		editDialog.add(stockField, gbc);

		gbc.gridy = 5;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		JButton updateProductButton = new JButton("Update Product");
		updateProductButton.setBackground(darkGray);
		updateProductButton.setForeground(Color.WHITE);
		updateProductButton.setFocusPainted(false);
		editDialog.add(updateProductButton, gbc);

		updateProductButton.addActionListener(e -> {
			try {
				String newName = nameField.getText();
				String newCat = categoryField.getText();
				double newPrice = Double.parseDouble(priceField.getText());
				int newStock = Integer.parseInt(stockField.getText());

				// Using the central dbUrl
				try (java.sql.Connection connection = java.sql.DriverManager.getConnection(this.dbUrl);
						java.sql.PreparedStatement preparedStatement = connection.prepareStatement(
								"UPDATE Products SET product_name = ?, category = ?, price = ?, stock = ? WHERE product_name = ?")) {

					preparedStatement.setString(1, newName);
					preparedStatement.setString(2, newCat);
					preparedStatement.setDouble(3, newPrice);
					preparedStatement.setInt(4, newStock);
					preparedStatement.setString(5, oldName);

					preparedStatement.executeUpdate();

					logger.info("Updated product: " + oldName);
					editDialog.dispose();
					loadInventoryData();

				} catch (Exception sqlEx) {
					logger.error("Database error while editing", sqlEx);
					javax.swing.JOptionPane.showMessageDialog(editDialog, "Error: Check if product name exists!");
				}
			} catch (NumberFormatException numError) {
				javax.swing.JOptionPane.showMessageDialog(editDialog, "Please enter valid numbers!");
			}
		});

		editDialog.setVisible(true);
	}

	private void deleteSelectedProducts() {
		int[] selectedRows = inventoryTable.getSelectedRows();

		if (selectedRows.length == 0) {
			javax.swing.JOptionPane.showMessageDialog(this,
					"Please select at least one product from the table to delete.");
			return;
		}

		int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete " + selectedRows.length + " selected product(s)?", "Confirm Delete",
				javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);

		if (confirm == javax.swing.JOptionPane.YES_OPTION) {
			// Using the central dbUrl
			try (java.sql.Connection connection = java.sql.DriverManager.getConnection(this.dbUrl);
					java.sql.PreparedStatement preparedstatement = connection
							.prepareStatement("DELETE FROM Products WHERE product_name = ?")) {

				for (int i = 0; i < selectedRows.length; i++) {
					// IMPORTANT: Convert the table's visual row index back to the underlying
					// model's data index!
					// Because the user might be filtering/sorting the table, row 0 on the screen
					// might not be row 0 in the data.
					int modelRowIndex = inventoryTable.convertRowIndexToModel(selectedRows[i]);
					String productName = tableModel.getValueAt(modelRowIndex, 0).toString();

					preparedstatement.setString(1, productName);
					preparedstatement.executeUpdate();
					logger.info("Deleted product: " + productName);
				}

				loadInventoryData();
				javax.swing.JOptionPane.showMessageDialog(this,
						"Successfully deleted " + selectedRows.length + " item(s).");

			} catch (Exception sqlEx) {
				logger.error("Database error while deleting", sqlEx);
				javax.swing.JOptionPane.showMessageDialog(this, "Error deleting products!");
			}
		}
	}
}