import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SequencerTest {
  /** Instance of sequencer. */
  private Sequencer seq;
  private Warehouse warehouse;
  private ArrayList<String> skuList;
  private Simulator sim;
  
  @Before
  public void setup() throws FileNotFoundException {
    sim = new Simulator();
    ArrayList<String> traversal = Simulator.fm.readFromFile("traversal_table.csv");
    ArrayList<String> initial = Simulator.fm.readFromFile("initial.csv");
    ArrayList<String> order = Simulator.fm.readFromFile("16orders.txt");
    ArrayList<String> trans = Simulator.fm.readFromFile("translation.csv");
    warehouse = new Warehouse(order, initial, traversal, trans);    
    seq = new Sequencer("seq", warehouse);
    
    List<String> skuList = new ArrayList<>();
    skuList.add("27");
    skuList.add("11");
    skuList.add("21");
    skuList.add("37");
    skuList.add("28");
    skuList.add("12");
    skuList.add("22");
    skuList.add("38");
    seq.setExpected((ArrayList<String>) skuList);

    List<String> locations = new ArrayList<String>(); // sequencer doesn't need
    ArrayList<Order> orders = new ArrayList<>(); // sequencer doesn't need    
    
    PickRequest pickRequest = new PickRequest(locations, skuList, orders, 1);
    seq.setPickRequest(pickRequest);
  }
  
  @After
  public void cleanup() {
    sim = null;
    System.out.println("Sequencer unit testing has finished.");
  }
  
  @Test
  public void testSequencerGetName() {
    Sequencer sequencerTest = new Sequencer("Sue", warehouse);
    String expected = "Sue";
    String actual = sequencerTest.getName();
    assertEquals(expected, actual);
  }
  
  @Test
  public void testSetExpected() {
    seq.setExpected(skuList);
    assertEquals(skuList, seq.getExpected());
    
    seq.setExpected(null);
    assertEquals(null, seq.getExpected());
  }

  @Test
  public void testScan() {
    // test correct scans
    assertEquals(0, seq.position);
    seq.scanSku("27");
    seq.scanSku("11");
    seq.scanSku("21");
    seq.scanSku("37");
    assertEquals(4, seq.position);
    seq.scanSku("28");
    seq.scanSku("12");
    seq.scanSku("22");
    seq.scanSku("38");
    
    //test reset/rescan
    assertEquals(8, seq.position);
    seq.resetScan();
    assertEquals(0, seq.position);
    
    //test wrong scan
    seq.scanSku("27");
    seq.scanSku("11");
    seq.scanSku("21");
    seq.scanSku("37");
    assertEquals(4, seq.position);
    seq.scanSku("99"); // wrong scan
    assertEquals(4, seq.position);
  }
  
  @Test
  public void testReady() {
    assertEquals(false, seq.busy);
    seq.busy = true;
    assertEquals(true, seq.busy);
  }
  


}
