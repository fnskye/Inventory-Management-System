package Module;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.Main; // Imported for routing

public class SalesReport extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SalesReport.class);

	// Core Variables
	private String loggedInUser;
	private String dbUrl;

	// UI Components
	public JLabel lblTotalSales;
	public JLabel lblTotalOrders;
	public JTable transactionTable;
	public DefaultTableModel transactionModel;
	public JButton btnPrintReport, btnBack;

	// Custom Colors
	Color darkGray = new Color(0, 102, 102);
	Color slateGray = new Color(105, 115, 132);
	Color lightGrayBg = new Color(240, 240, 240);

	public SalesReport(String username) {
		logger.info("Initializing Full-Stack Sales Report Menu for user: " + username);
		this.loggedInUser = username;

		// Setup EXE-Safe Database Connection
		String currentFolder = System.getProperty("user.dir");
		this.dbUrl = "jdbc:sqlite:" + currentFolder + "/database.db";

		setTitle("Sales Report Menu");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout(20, 20));

		setJMenuBar(Dropdown.createTopMenu(true, this.loggedInUser, this));

		// Build UI
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);
		add(createBottomPanel(), BorderLayout.SOUTH);

		((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

		setLocationRelativeTo(null);

		// Wire up logic and load database
		setupBackendLogic();
	}

	// UI Assembly
	private JPanel createHeaderPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("Sales Report Menu", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
		topPanel.add(titleLabel, BorderLayout.CENTER);
		return topPanel;
	}

	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout(0, 30));

		// Top Panel
		JPanel summaryWrapper = new JPanel(new BorderLayout(0, 10));
		JLabel summaryTitle = new JLabel("B U S I N E S S   S U M M A R Y", SwingConstants.CENTER);
		summaryTitle.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 18));

		JPanel summaryBox = new JPanel(new GridLayout(2, 2, 20, 20));
		summaryBox.setBackground(lightGrayBg);
		summaryBox.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

		JLabel lblSalesText = new JLabel("TOTAL SALES", SwingConstants.RIGHT);
		lblTotalSales = new JLabel("₱ 0.00", SwingConstants.LEFT);
		lblTotalSales.setFont(new Font("Arial", Font.BOLD, 16));

		JLabel lblOrdersText = new JLabel("TOTAL ORDERS", SwingConstants.RIGHT);
		lblTotalOrders = new JLabel("0", SwingConstants.LEFT);
		lblTotalOrders.setFont(new Font("Arial", Font.BOLD, 16));

		summaryBox.add(lblSalesText);
		summaryBox.add(lblTotalSales);
		summaryBox.add(lblOrdersText);
		summaryBox.add(lblTotalOrders);

		summaryWrapper.add(summaryTitle, BorderLayout.NORTH);
		summaryWrapper.add(summaryBox, BorderLayout.CENTER);

		// Bottom Panel
		JPanel tableWrapper = new JPanel(new BorderLayout(0, 10));
		JLabel tableTitle = new JLabel("O V E R A L L   T R A N S A C T I O N   H I S T O R Y", SwingConstants.CENTER);
		tableTitle.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 18));

		String[] columns = { "TIME/DATE", "ORDER #", "CLIENT NAME", "TOTAL PRICE" };
		transactionModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		transactionTable = new JTable(transactionModel);

		transactionTable.setRowHeight(40);
		transactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
		transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
		transactionTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
		transactionTable.getTableHeader().setBackground(lightGrayBg);
		transactionTable.getTableHeader().setReorderingAllowed(false);

		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < transactionTable.getColumnCount(); i++) {
			transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		transactionTable.setFillsViewportHeight(true);

		tableWrapper.add(tableTitle, BorderLayout.NORTH);
		tableWrapper.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

		centerPanel.add(summaryWrapper, BorderLayout.NORTH);
		centerPanel.add(tableWrapper, BorderLayout.CENTER);

		return centerPanel;
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

		Color lightGreen = new Color(0, 204, 102);

		btnBack = new JButton("Back");
		btnBack.setPreferredSize(new Dimension(150, 45));
		btnBack.setBackground(slateGray);
		btnBack.setForeground(Color.WHITE);
		btnBack.setFont(new Font("Arial", Font.BOLD, 14));
		btnBack.setFocusPainted(false);

		btnPrintReport = new JButton("Print Report");
		btnPrintReport.setPreferredSize(new Dimension(250, 45));
		btnPrintReport.setBackground(lightGreen);
		btnPrintReport.setForeground(Color.WHITE);
		btnPrintReport.setFont(new Font("Arial", Font.BOLD, 14));
		btnPrintReport.setFocusPainted(false);
		bottomPanel.add(btnBack);
		bottomPanel.add(btnPrintReport);

		return bottomPanel;
	}

	// Backend Logic
	private void setupBackendLogic() {
		// Load Data from SQLite Database
		loadSalesData();

		// Routing Logic
		btnBack.addActionListener(e -> {
			dispose();
			Main.openMainMenu(loggedInUser, this);
		});

		btnPrintReport.addActionListener(e -> {
			JOptionPane.showMessageDialog(SalesReport.this, "Print Successful!\nReturn to Main Menu");
			dispose(); // Close Print Report
			Main.openMainMenu(this.loggedInUser, this);
		});
	}

	private void loadSalesData() {
		transactionModel.setRowCount(0);
		double totalRevenue = 0.0;
		int totalOrderCount = 0;

		try (Connection conn = DriverManager.getConnection(dbUrl);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT order_date, order_number, customer_name, total_amount FROM Transactions")) {

			while (rs.next()) {
				String date = rs.getString("order_date");
				String orderNo = rs.getString("order_number");
				String customer = rs.getString("customer_name");
				double amount = rs.getDouble("total_amount");

				// Add row to UI table
				String formattedAmount = String.format("₱ %,.2f", amount);
				transactionModel.addRow(new Object[] { date, orderNo, customer, formattedAmount });

				// Run background math
				totalRevenue += amount;
				totalOrderCount++;
			}

			// Push calculated math to the UI Summary Labels
			lblTotalSales.setText(String.format("₱ %,.2f", totalRevenue));
			lblTotalOrders.setText(String.valueOf(totalOrderCount));
			logger.info("Successfully loaded " + totalOrderCount + " transactions from database.");

		} catch (Exception e) {
			logger.error("Failed to load sales data. Make sure the 'Transactions' table exists in database.db", e);
			JOptionPane.showMessageDialog(this, "Database Error: Could not load transactions.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}