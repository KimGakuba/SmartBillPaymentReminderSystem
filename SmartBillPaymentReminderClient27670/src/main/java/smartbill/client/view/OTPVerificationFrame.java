package smartbill.client.view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import smartbill.server.model.User;
import smartbill.server.service.OTPService;

public class OTPVerificationFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(OTPVerificationFrame.class.getName());

    private OTPService otpService;
    private User loggedInUser;
    private String generatedOTP;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color SUCCESS   = new Color(40, 167, 69);

    private javax.swing.JTextField txtOTP;
    private javax.swing.JButton btnVerify;
    private javax.swing.JButton btnResend;
    private javax.swing.JLabel lblSimOTP;

    public OTPVerificationFrame(User user) {
        this.loggedInUser = user;
        initComponents();
        initRMI();
        buildUI();
        sendOTP();
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
        setTitle("Smart Bill Payment Reminder — OTP Verification");
        setSize(430, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG);

        // Header
        JPanel header = new JPanel(null);
        header.setBackground(PRIMARY);
        header.setBounds(0, 0, 430, 90);
        mainPanel.add(header);

        JLabel lblTitle = new JLabel("OTP Verification");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(WHITE);
        lblTitle.setBounds(110, 18, 250, 30);
        header.add(lblTitle);

        JLabel lblSub = new JLabel("Enter the OTP sent to verify your identity.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(SECONDARY);
        lblSub.setBounds(65, 52, 320, 20);
        header.add(lblSub);

        // Card
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(SECONDARY, 1));
        card.setBounds(40, 110, 350, 240);
        mainPanel.add(card);

        // User info
        JLabel lblUser = new JLabel("Logged in as: " + loggedInUser.getUsername());
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUser.setForeground(SECONDARY);
        lblUser.setBounds(20, 15, 310, 20);
        card.add(lblUser);

        JSeparator sep = new JSeparator();
        sep.setBounds(20, 40, 310, 2);
        sep.setForeground(new Color(230, 220, 210));
        card.add(sep);

        // Simulation label — shows OTP on screen for academic purposes
        JLabel lblSimLabel = new JLabel("Simulated OTP (for testing):");
        lblSimLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSimLabel.setForeground(SECONDARY);
        lblSimLabel.setBounds(20, 50, 220, 18);
        card.add(lblSimLabel);

        lblSimOTP = new JLabel("------");
        lblSimOTP.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblSimOTP.setForeground(SUCCESS);
        lblSimOTP.setBounds(20, 70, 310, 38);
        card.add(lblSimOTP);

        JSeparator sep2 = new JSeparator();
        sep2.setBounds(20, 115, 310, 2);
        sep2.setForeground(new Color(230, 220, 210));
        card.add(sep2);

        // OTP input
        JLabel lblOTPInput = new JLabel("Enter OTP");
        lblOTPInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblOTPInput.setForeground(PRIMARY);
        lblOTPInput.setBounds(20, 125, 100, 20);
        card.add(lblOTPInput);

        txtOTP = new javax.swing.JTextField();
        txtOTP.setBounds(20, 148, 310, 38);
        txtOTP.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtOTP.setHorizontalAlignment(JTextField.CENTER);
        txtOTP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        card.add(txtOTP);

        // Verify button
        btnVerify = new javax.swing.JButton("Verify OTP");
        btnVerify.setBounds(20, 200, 145, 35);
        btnVerify.setBackground(PRIMARY);
        btnVerify.setForeground(WHITE);
        btnVerify.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVerify.setFocusPainted(false);
        btnVerify.setBorderPainted(false);
        btnVerify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnVerify);

        // Resend button
        btnResend = new javax.swing.JButton("Resend OTP");
        btnResend.setBounds(185, 200, 145, 35);
        btnResend.setBackground(SECONDARY);
        btnResend.setForeground(WHITE);
        btnResend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnResend.setFocusPainted(false);
        btnResend.setBorderPainted(false);
        btnResend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnResend);

        // Footer
        JLabel lblFooter = new JLabel("© 2026 Smart Bill Payment Reminder System");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(SECONDARY);
        lblFooter.setBounds(80, 375, 300, 20);
        mainPanel.add(lblFooter);

        setContentPane(mainPanel);

        btnVerify.addActionListener(e -> verifyAction());
        btnResend.addActionListener(e -> sendOTP());
    }

    private void sendOTP() {
        try {
            generatedOTP = otpService.generateOTP(loggedInUser.getUsername());
            // Show OTP in simulation label
            lblSimOTP.setText(generatedOTP);
            JOptionPane.showMessageDialog(this,
                "OTP sent successfully!\nYour OTP is: " + generatedOTP +
                "\n(Valid for 5 minutes)",
                "OTP Sent", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error sending OTP: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifyAction() {
        String otp = txtOTP.getText().trim();

        // Technical validation
        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter the OTP.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Technical validation — OTP must be 6 digits
        if (!otp.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(this,
                "OTP must be exactly 6 digits.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean valid = otpService.verifyOTP(loggedInUser.getUsername(), otp);
            if (valid) {
                JOptionPane.showMessageDialog(this,
                    "OTP verified successfully! Welcome, " +
                    loggedInUser.getUsername() + "!",
                    "Verification Successful", JOptionPane.INFORMATION_MESSAGE);
                new MainFrame(loggedInUser).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid OTP. Please try again.",
                    "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Verification Failed", JOptionPane.ERROR_MESSAGE);
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
        java.awt.EventQueue.invokeLater(() ->
            new OTPVerificationFrame(null).setVisible(true));
    }

    // Variables declaration - do not modify
    // End of variables declaration
}