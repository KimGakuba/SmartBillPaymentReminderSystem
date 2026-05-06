package smartbill.server.model;

import java.io.Serializable;

public class Reminder implements Serializable {

    private int reminderId;
    private String triggerDate;
    private int daysBefore;
    private boolean isDismissed;
    private Bill bill;

    // Constructors
    public Reminder() {}

    public Reminder(String triggerDate, int daysBefore, boolean isDismissed, Bill bill) {
        this.triggerDate = triggerDate;
        this.daysBefore = daysBefore;
        this.isDismissed = isDismissed;
        this.bill = bill;
    }

    // Getters and Setters
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