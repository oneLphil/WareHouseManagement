import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OrderHandlerTest {
  
  /**A test Picker.*/
  public Picker picker;
  /**A test OrderHandler.*/
  public OrderHandler orderHandler;
  /**A test Warehouse.*/
  public Warehouse warehouse;
  /**A test Simulator.*/
  public Simulator simulator;
  
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  
  @Before
  public void setUp() {
    simulator = new Simulator();
    System.setOut(new PrintStream(outContent));
    ArrayList<String> traversal = Simulator.fm.readFromFile("traversal_table.csv");
    ArrayList<String> initial = Simulator.fm.readFromFile("initial.csv");
    ArrayList<String> translation = Simulator.fm.readFromFile("translation.csv");
    ArrayList<String> emptyEvent = new ArrayList<String>();
    warehouse = new Warehouse(emptyEvent, initial, traversal, translation);
    orderHandler = warehouse.getOrderHandler();
    picker = new Picker("Bob", warehouse);
    Order order = new Order("S", "White");
    for (int i = 0; i < 8; i++) {
      orderHandler.addOrder(order);
    }
  }
  
  @After
  public void cleanup() {
    simulator = null;
    System.setOut(null);
  }

  /**Test if the ProcessQueue is empty after adding 8 orders.*/
  @Test
  public void testProcessQueueIsEmpty() {
    boolean expected = false;
    boolean actual = orderHandler.getProcessQueue().isEmpty();
    assertEquals(expected, actual);
  }
  /**Test the size of process queue after adding 8 orders.*/
  @Test
  public void testProcessQueueSize() {
    int expected = 2;
    int actual = orderHandler.getProcessQueue().size();
    assertEquals(expected, actual);
  }
  /**Test if PickRequest is making the correct ID.*/
  @Test
  public void testPickRequestId() {
    int expected = 1;
    int actual = orderHandler.getProcessQueue().peek().getPickRequestId();
    assertEquals(expected, actual);
  }
  /**Test the order of SKUs is formatted correctly.*/
  @Test 
  public void testPickSkus() {
    String expected = "1";
    String actual = orderHandler.getProcessQueue().peek().getSkuPackage().get(0);
    assertEquals(expected, actual);
    expected = "2";
    actual = orderHandler.getProcessQueue().peek().getSkuPackage().get(5);
    assertEquals(expected, actual);
  }
  /**Test if pick locations are traversed correctly*/
  @Test
  public void testPickLocations() {
    String expected = "A 0 0 0 1";
    String actual = orderHandler.getProcessQueue().peek().getTraversalLocations().get(0);
    assertEquals(expected, actual);
    expected = "A 0 0 1 2";
    actual = orderHandler.getProcessQueue().peek().getTraversalLocations().get(5);
    assertEquals(expected, actual);
  }
  /**Test if adding priorityQueue method adds pickRequest back into queue.*/
  @Test
  public void testAddFirst() {
    int expected = 1;
    int actual = orderHandler.getProcessQueue().peek().getPickRequestId();
    assertEquals(expected, actual);
    expected = 2;
    PickRequest temp = orderHandler.getProcessQueue().pop();
    actual = orderHandler.getProcessQueue().peek().getPickRequestId();
    assertEquals(expected, actual);
    orderHandler.priorityQueue(temp);
    expected = 1;
    actual = orderHandler.getProcessQueue().peek().getPickRequestId();
    assertEquals(expected, actual);
  }
  /**Test if invalid orders work as intended.
   * Two case:
   * - Order is not in translation table.
   * - Order is in translation table but not in the warehouse*/
  @Test
  public void testInvalidOrders() {
    Order order = new Order("YU", "Rainbow");
    orderHandler.addOrder(order);
    String expected = "Invalid Order: Order not in Translation Table - Rainbow YU";
    String actual = fetchLastError("Invalid Order");
    assertEquals(expected, actual);
    Order order2 = new Order("S", "Gold");
    orderHandler.addOrder(order2);
    expected = "Invalid Order: SKUs not in Warehouse - 65 66";
    actual = fetchLastError("Invalid Order");
    assertEquals(expected, actual); 
  }
  /**Test if sending PickRequest to Picker works.*/
  @Test
  public void testSendPickRequest() {
    orderHandler.sendPickRequest(picker);
    int actual = picker.getPickNum();
    int expected = 1;
    assertEquals(expected, actual);
  }
  /**Test if there are no PickRequest would the error*/
  @Test
  public void testNoPickRequest() {
    while (!orderHandler.getProcessQueue().isEmpty()) {
      orderHandler.getProcessQueue().pop();
    }
    assertEquals(orderHandler.getProcessQueue().isEmpty(), true);
    orderHandler.sendPickRequest(picker);
    String actual = fetchLastError("No Pick");
    String expected = "No Pick Request Ready";
    assertEquals(expected, actual);
  }
  

  private String fetchLastError(String phrase) {
    String paragraph = outContent.toString();
    String lastLine = paragraph.substring(paragraph.lastIndexOf(phrase));
    lastLine = lastLine.replace("\n", "").replace("\r", "");
    return lastLine;
  }
}
