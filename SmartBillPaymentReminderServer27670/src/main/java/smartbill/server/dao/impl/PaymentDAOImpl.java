package smartbill.server.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import smartbill.server.dao.PaymentDAO;
import smartbill.server.model.Payment;
import smartbill.server.util.HibernateUtil;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void save(Payment payment) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.persist(payment);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error saving payment: " + e.getMessage());
        }
    }

    @Override
    public Payment findById(int paymentId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.get(Payment.class, paymentId);
        } catch (Exception e) {
            System.err.println("Error finding payment by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Payment> findByBill(int billId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT p FROM Payment p WHERE p.bill.billId = :billId", Payment.class)
                .setParameter("billId", billId)
                .list();
        } catch (Exception e) {
            System.err.println("Error finding payments by bill: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Payment> findAll() {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT p FROM Payment p", Payment.class)
                .list();
        } catch (Exception e) {
            System.err.println("Error fetching all payments: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(Payment payment) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.merge(payment);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error updating payment: " + e.getMessage());
        }
    }

    @Override
    public void delete(int paymentId) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            Payment payment = ss.get(Payment.class, paymentId);
            if (payment != null) {
                ss.remove(payment);
            }
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error deleting payment: " + e.getMessage());
        }
    }

}