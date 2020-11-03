package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {

    private EmployeeTaskDao taskDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new EmployeeTaskDao(dataSource);
    }

    @Test
    void shouldListAllTasks(){
        EmployeeTask task1 = exampleTask();
        EmployeeTask task2 = exampleTask();
        assertThat(taskDao.list())
            .extracting(EmployeeTask::getName)
            .contains(task1.getName(), task2.getName());
    }

    @Test
    void shouldRetreiveAllCategoryProperties() throws SQLException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        EmployeeTask task = exampleTask();

        assertThat(taskDao.retreive(task.getId()))
                .usingRecursiveComparison()
                .isEqualTo(task);

    }

    private EmployeeTask exampleTask() {
    return null;
    }

}
