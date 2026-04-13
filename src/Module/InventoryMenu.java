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
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InventoryMenu extends JFrame {

	// 50% AI

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InventoryMenu.class);

	// Initialization of variables
	private String loggedInUser;

	// UI Components
	public JTextField searchField;
	public JTable inventoryTable;
	public DefaultTableModel tableModel;
	public JButton btnAdd, btnRestock, btnEdit, btnDelete, searchBtn;

	// Custom Colors
	Color darkGray = new Color(0, 102, 102);
	Color lightGreen = new Color(0, 204, 102);

	public InventoryMenu(String username) {
		this.loggedInUser = username;
		logger.info("Initializing Inventory Menu for user: " + username);

		setTitle("Inventory Management");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		logger.debug("Setting up top menu bar...");
		setJMenuBar(Dropdown.createTopMenu(true, this.loggedInUser, this));

		// --- Modular UI ---
		logger.debug("Assembling modular UI panels...");
		add(createTopPanel(), BorderLayout.NORTH);
		add(createTablePanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		logger.info("Inventory Menu initialized successfully.");
	}

	private JPanel createTopPanel() {
		logger.debug("Building Top Panel (Title & Search)...");
		JPanel topPanel = new JPanel(new BorderLayout(5, 5));
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

		JLabel titleLabel = new JLabel("Inventory Management", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 28));

		searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(800, 35));

		searchBtn = new JButton("Search");
		searchBtn.setBackground(lightGreen);
		searchBtn.setForeground(Color.WHITE);
		searchBtn.setPreferredSize(new Dimension(100, 35));
		searchBtn.setFocusPainted(false);

		JPanel searchContainer = new JPanel(new BorderLayout(10, 0));
		searchContainer.add(searchField, BorderLayout.CENTER);
		searchContainer.add(searchBtn, BorderLayout.EAST);

		topPanel.add(titleLabel, BorderLayout.NORTH);
		topPanel.add(searchContainer, BorderLayout.SOUTH);

		return topPanel;
	}

	private JScrollPane createTablePanel() {
		logger.debug("Building Center Panel (Data Table)...");
		String[] columns = { "Product Name", "Category", "Price", "Stock", "Status" };

		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			} // Locks cells from being typed in
		};

		inventoryTable = new JTable(tableModel);

		// Table Styling
		inventoryTable.setRowHeight(35);
		inventoryTable.setFont(new Font("Arial", Font.PLAIN, 14));
		inventoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
		inventoryTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		inventoryTable.getTableHeader().setReorderingAllowed(false); // Locks columns from being dragged

		// Centering Text in Table Cells
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

	private JPanel createBottomPanel() {
		logger.debug("Building Bottom Panel (Action Buttons)...");
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

		// Apply standard styling to the other buttons
		styleButton(btnAdd);
		styleButton(btnRestock);
		styleButton(btnEdit);

		bottomPanel.add(btnAdd);
		bottomPanel.add(btnRestock);
		bottomPanel.add(btnEdit);
		bottomPanel.add(btnDelete);

		return bottomPanel;
	}

	private void styleButton(JButton button) {
		button.setPreferredSize(new Dimension(150, 40));
		button.setBackground(darkGray);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
	}
}