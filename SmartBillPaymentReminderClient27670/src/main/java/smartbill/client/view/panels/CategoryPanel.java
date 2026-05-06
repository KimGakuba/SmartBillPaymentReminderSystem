package smartbill.client.view.panels;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import smartbill.server.model.Category;
import smartbill.server.model.User;
import smartbill.server.service.CategoryService;

public class CategoryPanel extends JPanel {

    private User loggedInUser;
    private CategoryService categoryService;
    private List<Category> categories;

    private static final Color PRIMARY   = new Color(0x6F, 0x4E, 0x37);
    private static final Color SECONDARY = new Color(0xAC, 0x98, 0x84);
    private static final Color BG        = new Color(0xF5, 0xF0, 0xEB);
    private static final Color WHITE     = Color.WHITE;
    private static final Color DANGER    = new Color(220, 53, 69);

    private JTable table;
    private DefaultTableModel tableModel;
    private javax.swing.JTextField txtSearch;

    public CategoryPanel(User user) {
        this.loggedInUser = user;
        setLayout(new BorderLayout());
        setBackground(BG);
        initRMI();
        buildUI();
    }

    private void initRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 5000);
            categoryService = (CategoryService) registry.lookup("CategoryService");
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

        JLabel lblTitle = new JLabel("Category Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(PRIMARY);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setBackground(WHITE);

        txtSearch = new javax.swing.JTextField(14);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JButton btnSearch  = createBtn("Search",           SECONDARY);
        JButton btnAdd     = createBtn("+ Add Category",   PRIMARY);
        JButton btnEdit    = createBtn("Edit",             new Color(0x8B, 0x65, 0x45));
        JButton btnDelete  = createBtn("Delete",           DANGER);
        JButton btnRefresh = createBtn("Refresh",          SECONDARY);

        controls.add(txtSearch);
        controls.add(btnSearch);
        controls.add(btnAdd);
        controls.add(btnEdit);
        controls.add(btnDelete);
        controls.add(btnRefresh);

        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(controls, BorderLayout.EAST);

        // ── Table ──
        String[] cols = {"ID", "Category Name", "Description"};
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

        add(topBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Actions
        btnSearch.addActionListener(e -> filterCategories());
        btnAdd.addActionListener(e -> showCategoryDialog(null));
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
    }

    public void loadData() {
        try {
            categories = categoryService.getAllCategories();
            populateTable(categories);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading categories: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterCategories() {
        if (categories == null) return;
        String search = txtSearch.getText().trim().toLowerCase();
        List<Category> filtered = categories.stream()
            .filter(c -> search.isEmpty() ||
                         c.getName().toLowerCase().contains(search))
            .toList();
        populateTable(filtered);
    }

    private void populateTable(List<Category> data) {
        tableModel.setRowCount(0);
        for (Category c : data) {
            tableModel.addRow(new Object[]{
                c.getCategoryId(),
                c.getName(),
                c.getDescription() != null ? c.getDescription() : "-"
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a category to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        categories.stream()
            .filter(c -> c.getCategoryId() == id)
            .findFirst()
            .ifPresent(this::showCategoryDialog);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a category to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this category?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                categoryService.deleteCategory(id);
                JOptionPane.showMessageDialog(this,
                    "Category deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCategoryDialog(Category existing) {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            existing == null ? "Add Category" : "Edit Category", true);
        dialog.setSize(420, 310);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null);
        panel.setBackground(BG);

        // Header
        JPanel dHeader = new JPanel(null);
        dHeader.setBackground(PRIMARY);
        dHeader.setBounds(0, 0, 420, 55);
        panel.add(dHeader);

        JLabel dlblTitle = new JLabel(
            existing == null ? "Add New Category" : "Edit Category");
        dlblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dlblTitle.setForeground(WHITE);
        dlblTitle.setBounds(20, 15, 300, 25);
        dHeader.add(dlblTitle);

        // Name
        addDLabel(panel, "Category Name *", 20, 70);
        JTextField txtName = addDField(panel, 20, 93);

        // Description
        addDLabel(panel, "Description (Optional)", 20, 140);
        JTextField txtDescription = addDField(panel, 20, 163);

        // Populate if editing
        if (existing != null) {
            txtName.setText(existing.getName());
            txtDescription.setText(
                existing.getDescription() != null ? existing.getDescription() : "");
        }

        // Buttons
        JButton btnSave = createBtn(
            existing == null ? "Save Category" : "Update Category", PRIMARY);
        btnSave.setBounds(20, 230, 180, 38);
        panel.add(btnSave);

        JButton btnCancel = createBtn("Cancel", SECONDARY);
        btnCancel.setBounds(220, 230, 100, 38);
        btnCancel.addActionListener(e -> dialog.dispose());
        panel.add(btnCancel);

        btnSave.addActionListener(e -> {
            String name        = txtName.getText().trim();
            String description = txtDescription.getText().trim();

            // Technical validation
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Category name is required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Business validation — name length
            if (name.length() < 2) {
                JOptionPane.showMessageDialog(dialog,
                    "Category name must be at least 2 characters.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (existing == null) {
                    Category category = new Category(name, description);
                    categoryService.addCategory(category);
                    JOptionPane.showMessageDialog(dialog,
                        "Category added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    existing.setName(name);
                    existing.setDescription(description);
                    categoryService.updateCategory(existing);
                    JOptionPane.showMessageDialog(dialog,
                        "Category updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addDLabel(JPanel p, String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(PRIMARY);
        lbl.setBounds(x, y, 280, 20);
        p.add(lbl);
    }

    private JTextField addDField(JPanel p, int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 380, 32);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        p.add(f);
        return f;
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