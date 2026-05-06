package smartbill.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import smartbill.server.service.impl.BillServiceImpl;
import smartbill.server.service.impl.CategoryServiceImpl;
import smartbill.server.service.impl.PaymentServiceImpl;
import smartbill.server.service.impl.ReminderServiceImpl;
import smartbill.server.service.impl.UserServiceImpl;
import smartbill.server.service.impl.OTPServiceImpl;

public class ServerMain {

    public static void main(String[] args) {
        try {
            // Port within required range 3000 - 6000
            int port = 5000;

            // Create RMI registry on port 5000
            Registry registry = LocateRegistry.createRegistry(port);

            // Instantiate all service implementations
            UserServiceImpl userService         = new UserServiceImpl();
            BillServiceImpl billService         = new BillServiceImpl();
            PaymentServiceImpl paymentService   = new PaymentServiceImpl();
            ReminderServiceImpl reminderService = new ReminderServiceImpl();
            CategoryServiceImpl categoryService = new CategoryServiceImpl();
            OTPServiceImpl otpService = new OTPServiceImpl();
registry.rebind("OTPService", otpService);
System.out.println("  OTP Service registered.");

            // Register all services in the RMI registry
            registry.rebind("UserService",     userService);
            registry.rebind("BillService",     billService);
            registry.rebind("PaymentService",  paymentService);
            registry.rebind("ReminderService", reminderService);
            registry.rebind("CategoryService", categoryService);

            System.out.println("=========================================");
            System.out.println("  Smart Bill Payment Reminder Server");
            System.out.println("  Running on port: " + port);
            System.out.println("  All services registered successfully.");
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

}