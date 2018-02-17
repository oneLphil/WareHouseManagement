import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PickRequestTest {
  
  /**A test PickRequest.*/
  public PickRequest pickRequest;
  
  @Before
  public void setup() {
    final int id = 5;
    List<String> skuPackage = new ArrayList<String>();
    skuPackage.add("99");
    List<String> traversalLocations = new ArrayList<String>();
    traversalLocations.add("A 1 1 1 99");
    ArrayList<Order> orders = new ArrayList<Order>();
    orders.add(new Order("S", "White"));
    pickRequest = new PickRequest(traversalLocations, skuPackage, orders, id);
  }
  
  /**Test if sku sequence is set correctly.*/
  @Test
  public void testGetSkuPackge() {
    String expected = "99";
    String actual = pickRequest.getSkuPackage().get(0);
    assertEquals(expected, actual);
  }
  /**Test f traversal locations is set correctly.*/
  @Test
  public void testGetTraversalLocations() {
    String expected = "A 1 1 1 99";
    String actual = pickRequest.getTraversalLocations().get(0);
    assertEquals(expected, actual);
  }
  
  /**Test if getting orders is working correctly.*/
  @Test
  public void testGetOrders() {
    String expected = "White";
    String actual = pickRequest.getOrders().get(0).getColour();
    assertEquals(expected, actual);
  }
  
  /**Test if getting PickRequest ID is working correctly.*/
  @Test
  public void testGetPickRequestId() {  
    int expected = 5;
    int actual = pickRequest.getPickRequestId();
    assertEquals(expected, actual);
  }
}
