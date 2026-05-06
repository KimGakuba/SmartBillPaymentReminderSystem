package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Bill;
import smartbill.server.model.Category;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.CategoryService;

public class BillPanel extends JPanel {

    private User loggedInUser;
    private BillService billService;
    private CategoryService categoryService;
    private List<Bill> bills;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;

    private JTable table;
    private DefaultTableModel tableModel;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JComboBox<String> cmbStatusFilter;

    public BillPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            billService     = (BillService) registry.lookup("BillService");
            categoryService = (CategoryService) registry.lookup("CategoryService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        // ── Top Bar ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel lblTitle = new JLabel("Bills Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        txtSearch = new javax.swing.JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        cmbStatusFilter = new javax.swing.JComboBox<>(
            new String[]{"ALL", "Unpaid", "Paid", "Overdue"});
        cmbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnSearch  = createBtn("Search",      SECONDARY);
        JButton btnAdd     = createBtn("+ Add Bill",  PRIMARY);
        JButton btnEdit    = createBtn("Edit",        new Color(0x8B, 0x65, 0x45));
        JButton btnDelete  = createBtn("Delete",      new Color(180, 60, 60));
        JButton btnRefresh = createBtn("Refresh",     SECONDARY);

        controls.add(new JLabel("Status:"));
        controls.add(cmbStatusFilter);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnAdd);
        controls.add(btnEdit);
        controls.add(btnDelete);
        controls.add(btnRefresh);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"ID", "Bill Name", "Amount (RWF)", "Due Date",
                         "Recurrence", "Status", "Category"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(SECONDARY);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(230, 220, 210));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Actions
        btnSearch.addActionListener(e -> filterBills());
        btnAdd.addActionListener(e -> showBillDialog(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        cmbStatusFilter.addActionListener(e -> filterBills());
    }

    public void loadData() {
        try {
            bills = billService.getBillsByUser(loggedInUser.getUserId());
            populateTable(bills);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading bills: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterBills() {
        if (bills == null) return;
        String search = txtSearch.getText().trim().toLowerCase();
        String status = (String) cmbStatusFilter.getSelectedItem();
        List<Bill> filtered = bills.stream()
            .filter(b -> "ALL".equals(status) ||
                         b.getStatus().equalsIgnoreCase(status))
            .filter(b -> search.isEmpty() ||
                         b.getName().toLowerCase().contains(search))
            .toList();
        populateTable(filtered);
    }

    private void populateTable(List<Bill> data) {
        tableModel.setRowCount(0);
        for (Bill b : data) {
            tableModel.addRow(new Object[]{
                b.getBillId(),
                b.getName(),
                String.format("%.2f", b.getAmount()),
                b.getDueDate(),
                b.getRecurrence(),
                b.getStatus(),
                b.getCategories() != null && !b.getCategories().isEmpty()
                    ? b.getCategories().get(0).getName() : "-"
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a bill to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        bills.stream()
            .filter(b -> b.getBillId() == id)
            .findFirst()
            .ifPresent(this::showBillDialog);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a bill to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this bill? This will also remove related payments and reminders.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                billService.deleteBill(id);
                JOptionPane.showMessageDialog(this,
                    "Bill deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBillDialog(Bill existing) {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add New Bill" : "Edit Bill", true);
        dialog.setSize(460, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        // Dialog header
        JPanel dHeader = new JPanel(null);
        dHeader.setBackground(PRIMARY);
        dHeader.setBounds(0, 0, 460, 55);
        panel.add(dHeader);

        JLabel dlblTitle = new JLabel(existing == null ? "Add New Bill" : "Edit Bill");
        dlblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlblTitle.setForeground(WHITE);
        dlblTitle.setBounds(20, 15, 300, 25);
        dHeader.add(dlblTitle);

        // Fields
        addDLabel(panel, "Bill Name *",              20,  70);
        JTextField txtName = addDField(panel,         20,  93);

        addDLabel(panel, "Amount (RWF) *",            20, 138);
        JTextField txtAmount = addDField(panel,        20, 161);

        addDLabel(panel, "Due Date (YYYY-MM-DD) *",   20, 206);
        JTextField txtDueDate = addDField(panel,       20, 229);

        addDLabel(panel, "Recurrence",                20, 274);
        JComboBox<String> cmbRecurrence = new JComboBox<>(
            new String[]{"Once", "Daily", "Weekly", "Monthly", "Yearly"});
        cmbRecurrence.setBounds(20, 297, 200, 32);
        cmbRecurrence.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbRecurrence);

        addDLabel(panel, "Status",                   250, 274);
        JComboBox<String> cmbStatus = new JComboBox<>(
            new String[]{"Unpaid", "Paid", "Overdue"});
        cmbStatus.setBounds(250, 297, 185, 32);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbStatus);

        addDLabel(panel, "Category",                  20, 342);
        JComboBox<String> cmbCategory = new JComboBox<>();
        cmbCategory.setBounds(20, 365, 415, 32);
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbCategory);

        // Load categories
        try {
            List<Category> cats = categoryService.getAllCategories();
            cmbCategory.addItem("-- Select Category --");
            cats.forEach(c -> cmbCategory.addItem(c.getName()));
        } catch (Exception ignored) {}

        // Populate if editing
        if (existing != null) {
            txtName.setText(existing.getName());
            txtAmount.setText(String.valueOf(existing.getAmount()));
            txtDueDate.setText(existing.getDueDate());
            cmbRecurrence.setSelectedItem(existing.getRecurrence());
            cmbStatus.setSelectedItem(existing.getStatus());
        }

        // Save button
        JButton btnSave = createBtn(
            existing == null ? "Save Bill" : "Update Bill", PRIMARY);
        btnSave.setBounds(20, 415, 180, 38);
        panel.add(btnSave);

        JButton btnCancel = createBtn("Cancel", SECONDARY);
        btnCancel.setBounds(220, 415, 100, 38);
        btnCancel.addActionListener(e -> dialog.dispose());
        panel.add(btnCancel);

        btnSave.addActionListener(e -> {
            String name      = txtName.getText().trim();
            String amountStr = txtAmount.getText().trim();
            String dueDate   = txtDueDate.getText().trim();
            String recurrence = (String) cmbRecurrence.getSelectedItem();
            String status     = (String) cmbStatus.getSelectedItem();

            // Technical validations
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Bill name is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Amount is required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            if (!dueDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog,
                    "Due date must be in format YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Amount must be a valid number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }
            // Business validation
            if (amount <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Amount must be greater than zero.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }

            try {
                if (existing == null) {
                    Bill bill = new Bill(name, amount, dueDate, recurrence,
                        status, LocalDateTime.now().toString(), loggedInUser);
                    billService.addBill(bill);
                    JOptionPane.showMessageDialog(dialog,
                        "Bill added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    existing.setName(name);
                    existing.setAmount(amount);
                    existing.setDueDate(dueDate);
                    existing.setRecurrence(recurrence);
                    existing.setStatus(status);
                    billService.updateBill(existing);
                    JOptionPane.showMessageDialog(dialog,
                        "Bill updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addDLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(PRIMARY);
        lbl.setBounds(x, y, 250, 20);
        p.add(lbl);
    }

    private JTextField addDField(JPanel p, int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 415, 32);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        p.add(f);
        return f;
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}