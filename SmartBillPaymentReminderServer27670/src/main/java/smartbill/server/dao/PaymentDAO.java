package smartbill.server.dao;

import smartbill.server.model.Payment;
import java.util.List;

public interface PaymentDAO {

    void save(Payment payment);
    Payment findById(int paymentId);
    List<Payment> findByBill(int billId);
    List<Payment> findAll();
    void update(Payment payment);
    void delete(int paymentId);

}