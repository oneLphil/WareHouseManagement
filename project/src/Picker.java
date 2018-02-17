import java.util.ArrayList;
import java.util.List;

/** Manages the checklist of a Picker in the system and relays associated system messges.
 * 
 * <p>The Warehouse will create Picker classes as needed by via the 'ready' order event.
 * When ready, the Picker informs the warehouse's OrderHandler that it is ready. 
 * If an order is available and the Picker is not preoccupied, OrderHandler will 
 * send the Picker's barcode reader a positionArray with locations to collect stock 
 * (including fascia). Every time the Picker gets a stock item, the barcode reader 
 * will indicate where to get the next stock item on the list until all location 
 * messages have been processed. The picker then may be sent to Marshalling to 
 * deposit collected stock.
 * 
 * <p>Pickers on the floor scan SKU they find, which are processed by Warehouse via event;
 * the StockRoom will inform Pickers if the SKU is valid, and advance their checklist
 * position if that is the case. If the Picker has all the stock it can hold, it will
 * not accept more stock.
 * */
public class Picker extends Worker {
  /** The current pickRequest job number. */
  private int pickId = -1;
  /** The positions of the stock items to be collected, followed by the SKU. */
  private List<String> positionArray = new ArrayList<String>();
  
  /** Initializes a Picker in the Warehouse that calls it.
   * 
   * @param newName
   * The picker's name
   * @param warehouse
   * The Warehouse for which the Picker works.
   */
  public Picker(String newName, Warehouse warehouse) {
    super(newName, warehouse);
    Simulator.fm.logInfo(3, "Picker " + name + " has arrived at the warehouse.");
  }
  
  /** Alerts a Picker's OrderHandler that the Picker is ready for a pick request, 
   * if not preoccupied. 
   */
  public void ready() {
    Simulator.fm.logInfo(3, "Picker " + name + ": Processing 'ready' event.");
    if (pickId == -1) { // Checks if ready / has a pick request
      OrderHandler orderHandler = myWarehouse.getOrderHandler();
      orderHandler.sendPickRequest(this);
    } else { // Picker is not ready for a new request
      Simulator.fm.logInfo(3, "Error: Picker " + name + " is currently busy fulfilling a pick request.");
    }
  }
  
  /** Sends this picker to his Marshalling station and resets his/her 'ready' status. 
   * 
   * <p>The Picker will not be sent if he/she has not finished their pick job. 
   */
  public void complete() {
    if (position == capacity) { // Confirms that the Picker has finished collecting. 
      Marshalling marshalling = myWarehouse.getMarshalling();
      marshalling.receivePicker(pickId); 
      Simulator.fm.logInfo(3, "Picker " + name + " has deposited his collection "
          + "of product at Marshalling.");
      initArrays();
    } else { // Picker has not finished their pick job.
      Simulator.fm.logInfo(2, "Error: Picker " + name + " has not finished fulfilling their pick job.");
      printCurrentPick();
    }
  }

  /** Gives the Picker a new pick request so that he/she may gather SKU on the floor.
   * 
   * @param newId
   *    The pick request job number, to hand to their Marshalling station when finished.
   * @param positions
   *    The locations where Picker will be told to go by the system and their associated SKUs.
   *      Format: zone,aisle,shelf,level,sku [String(char,int,int,int,int,String)] 
   */
  public void setPickRequest(int newId, List<String> positions) {
    if (positions.size() != capacity) { // The size of the PickRequest is too large/small.
      Simulator.fm.logInfo(3, "Error: Picker " + name + " cannot receive this pick request; "
          + "the request is too large/small.");
    } else { // Pick request is of satisfactory size
      pickId = newId;
      positionArray = ((ArrayList<String>) positions);
      // Defining expectedArray to mirror Worker's scanSKU method.
      String[] values;
      for (int i = 0; i < capacity; i++) {
        values = positionArray.get(i).split(" ");
        expectedArray.add(values[4]);
      }
      Simulator.fm.logInfo(3, "Picker " + name + " has received pick request #" + pickId + "!");
    }
  }
  
  /** Scans an SKU number and adds it to the Picker's pickArray at the current position, and 
   * reports the next location for the Picker to travel to.
   * 
   * @param skuNum
   * The SKU number to be stored by Picker's pickArray.
   */
  public void scanSku(String skuNum) {
    
    // Check if Picker can receive an SKU...
    if (pickId == -1) { // Picker is not ready with a pick request
      Simulator.fm.logInfo(2, "Error: Picker " + name + " is not ready to perform picks.");
    } else { // Check and pick if the stock can be picked
      
      if (position > capacity) { // Picker at capacity...  SKU not recorded.
        Simulator.fm.logInfo(3, "Picker " + name + " attempted to pick more product than he can carry." 
            + " The SKU " + skuNum + " has not been recorded by their barcode reader. "
            + "Please return this item.");
        
      } else { // Picker can receive an SKU.
        
        if (expectedArray.get(position).equals(skuNum)) { // The scanned SKU matches
          Simulator.fm.logInfo(3, "Confirming Picker " + name + "'s SKU pick with the stock room...");
          StockRoom stockRoom = myWarehouse.getStockRoom();
          boolean inStock = stockRoom.takeProduct(skuNum);
          if (inStock) { // Sets the designated SKU
            position++;
            Simulator.fm.logInfo(3, "Picker " + name + " picked " + skuNum + " from the stock room.");
            if (position < capacity) { // Picker gets the next location.
              printCurrentPick();
            } else if (position == capacity) { // Picker has finished collecting.
              Simulator.fm.logInfo(3, "Picker " + name + ", please report to Marshalling.");
            } 
          } else { // Invalid SKU
            Simulator.fm.logInfo(2, "Error: Picker " + name + " attempted to pick an unstocked SKU."
                + " The SKU " + skuNum + " has not been recorded by their barcode reader.");
          }
          
        } else { // The scanned SKU is incorrect
          Simulator.fm.logInfo(2, "Picker " + name + " did not pick the currently requested SKU. "
              + " The SKU " + skuNum + " has not been recorded by their barcode reader.");
          printCurrentPick();
        }
      }
    }
  }
  
  /** Returns null if a getPickRequest attempt is made.
   * Picker does not handle Pick Requests; symptom of inheritance from Worker.
   * 
   * @return
   *    null; Picker does not manage this object.
   */
  public PickRequest getPickRequest() {
    return null;
  }
  
  /** Returns the pick request number that Picker is currently managing. 
   * 
   * @return
   *      The pick request number, or -1 if Picker doesn't have a job.
   */
  public int getPickNum() {
    return pickId;
  }

  /** System prints all picks that should be performed by Picker. */
  public void printPickInfo() {
    Simulator.fm.logInfo(3, "Printing out " + name + "'s pick order...");
    for (int i = 0; i < capacity; i++) {
      Simulator.fm.logInfo(3, expectedArray.get(i));
    }
  }

  /** Resets the Picker in preparation for a new pick request from OrderHandler. */
  private void initArrays() {
    position = 0;
    pickId = -1;
    if (!expectedArray.isEmpty()) {
      expectedArray.clear();
    }
    Simulator.fm.logInfo(3, "Picker " + name + " can now recieve an order!");
  }

  /** Informs the Picker where to pick their next SKU. */
  private void printCurrentPick() {
    if (expectedArray == null || expectedArray.isEmpty()) { //TODO: temporary patch to get program running
      return; //TODO: gives a null pointer??? needs to be debugged across 
    }
    Simulator.fm.logInfo(3, "Picker " + name + ", please pick: " + positionArray.get(position)); 
  }
}
