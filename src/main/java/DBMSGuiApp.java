import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.io.*;
import javax.swing.border.TitledBorder;

public class DBMSGuiApp extends JFrame {
    private JTextArea queryArea;
    private JButton executeButton;
    private JTextField filterField;
    private JButton filterButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private JComboBox<String> databaseComboBox;
    private JButton exportCsvButton;
    private float zoomFactor = 1.0f;
    private JPanel dbPanel, topPanel, buttonPanel, middlePanel, filterPanel, bottomPanel;
    private JScrollPane resultsScrollPane;
    private JLabel titleLabel;

    public DBMSGuiApp() {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            // Customize colors for a more modern look
            UIManager.put("Panel.background", new Color(248, 250, 252));
            UIManager.put("Button.background", new Color(59, 130, 246));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", new Color(37, 99, 235));
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(241, 245, 249));
            UIManager.put("TitledBorder.titleColor", new Color(31, 41, 55));
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // fallback
            }
        }

        setTitle("DBMS GUI Application");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen
        setLayout(new BorderLayout(10, 10)); // Add spacing

        // Set default larger font (2x normal size) for UI components only
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 16); // Increased from default ~12 to 16
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        // Note: Table font will be handled separately for zoom functionality
        UIManager.put("Table.font", defaultFont);

        // Initialize components
        queryArea = new JTextArea(5, 20);
        executeButton = new JButton("▶️ Execute Query");
        filterField = new JTextField(20);
        filterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        filterButton = new JButton("🔍 Filter Results");
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        databaseComboBox = new JComboBox<>();
        databaseComboBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        exportCsvButton = new JButton("📊 Export to CSV");

        // Add a large, bold title label at the top
        titleLabel = new JLabel("🚀 DBMS GUI Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.PAGE_START);

        // Database selection panel
        dbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        dbPanel.add(new JLabel("Select Database:"));
        dbPanel.add(databaseComboBox);
        dbPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Top panel for query input
        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query",
                TitledBorder.LEFT, TitledBorder.TOP));
        dbPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(dbPanel);
        JLabel queryLabel = new JLabel("Enter SQL Query:");
        queryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        queryLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topPanel.add(queryLabel);
        JScrollPane queryScroll = new JScrollPane(queryArea);
        queryScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        queryScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        queryScroll.setPreferredSize(new Dimension(900, 80));
        topPanel.add(queryScroll);

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        executeButton.setToolTipText("Execute the SQL query");
        exportCsvButton.setToolTipText("Export results to CSV");
        buttonPanel.add(executeButton);
        buttonPanel.add(exportCsvButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Middle panel for filter
        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter & Actions",
                TitledBorder.LEFT, TitledBorder.TOP));
        filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel filterLabel = new JLabel("Filter Results:");
        filterLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        filterPanel.add(Box.createHorizontalGlue());
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(filterField);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(filterButton);
        filterPanel.add(Box.createHorizontalGlue());
        middlePanel.add(filterPanel);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        middlePanel.add(buttonPanel);

        // Bottom panel for results
        bottomPanel = new JPanel(new BorderLayout(5, 5));
        JLabel resultsLabel = new JLabel("Query Results:");
        resultsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        bottomPanel.add(resultsLabel, BorderLayout.NORTH);
        resultsScrollPane = new JScrollPane(resultTable);
        resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        resultTable.setPreferredScrollableViewportSize(new Dimension(1000, 400));
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(28);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(new Color(226, 232, 240));
        resultTable.setIntercellSpacing(new Dimension(2, 2));
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
        resultTable.setToolTipText("Query results will appear here");
        bottomPanel.add(resultsScrollPane, BorderLayout.CENTER);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Results",
                TitledBorder.LEFT, TitledBorder.TOP));

        // Create a main panel with vertical BoxLayout to hold all sections
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Add the topPanel (query area) and middlePanel (filter/actions) and
        // bottomPanel (results)
        mainPanel.add(topPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(middlePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(bottomPanel);

        // Add panels to frame
        add(titleLabel, BorderLayout.PAGE_START);
        add(mainPanel, BorderLayout.CENTER);

        // Setup database connection
        setupDatabaseConnection();

        // Add action listeners
        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeQuery();
            }
        });

        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterResults();
            }
        });

        databaseComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectDatabase();
            }
        });

        exportCsvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportToCsv();
            }
        });

        // Add key listeners for Enter functionality
        queryArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    executeQuery();
                    if (!filterField.getText().trim().isEmpty()) {
                        filterResults();
                    }
                }
            }
        });

        filterField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    if (filterField.getText().trim().isEmpty()) {
                        executeQuery();
                    } else {
                        filterResults();
                    }
                }
            }
        });

        // Add mouse wheel listener for table zoom only
        MouseAdapter zoomAdapter = new MouseAdapter() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown() && e.getSource() == resultTable) {
                    float zoomChange = e.getWheelRotation() < 0 ? 0.1f : -0.1f;
                    zoomFactor = Math.max(0.5f, Math.min(3.0f, zoomFactor + zoomChange));
                    applyTableZoom();
                }
            }
        };

        // Add panning functionality with mouse drag
        MouseAdapter panAdapter = new MouseAdapter() {
            private Point startPoint;

            public void mousePressed(MouseEvent e) {
                if (e.getSource() == resultTable && e.isShiftDown()) {
                    startPoint = e.getPoint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                if (e.getSource() == resultTable && e.isShiftDown() && startPoint != null) {
                    JScrollPane scrollPane = (JScrollPane) resultTable.getParent().getParent();
                    JViewport viewport = scrollPane.getViewport();

                    Point currentPoint = e.getPoint();
                    int deltaX = startPoint.x - currentPoint.x;
                    int deltaY = startPoint.y - currentPoint.y;

                    Point viewPosition = viewport.getViewPosition();
                    viewPosition.translate(deltaX, deltaY);

                    // Ensure we don't scroll beyond bounds
                    Dimension viewSize = viewport.getViewSize();
                    Dimension extentSize = viewport.getExtentSize();

                    viewPosition.x = Math.max(0, Math.min(viewPosition.x, viewSize.width - extentSize.width));
                    viewPosition.y = Math.max(0, Math.min(viewPosition.y, viewSize.height - extentSize.height));

                    viewport.setViewPosition(viewPosition);
                    startPoint = currentPoint;
                }
            }
        };

        // Add zoom and pan listeners only to result table
        resultTable.addMouseWheelListener(zoomAdapter);
        resultTable.addMouseListener(panAdapter);
        resultTable.addMouseMotionListener(panAdapter);

        // Add column selection listener for auto-resizing
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedColumn = resultTable.getSelectedColumn();
                if (selectedColumn >= 0) {
                    autoResizeColumn(selectedColumn);
                }
            }
        });

        // Also add column header selection listener
        resultTable.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = resultTable.columnAtPoint(e.getPoint());
                if (column >= 0) {
                    resultTable.setColumnSelectionInterval(column, column);
                    autoResizeColumn(column);
                }
            }
        });
    }

    private void setupDatabaseConnection() {
        // Show login dialog
        String[] credentials = showLoginDialog();
        if (credentials == null) {
            JOptionPane.showMessageDialog(this, "Login canceled. Application will exit.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        String user = credentials[0];
        String password = credentials[1];

        try {
            // Connect to MySQL server without specifying a database
            String url = "jdbc:mysql://localhost:3306";
            connection = DriverManager.getConnection(url, user, password);

            // Load available databases
            loadDatabases();

            JOptionPane.showMessageDialog(this, "Connected to MySQL server successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadDatabases() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getCatalogs();
            databaseComboBox.removeAllItems();
            String firstDb = null;
            while (rs.next()) {
                String dbName = rs.getString("TABLE_CAT");
                if (firstDb == null) {
                    firstDb = dbName;
                }
                databaseComboBox.addItem(dbName);
            }
            rs.close();

            // Select the first database by default
            if (firstDb != null) {
                databaseComboBox.setSelectedItem(firstDb);
                connection.setCatalog(firstDb);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load databases: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeQuery() {
        String query = queryArea.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a query", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing table data
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            // Set column names
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Add rows
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

            rs.close();
            stmt.close();

            // Auto-resize columns to fit content after loading data
            autoResizeAllColumns();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Query execution failed: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterResults() {
        String filterText = filterField.getText().trim().toLowerCase();
        if (filterText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a filter term", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simple client-side filtering
        for (int row = tableModel.getRowCount() - 1; row >= 0; row--) {
            boolean matches = false;
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                Object value = tableModel.getValueAt(row, col);
                if (value != null && value.toString().toLowerCase().contains(filterText)) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                tableModel.removeRow(row);
            }
        }
    }

    private void exportToCsv() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("results.csv"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.print(escapeCsv(tableModel.getColumnName(i)));
                    if (i < tableModel.getColumnCount() - 1)
                        writer.print(",");
                }
                writer.println();

                // Write data
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        writer.print(escapeCsv(value != null ? value.toString() : ""));
                        if (col < tableModel.getColumnCount() - 1)
                            writer.print(",");
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this, "Data exported to CSV successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to export to CSV: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper to escape CSV values
    private String escapeCsv(String value) {
        if (value == null)
            return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }

    private void selectDatabase() {
        String selectedDb = (String) databaseComboBox.getSelectedItem();
        if (selectedDb != null) {
            try {
                connection.setCatalog(selectedDb);
                JOptionPane.showMessageDialog(this, "Switched to database: " + selectedDb);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to switch database: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyTableZoom() {
        // Apply zoom only to the results table
        Font tableFont = new Font("SansSerif", Font.PLAIN, (int) (16 * zoomFactor));
        resultTable.setFont(tableFont);

        // Adjust row height proportionally
        resultTable.setRowHeight((int) (20 * zoomFactor));

        // Adjust column widths proportionally but maintain auto-resize logic
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            TableColumn column = resultTable.getColumnModel().getColumn(i);
            // Scale the current width with zoom factor
            int currentWidth = column.getPreferredWidth();
            int newWidth = (int) (currentWidth * zoomFactor);
            // Ensure reasonable bounds
            newWidth = Math.max(80, Math.min(newWidth, 500));
            column.setPreferredWidth(newWidth);
            column.setWidth(newWidth);
        }

        // Refresh the table
        resultTable.revalidate();
        resultTable.repaint();
    }

    private void autoResizeColumn(int columnIndex) {
        if (columnIndex < 0 || columnIndex >= resultTable.getColumnCount()) {
            return;
        }

        TableColumn column = resultTable.getColumnModel().getColumn(columnIndex);
        int maxWidth = 0;

        // Get the renderer for this column
        TableCellRenderer renderer = resultTable.getCellRenderer(0, columnIndex);
        if (renderer == null) {
            renderer = resultTable.getDefaultRenderer(resultTable.getColumnClass(columnIndex));
        }

        // Calculate width needed for header
        Component headerComp = resultTable.getTableHeader().getDefaultRenderer()
                .getTableCellRendererComponent(resultTable, column.getHeaderValue(), false, false, -1, columnIndex);
        maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width + 20); // Add padding

        // Calculate width needed for each cell in this column
        for (int row = 0; row < resultTable.getRowCount(); row++) {
            Component cellComp = renderer.getTableCellRendererComponent(
                    resultTable, resultTable.getValueAt(row, columnIndex),
                    false, false, row, columnIndex);
            maxWidth = Math.max(maxWidth, cellComp.getPreferredSize().width + 20); // Add padding
        }

        // Set minimum and maximum widths
        maxWidth = Math.max(maxWidth, 100); // Minimum width
        maxWidth = Math.min(maxWidth, 400); // Maximum width to prevent too wide columns

        column.setPreferredWidth(maxWidth);
        column.setWidth(maxWidth);

        // Refresh the table
        resultTable.revalidate();
        resultTable.repaint();
    }

    private void autoResizeAllColumns() {
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            autoResizeColumn(i);
        }
    }

    private String[] showLoginDialog() {
        java.util.List<String> users = getAllMySQLUsers();
        boolean userFetchFailed = users.isEmpty();
        JComboBox<String> userComboBox;
        if (userFetchFailed) {
            userComboBox = null;
        } else {
            userComboBox = new JComboBox<>(users.toArray(new String[0]));
        }
        JTextField usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("👤 Username:"));
        if (userFetchFailed) {
            panel.add(usernameField);
        } else {
            userComboBox.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225), 1, true));
            panel.add(userComboBox);
        }
        panel.add(new JLabel("🔒 Password:"));
        panel.add(passwordField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "MySQL Login", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return null;
            }
            String username;
            if (userFetchFailed) {
                username = usernameField.getText().trim();
            } else {
                username = (String) userComboBox.getSelectedItem();
            }
            String password = new String(passwordField.getPassword());
            if (username != null && !username.isEmpty()) {
                // Try connecting with these credentials
                try {
                    Connection testConn = DriverManager.getConnection("jdbc:mysql://localhost:3306", username,
                            password);
                    testConn.close();
                    return new String[] { username, password };
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Wrong password for user '" + username + "'. Please try again.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            }
        }
    }

    // Helper: Get all MySQL users from the server (localhost, no password)
    private java.util.List<String> getAllMySQLUsers() {
        java.util.List<String> users = new java.util.ArrayList<>();
        try {
            // Try to connect as root with no password (or use a default user)
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user FROM mysql.user");
            while (rs.next()) {
                String user = rs.getString("user");
                if (user != null && !user.isEmpty() && !users.contains(user)) {
                    users.add(user);
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // Fallback: just return empty list
        }
        return users;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DBMSGuiApp().setVisible(true);
            }
        });
    }
}
