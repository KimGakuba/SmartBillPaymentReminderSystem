package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import smartbill.server.service.OTPService;

public class OTPServiceImpl extends UnicastRemoteObject implements OTPService {

    // Stores username -> OTP
    private static final Map<String, String> otpStore = new HashMap<>();

    // Stores username -> expiry time (5 minutes)
    private static final Map<String, Long> otpExpiry = new HashMap<>();

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000; // 5 minutes

    public OTPServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String generateOTP(String username) throws RemoteException {
        // Technical validation
        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Store OTP with expiry
        otpStore.put(username, otp);
        otpExpiry.put(username, System.currentTimeMillis() + OTP_VALIDITY_MS);

        // Simulate sending OTP — in production this would send an email/SMS
        System.out.println("============================================");
        System.out.println("  OTP NOTIFICATION — SmartBill System");
        System.out.println("  User     : " + username);
        System.out.println("  OTP Code : " + otp);
        System.out.println("  Expires  : 5 minutes");
        System.out.println("============================================");

        // Return OTP so client can display it in simulation mode
        return otp;
    }

    @Override
    public boolean verifyOTP(String username, String otp) throws RemoteException {
        // Technical validation
        if (username == null || username.trim().isEmpty()) {
            throw new RemoteException("Username cannot be empty.");
        }
        if (otp == null || otp.trim().isEmpty()) {
            throw new RemoteException("OTP cannot be empty.");
        }

        // Check if OTP exists
        if (!otpStore.containsKey(username)) {
            throw new RemoteException("No OTP found for this user. Please request a new one.");
        }

        // Check if OTP has expired
        long expiry = otpExpiry.getOrDefault(username, 0L);
        if (System.currentTimeMillis() > expiry) {
            otpStore.remove(username);
            otpExpiry.remove(username);
            throw new RemoteException("OTP has expired. Please request a new one.");
        }

        // Verify OTP
        boolean valid = otpStore.get(username).equals(otp.trim());

        // Remove OTP after use
        if (valid) {
            otpStore.remove(username);
            otpExpiry.remove(username);
        }

        return valid;
    }

}