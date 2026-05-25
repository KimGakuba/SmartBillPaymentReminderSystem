package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.OTPService;

public class OTPVerificationFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(
            OTPVerificationFrame.class.getName());

    private OTPService otpService;
    private User loggedInUser;

    private static final Color PRIMARY   = new Color(0x4A, 0x2A, 0x1A);
    private static final Color SECONDARY = new Color(0x8A, 0x5A, 0x3C);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color SUCCESS   = new Color(40, 167, 69);

    private javax.swing.JTextField txtOTP;
    private javax.swing.JButton btnVerify;
    private javax.swing.JButton btnResend;
    private javax.swing.JLabel lblTimer;
    private javax.swing.Timer countdownTimer;
    private int secondsLeft = 300;

    public OTPVerificationFrame(User user) {
        this.loggedInUser = user;
        initComponents();
        initRMI();
        buildUI();
        startCountdown();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            otpService = (OTPService) registry.lookup("OTPService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        setTitle("SmartBill — OTP Verification");
        setSize(1100, 650);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setBackground(BG);

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

        JLabel subTitle = new JLabel("Secure OTP Verification");
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

        JLabel desc = new JLabel("<html>We sent a one-time password<br>to your registered email.<br>Enter it to complete your login.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        desc.setForeground(new Color(235, 220, 205));
        left.gridy = 4;
        left.insets = new Insets(0, 0, 35, 0);
        leftPanel.add(desc, left);

        String[] steps = {
            "01  Credentials checked",
            "02  OTP sent to email",
            "03  Enter 6-digit code",
            "04  Access your dashboard"
        };

        for (int i = 0; i < steps.length; i++) {
            JLabel step = new JLabel(steps[i]);
            step.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            step.setForeground(new Color(210, 185, 165));
            left.gridy = 5 + i;
            left.insets = new Insets(8, 0, 8, 0);
            leftPanel.add(step, left);
        }

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

        JLabel formTitle = new JLabel("Verify your account");
        formTitle.setFont(new Font("Serif", Font.BOLD, 30));
        formTitle.setForeground(PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(formTitle, gbc);

        JLabel formSub = new JLabel("Enter the OTP sent to your email");
        formSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formSub.setForeground(SECONDARY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(formSub, gbc);

        JLabel lblUser = new JLabel("Logged in as: " + loggedInUser.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(lblUser, gbc);

        JLabel lblEmail = new JLabel("OTP sent to: " + loggedInUser.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setForeground(PRIMARY);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(lblEmail, gbc);

        JSeparator separator1 = new JSeparator();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(separator1, gbc);

        JLabel lblOTPInput = new JLabel("Enter OTP Code");
        lblOTPInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOTPInput.setForeground(SECONDARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 7, 0);
        card.add(lblOTPInput, gbc);

        txtOTP = new JTextField();
        txtOTP.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtOTP.setHorizontalAlignment(JTextField.CENTER);
        txtOTP.setPreferredSize(new Dimension(380, 48));
        txtOTP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 205, 190), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(txtOTP, gbc);

        lblTimer = new JLabel("Time remaining: 05:00", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTimer.setForeground(SUCCESS);
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(lblTimer, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(WHITE);

        btnVerify = new JButton("Verify OTP");
        btnVerify.setBackground(SECONDARY);
        btnVerify.setForeground(WHITE);
        btnVerify.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVerify.setFocusPainted(false);
        btnVerify.setBorderPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerify.setPreferredSize(new Dimension(180, 45));

        btnResend = new JButton("Resend OTP");
        btnResend.setBackground(PRIMARY);
        btnResend.setForeground(WHITE);
        btnResend.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnResend.setFocusPainted(false);
        btnResend.setBorderPainted(false);
        btnResend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResend.setPreferredSize(new Dimension(180, 45));

        buttonPanel.add(btnVerify);
        buttonPanel.add(btnResend);

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(buttonPanel, gbc);

        JSeparator separator2 = new JSeparator();
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(separator2, gbc);

        JLabel lblBack = new JLabel("Back to Login", SwingConstants.CENTER);
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBack.setForeground(SECONDARY);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(lblBack, gbc);

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

        btnVerify.addActionListener(e -> verifyAction());
        btnResend.addActionListener(e -> resendOTP());

        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                stopCountdown();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        txtOTP.addActionListener(e -> verifyAction());
    }

    private void startCountdown() {
        secondsLeft = 300;
        countdownTimer = new javax.swing.Timer(1000, e -> {
            secondsLeft--;
            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            lblTimer.setText(String.format(
                "Time remaining: %02d:%02d", minutes, seconds));

            if (secondsLeft <= 60) {
                lblTimer.setForeground(new Color(220, 53, 69));
            } else if (secondsLeft <= 120) {
                lblTimer.setForeground(new Color(255, 193, 7));
            }

            if (secondsLeft <= 0) {
                stopCountdown();
                lblTimer.setText("OTP expired.");
                btnVerify.setEnabled(false);
                JOptionPane.showMessageDialog(
                    OTPVerificationFrame.this,
                    "Your OTP has expired.\n" +
                    "Please click Resend OTP to get a new one.",
                    "OTP Expired", JOptionPane.WARNING_MESSAGE);
            }
        });
        countdownTimer.start();
    }

    private void stopCountdown() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }
    }

    private void resendOTP() {
        try {
            otpService.generateOTP(loggedInUser.getUsername());
            stopCountdown();
            startCountdown();
            btnVerify.setEnabled(true);
            txtOTP.setText("");
            JOptionPane.showMessageDialog(this,
                "A new OTP has been sent to:\n" +
                loggedInUser.getEmail() + "\n\n" +
                "Please check your inbox.",
                "OTP Resent", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error resending OTP: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifyAction() {
        String otp = txtOTP.getText().trim();

        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the OTP.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!otp.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(this,
                "OTP must be exactly 6 digits.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean valid = otpService.verifyOTP(
                loggedInUser.getUsername(), otp);

            if (valid) {
                stopCountdown();
                JOptionPane.showMessageDialog(this,
                    "OTP verified successfully!\n" +
                    "Welcome, " + loggedInUser.getUsername() + "!",
                    "Verification Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                if (loggedInUser.isAdmin()) {
                    new MainFrame(loggedInUser).setVisible(true);
                } else {
                    new MainFrame(loggedInUser).setVisible(true);
                }
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid OTP. Please try again.",
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
                txtOTP.setText("");
                txtOTP.requestFocus();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
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
            () -> new OTPVerificationFrame(null).setVisible(true));
    }
}