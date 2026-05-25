package smartbill.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import smartbill.server.model.Category;
import smartbill.server.model.User;
import smartbill.server.service.CategoryService;
import smartbill.server.service.UserService;
import smartbill.server.service.impl.BillServiceImpl;
import smartbill.server.service.impl.CategoryServiceImpl;
import smartbill.server.service.impl.OTPServiceImpl;
import smartbill.server.service.impl.PaymentServiceImpl;
import smartbill.server.service.impl.ReminderServiceImpl;
import smartbill.server.service.impl.UserServiceImpl;

public class ServerMain {

    public static void main(String[] args) {
        try {
            // ── Port within required range 3000 - 6000 ──
            int port = 5000;

            // ── Create RMI Registry ──
            Registry registry = LocateRegistry.createRegistry(port);

            // ── Instantiate all service implementations ──
            UserServiceImpl     userService     = new UserServiceImpl();
            BillServiceImpl     billService     = new BillServiceImpl();
            PaymentServiceImpl  paymentService  = new PaymentServiceImpl();
            ReminderServiceImpl reminderService = new ReminderServiceImpl();
            CategoryServiceImpl categoryService = new CategoryServiceImpl();
            OTPServiceImpl      otpService      = new OTPServiceImpl();

            // ── Register all services in RMI registry ──
            registry.rebind("UserService",     userService);
            registry.rebind("BillService",     billService);
            registry.rebind("PaymentService",  paymentService);
            registry.rebind("ReminderService", reminderService);
            registry.rebind("CategoryService", categoryService);
            registry.rebind("OTPService",      otpService);

            System.out.println("=========================================");
            System.out.println("  Smart Bill Payment Reminder Server");
            System.out.println("  Running on port : " + port);
            System.out.println("  All services registered successfully.");
            System.out.println("=========================================");

            // ── Seed default admin ──
            seedAdminUser(userService);

            // ── Seed predefined categories ──
            seedPredefinedCategories(categoryService);

            System.out.println("=========================================");
            System.out.println("  Server is ready and waiting...");
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Seed Default Admin ────────────────────────────────────────────────────
    private static void seedAdminUser(UserService userService) {
        try {
            if (userService.getUserByUsername("admin") == null) {
                User admin = new User(
                    "admin",
                    "kimgakuba@gmail.com",
                    "admin123",
                    "0780000000",
                    LocalDateTime.now().toString(),
                    "ADMIN"
                );
                // Save directly via DAO to bypass email validation
                // since admin is seeded internally
                smartbill.server.dao.impl.UserDAOImpl userDAO =
                    new smartbill.server.dao.impl.UserDAOImpl();
                userDAO.save(admin);

                System.out.println("  Default admin seeded.");
                System.out.println("  Username : admin");
                System.out.println("  Password : admin123");
                System.out.println("  Email    : kimgakuba@gmail.com");
            } else {
                System.out.println("  Admin already exists — skipping.");
            }
        } catch (Exception e) {
            System.err.println(
                "Error seeding admin: " + e.getMessage());
        }
    }

    // ── Seed Predefined Categories ────────────────────────────────────────────
    private static void seedPredefinedCategories(
            CategoryService categoryService) {
        String[][] predefined = {
            {"Utilities",     "Water, electricity, gas bills"},
            {"Rent",          "Monthly rent payments"},
            {"Loan",          "Bank or personal loan repayments"},
            {"Subscription",  "Streaming, software subscriptions"},
            {"Insurance",     "Health, car, life insurance"},
            {"Internet",      "Monthly internet bills"},
            {"Phone",         "Mobile phone bills"},
            {"Water",         "Water supply bills"},
            {"Electricity",   "Electricity supply bills"},
            {"Transport",     "Fuel, transport costs"},
            {"Education",     "School fees, tuition"},
            {"Medical",       "Hospital, pharmacy bills"},
            {"Taxes",         "Government tax payments"},
            {"Groceries",     "Food and household supplies"},
            {"Entertainment", "Events, outings, leisure"}
        };

        for (String[] entry : predefined) {
            try {
                if (categoryService.getCategoryByName(entry[0]) == null) {
                    Category c = new Category(entry[0], entry[1]);
                    // userId = null means predefined — shared by all
                    categoryService.addCategory(c);
                    System.out.println(
                        "  Category seeded: " + entry[0]);
                }
            } catch (Exception ignored) {}
        }
        System.out.println("  Predefined categories ready.");
    }

}