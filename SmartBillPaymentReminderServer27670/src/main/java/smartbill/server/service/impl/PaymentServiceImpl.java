package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import smartbill.server.dao.PaymentDAO;
import smartbill.server.dao.impl.PaymentDAOImpl;
import smartbill.server.model.Payment;
import smartbill.server.service.PaymentService;

public class PaymentServiceImpl extends UnicastRemoteObject implements PaymentService {

    private final PaymentDAO paymentDAO;

    public PaymentServiceImpl() throws RemoteException {
        super();
        this.paymentDAO = new PaymentDAOImpl();
    }

    @Override
    public void addPayment(Payment payment) throws RemoteException {
        // Technical validation — bill must be assigned
        if (payment.getBill() == null) {
            throw new RemoteException("Payment must be linked to a bill.");
        }
        // Business validation — amount must be greater than zero
        if (payment.getAmountPaid() <= 0) {
            throw new RemoteException("Payment amount must be greater than zero.");
        }
        // Technical validation — date must not be empty
        if (payment.getDatePaid() == null || payment.getDatePaid().trim().isEmpty()) {
            throw new RemoteException("Payment date cannot be empty.");
        }
        // Business validation — payment method must not be empty
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().trim().isEmpty()) {
            throw new RemoteException("Payment method cannot be empty.");
        }
        paymentDAO.save(payment);
    }

    @Override
    public Payment getPaymentById(int paymentId) throws RemoteException {
        Payment payment = paymentDAO.findById(paymentId);
        if (payment == null) {
            throw new RemoteException("Payment with ID " + paymentId + " not found.");
        }
        return payment;
    }

    @Override
    public List<Payment> getPaymentsByBill(int billId) throws RemoteException {
        // Technical validation — billId must be valid
        if (billId <= 0) {
            throw new RemoteException("Invalid bill ID.");
        }
        return paymentDAO.findByBill(billId);
    }

    @Override
    public List<Payment> getAllPayments() throws RemoteException {
        return paymentDAO.findAll();
    }

    @Override
    public void updatePayment(Payment payment) throws RemoteException {
        if (paymentDAO.findById(payment.getPaymentId()) == null) {
            throw new RemoteException("Payment not found. Cannot update.");
        }
        // Business validation — amount must be greater than zero
        if (payment.getAmountPaid() <= 0) {
            throw new RemoteException("Payment amount must be greater than zero.");
        }
        paymentDAO.update(payment);
    }

    @Override
    public void deletePayment(int paymentId) throws RemoteException {
        if (paymentDAO.findById(paymentId) == null) {
            throw new RemoteException("Payment not found. Cannot delete.");
        }
        paymentDAO.delete(paymentId);
    }

}