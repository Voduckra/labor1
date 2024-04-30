package repo;

import java.sql.SQLException;
import java.util.List;

public interface IRepo<T> {
    void insert(T object) throws SQLException;
    void delete(int id) throws SQLException;
    void update(T object) throws SQLException;
    List<T> getAll() throws SQLException;
}
