package smartbill.client.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.client.view.panels.*;

public class MainFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(MainFrame.class.getName());

    private User loggedInUser;

    private static final Color ESPRESSO   = new Color(0x3B, 0x22, 0x12);
    private static final Color PRIMARY    = new Color(0x6F, 0x4E, 0x37);
    private static final Color CARAMEL    = new Color(0x8B, 0x65, 0x45);
    private static final Color SECONDARY  = new Color(0xAC, 0x98, 0x84);
    private static final Color CREAM      = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE      = Color.WHITE;
    private static final Color DIVIDER    = new Color(0xE0, 0xD5, 0xC8);
    private static final Color TEXT_DARK  = new Color(0x2C, 0x1A, 0x0E);
    private static final Color TEXT_LIGHT = new Color(0x7A, 0x65, 0x52);
    private static final Color SIDEBAR_BG = new Color(0x2E, 0x1A, 0x0C);
    private static final Color NAV_HOVER  = new Color(0x4A, 0x30, 0x1C);
    private static final Color NAV_ACTIVE = new Color(0x6F, 0x4E, 0x37);
    private static final Color DANGER     = new Color(0xC0, 0x39, 0x2B);

    private JPanel contentPanel;
    private JLabel lblPageTitle;
    private JButton activeNavBtn = null;

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
        setTitle("SmartBill — Payment Reminder System");
        setSize(1400, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CREAM);

        JPanel sidebar = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, SIDEBAR_BG,
                    0, getHeight(), new Color(0x1E, 0x10, 0x08));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(270, 820));

        JPanel logoArea = new JPanel(null);
        logoArea.setOpaque(false);
        logoArea.setBounds(0, 0, 270, 100);
        sidebar.add(logoArea);

        JLabel lblMark = new JLabel("SB");
        lblMark.setFont(new Font("Georgia", Font.BOLD, 28));
        lblMark.setForeground(WHITE);
        lblMark.setHorizontalAlignment(SwingConstants.CENTER);
        lblMark.setBounds(24, 24, 56, 56);
        lblMark.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 2));
        logoArea.add(lblMark);

        JLabel lblBrand = new JLabel("SmartBill");
        lblBrand.setFont(new Font("Georgia", Font.BOLD, 28));
        lblBrand.setForeground(WHITE);
        lblBrand.setBounds(95, 28, 180, 30);
        logoArea.add(lblBrand);

        JLabel lblTagline = new JLabel("Payment Reminder");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTagline.setForeground(SECONDARY);
        lblTagline.setBounds(95, 58, 180, 18);
        logoArea.add(lblTagline);

        JPanel topSep = new JPanel();
        topSep.setBackground(new Color(255, 255, 255, 18));
        topSep.setBounds(24, 100, 220, 1);
        sidebar.add(topSep);

        JPanel userCard = new JPanel(null);
        userCard.setOpaque(false);
        userCard.setBounds(0, 110, 270, 90);
        sidebar.add(userCard);

        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARAMEL);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 22));

                String initial = loggedInUser != null
                    ? String.valueOf(loggedInUser.getUsername().charAt(0)).toUpperCase()
                    : "U";

                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial,
                    (getWidth() - fm.stringWidth(initial)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setBounds(24, 18, 52, 52);
        userCard.add(avatar);

        String uname = loggedInUser != null ? loggedInUser.getUsername() : "User";

        JLabel lblName = new JLabel(uname);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(WHITE);
        lblName.setBounds(90, 24, 170, 24);
        userCard.add(lblName);

        JLabel lblRole = new JLabel("Account Holder");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setForeground(SECONDARY);
        lblRole.setBounds(90, 50, 170, 18);
        userCard.add(lblRole);

        JPanel navSep = new JPanel();
        navSep.setBackground(new Color(255, 255, 255, 18));
        navSep.setBounds(24, 205, 220, 1);
        sidebar.add(navSep);

        JLabel lblNav = new JLabel("NAVIGATION");
        lblNav.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNav.setForeground(new Color(0x8C, 0x78, 0x65));
        lblNav.setBounds(24, 220, 220, 18);
        sidebar.add(lblNav);

        String[][] navItems = {
            {"Dashboard",  "⊞", "dashboard"},
            {"Bills",      "📋", "bills"},
            {"Payments",   "💳", "payments"},
            {"Reminders",  "🔔", "reminders"},
            {"Categories", "⊟", "categories"},
            {"Reports",    "📊", "reports"},
        };

        int ny = 250;
        for (String[] item : navItems) {
            JButton btn = makeNavButton(item[0], item[1], item[2]);
            btn.setBounds(14, ny, 240, 52);
            sidebar.add(btn);
            ny += 60;
        }

        JPanel botSep = new JPanel();
        botSep.setBackground(new Color(255, 255, 255, 18));
        botSep.setBounds(24, 710, 220, 1);
        sidebar.add(botSep);

        JButton btnLogout = new JButton("Sign Out") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = getModel().isRollover()
                    ? new Color(0xC0, 0x39, 0x2B, 210)
                    : new Color(0xC0, 0x39, 0x2B, 100);

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(new Color(0xFF, 0xDD, 0xD5));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));

                FontMetrics fm = g2.getFontMetrics();
                String t = getText();

                g2.drawString(t,
                    (getWidth() - fm.stringWidth(t)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);

                g2.dispose();
            }
        };

        btnLogout.setBounds(14, 730, 240, 46);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?",
                "Sign Out",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        sidebar.add(btnLogout);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(CREAM);

        JPanel topBar = new JPanel(null);
        topBar.setBackground(WHITE);
        topBar.setPreferredSize(new Dimension(960, 82));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

        lblPageTitle = new JLabel("Dashboard");
        lblPageTitle.setFont(new Font("Georgia", Font.BOLD, 30));
        lblPageTitle.setForeground(TEXT_DARK);
        lblPageTitle.setBounds(34, 22, 400, 36);
        topBar.add(lblPageTitle);

        JLabel lblDate = new JLabel(java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d yyyy")));

        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(TEXT_LIGHT);
        lblDate.setOpaque(true);
        lblDate.setBackground(CREAM);
        lblDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)));

        lblDate.setBounds(980, 24, 260, 36);
        topBar.add(lblDate);

        contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(CREAM);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dashboardPanel = new DashboardPanel(loggedInUser);
        billPanel = new BillPanel(loggedInUser);
        paymentPanel = new PaymentPanel(loggedInUser);
        reminderPanel = new ReminderPanel(loggedInUser);
        categoryPanel = new CategoryPanel(loggedInUser);
        reportPanel = new ReportPanel(loggedInUser);

        mainArea.add(topBar, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JButton makeNavButton(String label, String icon, String panelKey) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                if (this == activeNavBtn) {
                    g2.setColor(NAV_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                    g2.setColor(new Color(0xD4, 0xA5, 0x74));
                    g2.fillRoundRect(getWidth() - 5, 10, 5, getHeight() - 20, 5, 5);

                } else if (getModel().isRollover()) {
                    g2.setColor(NAV_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.setColor(this == activeNavBtn ? WHITE : SECONDARY);
                g2.drawString(icon, 18, 34);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g2.setColor(this == activeNavBtn ? WHITE : new Color(0xB8, 0xA8, 0x98));
                g2.drawString(label, 56, 34);

                g2.dispose();
            }
        };

        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            activeNavBtn = btn;
            lblPageTitle.setText(label);
            showPanel(panelKey);
            repaint();
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });

        return btn;
    }

    public void showPanel(String key) {
        contentPanel.removeAll();

        switch (key) {
            case "dashboard" -> {
                dashboardPanel.loadData();
                contentPanel.add(dashboardPanel, BorderLayout.CENTER);
            }
            case "bills" -> {
                billPanel.loadData();
                contentPanel.add(billPanel, BorderLayout.CENTER);
            }
            case "payments" -> {
                paymentPanel.loadData();
                contentPanel.add(paymentPanel, BorderLayout.CENTER);
            }
            case "reminders" -> {
                reminderPanel.loadData();
                contentPanel.add(reminderPanel, BorderLayout.CENTER);
            }
            case "categories" -> {
                categoryPanel.loadData();
                contentPanel.add(categoryPanel, BorderLayout.CENTER);
            }
            case "reports" -> {
                reportPanel.loadData();
                contentPanel.add(reportPanel, BorderLayout.CENTER);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

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
    }

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
}