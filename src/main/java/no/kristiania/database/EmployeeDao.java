package no.kristiania.database;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class EmployeeDao {

    private final DataSource dataSource;

    public EmployeeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Employee employee) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into employees (first_name, last_name, email) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, employee.getFirstName());
                statement.setString(2, employee.getLastName());
                statement.setString(3, employee.getEmail());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    employee.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public Employee retrieve(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToWorkers(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private Employee mapRowToWorkers(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        employee.setFirstName(rs.getString("first_Name"));
        employee.setLastName(rs.getString("last_Name"));
        employee.setEmail(rs.getString("email"));
        return employee;
    }

    public List<Employee> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(" SELECT * FROM employees")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()) {
                        Employee employee = new Employee();
                        employees.add(mapRowToWorkers(rs));
                        rs.getString("first_name");
                        rs.getString("last_name");
                        rs.getString("email");
                        employee.setId(rs.getLong("id"));
                    }
                    return employees;
                }
            }
        }
    }

}
