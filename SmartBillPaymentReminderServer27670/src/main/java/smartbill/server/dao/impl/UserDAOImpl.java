package smartbill.server.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import smartbill.server.dao.UserDAO;
import smartbill.server.model.User;
import smartbill.server.util.HibernateUtil;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void save(User user) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.persist(user);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public User findById(int userId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.get(User.class, userId);
        } catch (Exception e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User findByUsername(String username) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery("SELECT u FROM User u", User.class).list();
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(User user) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.merge(user);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(int userId) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            User user = ss.get(User.class, userId);
            if (user != null) {
                ss.remove(user);
            }
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

}