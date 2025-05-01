package project.toDoListApp.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;
import project.toDoListApp.model.TaskRegister;

/**
 * Class FileUtility represents a simple helper class that is tasked with
 * reading and writing objects to and from the disk.
 */
public class FileUtility {
  /**
   * The logger.
   */
  private final Logger logger;

  /**
   * FileUtility constructor.
   */
  public FileUtility() {
    this.logger = Logger.getLogger(this.getClass().toString());
  }

  /**
   * Save a binary version of the object to the given file.
   * If the file name is not an absolute path, then it is assumed
   * to be relative to the current project folder.
   *
   * @param destinationFile The file where the details are to be saved.
   * @param object          The object to be saved in binary version.
   * @throws IOException If the saving process fails for any reason.
   */
  public void saveToFile(String destinationFile, Object object) throws IOException {
    File dir = new File("tasks");
    if (!dir.exists()) {
      dir.mkdir();
    }
    File outputFile = new File(destinationFile);
    try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputFile))) {
      os.writeObject(object);
    }
  }

  /**
   * Read the binary version of an address book from the given file.
   * If the file name is not an absolute path, then it is assumed
   * to be relative to the current project folder.
   *
   * @param sourceFile The file from where the details are to be read.
   * @return The Task object.
   * @throws IOException If the reading process fails for any reason.
   */
  public Object readFromFile(String sourceFile) throws IOException, ClassNotFoundException {
    Object obj = null;
    try (FileInputStream fi = new FileInputStream(sourceFile);
         ObjectInputStream oi = new ObjectInputStream(fi)) {

      obj = oi.readObject();
    } catch (IOException e) {
      throw new IOException("Unable to make a valid filename for " + sourceFile);
    }
    return obj;
  }

  /**
   * Returns the saved TaskRegister from the disk if possible, returns a new TaskRegister otherwise.
   *
   * @return The saved TaskRegister instance from the disk
   */
  public TaskRegister getRegister() {
    TaskRegister register = null;

    try {
      register = (TaskRegister) this.readFromFile("tasks/savedTasks.txt");
    } catch (IOException | ClassNotFoundException ignored) {
    }

    if (register == null) {
      // The register is still null, wasn't loaded properly or no previous saves exist ->
      // Create a new register
      register = new TaskRegister();
    }

    return register;
  }
}
