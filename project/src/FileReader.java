import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {

  public static ArrayList<String> readFromFile(String filePath) throws FileNotFoundException {
    Scanner scanner;
    Simulator.fm.logInfo(3, "File Manager: Opening file at " + filePath);
    scanner = new Scanner(new FileInputStream(filePath));
    ArrayList<String> data = new ArrayList<String>();
    // Writing spreadsheet rows to an ArrayList
    String currentLine;
    while (scanner.hasNextLine()) {
      currentLine = scanner.nextLine();
      // Skips comments and adds non-empty lines (((TODO: adding empty lines?)))
      if (currentLine.length() > 1 && currentLine.charAt(0) != '#') {
          data.add(currentLine);
          
      }
    }
    scanner.close();
    return data;
  }
}