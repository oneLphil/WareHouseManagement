import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/** The Simulator's File Management system.
 * 
 * <p>Manages file reading and writing tasks as well as Logger inputs from
 * the Simulation's many classes.
 * 
 * @author Yuesheng (Editor: Tyson)
 *
 */
public class FileManager {
  private Handler consoleHandler = new ConsoleHandler();
  private Handler fileHandler;
  /** The last string received by logInfo, used for debugging. */
  private String lastLine = "";
  private Logger simLog = Logger.getLogger(this.getClass().getName());
  /** Constructor of the FileManager class.
   *
   * @param filePath
   *     The path of the logging output file.
   * @throws IOException 
   *     An I/O file reading error.
   */
  public FileManager(String filePath) throws IOException {
    simLog.setLevel(Level.ALL);
    consoleHandler.setLevel(Level.ALL);
    simLog.addHandler(consoleHandler);
    File output = new File(filePath);
    if (! output.exists()) {
      output.createNewFile();
    }
    fileHandler = new FileHandler(filePath);
    simLog.addHandler(fileHandler);
  }

  /** Checks if the specified file exists.
   * 
   * @param filePath
   *      The path to the specified file.
   * @return boolean
   *      Does the file exist?
   */
  public boolean checkFile(String filePath) {
    File file = new File(filePath);
    return file.exists();
  }

  /** Reads a file from a given filePath, if the file doesn't exist, then log the
   * information.
   * 
   * @param filePath
   *     The path of the input file.
   * @return 
   *     The content of the input file, if any.
   */
  public ArrayList<String> readFromFile(String filePath) {
    Scanner scanner;
    try { // Opening the file...
      Simulator.fm.logInfo(3, "File Manager: Opening file at " + filePath);
      scanner = new Scanner(new FileInputStream(filePath));
    } catch (FileNotFoundException exception) { // File does not exist!
      this.logException(exception, "Error: The specified input file was not found.");
      return null;
    }
    Simulator.fm.logInfo(3, "File Manager: File opened!");
    ArrayList<String> data = new ArrayList<String>();

    // Writing spreadsheet rows to an ArrayList
    Simulator.fm.logInfo(3, "File Manager: Writing file contents to ArrayList...");
    String currentLine;
    while (scanner.hasNextLine()) {
      currentLine = scanner.nextLine();

      // Skips comments and adds non-empty lines (((TODO: adding empty lines?)))
      if (currentLine.length() > 1 && currentLine.charAt(0) != '#') {
//        if (currentLine.matches("\\S")){
          data.add(currentLine);
//        }
      }
    }
    Simulator.fm.logInfo(3, "File Manager: Finished! Closing file.");
    scanner.close();
    return data;
  }

  /** Splits Strings in an ArrayList with space characters.
   * 
   * @param info
   *    An ArrayList of String information 
   * @return 
   *    The formatted ArrayList.
   */
  public ArrayList<String[]> formatInfo(ArrayList<String> info) {
    ArrayList<String[]> output = new ArrayList<String[]>();
    for (String i : info) {
      output.add(i.split(" "));
    }
    return output;
  }

  /** Logs the given String info in the console and the log output file.
   * 
   * @param info
   *     A String of information to be logged.
   */
  public void logInfo(int severity, String info) {
    lastLine = info;
    System.out.println(info);
    Level level; // Default value
    switch (severity) {
      case 1: level = Level.SEVERE;
              break;
      case 2: level = Level.WARNING;
              break;
      case 3: level = Level.INFO;
              break;
      default: level = Level.ALL;
              break;
    }
    simLog.log(level, info);
  }
  
  /** Logs the given exception to the console and the log output file.
   * 
   * @param exception
   *    The Exception that occurred.
   * @param info
   *    A String describing the exception.
   */
  public void logException(Exception exception, String info) {
    System.out.println(info);
    simLog.log(Level.SEVERE, info, exception);
    exception.printStackTrace();
  }

  /** Returns the last string received by logInfo method. 
   * 
   * @return lastLine
   *     The last string received by logInfo
   */ 
  public String getLastLine() {
    return lastLine;
  }
}
