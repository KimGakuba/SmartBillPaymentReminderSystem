package smartbill.client.view;

import java.awt.*;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.client.view.panels.*;

public class MainFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(MainFrame.class.getName());

    private User loggedInUser;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color HOVER     = new Color(0x8B, 0x65, 0x45);

    private JPanel contentPanel;
    private JLabel lblCurrentUser;

    // Panels
    private DashboardPanel dashboardPanel;
    private BillPanel billPanel;
    private PaymentPanel paymentPanel;
    private ReminderPanel reminderPanel;
    private CategoryPanel categoryPanel;
    private ReportPanel reportPanel;

    public MainFrame(User user) {
        this.loggedInUser = user;
        initComponents();
        buildUI();
        showPanel("dashboard");
    }

    private void buildUI() {
        setTitle("Smart Bill Payment Reminder System");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // ── Sidebar ──────────────────────────────────────────
        JPanel sidebar = new JPanel(null);
        sidebar.setBackground(PRIMARY);
        sidebar.setPreferredSize(new Dimension(220, 680));

        // App logo area
        JPanel logoArea = new JPanel(null);
        logoArea.setBackground(new Color(0x5A, 0x3E, 0x2B));
        logoArea.setBounds(0, 0, 220, 80);
        sidebar.add(logoArea);

        JLabel lblLogo = new JLabel("SmartBill");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setForeground(WHITE);
        lblLogo.setBounds(25, 15, 180, 28);
        logoArea.add(lblLogo);

        JLabel lblTagline = new JLabel("Payment Reminder System");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTagline.setForeground(SECONDARY);
        lblTagline.setBounds(25, 45, 180, 18);
        logoArea.add(lblTagline);

        // User info area
        JPanel userArea = new JPanel(null);
        userArea.setBackground(new Color(0x5A, 0x3E, 0x2B));
        userArea.setBounds(0, 85, 220, 50);
        sidebar.add(userArea);

        lblCurrentUser = new JLabel("👤 " + loggedInUser.getUsername());
        lblCurrentUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCurrentUser.setForeground(SECONDARY);
        lblCurrentUser.setBounds(15, 15, 190, 20);
        userArea.add(lblCurrentUser);

        JSeparator sep = new JSeparator();
        sep.setBounds(15, 140, 190, 2);
        sep.setForeground(SECONDARY);
        sidebar.add(sep);

        // Nav buttons
        int y = 155;
        sidebar.add(navButton("Dashboard",  "dashboard",  y));       y += 48;
        sidebar.add(navButton("Bills",      "bills",      y));       y += 48;
        sidebar.add(navButton("Payments",   "payments",   y));       y += 48;
        sidebar.add(navButton("Reminders",  "reminders",  y));       y += 48;
        sidebar.add(navButton("Categories", "categories", y));       y += 48;
        sidebar.add(navButton("Reports",    "reports",    y));

        // Logout
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(15, 610, 190, 38);
        btnLogout.setBackground(new Color(180, 60, 60));
        btnLogout.setForeground(WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        sidebar.add(btnLogout);

        // ── Top bar ──────────────────────────────────────────
        JPanel topBar = new JPanel(null);
        topBar.setBackground(WHITE);
        topBar.setPreferredSize(new Dimension(880, 55));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY));

        JLabel lblSystem = new JLabel("Smart Bill Payment Reminder System");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSystem.setForeground(PRIMARY);
        lblSystem.setBounds(20, 15, 400, 25);
        topBar.add(lblSystem);

        JLabel lblDate = new JLabel(java.time.LocalDate.now().toString());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(SECONDARY);
        lblDate.setBounds(700, 18, 160, 20);
        topBar.add(lblDate);

        // ── Content Panel ─────────────────────────────────────
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG);

        // Initialize all panels
        dashboardPanel  = new DashboardPanel(loggedInUser);
        billPanel       = new BillPanel(loggedInUser);
        paymentPanel    = new PaymentPanel(loggedInUser);
        reminderPanel   = new ReminderPanel(loggedInUser);
        categoryPanel   = new CategoryPanel(loggedInUser);
        reportPanel     = new ReportPanel(loggedInUser);

        // Wrap content with topBar
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topBar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JButton navButton(String label, String panelKey, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(15, y, 190, 38);
        btn.setBackground(PRIMARY);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> showPanel(panelKey));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(PRIMARY);
            }
        });
        return btn;
    }

    public void showPanel(String key) {
        contentPanel.removeAll();
        switch (key) {
            case "dashboard"  -> { dashboardPanel.loadData();  contentPanel.add(dashboardPanel); }
            case "bills"      -> { billPanel.loadData();       contentPanel.add(billPanel); }
            case "payments"   -> { paymentPanel.loadData();    contentPanel.add(paymentPanel); }
            case "reminders"  -> { reminderPanel.loadData();   contentPanel.add(reminderPanel); }
            case "categories" -> { categoryPanel.loadData();   contentPanel.add(categoryPanel); }
            case "reports"    -> { reportPanel.loadData();     contentPanel.add(reportPanel); }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MainFrame(null).setVisible(true));
    }

    // Variables declaration - do not modify
    // End of variables declaration
}