package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import smartbill.server.dao.ReminderDAO;
import smartbill.server.dao.impl.ReminderDAOImpl;
import smartbill.server.model.Reminder;
import smartbill.server.service.ReminderService;

public class ReminderServiceImpl extends UnicastRemoteObject implements ReminderService {

    private final ReminderDAO reminderDAO;

    public ReminderServiceImpl() throws RemoteException {
        super();
        this.reminderDAO = new ReminderDAOImpl();
    }

    @Override
    public void createReminder(Reminder reminder) throws RemoteException {
        // Technical validation — bill must be assigned
        if (reminder.getBill() == null) {
            throw new RemoteException("Reminder must be linked to a bill.");
        }
        // Business validation — days before must be greater than zero
        if (reminder.getDaysBefore() <= 0) {
            throw new RemoteException("Days before due date must be greater than zero.");
        }
        // Business validation — trigger date must not be empty
        if (reminder.getTriggerDate() == null || reminder.getTriggerDate().trim().isEmpty()) {
            throw new RemoteException("Trigger date cannot be empty.");
        }
        // Business validation — reminder must not already exist for this bill
        if (reminderDAO.findByBill(reminder.getBill().getBillId()) != null) {
            throw new RemoteException("A reminder already exists for this bill.");
        }
        reminderDAO.save(reminder);
    }

    @Override
    public Reminder getReminderById(int reminderId) throws RemoteException {
        Reminder reminder = reminderDAO.findById(reminderId);
        if (reminder == null) {
            throw new RemoteException("Reminder with ID " + reminderId + " not found.");
        }
        return reminder;
    }

    @Override
    public Reminder getReminderByBill(int billId) throws RemoteException {
        // Technical validation — billId must be valid
        if (billId <= 0) {
            throw new RemoteException("Invalid bill ID.");
        }
        return reminderDAO.findByBill(billId);
    }

    @Override
    public List<Reminder> getPendingReminders(int userId) throws RemoteException {
        // Technical validation — userId must be valid
        if (userId <= 0) {
            throw new RemoteException("Invalid user ID.");
        }
        return reminderDAO.findPendingReminders(userId);
    }

    @Override
    public List<Reminder> getAllReminders() throws RemoteException {
        return reminderDAO.findAll();
    }

    @Override
    public void updateReminder(Reminder reminder) throws RemoteException {
        if (reminderDAO.findById(reminder.getReminderId()) == null) {
            throw new RemoteException("Reminder not found. Cannot update.");
        }
        // Business validation — days before must be greater than zero
        if (reminder.getDaysBefore() <= 0) {
            throw new RemoteException("Days before due date must be greater than zero.");
        }
        reminderDAO.update(reminder);
    }

    @Override
    public void dismissReminder(int reminderId) throws RemoteException {
        Reminder reminder = reminderDAO.findById(reminderId);
        if (reminder == null) {
            throw new RemoteException("Reminder not found. Cannot dismiss.");
        }
        reminder.setDismissed(true);
        reminderDAO.update(reminder);
    }

    @Override
    public void deleteReminder(int reminderId) throws RemoteException {
        if (reminderDAO.findById(reminderId) == null) {
            throw new RemoteException("Reminder not found. Cannot delete.");
        }
        reminderDAO.delete(reminderId);
    }

}