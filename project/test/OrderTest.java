import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OrderTest {

  /**A test Order.*/
  public Order order;
  
  @Before
  public void setUp() {
    order = new Order("S", "White");
  }
    
  /**Test if the attributes */
  @Test
  public void testGetAttributes() {
    assertEquals("White", order.getColour());
    assertEquals("S", order.getModel());
  }

}
