package smartbill.server.service;

import smartbill.server.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {

    // Auth
    void registerUser(User user) throws RemoteException;
    User loginUser(String username, String password) throws RemoteException;

    // Fetch
    User getUserById(int userId) throws RemoteException;
    User getUserByUsername(String username) throws RemoteException;
    User getUserByEmail(String email) throws RemoteException;
    List<User> getAllUsers() throws RemoteException;

    // Update
    void updateUser(User user) throws RemoteException;

    // Admin — User Management
    void activateUser(int userId) throws RemoteException;
    void deactivateUser(int userId) throws RemoteException;
    void resetUserAccount(int userId) throws RemoteException;
    void promoteToAdmin(int userId) throws RemoteException;
    void demoteToUser(int userId) throws RemoteException;
    void deleteUser(int userId) throws RemoteException;

}