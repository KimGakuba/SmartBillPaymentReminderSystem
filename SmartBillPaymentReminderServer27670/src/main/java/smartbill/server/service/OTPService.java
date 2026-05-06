package smartbill.server.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OTPService extends Remote {

    String generateOTP(String username) throws RemoteException;
    boolean verifyOTP(String username, String otp) throws RemoteException;

}