package smartbill.server.service;

import smartbill.server.model.Reminder;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ReminderService extends Remote {

    void createReminder(Reminder reminder) throws RemoteException;
    Reminder getReminderById(int reminderId) throws RemoteException;
    Reminder getReminderByBill(int billId) throws RemoteException;
    List<Reminder> getPendingReminders(int userId) throws RemoteException;
    List<Reminder> getAllReminders() throws RemoteException;
    void updateReminder(Reminder reminder) throws RemoteException;
    void dismissReminder(int reminderId) throws RemoteException;
    void deleteReminder(int reminderId) throws RemoteException;

}