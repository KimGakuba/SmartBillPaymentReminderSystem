package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import smartbill.server.model.Bill;
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
    private static final Color DIVIDER   = new Color(230, 220, 210);

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
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        JPanel wrapper = new JPanel(new BorderLayout(20, 20));
        wrapper.setBackground(BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(BG);

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));

        JLabel lblPage = new JLabel("Dashboard");
        lblPage.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblPage.setForeground(PRIMARY);

        JLabel lblSub = new JLabel("Overview of your billing activity");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSub.setForeground(SECONDARY);

        titleText.add(lblPage);
        titleText.add(Box.createVerticalStrut(5));
        titleText.add(lblSub);

        titleBar.add(titleText, BorderLayout.WEST);
        wrapper.add(titleBar, BorderLayout.NORTH);

        lblTotalBills = statValue("0");
        lblPaidBills = statValue("0");
        lblOverdueBills = statValue("0");
        lblPendingReminders = statValue("0");

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 18, 18));
        statsPanel.setBackground(BG);

        statsPanel.add(statCard("Total Bills", lblTotalBills, PRIMARY));
        statsPanel.add(statCard("Paid Bills", lblPaidBills, SUCCESS));
        statsPanel.add(statCard("Overdue Bills", lblOverdueBills, DANGER));
        statsPanel.add(statCard("Pending Reminders", lblPendingReminders, WARNING));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 18));
        centerPanel.setBackground(BG);
        centerPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel recentCard = new JPanel(new BorderLayout());
        recentCard.setBackground(WHITE);
        recentCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblRecent = new JLabel("Recent Bills");
        lblRecent.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblRecent.setForeground(PRIMARY);
        recentCard.add(lblRecent, BorderLayout.NORTH);

        recentPanel = new JPanel();
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));
        recentPanel.setBackground(WHITE);

        JScrollPane scroll = new JScrollPane(recentPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);

        recentCard.add(scroll, BorderLayout.CENTER);
        centerPanel.add(recentCard, BorderLayout.CENTER);

        wrapper.add(centerPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    public void loadData() {
        try {
            List<Bill> bills = billService.getBillsByUser(loggedInUser.getUserId());

            int total = bills.size();
            int paid = (int) bills.stream()
                .filter(b -> "Paid".equalsIgnoreCase(b.getStatus())).count();
            int overdue = (int) bills.stream()
                .filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count();
            int pending = reminderService
                .getPendingReminders(loggedInUser.getUserId()).size();

            lblTotalBills.setText(String.valueOf(total));
            lblPaidBills.setText(String.valueOf(paid));
            lblOverdueBills.setText(String.valueOf(overdue));
            lblPendingReminders.setText(String.valueOf(pending));

            recentPanel.removeAll();

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

            if (overdue > 0) {
                JOptionPane.showMessageDialog(this,
                    "You have " + overdue + " overdue bill(s)! Please review them.",
                    "Overdue Alert",
                    JOptionPane.WARNING_MESSAGE);
            }

            recentPanel.revalidate();
            recentPanel.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel statCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, color),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTitle.setForeground(SECONDARY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JLabel statValue(String val) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JPanel tableRow(Color bg, boolean isHeader,
                            String col1, String col2, String col3,
                            String col4, String col5) {
        JPanel row = new JPanel(new GridLayout(1, 5));
        row.setBackground(bg);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setPreferredSize(new Dimension(1000, 44));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER));

        Font font = isHeader
            ? new Font("Segoe UI", Font.BOLD, 15)
            : new Font("Segoe UI", Font.PLAIN, 15);

        for (String text : new String[]{col1, col2, col3, col4, col5}) {
            JLabel lbl = new JLabel("  " + text);
            lbl.setFont(font);
            lbl.setForeground(isHeader ? PRIMARY : Color.DARK_GRAY);
            row.add(lbl);
        }

        return row;
    }
}