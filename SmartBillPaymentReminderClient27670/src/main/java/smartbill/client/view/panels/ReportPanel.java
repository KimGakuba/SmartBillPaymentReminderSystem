package smartbill.client.view.panels;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        cmbReportType = new javax.swing.JComboBox<>(
            new String[]{"Bills Report", "Payments Report", "Overdue Bills Report"});
        cmbReportType.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        JButton btnGenerate    = createBtn("Generate",      PRIMARY);
        JButton btnExportCSV   = createBtn("Export CSV",    SUCCESS);
        JButton btnExportPDF   = createBtn("Export PDF",    new Color(180, 60, 60));
        JButton btnExportExcel = createBtn("Export Excel",  new Color(0x1F, 0x7A, 0x1F));
        JButton btnPrint       = createBtn("Print",         new Color(0x8B, 0x65, 0x45));
        JButton btnRefresh     = createBtn("Refresh",       SECONDARY);

        controls.add(new JLabel("Report Type:"));
        controls.add(cmbReportType);
        controls.add(btnGenerate);
        controls.add(btnExportCSV);
        controls.add(btnExportPDF);
        controls.add(btnExportExcel);
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

        summaryBar.add(summaryCard("Total Bill Amount (RWF)", lblTotalAmount,
            PRIMARY,  20, 10));
        summaryBar.add(summaryCard("Total Paid (RWF)",        lblTotalPaid,
            SUCCESS, 310, 10));
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
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.setSelectionBackground(SECONDARY);
        table.setSelectionForeground(WHITE);
        table.setGridColor(new Color(230, 220, 210));
        table.getTableHeader().setFont(
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel center = new JPanel(new BorderLayout());
        center.add(summaryBar, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // ── Actions ──
        btnGenerate.addActionListener(e -> generateReport());
        btnExportCSV.addActionListener(e -> exportCSV());
        btnExportPDF.addActionListener(e -> exportPDF());
        btnExportExcel.addActionListener(e -> exportExcel());
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

    // ── CSV Export ───────────────────────────────────────────────────────────
    private void exportCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data to export. Please generate a report first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("SmartBill_Report.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(
                    new FileWriter(fc.getSelectedFile()))) {
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    header.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) header.append(",");
                }
                pw.println(header);
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    StringBuilder line = new StringBuilder();
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object val = tableModel.getValueAt(row, col);
                        line.append(val != null ? val.toString() : "");
                        if (col < tableModel.getColumnCount() - 1)
                            line.append(",");
                    }
                    pw.println(line);
                }
                JOptionPane.showMessageDialog(this,
                    "CSV exported successfully to:\n" +
                    fc.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting CSV: " + e.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── PDF Export ───────────────────────────────────────────────────────────
    private void exportPDF() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data to export. Please generate a report first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("SmartBill_Report.pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document,
                    new FileOutputStream(fc.getSelectedFile()));
                document.open();

                // Title
                com.itextpdf.text.Font titleFont =
                    new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        16, com.itextpdf.text.Font.BOLD,
                        new BaseColor(0x6F, 0x4E, 0x37));
                Paragraph title = new Paragraph(
                    "Smart Bill Payment Reminder System\n" +
                    cmbReportType.getSelectedItem(), titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                // Info
                com.itextpdf.text.Font infoFont =
                    new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        10, com.itextpdf.text.Font.NORMAL,
                        BaseColor.GRAY);
                document.add(new Paragraph(
                    "Generated by : " + loggedInUser.getUsername(),
                    infoFont));
                document.add(new Paragraph(
                    "Generated on : " +
                    java.time.LocalDateTime.now().toString(), infoFont));
                document.add(new Paragraph(" "));

                // Summary
                com.itextpdf.text.Font summaryFont =
                    new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        11, com.itextpdf.text.Font.BOLD,
                        new BaseColor(0x6F, 0x4E, 0x37));
                document.add(new Paragraph(
                    "Total Bill Amount : RWF " + lblTotalAmount.getText() +
                    "    |    Total Paid : RWF " + lblTotalPaid.getText() +
                    "    |    Overdue Bills : " + lblTotalOverdue.getText(),
                    summaryFont));
                document.add(new Paragraph(" "));

                // PDF Table
                int colCount = tableModel.getColumnCount();
                PdfPTable pdfTable = new PdfPTable(colCount);
                pdfTable.setWidthPercentage(100);

                // Header row
                com.itextpdf.text.Font headerFont =
                    new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA,
                        11, com.itextpdf.text.Font.BOLD,
                        BaseColor.WHITE);
                for (int i = 0; i < colCount; i++) {
                    PdfPCell cell = new PdfPCell(
                        new Phrase(tableModel.getColumnName(i), headerFont));
                    cell.setBackgroundColor(new BaseColor(0x6F, 0x4E, 0x37));
                    cell.setPadding(8);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                // Data rows
                com.itextpdf.text.Font dataFont =
                    new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < colCount; col++) {
                        Object val = tableModel.getValueAt(row, col);
                        PdfPCell cell = new PdfPCell(
                            new Phrase(val != null ? val.toString() : "-",
                            dataFont));
                        cell.setPadding(6);
                        cell.setBackgroundColor(row % 2 == 0
                            ? BaseColor.WHITE
                            : new BaseColor(0xF5, 0xF0, 0xEB));
                        pdfTable.addCell(cell);
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this,
                    "PDF exported successfully to:\n" +
                    fc.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting PDF: " + e.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Excel Export ─────────────────────────────────────────────────────────
    private void exportExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No data to export. Please generate a report first.",
                "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("SmartBill_Report.xlsx"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {

                Sheet sheet = workbook.createSheet(
                    (String) cmbReportType.getSelectedItem());

                // Title row
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(
                    "Smart Bill Payment Reminder System — " +
                    cmbReportType.getSelectedItem());
                CellStyle titleStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont =
                    workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 14);
                titleFont.setColor(IndexedColors.DARK_RED.getIndex());
                titleStyle.setFont(titleFont);
                titleCell.setCellStyle(titleStyle);

                // Info rows
                sheet.createRow(1).createCell(0).setCellValue(
                    "Generated by: " + loggedInUser.getUsername());
                sheet.createRow(2).createCell(0).setCellValue(
                    "Generated on: " +
                    java.time.LocalDateTime.now().toString());

                // Summary row
                sheet.createRow(3).createCell(0).setCellValue(
                    "Total Bill Amount: RWF " + lblTotalAmount.getText() +
                    "  |  Total Paid: RWF " + lblTotalPaid.getText() +
                    "  |  Overdue: " + lblTotalOverdue.getText());

                // Empty row
                sheet.createRow(4);

                // Header style
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(
                    IndexedColors.BROWN.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                org.apache.poi.ss.usermodel.Font headerFont =
                    workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);

                // Header row
                Row headerRow = sheet.createRow(5);
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(tableModel.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                // Row styles
                CellStyle evenStyle = workbook.createCellStyle();
                evenStyle.setFillForegroundColor(
                    IndexedColors.WHITE.getIndex());
                evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                CellStyle oddStyle = workbook.createCellStyle();
                oddStyle.setFillForegroundColor(
                    IndexedColors.LEMON_CHIFFON.getIndex());
                oddStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // Data rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 6);
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object val = tableModel.getValueAt(row, col);
                        cell.setCellValue(val != null ? val.toString() : "-");
                        cell.setCellStyle(row % 2 == 0 ? evenStyle : oddStyle);
                    }
                }

                // Auto-size columns
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write file
                try (FileOutputStream fos =
                        new FileOutputStream(fc.getSelectedFile())) {
                    workbook.write(fos);
                }

                JOptionPane.showMessageDialog(this,
                    "Excel exported successfully to:\n" +
                    fc.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting Excel: " + e.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Print ────────────────────────────────────────────────────────────────
    private void printReport() {
        try {
            boolean complete = table.print(
                JTable.PrintMode.NORMAL,
                new java.text.MessageFormat(
                    "SmartBill Report — " + loggedInUser.getUsername()),
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

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JPanel summaryCard(String title, JLabel valueLabel,
                                Color color, int x, int y) {
        JPanel card = new JPanel(null);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, color));
        card.setBounds(x, y, 260, 68);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lblTitle.setForeground(SECONDARY);
        lblTitle.setBounds(12, 10, 236, 16);
        card.add(lblTitle);

        valueLabel.setBounds(12, 30, 236, 28);
        card.add(valueLabel);

        return card;
    }

    private JLabel summaryValue(String val) {
        JLabel lbl = new JLabel(val);
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}