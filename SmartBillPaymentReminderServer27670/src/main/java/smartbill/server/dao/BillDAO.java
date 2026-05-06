package smartbill.server.dao;

import smartbill.server.model.Bill;
import java.util.List;

public interface BillDAO {

    void save(Bill bill);
    Bill findById(int billId);
    List<Bill> findByUser(int userId);
    List<Bill> findOverdueBills(int userId);
    List<Bill> findByStatus(String status);
    List<Bill> findAll();
    void update(Bill bill);
    void delete(int billId);

}