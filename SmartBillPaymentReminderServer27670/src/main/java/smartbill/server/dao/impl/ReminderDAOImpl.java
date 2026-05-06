package smartbill.server.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import smartbill.server.dao.ReminderDAO;
import smartbill.server.model.Reminder;
import smartbill.server.util.HibernateUtil;
import java.util.List;

public class ReminderDAOImpl implements ReminderDAO {

    @Override
    public void save(Reminder reminder) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.persist(reminder);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error saving reminder: " + e.getMessage());
        }
    }

    @Override
    public Reminder findById(int reminderId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.get(Reminder.class, reminderId);
        } catch (Exception e) {
            System.err.println("Error finding reminder by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Reminder findByBill(int billId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT r FROM Reminder r WHERE r.bill.billId = :billId", Reminder.class)
                .setParameter("billId", billId)
                .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error finding reminder by bill: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Reminder> findPendingReminders(int userId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT r FROM Reminder r WHERE r.bill.user.userId = :userId " +
                "AND r.isDismissed = false", Reminder.class)
                .setParameter("userId", userId)
                .list();
        } catch (Exception e) {
            System.err.println("Error finding pending reminders: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Reminder> findAll() {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT r FROM Reminder r", Reminder.class)
                .list();
        } catch (Exception e) {
            System.err.println("Error fetching all reminders: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(Reminder reminder) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.merge(reminder);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error updating reminder: " + e.getMessage());
        }
    }

    @Override
    public void delete(int reminderId) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            Reminder reminder = ss.get(Reminder.class, reminderId);
            if (reminder != null) {
                ss.remove(reminder);
            }
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error deleting reminder: " + e.getMessage());
        }
    }

}