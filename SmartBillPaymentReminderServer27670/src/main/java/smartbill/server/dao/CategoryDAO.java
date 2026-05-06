package smartbill.server.dao;

import smartbill.server.model.Category;
import java.util.List;

public interface CategoryDAO {

    void save(Category category);
    Category findById(int categoryId);
    Category findByName(String name);
    List<Category> findAll();
    void update(Category category);
    void delete(int categoryId);

}