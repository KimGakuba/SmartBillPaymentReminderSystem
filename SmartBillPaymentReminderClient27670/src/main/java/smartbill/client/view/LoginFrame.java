package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.UserService;
//import smartbill.server.service.UserService;  // ← was smartbill.client.service
//import smartbill.server.model.User;            // ← was smartbill.client.model

public class LoginFrame extends javax.swing.JFrame {

    private UserService userService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginFrame() {
        initComponents();
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);

            System.out.println("Checking RMI services...");
            for (String name : registry.list()) {
                System.out.println("Found service: " + name);
            }

            userService = (UserService) registry.lookup("UserService");

            System.out.println("Connected to UserService successfully.");

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                "Could not connect to UserService.\n\n"
                + "Real error type: " + e.getClass().getName() + "\n"
                + "Real error message: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("Smart Bill Payment Reminder — Login");
        setSize(900, 650);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(0xF5, 0xF0, 0xEB));

        JPanel card = new JPanel(new GridLayout(5, 1, 10, 10));
        card.setPreferredSize(new Dimension(420, 280));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("SmartBill Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Create Account");

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.add(btnLogin);
        buttons.add(btnRegister);

        card.add(lblTitle);
        card.add(txtUsername);
        card.add(txtPassword);
        card.add(btnLogin);
        card.add(btnRegister);

        mainPanel.add(card);
        setContentPane(mainPanel);

        btnLogin.addActionListener(e -> loginAction());

        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void loginAction() {
        if (userService == null) {
            JOptionPane.showMessageDialog(this,
                "UserService is not connected.\nCheck server output and client imports.",
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

            JOptionPane.showMessageDialog(this,
                "Welcome back, " + user.getUsername() + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

            new MainFrame(user).setVisible(true);
            dispose();

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                "Login failed.\n\n"
                + "Real error type: " + e.getClass().getName() + "\n"
                + "Real error message: " + e.getMessage(),
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