package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.mail.MessagingException;
import smartbill.server.dao.impl.UserDAOImpl;
import smartbill.server.model.User;
import smartbill.server.service.OTPService;
import smartbill.server.util.EmailService;

public class OTPServiceImpl extends UnicastRemoteObject implements OTPService {

    private static final Map<String, String> otpStore = new HashMap<>();
    private static final Map<String, Long> otpExpiry = new HashMap<>();

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000;

    private final UserDAOImpl userDAO;

    public OTPServiceImpl() throws RemoteException {
        super();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public String generateOTP(String username) throws RemoteException {

        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }

        User user = userDAO.findByUsername(username);

        if (user == null) {
            throw new RemoteException("User not found.");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RemoteException("No email found for this user.");
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));

        try {
            EmailService.sendOTP(user.getEmail(), username, otp);
        } catch (MessagingException e) {
            throw new RemoteException(
                    "Failed to send OTP email. Check Gmail App Password or internet connection. "
                    + e.getMessage()
            );
        }

        otpStore.put(username, otp);
        otpExpiry.put(username, System.currentTimeMillis() + OTP_VALIDITY_MS);

        System.out.println("============================================");
        System.out.println("OTP GENERATED AND SENT TO EMAIL");
        System.out.println("User  : " + username);
        System.out.println("Email : " + user.getEmail());
        System.out.println("============================================");

        return otp;
    }

    @Override
    public boolean verifyOTP(String username, String otp)
            throws RemoteException {

        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }

        if (otp == null || otp.trim().isEmpty()) {
            throw new RemoteException("OTP cannot be empty.");
        }

        if (!otp.trim().matches("\\d{6}")) {
            throw new RemoteException("OTP must be exactly 6 digits.");
        }

        if (!otpStore.containsKey(username)) {
            throw new RemoteException("No OTP found. Please request a new one.");
        }

        long expiry = otpExpiry.getOrDefault(username, 0L);

        if (System.currentTimeMillis() > expiry) {
            otpStore.remove(username);
            otpExpiry.remove(username);
            throw new RemoteException("OTP has expired. Please request a new one.");
        }

        boolean valid = otpStore.get(username).equals(otp.trim());

        if (valid) {
            otpStore.remove(username);
            otpExpiry.remove(username);
        }

        return valid;
    }
}