package smartbill.server.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "payments")
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;

    @Column(name = "date_paid", nullable = false)
    private String datePaid;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "notes", length = 255)
    private String notes;

    // Many Payments belong to One Bill
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    public Payment() {}

    public Payment(double amountPaid, String datePaid, String paymentMethod, String notes, Bill bill) {
        this.amountPaid = amountPaid;
        this.datePaid = datePaid;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.bill = bill;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public String getDatePaid() { return datePaid; }
    public void setDatePaid(String datePaid) { this.datePaid = datePaid; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
}