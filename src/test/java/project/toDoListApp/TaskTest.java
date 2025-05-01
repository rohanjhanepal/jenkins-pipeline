package project.toDoListApp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import project.toDoListApp.model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest
{
    @Test
    @DisplayName("Test the constructor of the Task class")
    void testConstructor()
    {
        LocalDate endDate = this.getEndDate();
        LocalDate dateCreated = LocalDate.now();

        Task task = new Task("Test title", "Test description",
                "None", endDate);

        assertEquals("Test title", task.getTaskName());
        assertEquals("Test description", task.getDescription());
        assertEquals("None", task.getCategory());
        assertFalse(task.getStatus());
        assertEquals(endDate, task.getDueDate());
        assertEquals(dateCreated, task.getDateAdded());
    }

    @Test
    @DisplayName("Test the constructor of the Task class with null arguments")
    void testConstructorWithNull()
    {
        LocalDate endDate = this.getEndDate();
        boolean exceptionThrown = false;

        try {
            Task task = new Task(null, null,
                    "None", endDate);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    @DisplayName("Test the constructor of the Task class with a blank title")
    void testConstructorWithBlankTitle()
    {
        LocalDate endDate = this.getEndDate();
        boolean exceptionThrown = false;

        try {
            Task task = new Task("", "Test description",
                    "None", endDate);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
    }

    @Test
    @DisplayName("Test setting the task name")
    void testSettingTaskName()
    {
        LocalDate endDate = this.getEndDate();
        String newTitle = "Test title 2";
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setTaskName(newTitle);

        assertEquals(newTitle, task.getTaskName());
    }

    @Test
    @DisplayName("Test setting the task name to null")
    void testSettingNullTaskName()
    {
        LocalDate endDate = this.getEndDate();
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setTaskName(null);

        assertEquals("Test title", task.getTaskName());
    }

    @Test
    @DisplayName("Test setting a blank task name")
    void testSettingBlankTaskName()
    {
        LocalDate endDate = this.getEndDate();
        boolean exceptionThrown = false;
        String newTitle = "";
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        try {
            task.setTaskName(newTitle);
        }
        catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        assertTrue(exceptionThrown);
        assertEquals("Test title", task.getTaskName());
    }

    @Test
    @DisplayName("Test setting the task description")
    void testSettingTaskDescription()
    {
        LocalDate endDate = this.getEndDate();
        String newDescription = "Test description 2";
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setDescription(newDescription);

        assertEquals(newDescription, task.getDescription());
    }

    @Test
    @DisplayName("Test setting the task description to null")
    void testSettingTaskDescriptionToNull()
    {
        LocalDate endDate = this.getEndDate();
        String newDescription = "Test description 2";
        Task task = new Task("Test title", newDescription,
                "None", endDate);

        task.setTaskName(null);

        assertEquals(newDescription, task.getDescription());
    }

    @Test
    @DisplayName("Test setting the task's category")
    void testSettingTaskCategory()
    {
        LocalDate endDate = this.getEndDate();
        String newCategory = "Test category";
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setCategory(newCategory);

        assertEquals(newCategory, task.getCategory());
    }

    @Test
    @DisplayName("Test setting the task's category to null")
    void testSettingTaskCategoryToNull()
    {
        LocalDate endDate = this.getEndDate();
        String category = "Test category";
        Task task = new Task("Test title", "Test description",
                category, endDate);

        task.setCategory(null);

        assertEquals(category, task.getCategory());
    }

    @Test
    @DisplayName("Test setting the task's category to a blank string")
    void testSettingBlankTaskCategory()
    {
        LocalDate endDate = this.getEndDate();
        String category = "Test category";
        String blank = "";
        Task task = new Task("Test title", "Test description",
                category, endDate);

        task.setCategory(blank);

        assertEquals(category, task.getCategory());
    }

    @Test
    @DisplayName("Test setting the task's due date")
    void testSettingTaskDueDate()
    {
        LocalDate endDate = this.getEndDate();
        LocalDate newEndDate = LocalDate.parse("4100-12-01");
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setDueDate(newEndDate);

        assertEquals(newEndDate, task.getDueDate());
    }

    @Test
    @DisplayName("Test setting the task's due date to null")
    void testSettingTaskDueDateToNull()
    {
        LocalDate endDate = this.getEndDate();
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setDueDate(null);

        assertEquals(endDate, task.getDueDate());
    }

    @Test
    @DisplayName("Test setting the task's due date to a date before the task was created")
    void testSettingTaskDueDateToPreTaskCreationDate()
    {
        LocalDate endDate = this.getEndDate();
        LocalDate newEndDate = LocalDate.parse("1979-01-01");
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setDueDate(newEndDate);

        assertEquals(endDate, task.getDueDate());
    }

    @Test
    @DisplayName("Test setting the task's status to false")
    void testSettingTaskStatusToFalse()
    {
        LocalDate endDate = this.getEndDate();
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setActiveStatus(false);

        assertFalse(task.getStatus());
    }

    @Test
    @DisplayName("Test setting the task's status to true")
    void testSettingTaskStatusToTrue()
    {
        LocalDate endDate = this.getEndDate();
        Task task = new Task("Test title", "Test description",
                "None", endDate);

        task.setActiveStatus(true);

        assertTrue(task.getStatus());
    }

    /**
     * Returns a LocalDate that always set to 1000 years in the future from the current year
     * @return a LocalDate set a 1000 years in the future
     */
    private LocalDate getEndDate()
    {
        int currentYear = LocalDate.now().getYear();
        int endYear = currentYear + 1000;
        String endDate = endYear + "-12-01";
        return LocalDate.parse(endDate);
    }
}