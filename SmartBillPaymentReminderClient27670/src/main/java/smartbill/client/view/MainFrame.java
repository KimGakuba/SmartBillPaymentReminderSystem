package smartbill.client.view;

import java.awt.*;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.client.view.panels.AdminPanel;
import smartbill.client.view.panels.BillPanel;
import smartbill.client.view.panels.CategoryPanel;
import smartbill.client.view.panels.DashboardPanel;
import smartbill.client.view.panels.PaymentPanel;
import smartbill.client.view.panels.ReminderPanel;
import smartbill.client.view.panels.ReportPanel;

public class MainFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(MainFrame.class.getName());

    private User loggedInUser;

    private static final Color PRIMARY   = new Color(0x4A, 0x2A, 0x1A);
    private static final Color SECONDARY = new Color(0x8A, 0x5A, 0x3C);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color HOVER     = new Color(0x6B, 0x3F, 0x28);
    private static final Color ADMIN_CLR = new Color(0x8B, 0x00, 0x00);

    private JPanel contentPanel;
    private JLabel lblCurrentUser;
    private JLabel lblRole;

    private DashboardPanel  dashboardPanel;
    private BillPanel       billPanel;
    private PaymentPanel    paymentPanel;
    private ReminderPanel   reminderPanel;
    private CategoryPanel   categoryPanel;
    private ReportPanel     reportPanel;
    private AdminPanel      adminPanel;

    public MainFrame(User user) {
        this.loggedInUser = user;
        initComponents();
        buildUI();
        showPanel("dashboard");
    }

    private void buildUI() {
        setTitle("SmartBill — Dashboard");
        setSize(1200, 720);
        setMinimumSize(new Dimension(950, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(PRIMARY);
        sidebar.setPreferredSize(new Dimension(250, 720));

        JPanel sidebarTop = new JPanel();
        sidebarTop.setLayout(new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setBackground(PRIMARY);
        sidebarTop.setBorder(BorderFactory.createEmptyBorder(25, 25, 15, 25));

        JLabel logo = new JLabel("SB", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 28));
        logo.setForeground(WHITE);
        logo.setMaximumSize(new Dimension(70, 55));
        logo.setPreferredSize(new Dimension(70, 55));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(BorderFactory.createLineBorder(new Color(180, 150, 130)));

        JLabel appName = new JLabel("SmartBill");
        appName.setFont(new Font("Serif", Font.BOLD, 30));
        appName.setForeground(WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appSub = new JLabel("Payment Reminder System");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        appSub.setForeground(new Color(210, 185, 165));
        appSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userBox = new JPanel();
        userBox.setLayout(new BoxLayout(userBox, BoxLayout.Y_AXIS));
        userBox.setBackground(new Color(0x5A, 0x35, 0x22));
        userBox.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        userBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        userBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblCurrentUser = new JLabel(loggedInUser.getUsername());
        lblCurrentUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentUser.setForeground(WHITE);
        lblCurrentUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblRole = new JLabel(loggedInUser.isAdmin() ? "ADMIN ACCOUNT" : "USER ACCOUNT");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRole.setForeground(loggedInUser.isAdmin()
            ? new Color(0xFF, 0xD7, 0x00)
            : new Color(210, 185, 165));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        userBox.add(lblCurrentUser);
        userBox.add(Box.createVerticalStrut(5));
        userBox.add(lblRole);

        sidebarTop.add(logo);
        sidebarTop.add(Box.createVerticalStrut(22));
        sidebarTop.add(appName);
        sidebarTop.add(Box.createVerticalStrut(5));
        sidebarTop.add(appSub);
        sidebarTop.add(Box.createVerticalStrut(25));
        sidebarTop.add(userBox);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(PRIMARY);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        navPanel.add(navButton("Dashboard", "dashboard"));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(navButton("Bills", "bills"));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(navButton("Payments", "payments"));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(navButton("Reminders", "reminders"));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(navButton("Categories", "categories"));
        navPanel.add(Box.createVerticalStrut(8));
        navPanel.add(navButton("Reports", "reports"));

        if (loggedInUser.isAdmin()) {
            navPanel.add(Box.createVerticalStrut(18));

            JSeparator adminSep = new JSeparator();
            adminSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            navPanel.add(adminSep);
            navPanel.add(Box.createVerticalStrut(18));

            JButton btnAdmin = navButton("Admin Panel", "admin");
            btnAdmin.setBackground(ADMIN_CLR);
            btnAdmin.setForeground(new Color(0xFF, 0xD7, 0x00));
            navPanel.add(btnAdmin);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(PRIMARY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(180, 60, 60));
        btnLogout.setForeground(WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        bottomPanel.add(btnLogout);

        sidebar.add(sidebarTop, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(WHITE);
        topBar.setPreferredSize(new Dimension(900, 65));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 205, 190)));

        JPanel titleArea = new JPanel();
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        titleArea.setBackground(WHITE);
        titleArea.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));

        JLabel lblSystem = new JLabel("Smart Bill Payment Reminder System");
        lblSystem.setFont(new Font("Serif", Font.BOLD, 24));
        lblSystem.setForeground(PRIMARY);

        JLabel lblWelcome = new JLabel("Welcome, " + loggedInUser.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(SECONDARY);

        titleArea.add(lblSystem);
        titleArea.add(lblWelcome);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 18));
        rightTop.setBackground(WHITE);

        if (loggedInUser.isAdmin()) {
            JLabel lblAdminBadge = new JLabel("ADMIN MODE");
            lblAdminBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblAdminBadge.setForeground(WHITE);
            lblAdminBadge.setBackground(ADMIN_CLR);
            lblAdminBadge.setOpaque(true);
            lblAdminBadge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            rightTop.add(lblAdminBadge);
        }

        JLabel lblDate = new JLabel(java.time.LocalDate.now().toString());
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDate.setForeground(SECONDARY);
        rightTop.add(lblDate);

        topBar.add(titleArea, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dashboardPanel = new DashboardPanel(loggedInUser);
        billPanel      = new BillPanel(loggedInUser);
        paymentPanel   = new PaymentPanel(loggedInUser);
        reminderPanel  = new ReminderPanel(loggedInUser);
        categoryPanel  = new CategoryPanel(loggedInUser);
        reportPanel    = new ReportPanel(loggedInUser);

        if (loggedInUser.isAdmin()) {
            adminPanel = new AdminPanel(loggedInUser);
        }

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG);
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton navButton(String label, String panelKey) {
        JButton btn = new JButton(label);
        btn.setBackground(PRIMARY);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addActionListener(e -> showPanel(panelKey));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if ("Admin Panel".equals(label)) {
                    btn.setBackground(ADMIN_CLR);
                } else {
                    btn.setBackground(PRIMARY);
                }
            }
        });

        return btn;
    }

    public void showPanel(String key) {
        contentPanel.removeAll();
        switch (key) {
            case "dashboard" -> {
                dashboardPanel.loadData();
                contentPanel.add(dashboardPanel);
            }
            case "bills" -> {
                billPanel.loadData();
                contentPanel.add(billPanel);
            }
            case "payments" -> {
                paymentPanel.loadData();
                contentPanel.add(paymentPanel);
            }
            case "reminders" -> {
                reminderPanel.loadData();
                contentPanel.add(reminderPanel);
            }
            case "categories" -> {
                categoryPanel.loadData();
                contentPanel.add(categoryPanel);
            }
            case "reports" -> {
                reportPanel.loadData();
                contentPanel.add(reportPanel);
            }
            case "admin" -> {
                if (loggedInUser.isAdmin() && adminPanel != null) {
                    adminPanel.loadData();
                    contentPanel.add(adminPanel);
                }
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
            getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(
                        info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException |
                 javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(
            () -> new MainFrame(null).setVisible(true));
    }
}