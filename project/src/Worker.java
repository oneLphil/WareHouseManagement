import java.util.ArrayList;

/** An abstract class for managing and handling Worker events and attributes. 
 * 
 * @author Jack 
 *
 */
abstract class Worker {
  
  /** Indicates whether or not the Worker is preoccupied. */
  boolean busy = false;
  /** The number of stock items this Worker can hold. */
  int capacity = 8;
  /** The Worker's name. */
  String name;
  /** The current index number of the Worker's actualArray. */
  int position = 0;
  /** The Warehouse in which this Worker is working in. */
  Warehouse myWarehouse;
  /** A list of expected SKUs. */
  ArrayList<String> expectedArray = new ArrayList<String>();
  /** The pickRequest the worker is currently working on .*/
  protected PickRequest myPickRequest;
  /** Indicates when a scanner reads an invalid SKU. */
  private boolean misscanned = false;
  
  /** Constructor for an abstract Worker.
   * 
   * @param name
   *    The Worker's name.
   * @param warehouse
   *    The Warehouse in which a Worker operates. */
  public Worker(String name, Warehouse warehouse) {
    this.name = name;
    this.myWarehouse = warehouse;
  }
  
  /** Used to indicate that the specified Worker is ready to the system. */
  public abstract void ready();

  /** The Worker scans an SKU with their barcode reader. */
  public void scanSku(String skuNum) {
    String myIdentity = (this.getClass()).toString() + " " + name;
    if (misscanned) {
      Simulator.fm.logInfo(2, myIdentity + ": A misscan was detected on an earlier scan."
          + " Please rescan or discard this pick request.");
      return;
    }
    if (expectedArray == null || expectedArray.size() == 0) {
      Simulator.fm.logInfo(3, myIdentity + ": "
          + "This scanner does not have an assigned task. Please assign one."); 
    } else if (skuNum.equals(expectedArray.get(position))) { // Correct Scan!
      if (position < capacity) { // Space available for SKU!
        position++;
        Simulator.fm.logInfo(3, myIdentity + ": Correct SKU: " + skuNum + " detected.");
        if (position == capacity) { // Scanning just completed!
          Simulator.fm.logInfo(3, myIdentity + ": Verificiation Complete. "
              + "Please complete your assignment.");
        }
      } else { // Already Finished; refuse SKU scanned.
        Simulator.fm.logInfo(2, myIdentity + ": Verification has been completed; "
            + "SKU not registered. Please complete your assignment.");
      }
    } else { // Incorrect SKU scanned.
      Simulator.fm.logInfo(2, myIdentity + ": Invalid SKU " + skuNum + ", "
          + "Please rescan or discard this pick request. Should be: " + getExpected().get(position));
      misscanned = true;
    } 
  }

  /** Restarts the scanning procedure .*/
  public void resetScan() {
    position = 0;
    misscanned = false;
    System.out.println("WORKER RESCANS!!!");
  }

  /** Discards the current pickRequest. 
   * 
   * This means the Worker has found his batch to be in the incorrect order or with the wrong
   * products, so it is sent back to OrderHandler to be re-done, as well as back to the sequencerQueue. 
   */
  public void discard() {
    resetScan();
    System.out.println(myPickRequest.getSkuPackage());
    PickRequest discardedRequest = myPickRequest.clone();
    myWarehouse.getOrderHandler().priorityQueue(discardedRequest); // resend pickRequest to OrderHandler
    myWarehouse.getMarshalling().redoPickRequest(discardedRequest); // resend pickRequest to Marshalling
    Simulator.fm.logInfo(3, (this.getClass()).toString() + " " + name + ": Discard!");
    busy = false;
    setPickRequest(null);
  }

  /** After scanning is complete, perform the next action. */
  public abstract void complete();

  /** Returns capacity. 
   * 
   * @return
   *    The capacity of this Worker.
   */
  public int getCapacity() {
    return capacity;
  }
  
  /** Returns the Worker's name. 
   * 
   * @return
   *    The Worker's name.
   */
  public String getName() {
    return name;
  }

  /** Returns expectedArray. */
  public ArrayList<String> getExpected() {
    return expectedArray;
  }

  /** Sets expectedArray. */
  public void setExpected(ArrayList<String> expected) {
    expectedArray = expected;
  }

  /** Set pickRequest of Worker. 
   * 
   * @param pickRequest
   *    The pick request object to assign to this Worker.
   */
  public void setPickRequest(PickRequest pickRequest) {
    myPickRequest = pickRequest;
  }
  
  /** Returns Worker's pickRequest. 
   * 
   * @return
   *    The pick request held by this Worker, if any.
   */
  public PickRequest getPickRequest() {
    return myPickRequest;
  }  
}
