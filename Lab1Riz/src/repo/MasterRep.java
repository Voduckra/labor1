package repo;

import entity.Masters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterRep implements IRepo<Masters>{
    private static final Logger log= LoggerFactory.getLogger(MasterRep.class);

    public Connection connect() throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:h2:C:/Users/ПК/test.mv.db");
        if (conn==null){log.info("Нет соединения с бд");
            System.exit(0);
        }
        return conn;
    }
    public Statement createStmt(Connection conn)throws SQLException{
        return conn.createStatement();
    }



    @Override
    public void insert(Masters master) throws SQLException {
        String query = "INSERT INTO Masters (name, surname) VALUES (?,?)";
        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, master.getName());
            statement.setString(2, master.getSurname());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    master.setId(generatedKeys.getInt(1));
                    log.info("Выданный айди:"+master.getId());
                } else {
                    throw new SQLException("Failed to get generated id for client.");
                }
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "UPDATE Masters SET EXIST = FALSE WHERE ID_MASTER = ?";
        try (PreparedStatement statement = connect().prepareStatement(query)) {
            statement.setInt(1, id);
            if(statement.executeUpdate()>0){
                log.info("Запись успешно обновлена!");
            } else {
                log.info("Запись не была найдена или не была обновлена.");
            }
        }

    }




    @Override
    public void update(Masters master) throws SQLException {
        String query = "UPDATE Masters SET name = ?, surname = ? WHERE id_master = ?";
        try (PreparedStatement statement = connect().prepareStatement(query)) {
            statement.setString(1, master.getName());
            statement.setString(2, master.getSurname());
            statement.setInt(3, master.getId());
            if(statement.executeUpdate()>0){
                log.info("Запись успешно обновлена!");
            } else {
                log.info("Запись не была найдена или не была обновлена.");
            }
        }

    }

    @Override
    public List<Masters> getAll() throws SQLException {
        String query = ("SELECT * FROM Masters WHERE EXIST=TRUE");
        try (PreparedStatement statement = connect().prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            List<Masters> masters = new ArrayList<>();
            while (rs.next()) {
                Masters master = new Masters(rs.getInt("id_master"), rs.getString("name"), rs.getString("surname"));
                masters.add(master);
            }
            return masters;
        }
    }

}
