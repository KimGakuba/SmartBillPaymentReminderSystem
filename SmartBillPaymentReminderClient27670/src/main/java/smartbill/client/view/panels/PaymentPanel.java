package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Bill;
import smartbill.server.model.Payment;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.PaymentService;

public class PaymentPanel extends JPanel {

    private User loggedInUser;
    private BillService billService;
    private PaymentService paymentService;
    private List<Payment> payments;
    private List<Bill> userBills;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;

    private JTable table;
    private DefaultTableModel tableModel;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JComboBox<String> cmbBillFilter;

    public PaymentPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            billService    = (BillService) registry.lookup("BillService");
            paymentService = (PaymentService) registry.lookup("PaymentService");
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

        JLabel lblTitle = new JLabel("Payment Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        txtSearch = new javax.swing.JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        cmbBillFilter = new javax.swing.JComboBox<>();
        cmbBillFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnSearch    = createBtn("Search",          SECONDARY);
        JButton btnAdd       = createBtn("+ Add Payment",   PRIMARY);
        JButton btnDelete    = createBtn("Delete",          new Color(180, 60, 60));
        JButton btnRefresh   = createBtn("Refresh",         SECONDARY);

        controls.add(new JLabel("Bill:"));
        controls.add(cmbBillFilter);
        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnAdd);
        controls.add(btnDelete);
        controls.add(btnRefresh);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"ID", "Bill Name", "Amount Paid (RWF)",
                         "Date Paid", "Method", "Notes"};
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
        btnSearch.addActionListener(e -> filterPayments());
        btnAdd.addActionListener(e -> showPaymentDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        cmbBillFilter.addActionListener(e -> filterPayments());
    }

    public void loadData() {
        try {
            // Load bills into filter combo
            userBills = billService.getBillsByUser(loggedInUser.getUserId());
            cmbBillFilter.removeAllItems();
            cmbBillFilter.addItem("ALL");
            userBills.forEach(b -> cmbBillFilter.addItem(b.getBillId() + " - " + b.getName()));

            // Load all payments
            payments = paymentService.getAllPayments();
            populateTable(payments);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading payments: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterPayments() {
        if (payments == null) return;
        String search  = txtSearch.getText().trim().toLowerCase();
        int billIndex  = cmbBillFilter.getSelectedIndex();

        List<Payment> filtered = payments.stream()
            .filter(p -> {
                if (billIndex <= 0 || userBills == null) return true;
                Bill selected = userBills.get(billIndex - 1);
                return p.getBill() != null &&
                       p.getBill().getBillId() == selected.getBillId();
            })
            .filter(p -> search.isEmpty() ||
                (p.getBill() != null &&
                 p.getBill().getName().toLowerCase().contains(search)))
            .toList();
        populateTable(filtered);
    }

    private void populateTable(List<Payment> data) {
        tableModel.setRowCount(0);
        for (Payment p : data) {
            tableModel.addRow(new Object[]{
                p.getPaymentId(),
                p.getBill() != null ? p.getBill().getName() : "-",
                String.format("%.2f", p.getAmountPaid()),
                p.getDatePaid(),
                p.getPaymentMethod(),
                p.getNotes() != null ? p.getNotes() : "-"
            });
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a payment to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this payment?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                paymentService.deletePayment(id);
                JOptionPane.showMessageDialog(this,
                    "Payment deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showPaymentDialog() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Record Payment", true);
        dialog.setSize(450, 430);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        // Dialog header
        JPanel dHeader = new JPanel(null);
        dHeader.setBackground(PRIMARY);
        dHeader.setBounds(0, 0, 450, 55);
        panel.add(dHeader);

        JLabel dlblTitle = new JLabel("Record a Payment");
        dlblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlblTitle.setForeground(WHITE);
        dlblTitle.setBounds(20, 15, 300, 25);
        dHeader.add(dlblTitle);

        // Bill selector
        addDLabel(panel, "Select Bill *", 20, 70);
        JComboBox<String> cmbBill = new JComboBox<>();
        cmbBill.setBounds(20, 93, 410, 32);
        cmbBill.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBill.addItem("-- Select Bill --");
        if (userBills != null) {
            userBills.forEach(b ->
                cmbBill.addItem(b.getBillId() + " - " + b.getName()));
        }
        panel.add(cmbBill);

        // Amount
        addDLabel(panel, "Amount Paid (RWF) *", 20, 138);
        JTextField txtAmount = addDField(panel, 20, 161);

        // Date
        addDLabel(panel, "Date Paid (YYYY-MM-DD) *", 20, 206);
        JTextField txtDate = addDField(panel, 20, 229);
        txtDate.setText(LocalDateTime.now().toLocalDate().toString());

        // Method
        addDLabel(panel, "Payment Method", 20, 274);
        JComboBox<String> cmbMethod = new JComboBox<>(
            new String[]{"Cash", "MoMo", "Bank Transfer", "Card", "Other"});
        cmbMethod.setBounds(20, 297, 200, 32);
        cmbMethod.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(cmbMethod);

        // Notes
        addDLabel(panel, "Notes (Optional)", 240, 274);
        JTextField txtNotes = new JTextField();
        txtNotes.setBounds(240, 297, 190, 32);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        panel.add(txtNotes);

        // Buttons
        JButton btnSave = createBtn("Record Payment", PRIMARY);
        btnSave.setBounds(20, 355, 180, 38);
        panel.add(btnSave);

        JButton btnCancel = createBtn("Cancel", SECONDARY);
        btnCancel.setBounds(220, 355, 100, 38);
        btnCancel.addActionListener(e -> dialog.dispose());
        panel.add(btnCancel);

        btnSave.addActionListener(e -> {
            int billIndex  = cmbBill.getSelectedIndex();
            String amtStr  = txtAmount.getText().trim();
            String date    = txtDate.getText().trim();
            String method  = (String) cmbMethod.getSelectedItem();
            String notes   = txtNotes.getText().trim();

            // Technical validations
            if (billIndex <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a bill.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            if (amtStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Amount cannot be empty.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog,
                    "Date must be in format YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amtStr);
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
                Bill selectedBill = userBills.get(billIndex - 1);
                Payment payment   = new Payment(amount, date, method,
                                                notes, selectedBill);
                paymentService.addPayment(payment);
                JOptionPane.showMessageDialog(dialog,
                    "Payment recorded successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
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
        f.setBounds(x, y, 410, 32);
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