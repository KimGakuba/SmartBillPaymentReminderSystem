package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import smartbill.server.model.Bill;
import smartbill.server.model.Payment;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.PaymentService;
import smartbill.server.service.ReminderService;

public class DashboardPanel extends JPanel {

    private User loggedInUser;
    private BillService billService;
    private PaymentService paymentService;
    private ReminderService reminderService;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color SUCCESS   = new Color(40, 167, 69);
    private static final Color DANGER    = new Color(220, 53, 69);
    private static final Color WARNING   = new Color(255, 193, 7);

    private JLabel lblTotalBills;
    private JLabel lblPaidBills;
    private JLabel lblOverdueBills;
    private JLabel lblPendingReminders;
    private JPanel recentPanel;

    public DashboardPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            billService     = (BillService) registry.lookup("BillService");
            paymentService  = (PaymentService) registry.lookup("PaymentService");
            reminderService = (ReminderService) registry.lookup("ReminderService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        // ── Page Title ──
        JPanel titleBar = new JPanel(null);
        titleBar.setBackground(BG);
        titleBar.setPreferredSize(new Dimension(880, 60));

        JLabel lblPage = new JLabel("Dashboard");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPage.setForeground(PRIMARY);
        lblPage.setBounds(25, 15, 200, 30);
        titleBar.add(lblPage);

        JLabel lblSub = new JLabel("Overview of your billing activity");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(SECONDARY);
        lblSub.setBounds(25, 42, 300, 18);
        titleBar.add(lblSub);

        add(titleBar, BorderLayout.NORTH);

        // ── Main Content ──
        JPanel content = new JPanel(null);
        content.setBackground(BG);

        // ── Stat Cards ──
        lblTotalBills      = statValue("0");
        lblPaidBills       = statValue("0");
        lblOverdueBills    = statValue("0");
        lblPendingReminders = statValue("0");

        content.add(statCard("Total Bills",       lblTotalBills,       PRIMARY,  20,  10));
        content.add(statCard("Paid Bills",         lblPaidBills,        SUCCESS, 230,  10));
        content.add(statCard("Overdue Bills",      lblOverdueBills,     DANGER,  440,  10));
        content.add(statCard("Pending Reminders",  lblPendingReminders, WARNING, 650,  10));

        // ── Recent Bills ──
        JLabel lblRecent = new JLabel("Recent Bills");
        lblRecent.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRecent.setForeground(PRIMARY);
        lblRecent.setBounds(20, 145, 200, 25);
        content.add(lblRecent);

        recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBackground(WHITE);
        recentPanel.setBorder(BorderFactory.createLineBorder(SECONDARY, 1));

        JScrollPane scroll = new JScrollPane(recentPanel);
        scroll.setBounds(20, 175, 840, 350);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        content.add(scroll);

        add(content, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            List<Bill> bills = billService.getBillsByUser(loggedInUser.getUserId());

            int total   = bills.size();
            int paid    = (int) bills.stream()
                .filter(b -> "Paid".equalsIgnoreCase(b.getStatus())).count();
            int overdue = (int) bills.stream()
                .filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count();
            int pending = reminderService
                .getPendingReminders(loggedInUser.getUserId()).size();

            lblTotalBills.setText(String.valueOf(total));
            lblPaidBills.setText(String.valueOf(paid));
            lblOverdueBills.setText(String.valueOf(overdue));
            lblPendingReminders.setText(String.valueOf(pending));

            // Recent bills
            recentPanel.removeAll();

            // Header row
            JPanel headerRow = tableRow(
                new Color(0xF0, 0xE8, 0xE0), true,
                "Bill Name", "Amount (RWF)", "Due Date", "Recurrence", "Status"
            );
            recentPanel.add(headerRow);

            for (Bill b : bills) {
                JPanel row = tableRow(
                    WHITE, false,
                    b.getName(),
                    String.format("%.2f", b.getAmount()),
                    b.getDueDate(),
                    b.getRecurrence(),
                    b.getStatus()
                );
                recentPanel.add(row);
            }

            // Notify overdue
            if (overdue > 0) {
                JOptionPane.showMessageDialog(this,
                    "You have " + overdue + " overdue bill(s)! Please review them.",
                    "Overdue Alert", JOptionPane.WARNING_MESSAGE);
            }

            recentPanel.revalidate();
            recentPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading dashboard: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel statCard(String title, JLabel valueLabel, Color color, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, color));
        card.setBounds(x, y, 195, 100);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(SECONDARY);
        lblTitle.setBounds(15, 18, 165, 18);
        card.add(lblTitle);

        valueLabel.setBounds(15, 42, 165, 38);
        card.add(valueLabel);

        return card;
    }

    private JLabel statValue(String val) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JPanel tableRow(Color bg, boolean isHeader,
                             String col1, String col2, String col3, String col4, String col5) {
        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setBackground(bg);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 220, 210)));

        Font font = isHeader
            ? new Font("Segoe UI", Font.BOLD, 12)
            : new Font("Segoe UI", Font.PLAIN, 12);

        for (String text : new String[]{col1, col2, col3, col4, col5}) {
            JLabel lbl = new JLabel("  " + text);
            lbl.setFont(font);
            lbl.setForeground(isHeader ? PRIMARY : Color.DARK_GRAY);
            row.add(lbl);
        }
        return row;
    }

}