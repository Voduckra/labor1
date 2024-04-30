package repo;

import entity.Clients;
import entity.Masters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientsRepozitory implements IRepo<Clients> {
    private static final Logger log= LoggerFactory.getLogger(MasterRep.class);
    public Connection connect() throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:h2:C:/Users/ПК/test.mv.db");
        if (conn==null){log.warn("Нет соединения с БД!");
            System.exit(0);
        }
        return conn;
    }
    public Statement createStmt(Connection conn)throws SQLException{
        return conn.createStatement();
    }
    @Override
    public void insert(Clients client) throws SQLException {
        String query = "INSERT INTO Clients (surname, name, ID_MASTERS,exist) VALUES (?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getSurname());
            statement.setInt(3, client.getIdMasters());
            statement.setBoolean(4, client.isDelete());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                client.setId(generatedKeys.getInt(1));
                log.info("Выданный айди:"+client.getId());
                } else {
                throw new SQLException("Failed to get generated id for client.");
                }
            }
        }

    }


    @Override
    public void delete(int id) throws SQLException {
        String query = "UPDATE Clients SET EXIST = false WHERE ID_CLIENT = ?";
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
    public void update(Clients client) throws SQLException {
        String query = "UPDATE Clients SET name = ?, surname = ? WHERE ID_CLIENT= ?";
        try (PreparedStatement statement = connect().prepareStatement(query)) {
            statement.setString(1, client.getName());
            statement.setString(2, client.getSurname());
            statement.setInt(3, client.getId());
            if(statement.executeUpdate()>0){
                log.info("Запись успешно обновлена!");
            } else {
                log.info("Запись не была найдена или не была обновлена.");
            }
        }


    }

    @Override
    public List<Clients> getAll() throws SQLException {
        String query = ("SELECT * FROM Clients WHERE EXIST=TRUE");
        try (PreparedStatement statement = connect().prepareStatement(query)) {
            ResultSet rs= statement.executeQuery();
            List<Clients> clients = new ArrayList<>();
            while (rs.next()) {
                Clients client = new Clients(rs.getInt("id_client"), rs.getString("name"), rs.getString("surname"), rs.getInt("id_masters"));
                clients.add(client);
            }
            return clients;
        }
    }
    public void closeConnection(Statement stmt) throws SQLException {
        stmt.close();
    }

}
