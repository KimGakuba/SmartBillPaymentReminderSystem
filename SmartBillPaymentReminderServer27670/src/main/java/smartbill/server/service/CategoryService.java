package smartbill.server.service;

import smartbill.server.model.Category;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CategoryService extends Remote {

    void addCategory(Category category) throws RemoteException;
    Category getCategoryById(int categoryId) throws RemoteException;
    Category getCategoryByName(String name) throws RemoteException;
    List<Category> getAllCategories() throws RemoteException;
    void updateCategory(Category category) throws RemoteException;
    void deleteCategory(int categoryId) throws RemoteException;

}