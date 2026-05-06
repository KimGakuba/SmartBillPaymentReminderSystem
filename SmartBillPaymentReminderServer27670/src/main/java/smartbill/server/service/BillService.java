package smartbill.server.service;

import smartbill.server.model.Bill;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BillService extends Remote {

    void addBill(Bill bill) throws RemoteException;
    Bill getBillById(int billId) throws RemoteException;
    List<Bill> getBillsByUser(int userId) throws RemoteException;
    List<Bill> getOverdueBills(int userId) throws RemoteException;
    List<Bill> getBillsByStatus(String status) throws RemoteException;
    List<Bill> getAllBills() throws RemoteException;
    void updateBill(Bill bill) throws RemoteException;
    void deleteBill(int billId) throws RemoteException;

}