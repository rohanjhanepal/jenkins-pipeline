package project.toDoListApp.utility;

import java.util.Comparator;

/**
 * Class PriorityComparator represents a custom Comparator that enables
 * precise control over the sort order of String priorities.
 */
public class PriorityComparator implements Comparator<String> {

  /**
   * PriorityComparator constructor.
   */
  public PriorityComparator() {
  }

  /**
   * Returns an int representing the given priority.
   *
   * @param priority The priority String to check, not blank or null
   * @return The int representing the given priority
   * @throws IllegalArgumentException If the given String is blank or null
   */
  private int convertPriority(String priority) {
    if (priority == null || priority.isBlank()) {
      throw new IllegalArgumentException("Not a priority: " + priority);
    }

    switch (priority.toLowerCase()) {
      case "low":
        return 0;
      case "medium":
        return 1;
      case "high":
        return 2;
      default:
        throw new IllegalArgumentException("Not a priority: " + priority);
    }
  }

  @Override
  public int compare(String o1, String o2) {
    // Guard conditions
    if (o1 == null || o2 == null) {
      return -1;
    }
    if (o1.isBlank() || o2.isBlank()) {
      return -1;
    }

    return Integer.compare(this.convertPriority(o1), this.convertPriority(o2));
  }
}