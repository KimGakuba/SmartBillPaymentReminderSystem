package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.OTPService;
import smartbill.server.service.UserService;

public class LoginFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(LoginFrame.class.getName());

    private UserService userService;
    private OTPService otpService;

    private javax.swing.JTextField txtUsername;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;

    private static final Color PRIMARY   = new Color(0x4A, 0x2A, 0x1A);
    private static final Color SECONDARY = new Color(0x8A, 0x5A, 0x3C);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;

    public LoginFrame() {
        initComponents();
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            userService = (UserService) registry.lookup("UserService");
            otpService  = (OTPService)  registry.lookup("OTPService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.\n" +
                "Make sure the server is running.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("SmartBill — Login");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(BG);

        // LEFT SIDE
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(PRIMARY);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 70, 60, 70));

        GridBagConstraints left = new GridBagConstraints();
        left.gridx = 0;
        left.anchor = GridBagConstraints.WEST;
        left.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel("SB", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 30));
        logo.setForeground(WHITE);
        logo.setBorder(BorderFactory.createLineBorder(new Color(180, 150, 130)));
        logo.setPreferredSize(new Dimension(70, 70));
        left.gridy = 0;
        left.insets = new Insets(0, 0, 25, 0);
        leftPanel.add(logo, left);

        JLabel title = new JLabel("SmartBill");
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(WHITE);
        left.gridy = 1;
        left.insets = new Insets(0, 0, 10, 0);
        leftPanel.add(title, left);

        JLabel subTitle = new JLabel("Login to your account");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subTitle.setForeground(new Color(210, 185, 165));
        left.gridy = 2;
        left.insets = new Insets(0, 0, 25, 0);
        leftPanel.add(subTitle, left);

        JLabel line = new JLabel("━━━━");
        line.setFont(new Font("Segoe UI", Font.BOLD, 20));
        line.setForeground(new Color(180, 130, 90));
        left.gridy = 3;
        left.insets = new Insets(0, 0, 25, 0);
        leftPanel.add(line, left);

        JLabel desc = new JLabel("<html>Manage your bills, payments,<br>categories, and reminders in one<br>organized dashboard.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(new Color(235, 220, 205));
        left.gridy = 4;
        left.insets = new Insets(0, 0, 35, 0);
        leftPanel.add(desc, left);

        String[] steps = {
            "01  Login securely",
            "02  Verify with OTP",
            "03  Manage your bills",
            "04  Track reminders easily"
        };

        for (int i = 0; i < steps.length; i++) {
            JLabel step = new JLabel(steps[i]);
            step.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            step.setForeground(new Color(210, 185, 165));
            left.gridy = 5 + i;
            left.insets = new Insets(8, 0, 8, 0);
            leftPanel.add(step, left);
        }

        // RIGHT SIDE
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(BG);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 205, 190)),
            BorderFactory.createEmptyBorder(35, 45, 35, 45)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel formTitle = new JLabel("Welcome back");
        formTitle.setFont(new Font("Serif", Font.BOLD, 30));
        formTitle.setForeground(PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(formTitle, gbc);

        JLabel formSub = new JLabel("Enter your login details to continue");
        formSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formSub.setForeground(SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(formSub, gbc);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 7, 0);
        card.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(380, 42));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 205, 190)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(SECONDARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 7, 0);
        card.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(380, 42));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 205, 190)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(txtPassword, gbc);

        JLabel lblInfo = new JLabel("An OTP will be sent to your registered email.");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(SECONDARY);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 22, 0);
        card.add(lblInfo, gbc);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(SECONDARY);
        btnLogin.setForeground(WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(380, 45));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(btnLogin, gbc);

        JSeparator separator = new JSeparator();
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(separator, gbc);

        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noAccount.setForeground(SECONDARY);
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(noAccount, gbc);

        btnRegister = new JButton("Create Account");
        btnRegister.setBackground(WHITE);
        btnRegister.setForeground(SECONDARY);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setBorder(BorderFactory.createLineBorder(new Color(220, 205, 190)));
        btnRegister.setPreferredSize(new Dimension(380, 42));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRegister, gbc);

        GridBagConstraints right = new GridBagConstraints();
        right.gridx = 0;
        right.gridy = 0;
        right.weightx = 1;
        right.weighty = 1;
        right.fill = GridBagConstraints.NONE;
        rightPanel.add(card, right);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);

        btnLogin.addActionListener(e -> loginAction());
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        txtPassword.addActionListener(e -> loginAction());
    }

    private void loginAction() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your username.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter your password.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userService.loginUser(username, password);

            otpService.generateOTP(user.getUsername());

            JOptionPane.showMessageDialog(this,
                "Credentials verified!\n\n" +
                "An OTP has been sent to:\n" +
                user.getEmail() + "\n\n" +
                "Please check your inbox and enter the OTP\n" +
                "to complete your login.",
                "OTP Sent", JOptionPane.INFORMATION_MESSAGE);

            new OTPVerificationFrame(user).setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
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
            () -> new LoginFrame().setVisible(true));
    }
}