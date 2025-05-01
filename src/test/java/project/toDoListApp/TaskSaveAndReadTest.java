package project.toDoListApp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.toDoListApp.model.Task;
import project.toDoListApp.model.TaskRegister;
import project.toDoListApp.utility.FileUtility;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;


class TaskSaveAndReadTest {

    @Test
    @DisplayName("Test the task save function")
    void testSaveTask() throws IOException, ClassNotFoundException {
        //Change path and/or user after what is locally on your computer
        FileUtility fileUtility = new FileUtility();
        Task task = new Task(
                "Check test",
                "check test task to find issues in the program",
                "important",
                LocalDate.MAX
        );
        TaskRegister register = new TaskRegister();
        register.addTask(new Task("test","test","test",LocalDate.MAX));
        register.addTask(task);
        fileUtility.saveToFile("target/registerTest.txt",register);

        TaskRegister registerRead = (TaskRegister) fileUtility.readFromFile("target/registerTest.txt");

        assertTrue(registerRead.getAllTasks().contains(registerRead.getAllTasks().get(0)));
        assertTrue(registerRead.getAllTasks().contains(registerRead.getAllTasks().get(1)));
    }
}
