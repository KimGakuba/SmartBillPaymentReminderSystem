package smartbill.server.service;

import smartbill.server.model.Payment;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PaymentService extends Remote {

    void addPayment(Payment payment) throws RemoteException;
    Payment getPaymentById(int paymentId) throws RemoteException;
    List<Payment> getPaymentsByBill(int billId) throws RemoteException;
    List<Payment> getAllPayments() throws RemoteException;
    void updatePayment(Payment payment) throws RemoteException;
    void deletePayment(int paymentId) throws RemoteException;
    // Add this method to both server and client PaymentService interfaces
List<Payment> getPaymentsByUser(int userId) throws RemoteException;

}