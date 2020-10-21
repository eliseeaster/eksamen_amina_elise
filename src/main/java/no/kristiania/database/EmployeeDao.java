package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeDao {

    private final DataSource dataSource;

    public EmployeeDao(DataSource dataSource) { this.dataSource = dataSource; }

    public void insert(Employee employee) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO employees (employee_name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
                    )) {
                statement.setString(1, employee.getName());
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
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE Id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> employees = new ArrayList<>();
                    if (rs.next()) {
                        Employee employee = new Employee();
                        employee.setId(rs.getLong("id"));
                        employee.setName(rs.getString("employee_name"));
                        return employee;
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public List<String> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> employees = new ArrayList<>();
                    while (rs.next()) {
                        employees.add(rs.getString("employee_name"));
                    }
                    return employees;
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiasemployees");
        dataSource.setUser("kristianiasemployeesuser");
        dataSource.setPassword("hemmelig");

        EmployeeDao employeeDao = new EmployeeDao(dataSource);


        System.out.println("What's the name of the employee?");
        Scanner scanner = new Scanner(System.in);
        Employee employee = new Employee();
        employee.setName(scanner.nextLine());

        employeeDao.insert(employee);
        System.out.println(employeeDao.list());

    }
}
