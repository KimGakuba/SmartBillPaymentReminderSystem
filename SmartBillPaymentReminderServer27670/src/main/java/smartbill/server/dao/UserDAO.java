package smartbill.server.dao;


import smartbill.server.model.User;
import java.util.List;

public interface UserDAO {

    void save(User user);
    User findById(int userId);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void delete(int userId);

}