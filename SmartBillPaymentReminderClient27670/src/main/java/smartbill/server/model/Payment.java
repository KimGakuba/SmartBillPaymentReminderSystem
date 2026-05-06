package smartbill.server.model;

import java.io.Serializable;

public class Payment implements Serializable {

    private int paymentId;
    private double amountPaid;
    private String datePaid;
    private String paymentMethod;
    private String notes;
    private Bill bill;

    // Constructors
    public Payment() {}

    public Payment(double amountPaid, String datePaid, String paymentMethod, String notes, Bill bill) {
        this.amountPaid = amountPaid;
        this.datePaid = datePaid;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.bill = bill;
    }

    // Getters and Setters
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