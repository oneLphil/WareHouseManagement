import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;


public class WarehouseTest {
  // set up the Warehouse for testing.
  public Warehouse wh1;
  public Warehouse wh2;
  public Warehouse wh3;
  public Warehouse wh4;
  public Warehouse wh5;
  public Warehouse wh6;
  
  
  
  // set up the Events Array for Testing
  public ArrayList<String> event1 = new ArrayList<String>();
  public ArrayList<String> event2 = new ArrayList<String>();
  public ArrayList<String> event3 = new ArrayList<String>();
  public ArrayList<String> event4 = new ArrayList<String>();
  public ArrayList<String> event5 = new ArrayList<String>();
  public ArrayList<String> event6 = new ArrayList<String>();
  
  
  // set up the Order object for Testing
  Order order = new Order("Blue", "SES");
  
  // set up the entry for creating warehouse class
  public ArrayList<String> init = new ArrayList<String>();
  public ArrayList<String> trans = new ArrayList<String>();
  public ArrayList<String> triv = new ArrayList<String>();
  
  // set up the FileManager for file reading
  public FileManager fm ;

  @Before
  public void setUp() throws IOException {
    fm = new FileManager("infolog.txt");
    init = fm.readFromFile("initial.csv");
    trans = fm.readFromFile("translation.csv");
    triv = fm.readFromFile("traversal_table.csv");

    event1.add("Order SES Blue");
    event2.add("Picker Alice ready");
    event3.add("Picker Alice ready");
    event4.add("Picker Alice ready");
    event5.add("Picker Alice ready");
    event2.add("Picker Alice scans 21");
    event3.add("Picker Alice rescans");
    event4.add("Picker Alice completes");
    event5.add("Picker Alice discards");
    
    event6.add("Replenisher Alice replenish A 1 1 1");

    wh1 = new Warehouse(event1, init, triv, trans);
    wh2 = new Warehouse(event2, init, triv, trans);
    wh3 = new Warehouse(event3, init, triv, trans);
    wh4 = new Warehouse(event4, init, triv, trans);
    wh5 = new Warehouse(event5, init, triv, trans);
    wh6 = new Warehouse(event6, init, triv, trans);
  }

  @Test
  public void testOrder() throws IOException {
    wh1.runWarehouse();
    assertTrue(fm.getLastLine().equals("SES Blue"));
  }

  @Test
  public void testPickerReady() throws IOException {
    wh2.runWarehouse();
    assertTrue(fm.getLastLine().contains("ready") || fm.getLastLine().contains("busy"));
  }

  @Test
  public void testPickerScans() throws IOException {
    wh2.runWarehouse();
    assertTrue(fm.getLastLine().contains("Please"));
  }

  @Test
  public void testPickerRescans() throws IOException {
    wh3.runWarehouse();
    assertTrue(fm.getLastLine().contains("RESCANS"));
    
  }

  @Test
  public void testPickerComlpete() throws IOException {
    wh4.runWarehouse();
    assertTrue(fm.getLastLine().equals("Picker Alice has deposited his collection of product at Marshalling.") || 
        fm.getLastLine().equals("Error: Picker Alice has not finished fulfilling their pick job.") );
  }
  
  @Test
  public void testPickerDiscards() throws IOException {
    wh5.runWarehouse();
    assertTrue(fm.getLastLine().contains("Discard"));
  }
  
  

  @Test
  public void testReplenisher() throws IOException {
    wh6.runWarehouse();
//    verify(wh3.getStockRoom()).replenish("A 1 1 1");
    assertTrue(fm.getLastLine().contains("replenish"));
  }

  @Test
  public void testOHGetter() {
    assertThat(wh1.getOrderHandler(), instanceOf(OrderHandler.class));
  }

  @Test
  public void testSRGetter() {
    assertThat(wh1.getStockRoom(), instanceOf(StockRoom.class));
  }

  @Test
  public void testMAGetter() {
    assertThat(wh1.getMarshalling(), instanceOf(Marshalling.class));
  }

  @Test
  public void testWKGetter() {
    assertTrue(wh2.getWorker("Alice").getName().equals("Alice"));
  }

}
