package smartbill.server.service;

import smartbill.server.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {

    void registerUser(User user) throws RemoteException;
    User loginUser(String username, String password) throws RemoteException;
    User getUserById(int userId) throws RemoteException;
    User getUserByUsername(String username) throws RemoteException;
    User getUserByEmail(String email) throws RemoteException;
    List<User> getAllUsers() throws RemoteException;
    void updateUser(User user) throws RemoteException;
    void deleteUser(int userId) throws RemoteException;

}