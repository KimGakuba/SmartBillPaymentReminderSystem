package smartbill.client.view.panels;

import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Bill;
import smartbill.server.model.Payment;
import smartbill.server.model.User;
import smartbill.server.service.BillService;
import smartbill.server.service.PaymentService;

public class ReportPanel extends JPanel {

    private User loggedInUser;
    private BillService billService;
    private PaymentService paymentService;
    private List<Bill> bills;
    private List<Payment> payments;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color SUCCESS   = new Color(40, 167, 69);

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalAmount;
    private JLabel lblTotalPaid;
    private JLabel lblTotalOverdue;
    private javax.swing.JComboBox<String> cmbReportType;

    public ReportPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            billService    = (BillService) registry.lookup("BillService");
            paymentService = (PaymentService) registry.lookup("PaymentService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Could not connect to server.",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {
        // ── Top Bar ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel lblTitle = new JLabel("Reports & Export");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        cmbReportType = new javax.swing.JComboBox<>(
            new String[]{"Bills Report", "Payments Report", "Overdue Bills Report"});
        cmbReportType.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnGenerate   = createBtn("Generate",      PRIMARY);
        JButton btnExportCSV  = createBtn("Export CSV",    SUCCESS);
        JButton btnPrint      = createBtn("Print",         new Color(0x8B, 0x65, 0x45));
        JButton btnRefresh    = createBtn("Refresh",       SECONDARY);

        controls.add(new JLabel("Report Type:"));
        controls.add(cmbReportType);
        controls.add(btnGenerate);
        controls.add(btnExportCSV);
        controls.add(btnPrint);
        controls.add(btnRefresh);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // ── Summary Cards ──
        JPanel summaryBar = new JPanel(null);
        summaryBar.setBackground(BG);
        summaryBar.setPreferredSize(new Dimension(880, 90));

        lblTotalAmount  = summaryValue("0.00");
        lblTotalPaid    = summaryValue("0.00");
        lblTotalOverdue = summaryValue("0");

        summaryBar.add(summaryCard("Total Bill Amount (RWF)", lblTotalAmount,  PRIMARY,  20, 10));
        summaryBar.add(summaryCard("Total Paid (RWF)",        lblTotalPaid,    SUCCESS, 310, 10));
        summaryBar.add(summaryCard("Overdue Bills",           lblTotalOverdue,
            new Color(220, 53, 69), 600, 10));

        // ── Table ──
        String[] cols = {"ID", "Name", "Amount (RWF)", "Due Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(SECONDARY);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(230, 220, 210));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout());
        center.add(summaryBar, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // Actions
        btnGenerate.addActionListener(e -> generateReport());
        btnExportCSV.addActionListener(e -> exportCSV());
        btnPrint.addActionListener(e -> printReport());
        btnRefresh.addActionListener(e -> loadData());
        cmbReportType.addActionListener(e -> generateReport());
    }

    public void loadData() {
        try {
            bills    = billService.getBillsByUser(loggedInUser.getUserId());
            payments = paymentService.getAllPayments();
            generateReport();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading report data: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReport() {
        if (bills == null) return;
        String type = (String) cmbReportType.getSelectedItem();
        tableModel.setRowCount(0);

        // Update summary
        double totalAmount = bills.stream()
            .mapToDouble(Bill::getAmount).sum();
        double totalPaid   = bills.stream()
            .filter(b -> "Paid".equalsIgnoreCase(b.getStatus()))
            .mapToDouble(Bill::getAmount).sum();
        long overdueCount  = bills.stream()
            .filter(b -> "Overdue".equalsIgnoreCase(b.getStatus())).count();

        lblTotalAmount.setText(String.format("%.2f", totalAmount));
        lblTotalPaid.setText(String.format("%.2f", totalPaid));
        lblTotalOverdue.setText(String.valueOf(overdueCount));

        switch (type) {
            case "Bills Report" -> {
                tableModel.setColumnIdentifiers(
                    new String[]{"ID", "Bill Name", "Amount (RWF)",
                                 "Due Date", "Recurrence", "Status"});
                for (Bill b : bills) {
                    tableModel.addRow(new Object[]{
                        b.getBillId(), b.getName(),
                        String.format("%.2f", b.getAmount()),
                        b.getDueDate(), b.getRecurrence(), b.getStatus()
                    });
                }
            }
            case "Payments Report" -> {
                tableModel.setColumnIdentifiers(
                    new String[]{"ID", "Bill Name", "Amount Paid (RWF)",
                                 "Date Paid", "Method"});
                if (payments != null) {
                    for (Payment p : payments) {
                        tableModel.addRow(new Object[]{
                            p.getPaymentId(),
                            p.getBill() != null ? p.getBill().getName() : "-",
                            String.format("%.2f", p.getAmountPaid()),
                            p.getDatePaid(), p.getPaymentMethod()
                        });
                    }
                }
            }
            case "Overdue Bills Report" -> {
                tableModel.setColumnIdentifiers(
                    new String[]{"ID", "Bill Name", "Amount (RWF)",
                                 "Due Date", "Status"});
                bills.stream()
                    .filter(b -> "Overdue".equalsIgnoreCase(b.getStatus()))
                    .forEach(b -> tableModel.addRow(new Object[]{
                        b.getBillId(), b.getName(),
                        String.format("%.2f", b.getAmount()),
                        b.getDueDate(), b.getStatus()
                    }));
            }
        }
    }

    private void exportCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data to export. Please generate a report first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(
            new java.io.File("SmartBill_Report.csv"));
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(
                    new FileWriter(fileChooser.getSelectedFile()))) {

                // Write headers
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    header.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) header.append(",");
                }
                pw.println(header);

                // Write rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object val = tableModel.getValueAt(row, col);
                        line.append(val != null ? val.toString() : "");
                        if (col < tableModel.getColumnCount() - 1) line.append(",");
                    }
                    pw.println(line);
                }

                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" +
                    fileChooser.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + e.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printReport() {
        try {
            boolean complete = table.print(
                JTable.PrintMode.NORMAL,
                new java.text.MessageFormat("SmartBill Report — " + loggedInUser.getUsername()),
new java.text.MessageFormat("Page {0}"));
            if (complete) {
                JOptionPane.showMessageDialog(this,
                    "Report printed successfully.",
                    "Print Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error printing report: " + e.getMessage(),
                "Print Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel summaryCard(String title, JLabel valueLabel,
                                Color color, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, color));
        card.setBounds(x, y, 260, 68);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(SECONDARY);
        lblTitle.setBounds(12, 10, 236, 16);
        card.add(lblTitle);

        valueLabel.setBounds(12, 30, 236, 28);
        card.add(valueLabel);

        return card;
    }

    private JLabel summaryValue(String val) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}