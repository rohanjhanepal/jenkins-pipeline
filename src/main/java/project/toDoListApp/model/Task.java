package project.toDoListApp.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Class Task represents a single task in the To-do list application.
 * It stores a task name, description and category, as well as the due date,
 * status and the date on which the task was created.
 */
public class Task implements Serializable {
  private String taskName;
  private String description;
  private String category;
  private String priority;
  private LocalDate dueDate;
  private boolean status;

  private final LocalDate dateAdded;

  /**
   * Constructor for task objects.
   */
  public Task(String taskName, String description, String category, LocalDate dueDate) {
    if (taskName == null || description == null || category == null || dueDate == null) {
      throw new IllegalArgumentException(
          "taskName, description, category or dueDate can not be null!");
    }
    if (taskName.isBlank()) {
      throw new IllegalArgumentException("taskName can not be blank!");
    }

    this.taskName = taskName;
    this.description = description;
    this.category = category;
    this.dueDate = dueDate;
    this.priority = "Medium";

    this.status = false;
    this.dateAdded = LocalDate.now();
  }

  /**
   * Constructor for task objects without the dueDate.
   */
  public Task(String taskName, String description, String category) {
    if (taskName == null || description == null || category == null) {
      throw new IllegalArgumentException("taskName, description or category can not be null!");
    }
    if (taskName.isBlank()) {
      throw new IllegalArgumentException("taskName can not be blank!");
    }

    this.taskName = taskName;
    this.description = description;
    this.category = category;
    this.priority = "Medium";

    this.dueDate = null;
    this.status = false;
    this.dateAdded = LocalDate.now();
  }

  /**
   * Returns the task name (title).
   *
   * @return The task name as a String
   */
  public String getTaskName() {
    return this.taskName;
  }

  /**
   * Sets the given taskName.
   *
   * @param taskName The task name to set,
   *                 can not be blank or null
   * @throws IllegalArgumentException if the given task name is blank
   */
  public void setTaskName(String taskName) {
    if (taskName != null) {
      if (taskName.isBlank()) {
        throw new IllegalArgumentException("Task name can not be blank");
      }
      this.taskName = taskName;
    }
  }

  /**
   * Returns the task description.
   *
   * @return The task description as a String
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the given description.
   *
   * @param description The description to set,
   *                    can not be null
   */
  public void setDescription(String description) {
    if (description != null) {
      this.description = description;
    }
  }

  /**
   * Sets the priority of the task.
   *
   * @param priority The String priority to be set, not blank or null
   */
  public void setPriority(String priority) {
    if (priority != null && !priority.isBlank()) {
      this.priority = priority;
    }
  }

  /**
   * Returns the priority of the task.
   *
   * @return the priority of the task
   */
  public String getPriority() {
    return this.priority;
  }

  /**
   * Returns the category.
   *
   * @return Returns the category as a String
   */
  public String getCategory() {
    return this.category;
  }

  /**
   * Sets the given String as the task's category.
   *
   * @param category The category to set,
   *                 can not be blank or null
   */
  public void setCategory(String category) {
    if (category != null) {
      if (!category.isBlank()) {
        this.category = category;
      }
    }
  }

  /**
   * Returns the due date of the task.
   *
   * @return The due date of the task as a LocalDate
   */
  public LocalDate getDueDate() {
    return this.dueDate;
  }

  /**
   * Sets the due date of the task.
   *
   * @param dueDate The due date to set,
   *                can not be null or before the date the task was originally created
   */
  public void setDueDate(LocalDate dueDate) {
    if (dueDate != null) {
      if (dueDate.isAfter(this.getDateAdded())) {
        this.dueDate = dueDate;
      }
    }
  }

  /**
   * Returns the task status.
   *
   * @return True if the task is complete, false otherwise
   */
  public boolean getStatus() {
    return this.status;
  }

  /**
   * Sets the status of the task,
   * true if the task is complete, false otherwise.
   *
   * @param status The status to set
   */
  public void setActiveStatus(boolean status) {
    this.status = status;
  }

  /**
   * Returns the task creation date.
   *
   * @return The task creation date as a LocalDate
   */
  public LocalDate getDateAdded() {
    return this.dateAdded;
  }

  @Override
  public String toString() {
    return "Task{"
        + "taskName='" + this.getTaskName() + '\''
        + ", description=" + this.getDescription()
        + ", category=" + this.getCategory()
        + ", dueDate=" + this.getDueDate()
        + '}';
  }

  /**
   * The TaskBuilder class is responsible for building an instance
   * of a Task class according to the Builder design pattern.
   */
  public static final class TaskBuilder {
    private final String taskName;
    private final String description;
    private final String category;
    private LocalDate dueDate;
    private boolean status;

    /**
     * Instantiates the PatientBuilder with the required arguments.
     */
    public TaskBuilder(String taskName, String description, String category) {
      this.taskName = taskName;
      this.description = description;
      this.category = category;

      this.dueDate = null;
      this.status = false;
    }

    /**
     * Sets the task's dueDate and returns the TaskBuilder, intermediate operation.
     *
     * @param dueDate The task's dueDate to set
     * @return The TaskBuilder with the given dueDate
     */
    public TaskBuilder withDueDate(LocalDate dueDate) {
      if (dueDate != null) {
        this.dueDate = dueDate;
      }
      return this;
    }

    /**
     * Sets the task's complete status and returns the TaskBuilder, intermediate operation.
     *
     * @param status The task's status to set
     * @return The TaskBuilder with the given status
     */
    public TaskBuilder withStatus(boolean status) {
      this.status = status;
      return this;
    }

    /**
     * Returns an instance of a Task according to the provided arguments, terminal operation.
     *
     * @return An instance of a Task with the provided arguments
     */
    public Task build() {
      Task task = new Task(this.taskName, this.description, this.category);

      task.dueDate = this.dueDate;
      task.status = this.status;
      return task;
    }
  }
}
