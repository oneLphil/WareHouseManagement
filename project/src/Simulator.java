import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import exception.BadFileNameException;

/** A class in charge of running all Warehouse simulations and preparing all
 * information to be read by the Warehouse and it's subservient classes.
 * 
 * @author Yuesheng (Editor: Tyson)
 *
 */
public class Simulator {

  /**A counter to keep track of the number of Warehouses in the Simulation.
   * 
   * <p>Used to determine which Warehouse is which when files are saved.
   */
  private static int warehouseCounter = 0;
  /** An array containing the ID's of all Warehouses in the system. */
  private static ArrayList<Warehouse> warehouseList = new ArrayList<Warehouse>();
  /**A FileManager in charge of reading and logging for this simulator. */
  static FileManager fm;
  /** A String to indicate where does the simulator get its input */
  static String readfrom;

  /** Initializes the Simulator and creates a FileManager class. */
  public Simulator() {
    try { // Creating FileManager / Logger
      fm = new FileManager("infolog.txt");
    } catch (IOException exception) {
      // No logger available to handle exception, process crash.
      exception.printStackTrace();
    }
  }

  /** Runs the Warehouse Simulator.
   * 
   * <p>Warehouses are created, then run sequentially.
   * 
   * @param args
   *      Arguments to pass to the main() method. Currently not utilized.
   * @throws IOException
   *      Indicates that an I/O exception has occurred.
   */
  public static void main(String[] args) throws IOException {
    Simulator simulator = new Simulator();
    fm.logInfo(3, "::::::::::Warehouse Simulator 2017::::::::::"); 
    String file0 = "settings.txt";
    String file1 = "TestFile1.txt"; // Default files (used when running demo version)
    String file2 = "initial.csv";
    String file3 = "traversal_table.csv";
    String file4 = "translation.csv";
    // boolean useSettings = false; // TODO: Switch back to this if you want demo version
    if (args.length > 0) { // Argument(s) supplied
      // Check settings.txt
      if (args[0].matches("^\\w+\\.txt$")) {
        file0 = args[0];
        readfrom = "console";
      } else { // Invalid filename, use default.
        fm.logInfo(2, "Simulator: Supplied settings filename invalid, defaulting to settings.txt.");
        readfrom = "default";
      }
    }
    boolean useSettings = fm.checkFile(file0); // Check for settings.txt file
    warehouseCounter = 0;
    fm.logInfo(3, "Simulator: Generating Warehouses...");
    if (useSettings) {
      fm.logInfo(3, "Simulator: Detected " + file0 + ", reading...");
      simulator.readSetting(file0);
    } else {
      fm.logInfo(2, "Simulator: Warehouse Simulator 2017 could not access " + file0  
          + " Please ensure settings.txt is in the root folder. See help.txt for"
          + " more information. Running demo simulation...");
      simulator.generateWarehouse( // Makes a single warehouse
          file1, file2, file3, file4);
    }
    fm.logInfo(3, "Simulator: Opening Simulation: Beginning with Warehouse 0...");
    for (Warehouse i : warehouseList) { // Runs the Warehouses
      i.runWarehouse();
      fm.logInfo(3, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
      fm.logInfo(3, "############################################");
      fm.logInfo(3, "Warehouse " + warehouseCounter + " closing...");
      warehouseCounter++;
    }
    fm.logInfo(3, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    fm.logInfo(3, "############################################");
    fm.logInfo(3, "All Warehouses simulated. Closing program...");
  }

  /** Generates Warehouses depending on the contents of settings.txt
   * 
   * <p>Precondition: if setting.txt exists, then the content of the file 
   * must match the form specified in help.txt:
   * 
   * <p>Warehouse (event) (initial) (traversal) (translation) (root)
   * 
   * @param settingPath
   *      The path to the settings.txt file.
   * @throws FileNotFoundException 
   *      Error thrown when a file that is read does not exist.
   */
  private void readSetting(String settingPath) throws FileNotFoundException {
    ArrayList<String[]> setting = fm.formatInfo(fm.readFromFile(settingPath));
    int lineNum = 0;
    for (String[] i : setting) {
      if (i.length > 0) { // Checks that the line isn't empty
        
        // Confirming file names in settings.txt...
        if (!i[0].matches("^\\w+\\.txt$")) { // Checking Orders.txt filename
          fm.logException(new BadFileNameException(), "Error: The specified order.txt"
              + " filename on line " + lineNum + " is invalid. Please refer to help.txt.");
        }
        if (!i[1].matches("^\\w+\\.csv$")) { // Checking initial.csv filename
          fm.logException(new BadFileNameException(), "Error: The specified initial.csv"
              + " filename on line " + lineNum + " is invalid. Please refer to help.txt.");
        }
        if (!i[2].matches("^\\w+\\.csv$")) { // Checking traversal.csv filename
          fm.logException(new BadFileNameException(), "Error: The specified traversal.csv"
              + " filename on line " + lineNum + " is invalid. Please refer to help.txt.");
        }
        if (!i[3].matches("^\\w+\\.csv$")) { // Checking translation.csv filename
          fm.logException(new BadFileNameException(), "Error: The specified translation.csv"
              + " filename on line " + lineNum + " is invalid. Please refer to help.txt.");
        }
        
        // Confirm supplied directory...
        i[4] = System.getProperty("user.dir") + i[4].replaceAll("=", ""); 

        // Confirm usage of correct seperator
        String seperator = System.getProperty("file.separator");
        if (seperator == "\\") {
          i[4].replace("/", "\\");
        } else if (seperator == "/") {
          i[4].replace("\\", "/");
        } // Checking directory.
        File file = new File(i[4]);
        if (!file.isDirectory()) {
          fm.logException(new BadFileNameException(), "Error: The specified directory"
              + " on line " + lineNum + " is invalid. Please refer to help.txt.");
        }
        
        // Appending directory to filenames..
        for (int j = 0; j < 4; j++) { 
          file = new File(i[4], i[j]);
          i[j] = file.getPath();
        } // Creates a Warehouse using these input locations...
        this.generateWarehouse(i[0], i[1], i[2], i[3]);
        
      } else {
        // TODO: STUB - any method for ignored lines
      }
      lineNum++;
    }
  }

  /**
   * Creates a warehouse if the specified files exist.
   * 
   * @param orderFilePath
   *      The path to the events file (.txt).
   * @param initFilePath
   *      The path to the initial layout file (.csv).
   * @param traversalPath
   *      The path to the traversal file (.csv).
   * @param translationPath
   *      The path to the order translation file (.csv).
   * @throws FileNotFoundException 
   *      If a file doesn't exist, this error will be thrown.
   * @throws IOException
   *      Indicates that an I/O exception has occurred.
   */
  private void generateWarehouse(String orderFilePath, String initFilePath, String traversalPath,
      String translationPath) throws FileNotFoundException {
    ArrayList<String> order = fm.readFromFile(orderFilePath);
    ArrayList<String> traversal = fm.readFromFile(traversalPath);
    ArrayList<String> trans = fm.readFromFile(translationPath);
    ArrayList<String> initial = fm.readFromFile(initFilePath);
    if (order == null || traversal == null || trans == null) {
      fm.logInfo(1, "Warehouse Simulator 2017 could not access the necessary files " 
              + "to simulate Warehouse number " + warehouseCounter + ". Please check to see if the" 
              + " all files are in their specified locations.");
      // TODO: Exception??
    } else if (initial == null) {
      fm.logInfo(2, "Warehouse Simulator 2017 did not detect an initial stock file " 
            + "for Warehouse number " + warehouseCounter + ". The simulation will assume" 
            + " all shelves have been fully stocked.");
    } else {
      Warehouse house = new Warehouse(order, initial, traversal, trans);
      warehouseList.add(house);
    }
  }

  /** Write a string array into an CSV file with fileName.
   * 
   * @param entry
   *      The contents to be written to the file.
   * @param fileName
   *      The specified filename for the .csv file.
   * @throws IOException
   *      Indicates that an I/O exception has occurred.
   */
  protected static void writeFile(ArrayList<String> entry, String fileName) throws IOException {
    String[] fileArr = fileName.split("\\.");
    fileName = fileArr[0] + warehouseCounter + "." + fileArr[1];
    PrintWriter outFile = new PrintWriter(new FileWriter(fileName));
    for (String i : entry) {
      outFile.println(i);
    }
    outFile.close();
  }

}