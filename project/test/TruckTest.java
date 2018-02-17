import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TruckTest {
  /**A Test Truck*/
  public Truck truck;
  /**A Test Simulator*/
  public Simulator simulator;
  
  @Before
  public void setup() throws IOException {
    simulator = new Simulator();
    truck = new Truck();
    ArrayList<Order> fourOrders = new ArrayList<Order>();
    fourOrders.add(new Order("S", "White"));
    fourOrders.add(new Order("S", "White"));
    fourOrders.add(new Order("S", "White"));
    fourOrders.add(new Order("S", "White"));
    for (int i = 0; i < 20; i++){
      truck.loadPallet(fourOrders);
    truck.exportOrders();
    }
  }
  
  @After
  public void cleanup() {
    System.setOut(null);
  }
  /**Test for if Truck is full after adding 20 pick Requests.*/
  @Test
  public void testIsFull() {
    boolean expected = true;
    boolean actual = truck.isFull();
    assertEquals(expected, actual);
  }
  /**Test if Truck ID is assigned properly*/
  @Test
  public void testGetId() {
    int expected = 0;
    int actual = truck.getTruckId();
    assertEquals(expected, actual);
  }
  
  /**Test for the out put file orders.csv*/
  @Test
  public void testOutput() throws IOException {
    truck.exportOrders();
    Scanner scanner = new Scanner(new FileInputStream("orders0.csv"));
    String currentLine = scanner.nextLine();
    assertEquals("S, White", currentLine);
    scanner.close();
  }
  
}
