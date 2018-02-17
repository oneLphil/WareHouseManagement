import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

class MyReader {
  public ArrayList<String> readLines(BufferedReader input) {
    return null;
  }
}

public class PickerTest {
  
  /** A test Picker. */
  public Picker picker;
  /** A typical Simulator. */
  public Simulator normSimulator;
  /** A typical Warehouse. */
  public Warehouse normWarehouse;
  /** Used for tracking the output stream.*/
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  // TODO: replace with logger info?
 
  @Before
  public void setup() throws FileNotFoundException {
    // Prep to track out stream...
    System.setOut(new PrintStream(outContent));
    normSimulator = new Simulator();
    // Building a standard Warehouse...
    ArrayList<String> events = Simulator.fm.readFromFile("TestFile1.txt");
    ArrayList<String> initial = Simulator.fm.readFromFile("initial.csv");
    ArrayList<String> traversal = Simulator.fm.readFromFile("traversal_table.csv");
    ArrayList<String> translation = Simulator.fm.readFromFile("translation.csv");
    normWarehouse = new Warehouse(events, initial, traversal, translation);
    picker = new Picker("Jeff", normWarehouse);
//    StringReader sr = new StringReader("Order S Beige" +
//      "Order SES Green" +
//      "Order SE White" +
//      "Picker Alice pick 1" +
//      "Order SE Silver" +
//      "Picker Alice pick 2" +
//      "Picker Alice pick 3" +
//      "Order SE Tan" +
//      "Order S Green" +
//      "Order SES Graphite" +
//      "Order S White"
//        );
//    BufferedReader br = new BufferedReader(sr);
//    MyReader myReader = new MyReader();
//    myReader.readLines(br);
    
    
  }
  
  @After
  public void cleanup() {
    normSimulator = null;
    System.out.println("Picker unit testing has finished.");
    System.setOut(null);
  }
  
  
  @Test
  public void testPickerGetName() {
    String expected = "Jeff";
    String actual = picker.getName();
    assertEquals(expected, actual);
  }
  
  @Test
  public void testPickerReady() {    
    OrderHandler orderHandler = normWarehouse.getOrderHandler();
    Order order = new Order("SES", "Red");
    for (int i = 0; i < 4; i++) { // Creating 8 generic orders
      orderHandler.addOrder(order);
    }
    picker.ready();
    assertEquals(1, picker.getPickNum());
    picker.printPickInfo();
  }
  
  @Test
  public void testPickerNotReady() {    
    // Test with scanning...
    picker.scanSku("123A");
    String lastLine = fetchLastError();
    assertEquals("Error: Picker Jeff is not ready to perform picks.", lastLine);
    // Test with order in possession...
    OrderHandler orderHandler = normWarehouse.getOrderHandler();
    Order order = new Order("SES", "Red");
    for (int i = 0; i < 4; i++) { // Creating 8 generic orders
      orderHandler.addOrder(order);
    }
    picker.ready();
    picker.ready();
    lastLine = fetchLastError();
    assertEquals("Error: Picker Jeff is currently busy fulfilling a pick request.", 
        lastLine);
  }
  
  @Test
  public void testPickerReadyNoOrder() {    
    picker.ready();
    assertEquals(-1, picker.getPickNum());
    assertNull(picker.getPickRequest());
  }
  
  @Test
  public void testPickerToMarshalling() {
    // TODO Send a picker to marshalling
    fail("Incomplete test case...");
  }
  
  @Test
  public void testPickerSetLargeRequest() {
    // TODO "A pick request too large to accept"
    fail("Incomplete test case...");
  }
  
  @Test
  public void testPickerNotInStockRoom() {
    // TODO "A pick that isn't actually in the stock room."
    fail("Incomplete test case...");
  }
  
  private String fetchLastError() {
    String paragraph = outContent.toString();
    String lastLine = paragraph.substring(paragraph.lastIndexOf("Error: "));
    lastLine = lastLine.replace("\n", "").replace("\r", "");
    return lastLine;
  }
}