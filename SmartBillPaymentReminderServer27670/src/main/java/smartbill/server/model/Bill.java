package smartbill.server.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "bills")
public class Bill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private int billId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "due_date", nullable = false)
    private String dueDate;

    @Column(name = "recurrence", length = 20)
    private String recurrence;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private String createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Payment> payments;

    @OneToOne(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Reminder reminder;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "bill_categories",
        joinColumns = @JoinColumn(name = "bill_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

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