package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import smartbill.server.dao.CategoryDAO;
import smartbill.server.dao.impl.CategoryDAOImpl;
import smartbill.server.model.Category;
import smartbill.server.service.CategoryService;

public class CategoryServiceImpl extends UnicastRemoteObject implements CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryServiceImpl() throws RemoteException {
        super();
        this.categoryDAO = new CategoryDAOImpl();
    }

    @Override
    public void addCategory(Category category) throws RemoteException {
        // Technical validation — name must not be empty
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RemoteException("Category name cannot be empty.");
        }
        // Business validation — category name must not already exist
        if (categoryDAO.findByName(category.getName()) != null) {
            throw new RemoteException("Category already exists.");
        }
        categoryDAO.save(category);
    }

    @Override
    public Category getCategoryById(int categoryId) throws RemoteException {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new RemoteException("Category with ID " + categoryId + " not found.");
        }
        return category;
    }

    @Override
    public Category getCategoryByName(String name) throws RemoteException {
        // Technical validation — name must not be empty
        if (name == null || name.trim().isEmpty()) {
            throw new RemoteException("Category name cannot be empty.");
        }
        return categoryDAO.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() throws RemoteException {
        return categoryDAO.findAll();
    }

    @Override
    public void updateCategory(Category category) throws RemoteException {
        if (categoryDAO.findById(category.getCategoryId()) == null) {
            throw new RemoteException("Category not found. Cannot update.");
        }
        // Technical validation — name must not be empty
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RemoteException("Category name cannot be empty.");
        }
        categoryDAO.update(category);
    }

    @Override
    public void deleteCategory(int categoryId) throws RemoteException {
        if (categoryDAO.findById(categoryId) == null) {
            throw new RemoteException("Category not found. Cannot delete.");
        }
        categoryDAO.delete(categoryId);
    }

}