package no.kristiania.database;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeeDao {


    private final ArrayList<String> employees = new ArrayList<>();
    private DataSource dataSource;

    public EmployeeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(String employee) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO employees (employee_name) values (?)")) {
                statement.setString(1, employee);
                statement.executeUpdate();
            }
        }
        employees.add(employee);
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
        String employeeName = scanner.nextLine();

        employeeDao.insert(employeeName);
        System.out.println(employeeDao.list());

    }
}
