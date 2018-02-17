import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

public class SystemTest {

  /** Test if the entire system runs with test file and output correctly.*/
  @Test
  public void testSystemRunning() throws IOException {
    Simulator.main(new String[] {"settings.txt"});
    Scanner scanner = new Scanner(new FileInputStream("orders1.csv"));
    String currentLine = scanner.nextLine();
    assertEquals("SES, Blue", currentLine);
    currentLine = scanner.nextLine();
    assertEquals("SES, Red", currentLine);
    currentLine = scanner.nextLine();
    assertEquals("SE, Beige", currentLine);
    scanner.close();
  }

}
