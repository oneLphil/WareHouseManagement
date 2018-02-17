/** An Order that contains the attributes of Colour and Model 
 *
 */
public class Order {

  /** The model attribute of this order.*/
  private String model;

  /** The colour attribute of this Order.*/
  private String colour;
  
  /** Initialize the Order
   * 
   * @param model
   *  the model attribute of the Order 
   * @param colour
   *  the colour attribute of the Order
   */
  public Order(String model, String colour) {
    this.model = model;
    this.colour = colour;
  }

  /** Return the model attribute of this Order.*/
  public String getModel() {
    return model;
  }

  /** Return the colour attribute of this Order.*/
  public String getColour() {
    return colour;
  }
  
  
}
