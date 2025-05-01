package project.toDoListApp.controller;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import project.toDoListApp.model.Task;
import project.toDoListApp.model.TaskRegister;
import project.toDoListApp.utility.FileUtility;
import project.toDoListApp.view.ImageLoader;
import project.toDoListApp.view.ToDoListAppGUI;

/**
 * Class Controller represents the main controller for the application.
 * It is responsible for handling the events from the GUI.
 */
public class Controller {
  private static final String DATE_FORMAT = "dd/MM/yyyy";
  private final TaskRegister taskRegister;
  private final ObservableList<Task> taskListWrapper;
  private final FileUtility fileUtility;

  private boolean hideCompleteMode;
  private Task currentTask;

  /**
   * Instantiates the controller.
   */
  public Controller() {
    this.fileUtility = new FileUtility();
    this.taskRegister = this.fileUtility.getRegister();

    this.taskListWrapper = FXCollections.observableArrayList(this.taskRegister.getAllTasks());

    this.hideCompleteMode = false;
    this.currentTask = null;
    //this.fillRegisterWithTestTasks();
  }

  /**
   * Displays a given Task to the given parameters.
   *
   * @param task          The task to display, can not be null
   * @param taskTitle     The TextField to set the title to, can not be null
   * @param editor        The TextArea to set the task text content to, can not be null
   * @param dueDateButton The dueDateButton to enable after a Task is displayed, can not be null
   * @param dueDateLabel  The Label to set the end date text to, can not be null
   */
  public void displayTask(Task task, TextField taskTitle,
                          HTMLEditor editor, Button dueDateButton, Label dueDateLabel) {
    if (task != null
        && taskTitle != null
        && editor != null
        && dueDateButton != null
        && dueDateLabel != null) {
      // Save the current task to the register and update the task list
      this.saveTaskToRegister(taskTitle, editor);
      this.updateObservableList();

      this.currentTask = task;
      editor.setHtmlText(task.getDescription());
      taskTitle.setText(task.getTaskName());
      dueDateButton.setText("Set due date");
      dueDateLabel.setText("Due date: " + this.getLocalDateAsString(task.getDueDate()));
    }
  }

  /**
   * Saves the current task to the register after updating it's title and text content.
   *
   * @param editor    The TextArea to save the text from,
   *                  can not be null
   * @param taskTitle The TextField to get the text from,
   *                  can not be null
   */
  public void saveTaskToRegister(TextField taskTitle, HTMLEditor editor) {
    if (this.currentTask != null && taskTitle != null && editor != null) {
      if (taskTitle.getText().isBlank()) {
      } else {
        this.currentTask.setTaskName(taskTitle.getText());
      }
      this.currentTask.setDescription(editor.getHtmlText());

      this.taskRegister.addTask(this.currentTask);

      this.clearSelectedTask();
    }
  }

  /**
   * Clears the selected Task.
   */
  private void clearSelectedTask() {
    this.currentTask = null;
  }

  /**
   * Updates the observable list of tasks with fresh values from the task register.
   */
  private void updateObservableList() {
    if (this.hideCompleteMode) {
      this.taskListWrapper.setAll(this.taskRegister.getAllUncompletedTasks());
    } else {
      this.taskListWrapper.setAll(this.taskRegister.getAllTasks());
    }
  }

  /**
   * Adds a few tasks to the register for testing.
   */
  private void fillRegisterWithTestTasks() {
    this.taskRegister.addTask(new Task("Title 1", "Desc 1",
        "None", LocalDate.parse("2100-12-01")));
    this.taskRegister.addTask(new Task("Title 2", "Desc 2",
        "Cooking", LocalDate.parse("3100-12-01")));
    this.taskRegister.addTask(new Task
        .TaskBuilder("Title 3",
        "The quick brown fox jumps over the lazy dog",
        "None")
        .withStatus(true)
        .build());

    this.updateObservableList();
  }

  /**
   * Returns an ObservableList that holds the tasks.
   *
   * @return an ObservableList that holds the tasks
   */
  public ObservableList<Task> getTaskListWrapper() {
    return this.taskListWrapper;
  }

  /**
   * Returns a String representation of the given LocalDate, or 'No date set' if null.
   *
   * @param localDate The LocalDate to represent as a String, can be null
   * @return A String representation of the given LocalDate
   */
  private String getLocalDateAsString(LocalDate localDate) {
    String toReturn = "";
    if (localDate == null) {
      toReturn = "No date set";
    } else {
      toReturn = localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
    return toReturn;
  }

  /**
   * Sets the font scaling factor of the given HTMLEditor.
   *
   * @param htmlEditor  The HTMLEditor to set the font scaling factor to, can not be null
   * @param zoomLabel   The Label to set the scaling factor text to, can not be null
   * @param scaleFactor The font scaling factor as a double, can be both positive and negative
   */
  public void doZoom(HTMLEditor htmlEditor, Label zoomLabel, double scaleFactor) {
    if ((htmlEditor != null) && (zoomLabel != null) && (scaleFactor != 0)) {
      WebView webView = (WebView) htmlEditor.lookup(".web-view");
      if (webView != null) {
        double newFontScaleFactor = webView.getFontScale() + scaleFactor;
        // round the new font scaling factor before applying it
        double newRoundedFontScale = Math.round(newFontScaleFactor * 100) / 100.0;
        if ((0.5 <= newRoundedFontScale) && (newRoundedFontScale <= 2.0)) {
          webView.setFontScale(newRoundedFontScale);
          zoomLabel.setText(String.format("%.0f", newRoundedFontScale * 100) + "%");
        }
      }
    }
  }

  /**
   * Sets the new end date of the currently selected task.
   *
   * @param dateLabel The Label to set the new end date text to, can not be null
   */
  public void doSetNewEndDate(Label dateLabel) {
    if (this.currentTask == null) {
      this.showPleaseSelectItemDialog();
    } else {
      LocalDate newEndDate = this.doGetEndDateDialog();
      if (newEndDate != null && dateLabel != null) {
        this.currentTask.setDueDate(newEndDate);
        dateLabel.setText("Due date: " + this.getLocalDateAsString(newEndDate));
      }
    }
  }

  /**
   * Deletes a reminder form the list.
   *
   * @return Returns true if the current task was deleted, false otherwise
   */
  public boolean doDeleteReminder() {
    boolean success = false;
    if (this.currentTask == null) {
      this.showPleaseSelectItemDialog();
    } else {
      if (this.showDeleteConfirmationDialog()) {
        this.taskRegister.removeTask(this.currentTask);
        this.clearSelectedTask();
        success = true;

        this.updateObservableList();
      }
    }
    return success;
  }

  /**
   * Checks if the given Task should be shown in the Task table mode or not.
   *
   * @param task The Task whose visibility must be checked
   */
  public void checkTaskVisibility(Task task, ToDoListAppGUI toDoListAppGUI) {
    if (this.getHideCompleteMode()) {
      this.updateObservableList();

      // Disable the editor (center pane) and save the selected Task
      // only if the currently displayed Task is marked as complete
      if (task == this.getCurrentlySelectedTask()) {
        this.saveTaskToRegister(toDoListAppGUI.getTaskTitleTextField(),
            toDoListAppGUI.getHtmlEditor());

        toDoListAppGUI.disableCenterPane();
      }

      // Clear the table if the Task just marked as complete was the last one
      if (this.getTaskListWrapper().isEmpty()) {
        toDoListAppGUI.getTaskTableView().getItems().clear();
      }
    }
    toDoListAppGUI.refreshTable();
  }

  /**
   * Sets the display mode of the controller.
   *
   * @param hideCompleted The mode to set,
   *                      true to hide the completed Tasks, false otherwise
   */
  private void doChangeDisplayMode(boolean hideCompleted) {
    this.hideCompleteMode = hideCompleted;
  }

  /**
   * Toggles the hide completed Tasks mode.
   *
   * @param mode The mode to set, true to hide the completed Tasks, false otherwise
   */
  public void doToggleDisplayMode(boolean mode, ToDoListAppGUI toDoListAppGUI) {
    this.doChangeDisplayMode(mode);
    this.updateObservableList();

    if (this.getCurrentlySelectedTask() != null) {
      if (this.getHideCompleteMode() && this.getCurrentlySelectedTask().getStatus()) {
        // The selected Task should not be shown in the current mode ->
        // save it, disable the editor (centerPane) and clear the selection
        this.saveTaskToRegister(toDoListAppGUI.getTaskTitleTextField(),
            toDoListAppGUI.getHtmlEditor());

        toDoListAppGUI.disableCenterPane();
      }
    }
    toDoListAppGUI.refreshTable();
  }

  /**
   * Saves the task register to a file.
   *
   * @param showFeedbackMode True to show a feedback dialog, false otherwise
   */
  public void saveTaskRegisterToFile(boolean showFeedbackMode) {
    try {
      this.fileUtility.saveToFile("tasks/savedTasks.txt", this.taskRegister);
      if (showFeedbackMode) {
        this.doShowSaveSuccessfulDialog();
      }
    } catch (IOException e) {
      if (showFeedbackMode) {
        this.doShowSaveUnsuccessfulDialog();
      }
    }
  }

  // -----------------------------------------------------------
  //    DIALOGS
  // -----------------------------------------------------------

  /**
   * Shows the About Dialog.
   */
  public void showAboutDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    this.addIconToDialog(alert);
    alert.setTitle("About");
    alert.setHeaderText("To-Do List App");
    alert.setContentText("An application created by" + "\n"
        + "Group 2" + "\n"
        + "2021"
        + "\n\n"
        + "Running on Java " + System.getProperty("java.version")
        + " with JavaFX version " + System.getProperty("javafx.version"));

    alert.showAndWait();
  }

  /**
   * Displays an information dialog that notifies the user of a successful save.
   */
  private void doShowSaveSuccessfulDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    this.addIconToDialog(alert);
    alert.setTitle("Information");
    alert.setHeaderText("Successfully saved");
    alert.setContentText("The tasks were successfully saved to disk");
    alert.showAndWait();
  }

  /**
   * Displays a warning dialog that notifies the user of an unsuccessful save.
   */
  private void doShowSaveUnsuccessfulDialog() {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    this.addIconToDialog(alert);
    alert.setTitle("Warning");
    alert.setHeaderText("Not saved");
    alert.setContentText("The tasks were not saved to disk");
    alert.showAndWait();
  }

  /**
   * Application exit dialog. A confirmation dialog that is displayed before exiting.
   */
  public void quit(Event event, TextField taskTitle, HTMLEditor editor) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    this.addIconToDialog(alert);
    alert.setTitle("Confirm close");
    alert.setHeaderText("Exit this application?");
    alert.setContentText("Are you sure you want to exit the application?" + "\n"
        + "Your changes are automatically saved . . .");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent()) {
      if (result.get() == ButtonType.OK) {
        this.saveTaskToRegister(taskTitle, editor);
        this.saveTaskRegisterToFile(false);
        Platform.exit();
      } else {
        event.consume();
      }
    }
  }

  /**
   * Creates a new pop-up dialog box,
   * where the user can provide details of a new task to be added to the to-do list.
   */
  public void showNewReminderDialog() {
    Dialog<Task> newReminderDialog = new Dialog<>();

    this.addIconToDialog(newReminderDialog);
    newReminderDialog.setTitle("Reminder Details");
    newReminderDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField taskName = new TextField();
    taskName.setPromptText("Title");

    TextField description = new TextField();
    description.setPromptText("Description");

    TextField category = new TextField();
    category.setPromptText("Category");

    DatePicker datePicker = this.getDatePicker(true);

    grid.add(new Label("Task name:"), 0, 0);
    grid.add(taskName, 1, 0);
    grid.add(new Label("Description:"), 0, 1);
    grid.add(description, 1, 1);
    grid.add(new Label("Category:"), 0, 2);
    grid.add(category, 1, 2);
    grid.add(new Label("Due date:"), 0, 3);
    grid.add(datePicker, 1, 3);

    newReminderDialog.getDialogPane().setContent(grid);

    // Disable the OK Button if one of the required TextFields is blank
    BooleanBinding blankTextField = taskName.textProperty().isEmpty();
    newReminderDialog.getDialogPane().lookupButton(ButtonType.OK)
        .disableProperty()
        .bind(blankTextField);

    newReminderDialog.setResultConverter(
        (ButtonType button) -> {
          Task result = null;
          if (button == ButtonType.OK) {
            result = new Task
                .TaskBuilder(
                    taskName.getText(),
                    description.getText(),
                    category.getText())
                    .withDueDate(datePicker.getValue())
                    .build();
          }
          return result;
        }
    );

    Optional<Task> result = newReminderDialog.showAndWait();

    if (result.isPresent()) {
      Task newTask = result.get();
      this.taskRegister.addTask(newTask);
      this.updateObservableList();
    }
  }

  /**
   * Displays a warning telling the user to select
   * an element from the list.
   */
  private void showPleaseSelectItemDialog() {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    this.addIconToDialog(alert);
    alert.setTitle("Information");
    alert.setHeaderText("No items selected");
    alert.setContentText("No item is selected from the table.\n"
        + "Please select an item from the table.");

    alert.showAndWait();
  }

  /**
   * Displays a delete confirmation dialog. If the user confirms the delete,
   * <code>true</code> is returned.
   *
   * @return <code>true</code> if the user confirms the delete
   */
  public boolean showDeleteConfirmationDialog() {
    boolean deleteConfirmed = false;

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    this.addIconToDialog(alert);
    alert.setTitle("Delete confirmation");
    alert.setHeaderText("Delete confirmation");
    alert.setContentText("Are you sure you want to delete this reminder?");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent()) {
      deleteConfirmed = (result.get() == ButtonType.OK);
    }
    return deleteConfirmed;
  }

  /**
   * Displays a dialog in which the user can type the desired date as a String or
   * select it from the DatePicker.
   * If the user clicks okay, a LocalDate of the desired date is returned, otherwise null.
   *
   * @return The user selected LocalDate, null if cancelled
   */
  private LocalDate doGetEndDateDialog() {
    Dialog<LocalDate> dialog = new Dialog<>();
    this.addIconToDialog(dialog);
    dialog.setTitle("Date picker");
    dialog.getDialogPane().setPrefWidth(275);

    DatePicker datePicker = this.getDatePicker(false);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(5, 5, 5, 5));

    grid.add(datePicker, 0, 0);

    dialog.getDialogPane().getChildren().add(grid);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.setResultConverter((ButtonType button) -> {
      LocalDate result = null;
      if (button == ButtonType.OK && !datePicker.getEditor().getText().isBlank()) {
        result = datePicker.getValue();
      }
      return result;
    });

    return dialog.showAndWait().orElse(null);
  }

  /**
   * Returns a set-up DatePicker in the "dd/MM/yyyy" format and with disabled past dates.
   *
   * @param mode The mode toggle,
   *             true if the returned DatePicker should always be blank,
   *             false if the returned DatePicker should try to include
   *             the selected task's end date as text.
   * @return An already set-up DatePicker
   */
  private DatePicker getDatePicker(boolean mode) {
    DatePicker datePicker = null;
    if (!mode && this.currentTask != null && this.currentTask.getDueDate() != null) {
      datePicker = new DatePicker(this.currentTask.getDueDate());
    } else {
      datePicker = new DatePicker();
    }
    datePicker.setMinSize(210, 25);
    datePicker.setPromptText(DATE_FORMAT.toLowerCase());
    datePicker.setShowWeekNumbers(true);
    datePicker.setStyle("-fx-font-size: 1.1em;");

    datePicker.setConverter(new StringConverter<>() {
      @Override
      public String toString(LocalDate date) {
        String toReturn = "";
        if (date != null) {
          try {
            toReturn = DateTimeFormatter.ofPattern(DATE_FORMAT).format(date);
          } catch (DateTimeException | IllegalArgumentException ignored) {
          }
        }
        return toReturn;
      }

      @Override
      public LocalDate fromString(String string) {
        LocalDate localDateToReturn = null;
        if (string != null && !string.isBlank()) {
          try {
            localDateToReturn = LocalDate.parse(string, DateTimeFormatter.ofPattern(DATE_FORMAT));
          } catch (DateTimeParseException ignored) {
          }
        }
        return localDateToReturn;
      }
    });

    datePicker.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);

        this.setTooltip(new Tooltip("Select this date"));

        // Show weekends in red
        DayOfWeek day = DayOfWeek.from(date);
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
          this.setTextFill(Color.RED);
        }

        // Disable all past date and empty cells
        if (empty || date.isBefore(LocalDate.now())) {
          this.setDisable(true);
          this.setStyle("-fx-background-color: #ffe3e9;");
        }

        // Show the current date in blue
        if (date.isEqual(LocalDate.now())) {
          this.setStyle("-fx-background-color: #e3f3ff;");
          this.setTooltip(new Tooltip("Today"));
        }
      }
    });

    return datePicker;
  }

  /**
   * Adds an icon to the title bar of the given Dialog.
   * @param dialog The dialog to set the icon to, not null
   */
  private void addIconToDialog(Dialog<?> dialog) {
    if (dialog != null) {
      Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
      stage.getIcons().add(ImageLoader.getInstance().getIcon());
    }
  }

  // -----------------------------------------------------------
  //    GETTERS
  // -----------------------------------------------------------

  /**
   * Returns the display mode, true if hide completed Tasks is enabled, false otherwise.
   *
   * @return The display mode as a boolean
   */
  public boolean getHideCompleteMode() {
    return this.hideCompleteMode;
  }

  /**
   * Returns the currently selected Task.
   *
   * @return The currently selected Task
   */
  public Task getCurrentlySelectedTask() {
    return this.currentTask;
  }
}
