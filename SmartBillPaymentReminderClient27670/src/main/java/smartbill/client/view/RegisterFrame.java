package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.UserService;

public class RegisterFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(RegisterFrame.class.getName());

    private UserService userService;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JButton btnRegister;
    private javax.swing.JButton btnBack;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;

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
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("Smart Bill Payment Reminder — Register");
        setSize(450, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG);

        // Header
        JPanel header = new JPanel(null);
        header.setBackground(PRIMARY);
        header.setBounds(0, 0, 450, 90);
        mainPanel.add(header);

        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(WHITE);
        lblTitle.setBounds(130, 18, 250, 30);
        header.add(lblTitle);

        JLabel lblSub = new JLabel("Join SmartBill and stay on top of your bills.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(SECONDARY);
        lblSub.setBounds(80, 52, 320, 20);
        header.add(lblSub);

        // Form card
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(SECONDARY, 1));
        card.setBounds(40, 110, 370, 390);
        mainPanel.add(card);

        // Username
        addLabel(card, "Username", 20, 15);
        txtUsername = addField(card, 20, 38);

        // Email
        addLabel(card, "Email", 20, 83);
        txtEmail = addField(card, 20, 106);

        // Phone
        addLabel(card, "Phone", 20, 151);
        txtPhone = addField(card, 20, 174);

        // Password
        addLabel(card, "Password", 20, 219);
        txtPassword = new javax.swing.JPasswordField();
        styleField(txtPassword, 20, 242);
        card.add(txtPassword);

        // Confirm Password
        addLabel(card, "Confirm Password", 20, 287);
        txtConfirmPassword = new javax.swing.JPasswordField();
        styleField(txtConfirmPassword, 20, 310);
        card.add(txtConfirmPassword);

        // Register button
        btnRegister = new javax.swing.JButton("Register");
        btnRegister.setBounds(20, 358, 155, 18);
        btnRegister.setBackground(PRIMARY);
        btnRegister.setForeground(WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnRegister);

        // Back button
        btnBack = new javax.swing.JButton("Back to Login");
        btnBack.setBounds(195, 358, 155, 18);
        btnBack.setBackground(SECONDARY);
        btnBack.setForeground(WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnBack);

        // Footer
        JLabel lblFooter = new JLabel("© 2026 Smart Bill Payment Reminder System");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(SECONDARY);
        lblFooter.setBounds(80, 525, 300, 20);
        mainPanel.add(lblFooter);

        setContentPane(mainPanel);

        btnRegister.addActionListener(e -> registerAction());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void addLabel(JPanel panel, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(PRIMARY);
        lbl.setBounds(x, y, 200, 20);
        panel.add(lbl);
    }

    private javax.swing.JTextField addField(JPanel panel, int x, int y) {
        javax.swing.JTextField field = new javax.swing.JTextField();
        styleField(field, x, y);
        panel.add(field);
        return field;
    }

    private void styleField(javax.swing.JTextField field, int x, int y) {
        field.setBounds(x, y, 330, 35);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void registerAction() {
        String username        = txtUsername.getText().trim();
        String email           = txtEmail.getText().trim();
        String phone           = txtPhone.getText().trim();
        String password        = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        // Technical validations
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "All fields are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                "Phone number must contain digits only.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this,
                "Username must be at least 3 characters.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Business validations
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = new User(username, email, password, phone,
                LocalDateTime.now().toString());
            userService.registerUser(user);
            JOptionPane.showMessageDialog(this,
                "Account created successfully! Please login.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
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
        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }

    // Variables declaration - do not modify
    // End of variables declaration
}