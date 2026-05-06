package smartbill.server.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reminders")
public class Reminder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id")
    private int reminderId;

    @Column(name = "trigger_date", nullable = false)
    private String triggerDate;

    @Column(name = "days_before", nullable = false)
    private int daysBefore;

    @Column(name = "is_dismissed")
    private boolean isDismissed;

    // One Reminder belongs to One Bill
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false, unique = true)
    private Bill bill;

    public Reminder() {}

    public Reminder(String triggerDate, int daysBefore, boolean isDismissed, Bill bill) {
        this.triggerDate = triggerDate;
        this.daysBefore = daysBefore;
        this.isDismissed = isDismissed;
        this.bill = bill;
    }

    public int getReminderId() { return reminderId; }
    public void setReminderId(int reminderId) { this.reminderId = reminderId; }
    public String getTriggerDate() { return triggerDate; }
    public void setTriggerDate(String triggerDate) { this.triggerDate = triggerDate; }
    public int getDaysBefore() { return daysBefore; }
    public void setDaysBefore(int daysBefore) { this.daysBefore = daysBefore; }
    public boolean isDismissed() { return isDismissed; }
    public void setDismissed(boolean isDismissed) { this.isDismissed = isDismissed; }
    public Bill getBill() { return bill; }
    public void setBill(Bill bill) { this.bill = bill; }
}