package smartbill.server.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import smartbill.server.dao.CategoryDAO;
import smartbill.server.model.Category;
import smartbill.server.util.HibernateUtil;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    @Override
    public void save(Category category) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.persist(category);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error saving category: " + e.getMessage());
        }
    }

    @Override
    public Category findById(int categoryId) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.get(Category.class, categoryId);
        } catch (Exception e) {
            System.err.println("Error finding category by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Category findByName(String name) {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT c FROM Category c WHERE c.name = :name", Category.class)
                .setParameter("name", name)
                .uniqueResult();
        } catch (Exception e) {
            System.err.println("Error finding category by name: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Category> findAll() {
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            return ss.createQuery(
                "SELECT c FROM Category c", Category.class)
                .list();
        } catch (Exception e) {
            System.err.println("Error fetching all categories: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(Category category) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            ss.merge(category);
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error updating category: " + e.getMessage());
        }
    }

    @Override
    public void delete(int categoryId) {
        Transaction tr = null;
        try (Session ss = HibernateUtil.getSessionFactory().openSession()) {
            tr = ss.beginTransaction();
            Category category = ss.get(Category.class, categoryId);
            if (category != null) {
                ss.remove(category);
            }
            tr.commit();
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.err.println("Error deleting category: " + e.getMessage());
        }
    }

}