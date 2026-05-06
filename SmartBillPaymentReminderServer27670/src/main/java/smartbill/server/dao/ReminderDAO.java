package smartbill.server.dao;

import smartbill.server.model.Reminder;
import java.util.List;

public interface ReminderDAO {

    void save(Reminder reminder);
    Reminder findById(int reminderId);
    Reminder findByBill(int billId);
    List<Reminder> findPendingReminders(int userId);
    List<Reminder> findAll();
    void update(Reminder reminder);
    void delete(int reminderId);

}