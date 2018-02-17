import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StockRoomTest {
  /**A Test StockRoom.*/
  public StockRoom stockroom;
  /**A Test Simulator.*/
  public Simulator simulator;
  
  @Before
  public void setup() throws FileNotFoundException {
    simulator = new Simulator();
    File file = new File(System.getProperty("user.dir"), "traversal_table.csv"); 
    String path = file.getPath();
    if (!Simulator.fm.checkFile(path)) {
      fail("File Not Found");
    }
    ArrayList<String> traversal = Simulator.fm.readFromFile(path);
    ArrayList<String> initial = Simulator.fm.readFromFile("initial.csv");
    stockroom = new StockRoom(traversal, initial);
  }
  
  @After
  public void cleanup() {
    simulator = null;
    System.out.println("StockRoom unit testing has finished.");
  }
  
  /**Test for translationing a location with SKU#.*/
  @Test
  public void testGetLocation() {
    String expected = "B 0 0 1 26";
    String actual = stockroom.getLocation("26");
    assertEquals(expected, actual);
    actual = stockroom.getLocation("24");
    assertNotEquals(expected, actual);
  }
  /**Test for checking if warehouse has a specifice SKU number stored.*/
  @Test
  public void testHasSku() {
    boolean actual = stockroom.hasSku("1");
    assertEquals(true, actual);
    actual = stockroom.hasSku("A");
    assertEquals(false, actual);
  }
  /**Test if taking a product from warehouse updates stock.*/
  @Test
  public void testTakeProduct() {
    boolean expected = true; //SKUs 1 is taken from 
    boolean actual = stockroom.takeProduct("1");
    assertEquals(expected, actual);
    int i = 0;
    while (i < 30) {
      stockroom.takeProduct("1");
      i++;
    }
    expected = false;
    actual = stockroom.takeProduct("A1");
    assertEquals(expected, actual); 
  }
  /**Test if replenishing stock is reflected correctly.*/
  @Test
  public void testReplenish() {
    int actual = stockroom.getMap().get("1").getStock();
    assertEquals(30, actual);
    stockroom.replenish("A 0 0 0");;
    actual = stockroom.getMap().get("1").getStock();
    assertEquals(55, actual);
  }

  /**Test if the output final file is written correctly.*/
  @Test
  public void testOutputFinal() throws IOException {
    stockroom.csvExport();
    Scanner scanner = new Scanner(new FileInputStream("final0.csv"));
    String currentLine = scanner.nextLine();
    assertEquals("A,0,0,0,30", currentLine);
    scanner.close();
  }
  
}
