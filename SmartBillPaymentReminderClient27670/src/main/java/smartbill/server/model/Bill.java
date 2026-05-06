package smartbill.server.model;

import java.io.Serializable;
import java.util.List;

public class Bill implements Serializable {

    private int billId;
    private String name;
    private double amount;
    private String dueDate;
    private String recurrence;
    private String status;
    private String createdAt;
    private User user;
    private List<Payment> payments;
    private Reminder reminder;
    private List<Category> categories;

    // Constructors
    public Bill() {}

    public Bill(String name, double amount, String dueDate, String recurrence,
                String status, String createdAt, User user) {
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
        this.recurrence = recurrence;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public Reminder getReminder() { return reminder; }
    public void setReminder(Reminder reminder) { this.reminder = reminder; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

}