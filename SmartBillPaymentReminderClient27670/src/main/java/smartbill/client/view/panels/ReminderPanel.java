package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Bill;
import smartbill.server.model.Reminder;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.ReminderService;

public class ReminderPanel extends JPanel {

    private User loggedInUser;
    private BillService billService;
    private ReminderService reminderService;
    private List<Reminder> reminders;
    private List<Bill> userBills;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color SUCCESS   = new Color(40, 167, 69);
    private static final Color DANGER    = new Color(220, 53, 69);

    private JTable table;
    private DefaultTableModel tableModel;

    public ReminderPanel(User user) {
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
            reminderService = (ReminderService) registry.lookup("ReminderService");
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

        JLabel lblTitle = new JLabel("Reminder Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        JButton btnAdd     = createBtn("+ Add Reminder",  PRIMARY);
        JButton btnDismiss = createBtn("Dismiss",         SUCCESS);
        JButton btnDelete  = createBtn("Delete",          DANGER);
        JButton btnRefresh = createBtn("Refresh",         SECONDARY);

        controls.add(btnAdd);
        controls.add(btnDismiss);
        controls.add(btnDelete);
        controls.add(btnRefresh);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"ID", "Bill Name", "Trigger Date",
                         "Days Before", "Dismissed"};
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
        btnAdd.addActionListener(e -> showReminderDialog());
        btnDismiss.addActionListener(e -> dismissSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
    }

    public void loadData() {
        try {
            userBills = billService.getBillsByUser(loggedInUser.getUserId());
            reminders = reminderService.getPendingReminders(loggedInUser.getUserId());
            populateTable(reminders);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading reminders: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<Reminder> data) {
        tableModel.setRowCount(0);
        for (Reminder r : data) {
            tableModel.addRow(new Object[]{
                r.getReminderId(),
                r.getBill() != null ? r.getBill().getName() : "-",
                r.getTriggerDate(),
                r.getDaysBefore(),
                r.isDismissed() ? "Yes" : "No"
            });
        }
    }

    private void dismissSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a reminder to dismiss.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            reminderService.dismissReminder(id);
            JOptionPane.showMessageDialog(this,
                "Reminder dismissed successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a reminder to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this reminder?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                reminderService.deleteReminder(id);
                JOptionPane.showMessageDialog(this,
                    "Reminder deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showReminderDialog() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Add Reminder", true);
        dialog.setSize(430, 370);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        // Header
        JPanel dHeader = new JPanel(null);
        dHeader.setBackground(PRIMARY);
        dHeader.setBounds(0, 0, 430, 55);
        panel.add(dHeader);

        JLabel dlblTitle = new JLabel("Add New Reminder");
        dlblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlblTitle.setForeground(WHITE);
        dlblTitle.setBounds(20, 15, 300, 25);
        dHeader.add(dlblTitle);

        // Bill selector
        addDLabel(panel, "Select Bill *", 20, 70);
        JComboBox<String> cmbBill = new JComboBox<>();
        cmbBill.setBounds(20, 93, 390, 32);
        cmbBill.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBill.addItem("-- Select Bill --");
        if (userBills != null) {
            userBills.forEach(b ->
                cmbBill.addItem(b.getBillId() + " - " + b.getName()));
        }
        panel.add(cmbBill);

        // Trigger Date
        addDLabel(panel, "Trigger Date (YYYY-MM-DD) *", 20, 138);
        JTextField txtTriggerDate = addDField(panel, 20, 161);

        // Days Before
        addDLabel(panel, "Days Before Due Date *", 20, 206);
        JSpinner spnDays = new JSpinner(
            new SpinnerNumberModel(3, 1, 30, 1));
        spnDays.setBounds(20, 229, 120, 32);
        spnDays.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(spnDays);

        // Buttons
        JButton btnSave = createBtn("Save Reminder", PRIMARY);
        btnSave.setBounds(20, 295, 160, 38);
        panel.add(btnSave);

        JButton btnCancel = createBtn("Cancel", SECONDARY);
        btnCancel.setBounds(200, 295, 100, 38);
        btnCancel.addActionListener(e -> dialog.dispose());
        panel.add(btnCancel);

        btnSave.addActionListener(e -> {
            int billIndex    = cmbBill.getSelectedIndex();
            String trigDate  = txtTriggerDate.getText().trim();
            int daysBefore   = (int) spnDays.getValue();

            // Technical validations
            if (billIndex <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Please select a bill.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE); return;
            }
            if (!trigDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog,
                    "Trigger date must be in format YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }
            // Business validation
            if (daysBefore <= 0) {
                JOptionPane.showMessageDialog(dialog,
                    "Days before must be greater than zero.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE); return;
            }

            try {
                Bill selectedBill = userBills.get(billIndex - 1);
                Reminder reminder = new Reminder(trigDate, daysBefore,
                                                 false, selectedBill);
                reminderService.createReminder(reminder);
                JOptionPane.showMessageDialog(dialog,
                    "Reminder created successfully.",
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
        lbl.setBounds(x, y, 280, 20);
        p.add(lbl);
    }

    private JTextField addDField(JPanel p, int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 390, 32);
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