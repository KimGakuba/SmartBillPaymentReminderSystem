package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import smartbill.server.dao.UserDAO;
import smartbill.server.dao.impl.UserDAOImpl;
import smartbill.server.model.User;
import smartbill.server.service.UserService;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl() throws RemoteException {
        super();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public void registerUser(User user) throws RemoteException {
        // Business validation — username must not already exist
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new RemoteException("Username already exists.");
        }
        // Business validation — email must not already exist
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new RemoteException("Email already registered.");
        }
        // Business validation — password must be at least 6 characters
        if (user.getPassword().length() < 6) {
            throw new RemoteException("Password must be at least 6 characters.");
        }
        userDAO.save(user);
    }

    @Override
    public User loginUser(String username, String password) throws RemoteException {
        // Technical validation — fields must not be empty
        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RemoteException("Password cannot be empty.");
        }
        User user = userDAO.findByUsername(username);
        // Business validation — user must exist
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        // Business validation — password must match
        if (!user.getPassword().equals(password)) {
            throw new RemoteException("Incorrect password.");
        }
        return user;
    }

    @Override
    public User getUserById(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User with ID " + userId + " not found.");
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) throws RemoteException {
        return userDAO.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) throws RemoteException {
        return userDAO.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() throws RemoteException {
        return userDAO.findAll();
    }

    @Override
    public void updateUser(User user) throws RemoteException {
        if (userDAO.findById(user.getUserId()) == null) {
            throw new RemoteException("User not found. Cannot update.");
        }
        userDAO.update(user);
    }

    @Override
    public void deleteUser(int userId) throws RemoteException {
        if (userDAO.findById(userId) == null) {
            throw new RemoteException("User not found. Cannot delete.");
        }
        userDAO.delete(userId);
    }

}