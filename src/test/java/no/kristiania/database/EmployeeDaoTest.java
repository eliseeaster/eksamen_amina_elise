package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeDaoTest {

    @Test
    void shouldListInsertedEmployees() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();

        EmployeeDao employeeDao = new EmployeeDao(dataSource);
        String employee = exampleEmployeeName();
        employeeDao.insert(employee);
        assertThat(employeeDao.list()).contains(employee);
    }

    private String exampleEmployeeName() {
        String[] options = {"Elise", "Amina", "Trond", "Sarah"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}