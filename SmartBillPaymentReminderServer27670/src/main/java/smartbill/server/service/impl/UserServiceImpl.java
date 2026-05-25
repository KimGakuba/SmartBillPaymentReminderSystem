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

    // ── Validation Helpers ───────────────────────────────────────────────────

    private void validateEmail(String email) throws RemoteException {
        if (email == null || email.trim().isEmpty()) {
            throw new RemoteException("Email cannot be empty.");
        }
        if (!email.endsWith("@gmail.com") && !email.endsWith("@icloud.com")) {
            throw new RemoteException(
                "Email must end with @gmail.com or @icloud.com.");
        }
    }

    private void validatePhone(String phone) throws RemoteException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new RemoteException("Phone number cannot be empty.");
        }
        // Must be digits only after stripping leading +
        String stripped = phone.startsWith("+") ? phone.substring(1) : phone;
        if (!stripped.matches("\\d+")) {
            throw new RemoteException(
                "Phone number must contain digits only.");
        }
        // Validate known international prefixes
        if (!isValidPhonePrefix(phone)) {
            throw new RemoteException(
                "Phone number must start with a valid country prefix.\n" +
                "Examples: 078, 079, +1, +44, +33, +49, +27, +234, +91...");
        }
    }

    private boolean isValidPhonePrefix(String phone) {
        // Rwanda
        if (phone.matches("^(078|079|072|073)\\d{7}$")) return true;
        // Kenya
        if (phone.matches("^(070|071|072|074|079)\\d{7}$")) return true;
        // Uganda
        if (phone.matches("^(070|075|077)\\d{7}$")) return true;
        // Tanzania
        if (phone.matches("^(071|074|075|076)\\d{7}$")) return true;
        // International format — +countrycode
        if (phone.matches("^\\+1\\d{10}$"))   return true; // US/Canada
        if (phone.matches("^\\+44\\d{10}$"))  return true; // UK
        if (phone.matches("^\\+33\\d{9}$"))   return true; // France
        if (phone.matches("^\\+49\\d{10}$"))  return true; // Germany
        if (phone.matches("^\\+27\\d{9}$"))   return true; // South Africa
        if (phone.matches("^\\+234\\d{10}$")) return true; // Nigeria
        if (phone.matches("^\\+91\\d{10}$"))  return true; // India
        if (phone.matches("^\\+86\\d{11}$"))  return true; // China
        if (phone.matches("^\\+61\\d{9}$"))   return true; // Australia
        if (phone.matches("^\\+254\\d{9}$"))  return true; // Kenya intl
        if (phone.matches("^\\+255\\d{9}$"))  return true; // Tanzania intl
        if (phone.matches("^\\+256\\d{9}$"))  return true; // Uganda intl
        if (phone.matches("^\\+250\\d{9}$"))  return true; // Rwanda intl
        if (phone.matches("^\\+55\\d{11}$"))  return true; // Brazil
        if (phone.matches("^\\+52\\d{10}$"))  return true; // Mexico
        if (phone.matches("^\\+81\\d{10}$"))  return true; // Japan
        if (phone.matches("^\\+82\\d{10}$"))  return true; // South Korea
        if (phone.matches("^\\+39\\d{10}$"))  return true; // Italy
        if (phone.matches("^\\+34\\d{9}$"))   return true; // Spain
        if (phone.matches("^\\+31\\d{9}$"))   return true; // Netherlands
        if (phone.matches("^\\+32\\d{9}$"))   return true; // Belgium
        if (phone.matches("^\\+41\\d{9}$"))   return true; // Switzerland
        if (phone.matches("^\\+46\\d{9}$"))   return true; // Sweden
        if (phone.matches("^\\+47\\d{8}$"))   return true; // Norway
        if (phone.matches("^\\+45\\d{8}$"))   return true; // Denmark
        if (phone.matches("^\\+358\\d{9}$"))  return true; // Finland
        if (phone.matches("^\\+353\\d{9}$"))  return true; // Ireland
        if (phone.matches("^\\+351\\d{9}$"))  return true; // Portugal
        if (phone.matches("^\\+48\\d{9}$"))   return true; // Poland
        if (phone.matches("^\\+380\\d{9}$"))  return true; // Ukraine
        if (phone.matches("^\\+7\\d{10}$"))   return true; // Russia
        if (phone.matches("^\\+90\\d{10}$"))  return true; // Turkey
        if (phone.matches("^\\+20\\d{10}$"))  return true; // Egypt
        if (phone.matches("^\\+212\\d{9}$"))  return true; // Morocco
        if (phone.matches("^\\+213\\d{9}$"))  return true; // Algeria
        if (phone.matches("^\\+216\\d{8}$"))  return true; // Tunisia
        if (phone.matches("^\\+233\\d{9}$"))  return true; // Ghana
        if (phone.matches("^\\+237\\d{9}$"))  return true; // Cameroon
        if (phone.matches("^\\+243\\d{9}$"))  return true; // DRC
        if (phone.matches("^\\+251\\d{9}$"))  return true; // Ethiopia
        if (phone.matches("^\\+260\\d{9}$"))  return true; // Zambia
        if (phone.matches("^\\+263\\d{9}$"))  return true; // Zimbabwe
        if (phone.matches("^\\+966\\d{9}$"))  return true; // Saudi Arabia
        if (phone.matches("^\\+971\\d{9}$"))  return true; // UAE
        if (phone.matches("^\\+92\\d{10}$"))  return true; // Pakistan
        if (phone.matches("^\\+880\\d{10}$")) return true; // Bangladesh
        if (phone.matches("^\\+62\\d{10}$"))  return true; // Indonesia
        if (phone.matches("^\\+63\\d{10}$"))  return true; // Philippines
        if (phone.matches("^\\+84\\d{9}$"))   return true; // Vietnam
        if (phone.matches("^\\+66\\d{9}$"))   return true; // Thailand
        if (phone.matches("^\\+60\\d{9}$"))   return true; // Malaysia
        if (phone.matches("^\\+65\\d{8}$"))   return true; // Singapore
        if (phone.matches("^\\+64\\d{9}$"))   return true; // New Zealand
        if (phone.matches("^\\+54\\d{10}$"))  return true; // Argentina
        if (phone.matches("^\\+56\\d{9}$"))   return true; // Chile
        if (phone.matches("^\\+57\\d{10}$"))  return true; // Colombia
        if (phone.matches("^\\+51\\d{9}$"))   return true; // Peru
        return false;
    }

    // ── Auth ─────────────────────────────────────────────────────────────────

    @Override
    public void registerUser(User user) throws RemoteException {
        // Technical validations
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }
        if (user.getUsername().length() < 3) {
            throw new RemoteException(
                "Username must be at least 3 characters.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new RemoteException(
                "Password must be at least 6 characters.");
        }

        // Email validation
        validateEmail(user.getEmail());

        // Phone validation
        validatePhone(user.getPhone());

        // Business validations
        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new RemoteException("Username already exists.");
        }
        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new RemoteException("Email already registered.");
        }

        // Set defaults
        if (user.getRole() == null) user.setRole("USER");
        user.setActive(true);

        userDAO.save(user);
    }

    @Override
    public User loginUser(String username, String password)
            throws RemoteException {
        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RemoteException("Password cannot be empty.");
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        if (!user.isActive()) {
            throw new RemoteException(
                "Your account has been deactivated. " +
                "Please contact the administrator.");
        }
        if (!user.getPassword().equals(password)) {
            throw new RemoteException("Incorrect password.");
        }
        return user;
    }

    // ── Fetch ─────────────────────────────────────────────────────────────────

    @Override
    public User getUserById(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException(
                "User with ID " + userId + " not found.");
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

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public void updateUser(User user) throws RemoteException {
        if (userDAO.findById(user.getUserId()) == null) {
            throw new RemoteException("User not found. Cannot update.");
        }
        // Re-validate email and phone on update
        validateEmail(user.getEmail());
        validatePhone(user.getPhone());
        userDAO.update(user);
    }

    // ── Admin — User Management ───────────────────────────────────────────────

    @Override
    public void activateUser(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        if (user.isActive()) {
            throw new RemoteException("User is already active.");
        }
        user.setActive(true);
        userDAO.update(user);
    }

    @Override
    public void deactivateUser(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        // Business rule — cannot deactivate main admin
        if ("admin".equals(user.getUsername())) {
            throw new RemoteException(
                "Cannot deactivate the main system admin.");
        }
        user.setActive(false);
        userDAO.update(user);
    }

    @Override
    public void resetUserAccount(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        // Reset password to default
        user.setPassword("reset123");
        user.setActive(true);
        userDAO.update(user);
    }

    @Override
    public void promoteToAdmin(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new RemoteException("User is already an admin.");
        }
        user.setRole("ADMIN");
        userDAO.update(user);
    }

    @Override
    public void demoteToUser(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        if ("admin".equals(user.getUsername())) {
            throw new RemoteException(
                "Cannot demote the main system admin.");
        }
        user.setRole("USER");
        userDAO.update(user);
    }

    @Override
    public void deleteUser(int userId) throws RemoteException {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RemoteException("User not found.");
        }
        if ("admin".equals(user.getUsername())) {
            throw new RemoteException(
                "Cannot delete the main system admin.");
        }
        userDAO.delete(userId);
    }

}