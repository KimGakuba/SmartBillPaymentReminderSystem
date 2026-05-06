package smartbill.server.service.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import smartbill.server.dao.BillDAO;
import smartbill.server.dao.impl.BillDAOImpl;
import smartbill.server.model.Bill;
import smartbill.server.service.BillService;

public class BillServiceImpl extends UnicastRemoteObject implements BillService {

    private final BillDAO billDAO;

    public BillServiceImpl() throws RemoteException {
        super();
        this.billDAO = new BillDAOImpl();
    }

    @Override
    public void addBill(Bill bill) throws RemoteException {
        // Technical validation — name must not be empty
        if (bill.getName() == null || bill.getName().trim().isEmpty()) {
            throw new RemoteException("Bill name cannot be empty.");
        }
        // Business validation — amount must be greater than zero
        if (bill.getAmount() <= 0) {
            throw new RemoteException("Bill amount must be greater than zero.");
        }
        // Business validation — due date must not be empty
        if (bill.getDueDate() == null || bill.getDueDate().trim().isEmpty()) {
            throw new RemoteException("Due date cannot be empty.");
        }
        // Business validation — user must be assigned
        if (bill.getUser() == null) {
            throw new RemoteException("Bill must be assigned to a user.");
        }
        billDAO.save(bill);
    }

    @Override
    public Bill getBillById(int billId) throws RemoteException {
        Bill bill = billDAO.findById(billId);
        if (bill == null) {
            throw new RemoteException("Bill with ID " + billId + " not found.");
        }
        return bill;
    }

    @Override
    public List<Bill> getBillsByUser(int userId) throws RemoteException {
        return billDAO.findByUser(userId);
    }

    @Override
    public List<Bill> getOverdueBills(int userId) throws RemoteException {
        return billDAO.findOverdueBills(userId);
    }

    @Override
    public List<Bill> getBillsByStatus(String status) throws RemoteException {
        if (status == null || status.trim().isEmpty()) {
            throw new RemoteException("Status cannot be empty.");
        }
        return billDAO.findByStatus(status);
    }

    @Override
    public List<Bill> getAllBills() throws RemoteException {
        return billDAO.findAll();
    }

    @Override
    public void updateBill(Bill bill) throws RemoteException {
        if (billDAO.findById(bill.getBillId()) == null) {
            throw new RemoteException("Bill not found. Cannot update.");
        }
        // Business validation — amount must be greater than zero
        if (bill.getAmount() <= 0) {
            throw new RemoteException("Bill amount must be greater than zero.");
        }
        billDAO.update(bill);
    }

    @Override
    public void deleteBill(int billId) throws RemoteException {
        if (billDAO.findById(billId) == null) {
            throw new RemoteException("Bill not found. Cannot delete.");
        }
        billDAO.delete(billId);
    }

}