package no.kristiania.database;

import org.junit.jupiter.api.Test;

public class TaskDaoTest {

    @Test
    void shouldListAllTests(){

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

}
