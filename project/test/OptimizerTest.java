import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OptimizerTest {
  
  /**A test StockRoom.*/
  public StockRoom stockroom;
  /**A test Simulator.*/
  public Simulator simulator;
  /**A test initial final.*/
  public ArrayList<String> initial;
  
  @Before
  public void setUp() throws FileNotFoundException {
    simulator = new Simulator();
    ArrayList<String> traversal = Simulator.fm.readFromFile("traversal_table.csv");
    ArrayList<String> initial = Simulator.fm.readFromFile("initial.csv");
    stockroom = new StockRoom(traversal, initial);
  }

  @After
  public void cleanUp() {
    simulator = null;
  }
  
  /**Test the algorithm for sorting when SKUs are only numbers.*/
  @Test
  public void testNumberSortTranslation() {
    List<String> skus = new ArrayList<String>();
    skus.add("11");//Adding SKUs in reverse order
    skus.add("9");
    skus.add("25");
    String expected = "A 0 2 0 9";
    String actual = Optimizer.optimize(skus, stockroom).get(0);
    assertEquals(expected, actual);//Traversal locations sorted SKUs and 1st location is SKU# 9
    expected = "A 0 2 2 11";
    actual = Optimizer.optimize(skus, stockroom).get(1);
    assertEquals(expected, actual);
    expected = "B 0 0 0 25";
    actual = Optimizer.optimize(skus, stockroom).get(2);
    assertEquals(expected, actual);
  }
  
  /**Test the algorithm for sorting when SKUs are only numbers.*/
  @Test
  public void testLettersSortTranslation() {
    ArrayList<String> traversal = new ArrayList<String>();
    traversal.add("A 0 0 0 A15");
    traversal.add("A 0 0 1 B09");
    traversal.add("A 0 0 2 B67");
    stockroom = new StockRoom(traversal, initial);
    List<String> skus = new ArrayList<String>();
    skus.add("B09");//Adding SKUs in reverse order
    skus.add("B67");
    skus.add("A15");
    String expected = "A 0 0 0 A15";
    String actual = Optimizer.optimize(skus, stockroom).get(0);
    assertEquals(expected, actual);//Traversal locations sorted SKUs and 1st location is SKU# 9
    expected = "A 0 0 1 B09";
    actual = Optimizer.optimize(skus, stockroom).get(1);
    assertEquals(expected, actual);
    expected = "A 0 0 2 B67";
    actual = Optimizer.optimize(skus, stockroom).get(2);
    assertEquals(expected, actual);
  }

}
