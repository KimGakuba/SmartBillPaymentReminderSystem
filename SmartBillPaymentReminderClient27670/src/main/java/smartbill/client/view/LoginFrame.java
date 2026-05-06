package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.UserService;

public class LoginFrame extends javax.swing.JFrame {

    private UserService userService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    private static final Color ESPRESSO   = new Color(0x3B, 0x22, 0x12);
    private static final Color PRIMARY    = new Color(0x6F, 0x4E, 0x37);
    private static final Color CARAMEL    = new Color(0x8B, 0x65, 0x45);
    private static final Color SECONDARY  = new Color(0xAC, 0x98, 0x84);
    private static final Color CREAM      = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE      = Color.WHITE;
    private static final Color DIVIDER    = new Color(0xE0, 0xD5, 0xC8);
    private static final Color TEXT_DARK  = new Color(0x2C, 0x1A, 0x0E);
    private static final Color TEXT_LIGHT = new Color(0x7A, 0x65, 0x52);

    public LoginFrame() {
        initComponents();
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            userService = (UserService) registry.lookup("UserService");
            System.out.println("Connected to UserService successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Could not connect to UserService.\n" + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("SmartBill — Login");
        setSize(1000, 650);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(CREAM);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        JPanel leftPanel = createBrandPanel();
        JPanel rightPanel = createLoginPanel();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.48;
        gbc.weighty = 1;
        root.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.52;
        root.add(rightPanel, gbc);

        setContentPane(root);

        btnLogin.addActionListener(e -> loginAction());

        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        txtPassword.addActionListener(e -> loginAction());
    }

    private JPanel createBrandPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                    0, 0, ESPRESSO,
                    getWidth(), getHeight(), new Color(0x5A, 0x3E, 0x2B)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 150, -70, 260, 260);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-100, getHeight() - 180, 280, 280);

                g2.dispose();
            }
        };

        panel.setBackground(ESPRESSO);
        panel.setBorder(BorderFactory.createEmptyBorder(60, 55, 60, 55));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel lblMark = new JLabel("SB");
        lblMark.setFont(new Font("Georgia", Font.BOLD, 38));
        lblMark.setForeground(WHITE);
        lblMark.setHorizontalAlignment(SwingConstants.CENTER);
        lblMark.setMaximumSize(new Dimension(80, 80));
        lblMark.setPreferredSize(new Dimension(80, 80));
        lblMark.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMark.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 2));

        JLabel lblBrand = new JLabel("SmartBill");
        lblBrand.setFont(new Font("Georgia", Font.BOLD, 44));
        lblBrand.setForeground(WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagline = new JLabel("Payment Reminder System");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        lblTagline.setForeground(SECONDARY);
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel line = new JPanel();
        line.setBackground(CARAMEL);
        line.setMaximumSize(new Dimension(70, 4));
        line.setPreferredSize(new Dimension(70, 4));
        line.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(
            "<html><div style='width:320px; line-height:1.7;'>"
            + "Track your bills, monitor payments, receive reminders, "
            + "and stay financially organized in one clean dashboard."
            + "</div></html>"
        );
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(new Color(0xD8, 0xC8, 0xB8));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel features = new JLabel(
            "<html>"
            + "✓ Bill Tracking<br><br>"
            + "✓ Smart Reminders<br><br>"
            + "✓ Payment Reports<br><br>"
            + "✓ Secure Account Access"
            + "</html>"
        );
        features.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        features.setForeground(SECONDARY);
        features.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblMark);
        content.add(Box.createVerticalStrut(30));
        content.add(lblBrand);
        content.add(Box.createVerticalStrut(8));
        content.add(lblTagline);
        content.add(Box.createVerticalStrut(30));
        content.add(line);
        content.add(Box.createVerticalStrut(28));
        content.add(lblDesc);
        content.add(Box.createVerticalStrut(45));
        content.add(features);

        panel.add(content);
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(45, 45, 45, 45)
        ));

        JLabel lblWelcome = new JLabel("Welcome back");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblWelcome.setForeground(TEXT_LIGHT);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Sign in to your account");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 30));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblUser.setForeground(TEXT_LIGHT);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        styleInputField(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPass.setForeground(TEXT_LIGHT);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        styleInputField(txtPassword);

        btnLogin = createPrimaryButton("Sign In");
        btnRegister = createSecondaryButton("Create Account");

        JLabel lblQuestion = new JLabel("Don't have an account?");
        lblQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblQuestion.setForeground(TEXT_LIGHT);
        lblQuestion.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblWelcome);
        card.add(Box.createVerticalStrut(8));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(35));

        card.add(lblUser);
        card.add(Box.createVerticalStrut(8));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(22));

        card.add(lblPass);
        card.add(Box.createVerticalStrut(8));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(30));

        card.add(btnLogin);
        card.add(Box.createVerticalStrut(25));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(separator);
        card.add(Box.createVerticalStrut(20));

        card.add(lblQuestion);
        card.add(Box.createVerticalStrut(12));
        card.add(btnRegister);

        panel.add(card);
        return panel;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        field.setBackground(new Color(0xFF, 0xFC, 0xF8));
        field.setForeground(TEXT_DARK);
        field.setCaretColor(PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setPreferredSize(new Dimension(360, 48));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 17));
        button.setForeground(WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setPreferredSize(new Dimension(360, 50));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(PRIMARY);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(360, 46));
        button.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        return button;
    }

    private void loginAction() {
        if (userService == null) {
            JOptionPane.showMessageDialog(this,
                "UserService is not connected.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username and password are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userService.loginUser(username, password);
            // NEW — goes to OTP verification first
            new OTPVerificationFrame(user).setVisible(true);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Login failed: " + e.getMessage(),
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}