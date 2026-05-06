package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.UserService;

public class RegisterFrame extends javax.swing.JFrame {

    private UserService userService;
    private JTextField txtUsername, txtEmail, txtPhone;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister, btnBack;

    private static final Color ESPRESSO   = new Color(0x3B, 0x22, 0x12);
    private static final Color PRIMARY    = new Color(0x6F, 0x4E, 0x37);
    private static final Color CARAMEL    = new Color(0x8B, 0x65, 0x45);
    private static final Color SECONDARY  = new Color(0xAC, 0x98, 0x84);
    private static final Color CREAM      = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE      = Color.WHITE;
    private static final Color DIVIDER    = new Color(0xE0, 0xD5, 0xC8);
    private static final Color TEXT_DARK  = new Color(0x2C, 0x1A, 0x0E);
    private static final Color TEXT_LIGHT = new Color(0x7A, 0x65, 0x52);

    public RegisterFrame() {
        initComponents();
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            userService = (UserService) registry.lookup("UserService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("SmartBill — Create Account");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(CREAM);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.weightx = 0.45;
        root.add(createBrandPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        root.add(createRegisterPanel(), gbc);

        setContentPane(root);

        btnRegister.addActionListener(e -> registerAction());

        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
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
                g2.fillOval(getWidth() - 150, -80, 270, 270);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-110, getHeight() - 180, 290, 290);

                g2.dispose();
            }
        };

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

        JLabel lblTagline = new JLabel("Create your account");
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
            + "Start managing your bills with reminders, payments, categories, "
            + "and reports in one organized dashboard."
            + "</div></html>"
        );
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDesc.setForeground(new Color(0xD8, 0xC8, 0xB8));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel steps = new JLabel(
            "<html>"
            + "01&nbsp;&nbsp; Create your account<br><br>"
            + "02&nbsp;&nbsp; Add your bills<br><br>"
            + "03&nbsp;&nbsp; Get smart reminders<br><br>"
            + "04&nbsp;&nbsp; Track payments easily"
            + "</html>"
        );
        steps.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        steps.setForeground(SECONDARY);
        steps.setAlignmentX(Component.LEFT_ALIGNMENT);

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
        content.add(steps);

        panel.add(content);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createEmptyBorder(45, 65, 45, 65));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(38, 45, 38, 45)
        ));

        JLabel lblTitle = new JLabel("Create your account");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 30));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Fill in your details to get started");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        lblSubtitle.setForeground(TEXT_LIGHT);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        btnRegister = createPrimaryButton("Create Account");
        btnBack = createSecondaryButton("Back to Login");

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblSubtitle);
        card.add(Box.createVerticalStrut(28));

        card.add(label("Username"));
        card.add(Box.createVerticalStrut(6));
        styleField(txtUsername);
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(16));

        card.add(label("Email Address"));
        card.add(Box.createVerticalStrut(6));
        styleField(txtEmail);
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(16));

        card.add(label("Phone Number"));
        card.add(Box.createVerticalStrut(6));
        styleField(txtPhone);
        card.add(txtPhone);
        card.add(Box.createVerticalStrut(16));

        card.add(label("Password"));
        card.add(Box.createVerticalStrut(6));
        styleField(txtPassword);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(16));

        card.add(label("Confirm Password"));
        card.add(Box.createVerticalStrut(6));
        styleField(txtConfirmPassword);
        card.add(txtConfirmPassword);

        JLabel hint = new JLabel("Password must be at least 6 characters");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(SECONDARY);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalStrut(8));
        card.add(hint);
        card.add(Box.createVerticalStrut(25));

        card.add(btnRegister);
        card.add(Box.createVerticalStrut(20));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(separator);
        card.add(Box.createVerticalStrut(18));

        JLabel loginText = new JLabel("Already have an account?");
        loginText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginText.setForeground(TEXT_LIGHT);
        loginText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(loginText);
        card.add(Box.createVerticalStrut(10));
        card.add(btnBack);

        panel.add(card);
        return panel;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        field.setBackground(new Color(0xFF, 0xFC, 0xF8));
        field.setForeground(TEXT_DARK);
        field.setCaretColor(PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setPreferredSize(new Dimension(380, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(9, 14, 9, 14)
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
        button.setPreferredSize(new Dimension(380, 50));
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
        button.setPreferredSize(new Dimension(380, 46));
        button.setBorder(BorderFactory.createLineBorder(DIVIDER, 1));
        return button;
    }

    private void registerAction() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                "Phone number must contain digits only.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this,
                "Username must be at least 3 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = new User(username, email, password, phone,
                    LocalDateTime.now().toString());

            userService.registerUser(user);

            JOptionPane.showMessageDialog(this,
                "Account created successfully! Please login.",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

            new LoginFrame().setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}