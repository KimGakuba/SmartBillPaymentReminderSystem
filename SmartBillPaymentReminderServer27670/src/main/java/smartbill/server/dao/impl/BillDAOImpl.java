package smartbill.server.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import smartbill.server.dao.BillDAO;
import smartbill.server.model.Bill;
import smartbill.server.util.HibernateUtil;
import java.util.List;

public class BillDAOImpl implements BillDAO {

    @Override
    public void save(Bill bill) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.persist(bill);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error saving bill: " + e.getMessage());
        }
    }

    @Override
    public Bill findById(int billId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.get(Bill.class, billId);
        } catch (Exception e) {
            System.err.println("Error finding bill by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Bill> findByUser(int userId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT b FROM Bill b WHERE b.user.userId = :userId", Bill.class)
                .setParameter("userId", userId)
                .list();
        } catch (Exception e) {
            System.err.println("Error finding bills by user: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Bill> findOverdueBills(int userId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT b FROM Bill b WHERE b.user.userId = :userId " +
                "AND b.status = 'Overdue'", Bill.class)
                .setParameter("userId", userId)
                .list();
        } catch (Exception e) {
            System.err.println("Error finding overdue bills: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Bill> findByStatus(String status) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT b FROM Bill b WHERE b.status = :status", Bill.class)
                .setParameter("status", status)
                .list();
        } catch (Exception e) {
            System.err.println("Error finding bills by status: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Bill> findAll() {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT b FROM Bill b", Bill.class)
                .list();
        } catch (Exception e) {
            System.err.println("Error fetching all bills: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(Bill bill) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.merge(bill);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error updating bill: " + e.getMessage());
        }
    }

    @Override
    public void delete(int billId) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            Bill bill = ss.get(Bill.class, billId);
            if (bill != null) {
                ss.remove(bill);
            }
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error deleting bill: " + e.getMessage());
        }
    }

}