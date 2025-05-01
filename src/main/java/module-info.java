module project.toDoListApp {
  requires javafx.controls;
  requires javafx.web;
  requires java.logging;

  // Opens the package to "anyone" to enable access by reflection for the Jupiter test engine
  opens project.toDoListApp.model;

  exports project.toDoListApp.view;
}