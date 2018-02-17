import java.io.IOException;
import java.util.ArrayList;

/** A truck to be sent out to the factory from the warehouse.
 * 
 * <p>Contains a stack of pallets with location in truck store them
 * based on its specified capacity. Once filled with fascia pallets, 
 * it is sent out to the factory.
 * 
 * <p>By default, Trucks have 80 orders / 160 fascia per load.
 * Marshaling records all 'sent' Trucks in a List.
 *
 * @author Jing (Editor: Tyson)
 */

/*
        BACK OF TRUCK BED

        PICKING    PICKING
        REQUEST i  REQUEST i+1

         ^^^^     ^^^^
        | F4 |   | F4 |
        | F3 |   | F3 |
        | F2 |   | F2 |
        | F1 |   | F1 |
Left     ----     ----      Right

         ^^^^     ^^^^
        | R4 |   | R4 |
        | R3 |   | R3 |
        | R2 |   | R2 |
        | R1 |   | R1 |
         ----     ----

        FRONT OF TRUCK BED
*/

public class Truck {
  /** Counter for total trucks created across all Warehouse simulations. */
  public static int truckCount = 0;
  /** The current load of this truck.*/
  private int currentLoad = 0;
  /** Stores the orders on this truck. */
  private ArrayList<Order> orderList = new ArrayList<Order>();
  /** Stores this truck's identification number (not instance ID).*/
  private int truckId;
  /** The maximum height that pick requests may be stacked. */
  private int truckHeight = 10;
  /** The maximum width that pick requests may fit in the truck. */
  private int truckWidth = 2;
  /** The truck capacity.*/
  private int truckCapacity = truckHeight * truckWidth;
  /** Creates a Truck for storing Pallets to be shipped from the Warehouse. */
  public Truck() {
    this.truckId = truckCount;
    truckCount += 1;
  }


  /** Loads the specified front and back Pallets onto this Truck. */
  public void loadPallet(ArrayList<Order> fourOrders) {
    if (!this.isFull()){
      for (int i = 0; i < fourOrders.size(); i++) { //add the corresponding 4 orders to the orderList
        orderList.add(fourOrders.get(i));
      }
      currentLoad++;
    }
  }
  
  /** Returns True if this Truck is full. */
  public boolean isFull() {
    return currentLoad == truckCapacity;
  }
   
  /** Get the identification number of this Truck.*/
  public int getTruckId() {
    return truckId;
  }


  /** Create a String ArrayList of all the orders on this truck for export." 
   * 
   * @throws IOException
   *  Throws an IOException when the input and out operation fails.
   * */
  public void exportOrders() throws IOException { 
    ArrayList<String> outArray = new ArrayList<>();
    for (Order order: orderList) {
      outArray.add(order.getModel() + ", " + order.getColour());
    }
    Simulator.writeFile(outArray, "orders.csv");
  }
}
