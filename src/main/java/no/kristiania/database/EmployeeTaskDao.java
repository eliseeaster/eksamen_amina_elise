package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTaskDao {
    private DataSource dataSource;

    public EmployeeTaskDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<EmployeeTask> list() throws SQLException {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employee_tasks")) {
                    try (ResultSet rs = statement.executeQuery()) {
                        List<EmployeeTask> employees = new ArrayList<>();
                        while (rs.next()) {
                            employees.add(mapRowToTask(rs));
                        }
                        return employees;
                    }
                }
            }
        }

    private EmployeeTask mapRowToTask(ResultSet rs) throws SQLException {
        EmployeeTask task = new EmployeeTask();
        task.setName(rs.getString("name"));
        return task;
    }


    public void insert(EmployeeTask task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO employee_tasks (name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, task.getName());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    task.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public EmployeeTask retreive(Long id) throws SQLException {

        return null;
    }
}
