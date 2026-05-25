package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Bill;
import smartbill.server.model.Category;
import smartbill.server.model.Payment;
import smartbill.server.model.Reminder;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.CategoryService;
import smartbill.server.service.PaymentService;
import smartbill.server.service.ReminderService;
import smartbill.server.service.UserService;

public class AdminPanel extends JPanel {

    private User loggedInUser;
    private UserService userService;
    private BillService billService;
    private PaymentService paymentService;
    private ReminderService reminderService;
    private CategoryService categoryService;
    private List<User> allUsers;

    private static final Color PRIMARY = new Color(0x4A, 0x2A, 0x1A);
    private static final Color SECONDARY = new Color(0x8A, 0x5A, 0x3C);
    private static final Color BG = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE = Color.WHITE;
    private static final Color DANGER = new Color(220, 53, 69);
    private static final Color SUCCESS = new Color(40, 167, 69);
    private static final Color WARNING = new Color(255, 193, 7);
    private static final Color ADMIN_CLR = new Color(0x8B, 0x00, 0x00);
    private static final Color BORDER = new Color(220, 205, 190);

    private JLabel lblTotalUsers;
    private JLabel lblActiveUsers;
    private JLabel lblTotalBills;
    private JLabel lblPaidBills;
    private JLabel lblOverdueBills;
    private JLabel lblTotalPayments;
    private JLabel lblTotalReminders;

    private JTable tblUsers;
    private DefaultTableModel userTableModel;
    private JTabbedPane tabbedPane;

    public AdminPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            userService = (UserService) registry.lookup("UserService");
            billService = (BillService) registry.lookup("BillService");
            paymentService = (PaymentService) registry.lookup("PaymentService");
            reminderService = (ReminderService) registry.lookup("ReminderService");
            categoryService = (CategoryService) registry.lookup("CategoryService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not connect to server.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        removeAll();

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(WHITE);

        JLabel lblTitle = new JLabel("Admin Control Panel");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 30));
        lblTitle.setForeground(PRIMARY);

        JLabel lblSubtitle = new JLabel("Manage users, categories, reports, and system monitoring");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(SECONDARY);

        headerText.add(lblTitle);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(lblSubtitle);

        JLabel lblAdmin = new JLabel("Logged in as: " + loggedInUser.getUsername() + "  |  ADMIN");
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAdmin.setForeground(WHITE);
        lblAdmin.setOpaque(true);
        lblAdmin.setBackground(ADMIN_CLR);
        lblAdmin.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        header.add(headerText, BorderLayout.WEST);
        header.add(lblAdmin, BorderLayout.EAST);

        JPanel statsBar = new JPanel(new GridLayout(1, 7, 14, 14));
        statsBar.setBackground(BG);

        lblTotalUsers = statValue("0");
        lblActiveUsers = statValue("0");
        lblTotalBills = statValue("0");
        lblPaidBills = statValue("0");
        lblOverdueBills = statValue("0");
        lblTotalPayments = statValue("0");
        lblTotalReminders = statValue("0");

        statsBar.add(statCard("Total Users", lblTotalUsers, PRIMARY));
        statsBar.add(statCard("Active Users", lblActiveUsers, SUCCESS));
        statsBar.add(statCard("Total Bills", lblTotalBills, new Color(70, 130, 180)));
        statsBar.add(statCard("Paid Bills", lblPaidBills, SUCCESS));
        statsBar.add(statCard("Overdue Bills", lblOverdueBills, DANGER));
        statsBar.add(statCard("Payments", lblTotalPayments, new Color(100, 70, 180)));
        statsBar.add(statCard("Reminders", lblTotalReminders, WARNING));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(WHITE);
        tabbedPane.setForeground(PRIMARY);
        tabbedPane.setBorder(BorderFactory.createLineBorder(BORDER));

        tabbedPane.addTab("User Management", buildUserManagementTab());
        tabbedPane.addTab("Category Management", buildCategoryTab());
        tabbedPane.addTab("System Reports", buildReportsTab());
        tabbedPane.addTab("Reminder Monitor", buildReminderMonitorTab());

        JPanel topSection = new JPanel(new BorderLayout(0, 18));
        topSection.setBackground(BG);
        topSection.add(header, BorderLayout.NORTH);
        topSection.add(statsBar, BorderLayout.CENTER);

        root.add(topSection, BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel buildUserManagementTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = actionBar("User Actions",
                "Passwords are never displayed for security reasons.");

        JButton btnActivate = createBtn("Activate", SUCCESS);
        JButton btnDeactivate = createBtn("Deactivate", DANGER);
        JButton btnReset = createBtn("Reset Account", WARNING);
        JButton btnPromote = createBtn("Promote to Admin", new Color(70, 130, 180));
        JButton btnDemote = createBtn("Demote to User", SECONDARY);
        JButton btnRefresh = createBtn("Refresh", PRIMARY);

        controls.add(btnActivate);
        controls.add(btnDeactivate);
        controls.add(btnReset);
        controls.add(btnPromote);
        controls.add(btnDemote);
        controls.add(btnRefresh);

        String[] cols = {"ID", "Username", "Email", "Phone", "Role", "Status", "Registered"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblUsers = new JTable(userTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                String role = String.valueOf(getModel().getValueAt(row, 4));
                String status = String.valueOf(getModel().getValueAt(row, 5));

                if (!isRowSelected(row)) {
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        c.setBackground(new Color(0xF7, 0xEF, 0xE8));
                        c.setForeground(ADMIN_CLR);
                    } else if ("Inactive".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(0xFF, 0xEB, 0xEB));
                        c.setForeground(DANGER);
                    } else {
                        c.setBackground(WHITE);
                        c.setForeground(Color.DARK_GRAY);
                    }
                }
                return c;
            }
        };

        styleTable(tblUsers);

        JScrollPane scroll = new JScrollPane(tblUsers);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        btnActivate.addActionListener(e -> activateSelected());
        btnDeactivate.addActionListener(e -> deactivateSelected());
        btnReset.addActionListener(e -> resetSelected());
        btnPromote.addActionListener(e -> promoteSelected());
        btnDemote.addActionListener(e -> demoteSelected());
        btnRefresh.addActionListener(e -> loadData());

        return panel;
    }

    private JPanel buildCategoryTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = actionBar("Category Actions",
                "Admin manages predefined categories visible to all users.");

        JButton btnAdd = createBtn("+ Add Category", PRIMARY);
        JButton btnEdit = createBtn("Edit", SECONDARY);
        JButton btnDelete = createBtn("Delete", DANGER);
        JButton btnRefresh = createBtn("Refresh", new Color(70, 130, 180));

        controls.add(btnAdd);
        controls.add(btnEdit);
        controls.add(btnDelete);
        controls.add(btnRefresh);

        String[] cols = {"ID", "Category Name", "Description", "Type"};
        DefaultTableModel catModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable tblCategories = new JTable(catModel);
        styleTable(tblCategories);

        JScrollPane scroll = new JScrollPane(tblCategories);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadCategoriesIntoTable(catModel);

        btnAdd.addActionListener(e -> showAddCategoryDialog(catModel));
        btnEdit.addActionListener(e -> editCategorySelected(tblCategories, catModel));
        btnDelete.addActionListener(e -> deleteCategorySelected(tblCategories, catModel));
        btnRefresh.addActionListener(e -> loadCategoriesIntoTable(catModel));

        return panel;
    }

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = actionBar("System Reports",
                "Summary of users, bills, payments, and bill status.");
        JButton btnRefresh = createBtn("Refresh Report", PRIMARY);
        controls.add(btnRefresh);

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(BG);

        JPanel summaryCards = new JPanel(new GridLayout(1, 6, 14, 14));
        summaryCards.setBackground(BG);

        JLabel lTotalU = statValue("0");
        JLabel lActiveU = statValue("0");
        JLabel lTotalB = statValue("0");
        JLabel lPaidB = statValue("0");
        JLabel lOverB = statValue("0");
        JLabel lTotalP = statValue("0");

        summaryCards.add(statCard("Total Users", lTotalU, PRIMARY));
        summaryCards.add(statCard("Active Users", lActiveU, SUCCESS));
        summaryCards.add(statCard("Total Bills", lTotalB, new Color(70, 130, 180)));
        summaryCards.add(statCard("Paid Bills", lPaidB, SUCCESS));
        summaryCards.add(statCard("Overdue Bills", lOverB, DANGER));
        summaryCards.add(statCard("Total Payments", lTotalP, new Color(100, 70, 180)));

        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblBillStatus = new JLabel("Bills Summary by Status");
        lblBillStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBillStatus.setForeground(PRIMARY);

        String[] cols = {"Status", "Count", "Total Amount (RWF)"};
        DefaultTableModel rptModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable tblReport = new JTable(rptModel);
        styleTable(tblReport);

        tableCard.add(lblBillStatus, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblReport), BorderLayout.CENTER);

        content.add(summaryCards, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        loadReportData(lTotalU, lActiveU, lTotalB, lPaidB, lOverB, lTotalP, rptModel);

        btnRefresh.addActionListener(e ->
                loadReportData(lTotalU, lActiveU, lTotalB, lPaidB, lOverB, lTotalP, rptModel));

        return panel;
    }

    private JPanel buildReminderMonitorTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controls = actionBar("Reminder Monitor",
                "Admin can view all reminders but cannot change user settings.");
        JButton btnRefresh = createBtn("Refresh", PRIMARY);
        controls.add(btnRefresh);

        String[] cols = {"ID", "Bill Name", "User", "Trigger Date", "Days Before", "Dismissed"};
        DefaultTableModel remModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable tblReminders = new JTable(remModel);
        styleTable(tblReminders);

        JScrollPane scroll = new JScrollPane(tblReminders);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));

        panel.add(controls, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        loadRemindersIntoTable(remModel);

        btnRefresh.addActionListener(e -> loadRemindersIntoTable(remModel));

        return panel;
    }

    public void loadData() {
        try {
            allUsers = userService.getAllUsers();

            long active = allUsers.stream().filter(User::isActive).count();
            lblTotalUsers.setText(String.valueOf(allUsers.size()));
            lblActiveUsers.setText(String.valueOf(active));

            userTableModel.setRowCount(0);
            for (User u : allUsers) {
                userTableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getPhone(),
                    u.getRole(),
                    u.isActive() ? "Active" : "Inactive",
                    u.getCreatedAt() != null ? u.getCreatedAt().substring(0, 10) : "-"
                });
            }

            int totalBills = 0;
            int paidBills = 0;
            int overdueBills = 0;
            int totalPayments = 0;

            for (User u : allUsers) {
                List<Bill> bills = billService.getBillsByUser(u.getUserId());
                if (bills != null) {
                    totalBills += bills.size();
                    paidBills += bills.stream()
                            .filter(b -> "Paid".equalsIgnoreCase(b.getStatus())).count();
                    overdueBills += bills.stream()
                            .filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count();
                }
            }

            List<Payment> payments = paymentService.getAllPayments();
            if (payments != null) {
                totalPayments = payments.size();
            }

            List<Reminder> reminders = reminderService.getAllReminders();
            int totalReminders = reminders != null ? reminders.size() : 0;

            lblTotalBills.setText(String.valueOf(totalBills));
            lblPaidBills.setText(String.valueOf(paidBills));
            lblOverdueBills.setText(String.valueOf(overdueBills));
            lblTotalPayments.setText(String.valueOf(totalPayments));
            lblTotalReminders.setText(String.valueOf(totalReminders));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading admin data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoriesIntoTable(DefaultTableModel model) {
        try {
            List<Category> cats = categoryService.getAllCategories();
            model.setRowCount(0);
            if (cats != null) {
                for (Category c : cats) {
                    model.addRow(new Object[]{
                        c.getCategoryId(),
                        c.getName(),
                        c.getDescription() != null ? c.getDescription() : "-",
                        "Predefined"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading categories: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReportData(JLabel lTU, JLabel lAU, JLabel lTB,
                                JLabel lPB, JLabel lOB, JLabel lTP,
                                DefaultTableModel model) {
        try {
            List<User> users = userService.getAllUsers();
            long active = users.stream().filter(User::isActive).count();

            lTU.setText(String.valueOf(users.size()));
            lAU.setText(String.valueOf(active));

            int total = 0, paid = 0, overdue = 0, unpaid = 0;
            double totalAmt = 0, paidAmt = 0, overdueAmt = 0, unpaidAmt = 0;

            for (User u : users) {
                List<Bill> bills = billService.getBillsByUser(u.getUserId());
                if (bills != null) {
                    for (Bill b : bills) {
                        total++;
                        totalAmt += b.getAmount();

                        switch (b.getStatus().toLowerCase()) {
                            case "paid" -> {
                                paid++;
                                paidAmt += b.getAmount();
                            }
                            case "overdue" -> {
                                overdue++;
                                overdueAmt += b.getAmount();
                            }
                            default -> {
                                unpaid++;
                                unpaidAmt += b.getAmount();
                            }
                        }
                    }
                }
            }

            List<Payment> payments = paymentService.getAllPayments();
            int totalPay = payments != null ? payments.size() : 0;

            lTB.setText(String.valueOf(total));
            lPB.setText(String.valueOf(paid));
            lOB.setText(String.valueOf(overdue));
            lTP.setText(String.valueOf(totalPay));

            model.setRowCount(0);
            model.addRow(new Object[]{"Paid", paid, String.format("%.2f", paidAmt)});
            model.addRow(new Object[]{"Overdue", overdue, String.format("%.2f", overdueAmt)});
            model.addRow(new Object[]{"Unpaid", unpaid, String.format("%.2f", unpaidAmt)});
            model.addRow(new Object[]{"TOTAL", total, String.format("%.2f", totalAmt)});

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRemindersIntoTable(DefaultTableModel model) {
        try {
            List<Reminder> reminders = reminderService.getAllReminders();
            model.setRowCount(0);
            if (reminders != null) {
                for (Reminder r : reminders) {
                    model.addRow(new Object[]{
                        r.getReminderId(),
                        r.getBill() != null ? r.getBill().getName() : "-",
                        r.getBill() != null && r.getBill().getUser() != null
                                ? r.getBill().getUser().getUsername() : "-",
                        r.getTriggerDate(),
                        r.getDaysBefore(),
                        r.isDismissed() ? "Yes" : "No"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reminders: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void activateSelected() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to activate.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) userTableModel.getValueAt(row, 5);
        if ("Active".equals(status)) {
            JOptionPane.showMessageDialog(this, "User is already active.",
                    "Already Active", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) userTableModel.getValueAt(row, 0);
            userService.activateUser(id);
            JOptionPane.showMessageDialog(this, "User activated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deactivateSelected() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to deactivate.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTableModel.getValueAt(row, 1);

        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot deactivate the main system admin.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equals(loggedInUser.getUsername())) {
            JOptionPane.showMessageDialog(this,
                    "You cannot deactivate your own account.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate user '" + username + "'?\nThey will not be able to login until reactivated.",
                "Confirm Deactivate", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) userTableModel.getValueAt(row, 0);
                userService.deactivateUser(id);
                JOptionPane.showMessageDialog(this,
                        "User deactivated successfully.\nA notification email has been sent.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetSelected() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTableModel.getValueAt(row, 1);

        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot reset the main system admin account.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset account for '" + username + "'?\n\n"
                + "Their password will be reset to: reset123\n"
                + "Their account will be reactivated.\n"
                + "A notification email will be sent to them.",
                "Confirm Reset", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) userTableModel.getValueAt(row, 0);
                userService.resetUserAccount(id);
                JOptionPane.showMessageDialog(this,
                        "Account reset successfully.\nNew password: reset123\nA notification email has been sent.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void promoteSelected() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to promote.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = (String) userTableModel.getValueAt(row, 4);
        if ("ADMIN".equals(role)) {
            JOptionPane.showMessageDialog(this, "This user is already an admin.",
                    "Already Admin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Promote '" + username + "' to ADMIN role?\nThey will have full admin privileges.",
                "Confirm Promote", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) userTableModel.getValueAt(row, 0);
                userService.promoteToAdmin(id);
                JOptionPane.showMessageDialog(this,
                        "User promoted to Admin successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void demoteSelected() {
        int row = tblUsers.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to demote.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTableModel.getValueAt(row, 1);

        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot demote the main system admin.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equals(loggedInUser.getUsername())) {
            JOptionPane.showMessageDialog(this,
                    "You cannot demote your own account.",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Demote '" + username + "' to USER role?",
                "Confirm Demote", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) userTableModel.getValueAt(row, 0);
                userService.demoteToUser(id);
                JOptionPane.showMessageDialog(this,
                        "User demoted to User role successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddCategoryDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Add Predefined Category", true);

        dialog.setSize(480, 330);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("Add Predefined Category");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(WHITE);
        header.add(title, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel lblName = label("Category Name *");
        JTextField txtName = textField();

        JLabel lblDesc = label("Description");
        JTextField txtDesc = textField();

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(lblName, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        form.add(txtName, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(lblDesc, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        form.add(txtDesc, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(WHITE);

        JButton btnSave = createBtn("Save", PRIMARY);
        JButton btnCancel = createBtn("Cancel", SECONDARY);

        buttons.add(btnCancel);
        buttons.add(btnSave);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(buttons, gbc);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String desc = txtDesc.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Category name is required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (name.length() < 2) {
                JOptionPane.showMessageDialog(dialog,
                        "Category name must be at least 2 characters.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Category c = new Category(name, desc);
                categoryService.addCategory(c);

                JOptionPane.showMessageDialog(dialog,
                        "Category added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadCategoriesIntoTable(model);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(header, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void editCategorySelected(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a category to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String currentName = (String) model.getValueAt(row, 1);
        String currentDesc = (String) model.getValueAt(row, 2);

        String newName = JOptionPane.showInputDialog(this,
                "Edit Category Name:", currentName);
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        String newDesc = JOptionPane.showInputDialog(this,
                "Edit Description:", currentDesc);
        if (newDesc == null) {
            newDesc = currentDesc;
        }

        try {
            Category c = categoryService.getCategoryById(id);
            c.setName(newName.trim());
            c.setDescription(newDesc.trim());
            categoryService.updateCategory(c);

            JOptionPane.showMessageDialog(this,
                    "Category updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            loadCategoriesIntoTable(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategorySelected(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a category to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete category '" + name + "'?\nThis will affect all bills using this category.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) model.getValueAt(row, 0);
                categoryService.deleteCategory(id);

                JOptionPane.showMessageDialog(this,
                        "Category deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                loadCategoriesIntoTable(model);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel actionBar(String title, String note) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        left.setBackground(WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(PRIMARY);

        JLabel lblNote = new JLabel(note);
        lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNote.setForeground(SECONDARY);

        left.add(lblTitle);

        wrapper.add(left, BorderLayout.CENTER);
        wrapper.add(lblNote, BorderLayout.SOUTH);

        return left;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel accent = new JPanel();
        accent.setBackground(color);
        accent.setPreferredSize(new Dimension(6, 0));

        JPanel content = new JPanel();
        content.setBackground(WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(SECONDARY);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(8));
        content.add(valueLabel);

        card.add(accent, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JLabel statValue(String val) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        return btn;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(360, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(240, 235, 230));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(SECONDARY);
        table.setSelectionForeground(WHITE);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(100, 38));
    }
}