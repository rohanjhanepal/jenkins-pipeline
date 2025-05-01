package project.toDoListApp.view;

import java.util.Arrays;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import project.toDoListApp.controller.Controller;
import project.toDoListApp.model.Task;
import project.toDoListApp.utility.PriorityComparator;

/**
 * Class ToDoListAppGUI represents the main window in the application.
 */
public class ToDoListAppGUI extends Application {
  /**
   * The width of the left TableView and the bottom left Buttons.
   */
  private static final int LEFT_PANE_WIDTH = 200;

  private final Controller controller;

  private final BorderPane root;
  private final TableView<Task> taskTableView;
  private final HTMLEditor htmlEditor;
  private final TextField taskTitleTextField;
  private final Button dueDateButton;
  private final Label dateLabel;
  private final Label zoomLabel;

  /**
   * ToDoListAppGUI constructor.
   */
  public ToDoListAppGUI() {
    this.controller = new Controller();

    this.root = new BorderPane();
    this.taskTableView = new TableView<>();
    this.htmlEditor = new HTMLEditor();
    this.taskTitleTextField = new TextField();
    this.dueDateButton = new Button();
    this.dateLabel = new Label();
    this.zoomLabel = new Label();
  }

  /**
   * The main method.
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) {
    this.root.setTop(this.setupTopMenu());
    this.root.setLeft(this.setupLeft());
    this.root.setCenter(this.setupCenter());

    stage.setTitle("To-Do List App");
    stage.getIcons().add(ImageLoader.getInstance().getIcon());
    stage.setMinWidth(818);
    stage.setMinHeight(500);
    stage.setOnCloseRequest(e ->
            this.controller.quit(e, this.getTaskTitleTextField(), this.getHtmlEditor()));

    Scene scene = new Scene(this.root, 800, 500, Color.WHITE);
    stage.setScene(scene);
    stage.show();

    // Disable the center pane on first run
    this.disableCenterPane();

    this.root.requestFocus();
  }

  /**
   * Sets up the top menu bar.
   *
   * @return The already set up menu bar
   */
  private MenuBar setupTopMenu() {
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(this.setupFileMenu(), this.setupEditMenu(),
        this.setupViewMenu(), this.setupHelpMenu());
    return menuBar;
  }

  /**
   * Returns an already set-up top file menu bar.
   *
   * @return An already set-up top file menu bar
   */
  private Menu setupFileMenu() {
    Menu fileMenu = new Menu("File");

    SeparatorMenuItem separator1 = new SeparatorMenuItem();
    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    MenuItem newReminder = new MenuItem("New Reminder");
    KeyCombination keyCombinationNewReminder =
        new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
    newReminder.setAccelerator(keyCombinationNewReminder);
    newReminder.setOnAction(e -> {
      this.controller.showNewReminderDialog();
      this.refreshTable();
    });

    MenuItem deleteReminder = new MenuItem("Delete Reminder");
    KeyCombination keyCombinationDeleteReminder =
        new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN);
    deleteReminder.setAccelerator(keyCombinationDeleteReminder);
    deleteReminder.setOnAction(e -> this.deleteReminderAction());

    MenuItem saveAll = new MenuItem("Save all");
    KeyCombination keyCombinationSaveAll =
        new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    saveAll.setAccelerator(keyCombinationSaveAll);
    saveAll.setOnAction(e -> this.controller.saveTaskRegisterToFile(true));

    MenuItem exit = new MenuItem("Exit");
    KeyCombination keyCombinationExit =
        new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    exit.setAccelerator(keyCombinationExit);
    exit.setOnAction(e -> this.controller
        .quit(e, this.getTaskTitleTextField(), this.getHtmlEditor()));

    fileMenu.getItems().addAll(newReminder, deleteReminder, separator1, saveAll, separator2, exit);
    return fileMenu;
  }

  /**
   * Returns an already set-up top edit menu bar.
   *
   * @return An already set-up top edit menu bar
   */
  private Menu setupEditMenu() {
    Menu editMenu = new Menu("Edit");
    MenuItem editEndDate = new MenuItem("Edit end date");
    editEndDate.setOnAction(e -> this.controller.doSetNewEndDate(this.getDateLabel()));

    editMenu.getItems().add(editEndDate);
    return editMenu;
  }

  /**
   * Returns an already set-up top view menu bar.
   *
   * @return An already set-up top view menu bar
   */
  private Menu setupViewMenu() {
    Menu viewMenu = new Menu("View");

    SeparatorMenuItem separator1 = new SeparatorMenuItem();


    MenuItem zoomInMenuItem = new MenuItem("Zoom in");
    KeyCombination keyCombinationZoomIn =
        new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN);
    zoomInMenuItem.setAccelerator(keyCombinationZoomIn);
    zoomInMenuItem.setOnAction(e -> this.zoomInAction());

    MenuItem zoomOutMenuItem = new MenuItem("Zoom out");
    KeyCombination keyCombinationZoomOut =
        new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
    zoomOutMenuItem.setAccelerator(keyCombinationZoomOut);
    zoomOutMenuItem.setOnAction(e -> this.zoomOutAction());


    ToggleGroup showHideCompletedGroup = new ToggleGroup();

    RadioMenuItem showCompleted = new RadioMenuItem("Show completed");
    showCompleted.setOnAction(e ->
        this.controller.doToggleDisplayMode(false, this));
    showCompleted.setSelected(true);
    showCompleted.setToggleGroup(showHideCompletedGroup);

    RadioMenuItem hideCompleted = new RadioMenuItem("Hide completed");
    hideCompleted.setOnAction(e ->
        this.controller.doToggleDisplayMode(true, this));
    hideCompleted.setToggleGroup(showHideCompletedGroup);

    viewMenu.getItems().addAll(showCompleted, hideCompleted, separator1,
        zoomInMenuItem, zoomOutMenuItem);
    return viewMenu;
  }

  /**
   * Returns an already set-up top help menu bar.
   *
   * @return An already set-up top help menu bar
   */
  private Menu setupHelpMenu() {
    Menu helpMenu = new Menu("Help");

    MenuItem aboutMenuItem = new MenuItem("About");
    aboutMenuItem.setOnAction(e -> this.controller.showAboutDialog());

    helpMenu.getItems().add(aboutMenuItem);
    return helpMenu;
  }

  /**
   * Sets up the left node.
   *
   * @return The already set up left node
   */
  private VBox setupLeft() {
    VBox vBox = new VBox();
    vBox.setPrefWidth(LEFT_PANE_WIDTH);
    VBox.setVgrow(this.getTaskTableView(), Priority.ALWAYS);

    this.setupLeftTopTable();
    vBox.getChildren().addAll(this.getTaskTableView(), this.setupBottomLeftButtons());
    return vBox;
  }

  /**
   * Sets up the left table that contains the tasks.
   */
  private void setupLeftTopTable() {
    this.getTaskTableView().setPlaceholder(new Label("No tasks to display"));
    this.getTaskTableView().setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<Task, String> titleColumn = new TableColumn<>("Task Title");
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));

    TableColumn<Task, BooleanProperty> statusColumn = new TableColumn<>("Done");
    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    statusColumn.setCellFactory(col -> new CheckBoxTableCell<>(index -> {
      BooleanProperty active = new SimpleBooleanProperty(
          this.getTaskTableView().getItems().get(index).getStatus());
      active.addListener((obs, wasActive, isNowActive) -> {
        Task task = this.getTaskTableView().getItems().get(index);
        task.setActiveStatus(isNowActive);

        // Extra checks if the hide completed Tasks mode is turned on
        this.controller.checkTaskVisibility(task, this);
      });
      return active;
    }));
    statusColumn.setMinWidth(34);
    statusColumn.setMaxWidth(34);


    TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
    priorityColumn.setEditable(true);
    priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
    priorityColumn.setCellFactory(ComboBoxTableCell.forTableColumn("High", "Medium", "Low"));
    priorityColumn.setOnEditCommit((TableColumn.CellEditEvent<Task, String> e) -> {
      // new value coming from combobox
      String newValue = e.getNewValue();

      // index of editing task in the tableview
      int index = e.getTablePosition().getRow();

      // task currently being edited
      Task task = e.getTableView().getItems().get(index);

      task.setPriority(newValue);

      // Refresh the table after the priority has changed
      this.refreshTable();
    });
    priorityColumn.setComparator(new PriorityComparator());


    ContextMenu contextMenu = this.setupTableContextMenu();

    // Set context menu on row, but use a binding to make it only show for non-empty rows
    this.getTaskTableView().setRowFactory(tableView -> {
      TableRow<Task> row = new TableRow<>();
      row.contextMenuProperty().bind(
              Bindings.when(row.emptyProperty())
                      .then((ContextMenu) null)
                      .otherwise(contextMenu)
      );
      return row;
    });

    // set on left click action
    this.getTaskTableView().setOnMouseClicked((MouseEvent event) -> {
      Task task = this.getTaskTableView().getSelectionModel().getSelectedItem();

      if ((event.getButton().equals(MouseButton.PRIMARY)
          || event.getButton().equals(MouseButton.SECONDARY))
              && task != null) {
        this.controller.displayTask(task, this.getTaskTitleTextField(),
            this.getHtmlEditor(), this.getDueDateButton(), this.getDateLabel());

        this.enableCenterPane();
        this.refreshTable();

        if (event.getButton().equals(MouseButton.PRIMARY)) {
          contextMenu.hide();
        }
      }
    });

    // Make the table editable to allow the user to directly change the Task active status
    this.getTaskTableView().setEditable(true);

    this.getTaskTableView().setItems(this.controller.getTaskListWrapper());
    this.getTaskTableView()
        .getColumns().addAll(Arrays.asList(titleColumn, priorityColumn, statusColumn));
    // Set a default sort column
    this.getTaskTableView().getSortOrder().add(titleColumn);
  }

  /**
   * Sets up a ContextMenu containing the MenuItems for the left TableView.
   *
   * @return an already set-up ContextMenu for the left TableView
   */
  private ContextMenu setupTableContextMenu() {
    ContextMenu contextMenu = new ContextMenu();

    MenuItem markAsComplete = new MenuItem("Mark as complete");
    markAsComplete.setOnAction(e -> {
      Task task = this.controller.getCurrentlySelectedTask();
      if ((task != null) && !task.getStatus()) {
        task.setActiveStatus(true);
        this.controller.checkTaskVisibility(task, this);
      }
    });
    // Create a Binding that disables a button if the task is already marked as completed
    BooleanBinding taskCompletedBinding = Bindings.createBooleanBinding(() -> {
      boolean buttonDisabled = false;
      Task task = this.getTaskTableView().getSelectionModel().getSelectedItem();
      if ((task == null) || task.getStatus()) {
        buttonDisabled = true;
      }
      return buttonDisabled;
    }, this.getTaskTableView().getSelectionModel().selectedItemProperty());
    markAsComplete.disableProperty().bind(taskCompletedBinding);

    MenuItem markAsIncomplete = new MenuItem("Mark as incomplete");
    markAsIncomplete.setOnAction(e -> {
      Task task = this.controller.getCurrentlySelectedTask();
      if ((task != null) && task.getStatus()) {
        task.setActiveStatus(false);
        this.controller.checkTaskVisibility(task, this);
      }
    });
    // Create a Binding that disables a button if the task is already marked as not completed
    BooleanBinding taskNotCompletedBinding = Bindings.createBooleanBinding(() -> {
      boolean buttonDisabled = false;
      Task task = this.getTaskTableView().getSelectionModel().getSelectedItem();
      if ((task == null) || !task.getStatus()) {
        buttonDisabled = true;
      }
      return buttonDisabled;
    }, this.getTaskTableView().getSelectionModel().selectedItemProperty());
    markAsIncomplete.disableProperty().bind(taskNotCompletedBinding);

    SeparatorMenuItem separator1 = new SeparatorMenuItem();

    MenuItem setDueDateMenu = new MenuItem("Set due date");
    setDueDateMenu.setOnAction(e -> this.controller.doSetNewEndDate(this.getDateLabel()));

    SeparatorMenuItem separator2 = new SeparatorMenuItem();

    MenuItem deleteTaskMenu = new MenuItem("Delete task");
    deleteTaskMenu.setOnAction(e -> this.deleteReminderAction());

    contextMenu.getItems().addAll(markAsComplete, markAsIncomplete, separator1,
        setDueDateMenu, separator2, deleteTaskMenu);
    return contextMenu;
  }

  /**
   * Sets up a Vbox containing the bottom left buttons.
   *
   * @return an already set-up Vbox containing the bottom left buttons
   */
  private VBox setupBottomLeftButtons() {
    VBox buttonBox = new VBox();

    Button newReminderButton = new Button("New Reminder");
    newReminderButton.setTooltip(new Tooltip("Create a New Reminder"));
    newReminderButton.setOnAction(e -> {
      this.controller.showNewReminderDialog();
      this.refreshTable();
    });
    newReminderButton.setPrefWidth(LEFT_PANE_WIDTH);
    newReminderButton.setAlignment(Pos.CENTER);

    ImageView plusIcon = ImageLoader.getInstance().getImage("plus-icon");
    if (plusIcon != null) {
      plusIcon.setFitHeight(12);
      newReminderButton.setGraphic(plusIcon);
    }


    Button deleteReminderButton = new Button("Delete Reminder");
    deleteReminderButton.setTooltip(new Tooltip("Delete Selected Reminder"));
    deleteReminderButton.setPrefWidth(LEFT_PANE_WIDTH);
    deleteReminderButton.setAlignment(Pos.CENTER);
    deleteReminderButton.setOnAction(e -> this.deleteReminderAction());

    ImageView trashIcon = ImageLoader.getInstance().getImage("trash-icon");
    if (trashIcon != null) {
      trashIcon.setFitHeight(12);
      deleteReminderButton.setGraphic(trashIcon);
    }

    buttonBox.getChildren().addAll(newReminderButton, deleteReminderButton);
    return buttonBox;
  }

  /**
   * Sets up the center node.
   *
   * @return The already set-up center node
   */
  private VBox setupCenter() {
    VBox vBox = new VBox();

    HBox hBox = this.setupTopCenterHBox();
    HBox bottomZoomHBox = this.setupBottomZoomButtonBox();

    vBox.getChildren().addAll(hBox, this.getHtmlEditor(), bottomZoomHBox);
    VBox.setVgrow(this.getHtmlEditor(), Priority.ALWAYS);

    // Make sure that the internal WebView of the HtmlEditor always fills it's parent, bug in JavaFX
    WebView webview = (WebView) this.getHtmlEditor().lookup(".web-view");
    if (webview != null) {
      GridPane.setHgrow(webview, Priority.ALWAYS);
      GridPane.setVgrow(webview, Priority.ALWAYS);
    }

    return vBox;
  }

  /**
   * Sets up the top center HBox.
   *
   * @return The already set-up top center HBox
   */
  private HBox setupTopCenterHBox() {
    HBox hBox = new HBox();

    this.getDueDateButton().setOnAction(e -> this.controller.doSetNewEndDate(this.getDateLabel()));
    this.getDueDateButton().setPrefWidth(150);
    this.getDueDateButton().setMaxHeight(30);
    this.getDueDateButton().setAlignment(Pos.CENTER);

    this.taskTitleTextField.setStyle("-fx-font-size: 20");
    this.taskTitleTextField.setPrefHeight(40);
    this.taskTitleTextField.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

    VBox dateBox = new VBox();
    dateBox.setAlignment(Pos.CENTER);
    dateBox.getChildren().addAll(this.getDateLabel(), this.getDueDateButton());

    HBox.setHgrow(this.taskTitleTextField, Priority.ALWAYS);
    hBox.getChildren().addAll(this.taskTitleTextField, dateBox);
    return hBox;
  }

  /**
   * Sets up the bottom zoom HBox.
   *
   * @return The already set-up bottom zoom HBox
   */
  private HBox setupBottomZoomButtonBox() {
    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER_RIGHT);

    hBox.setPadding(new Insets(0, 15, 0, 15));
    hBox.setSpacing(10);
    hBox.setStyle("-fx-background-color: #d0d0d0");

    Button zoomOutButton = new Button("Zoom out");
    zoomOutButton.setOnAction(e -> this.zoomOutAction());
    ImageView zoomOutIcon = ImageLoader.getInstance().getImage("zoom-out");
    if (zoomOutIcon != null) {
      zoomOutIcon.setFitHeight(10);
      zoomOutButton.setGraphic(zoomOutIcon);
    }

    Button zoomInButton = new Button("Zoom in");
    zoomInButton.setOnAction(e -> this.zoomInAction());
    ImageView zoomInIcon = ImageLoader.getInstance().getImage("zoom-in");
    if (zoomOutIcon != null) {
      zoomInIcon.setFitHeight(10);
      zoomInButton.setGraphic(zoomInIcon);
    }

    this.getZoomLabel().setText("100%");
    this.getZoomLabel().setAlignment(Pos.CENTER_RIGHT);

    hBox.getChildren().addAll(zoomOutButton, zoomInButton, this.getZoomLabel());
    return hBox;
  }

  /**
   * Performs the zoom in action.
   */
  private void zoomInAction() {
    this.controller.doZoom(this.getHtmlEditor(), this.getZoomLabel(), 0.1);
  }

  /**
   * Performs the zoom out action.
   */
  private void zoomOutAction() {
    this.controller.doZoom(this.getHtmlEditor(), this.getZoomLabel(), -0.1);
  }

  /**
   * Performs the delete Task action.
   */
  private void deleteReminderAction() {
    boolean taskDeleted = this.controller.doDeleteReminder();
    if (taskDeleted) {
      this.disableCenterPane();
      this.refreshTable();
    }
  }

  /**
   * Clears the text of the given Control.
   *
   * @param control The Control item to clear the text from,
   *                can be a Button, TextArea, TextField or Label
   */
  private void clearControlText(Control control) {
    if (control != null) {
      if (control instanceof Labeled) {
        ((Labeled) control).setText("");
      }
      if (control instanceof TextInputControl) {
        ((TextInputControl) control).clear();
      }
      if (control instanceof HTMLEditor) {
        ((HTMLEditor) control).setHtmlText("");
      }
    }
  }

  /**
   * Disables and clears the text of center pane's TextFields, Buttons and Labels.
   */
  public void disableCenterPane() {
    this.clearControlText(this.getTaskTitleTextField());
    this.clearControlText(this.getHtmlEditor());
    this.clearControlText(this.getDueDateButton());
    this.clearControlText(this.getDateLabel());

    this.root.getCenter().setDisable(true);
  }

  /**
   * Enables the center pane.
   */
  private void enableCenterPane() {
    this.root.getCenter().setDisable(false);
  }

  /**
   * Refreshes the table and forces a sort of the data.
   */
  public void refreshTable() {
    this.getTaskTableView().refresh();
    this.getTaskTableView().sort();
  }

  // -----------------------------------------------------------
  //    GETTERS
  // -----------------------------------------------------------

  /**
   * Returns the editor.
   *
   * @return The HTML editor
   */
  public HTMLEditor getHtmlEditor() {
    return this.htmlEditor;
  }

  /**
   * Returns the Task title TextField.
   *
   * @return The Task title TextField
   */
  public TextField getTaskTitleTextField() {
    return this.taskTitleTextField;
  }

  /**
   * Returns the left TableView of Tasks.
   *
   * @return The left TableView of Tasks
   */
  public TableView<Task> getTaskTableView() {
    return this.taskTableView;
  }

  /**
   * Returns the due date Button.
   *
   * @return The due date Button
   */
  private Button getDueDateButton() {
    return this.dueDateButton;
  }

  /**
   * Returns the date Label.
   *
   * @return The date Label
   */
  private Label getDateLabel() {
    return this.dateLabel;
  }

  /**
   * Returns the zoom percentage Label.
   *
   * @return The zoom percentage Label
   */
  private Label getZoomLabel() {
    return this.zoomLabel;
  }
}
