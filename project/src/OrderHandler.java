import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/** Manages order events received from Warehouse (aka a FAX). They are translated into SKUs
 * then stored in orderQueue. Once orderQueue reaches processCapacity, it is trasversed
 * and packaged in a PickRequest and stored in processQueue waiting for pickup from a Picker.
 * 
 * <p>Orders are received in the format of color & model, which is then translated by the 
 * OrderHandler into a paired list of fascia for each minivan, using 'translation.csv'.
 * 
 * <p>When a picker is ready and pick request(s) are available, the first pick request is sent to 
 * the picker and marshalling
 */
public class OrderHandler {

  /** A Queue containing all orders received via FAX. */
  private Queue<Order> orderQueue = new LinkedList<Order>();
  /** A Queue containing all translated orders received via FAX. */
  private Queue<String> skuQueue = new LinkedList<String>();
  /** Stores the translation table for SKUs match model and colour of order. */
  private ArrayList<String[]> translationTable = new ArrayList<String[]>();
  /** A Queue containing all Pick Requests that are ready to go. */
  private Deque<PickRequest> processQueue = new LinkedList<PickRequest>();
  /** When orderQueue.size() == processCapacity, process
   *  a picking request. */
  private int processCapacity = 4;
  /** The total number of PickRequests created.
   * Also help create PickRequest IDs
   */
  private int pickRequestCount = 1;

  /** Stores pointer to the warehouse this OrderHandler is in. */
  private Warehouse warehouse;

  /** Initializes the OrderHandler.
   * 
   * @param translationArray
   *      A string array of data read from translation.csv.
   * @param warehouse
   *      A reference to the warehouse this OrderHandler is stored in. 
   */
  public OrderHandler(ArrayList<String> translationArray, Warehouse warehouse) {
    // Formats the translation array read by simulator and store in translationTable
    for (int i = 0; i < translationArray.size(); i++) {
      String[] values = translationArray.get(i).split(",");
      translationTable.add(values);
    } 
    this.warehouse = warehouse;
  }

  /** Adds a pending order to OrderHandler's system.
   * 
   * @param order
   *      An order in the format of [colour, model]
   */
  public void addOrder(Order order) {
    String[] currentOrder = translateOrder(order); 
    if (currentOrder[0] == null) { 
      // Check if order were in translationTable
      Simulator.fm.logInfo(2, "Invalid Order: Order not in Translation Table - "
        + order.getColour() + " " + order.getModel());
    } else { // Converts SKUs from string to Int
      if (warehouse.getStockRoom().hasSku(currentOrder[0])) { 
        //Check if SKUs are in the traversalMap
        skuQueue.add(currentOrder[0]);
        skuQueue.add(currentOrder[1]);
        orderQueue.add(order);
        if (orderQueue.size() == processCapacity) {
          //Check if orderQueue has reached processCapacity
          queuePickRequest();
        }
      } else { // If SKUs not in traveralMap, print appropriate message
        Simulator.fm.logInfo(2, "Invalid Order: SKUs not in Warehouse - "
                            + currentOrder[0] + " " + currentOrder[1]);
      }
    }
  }

  /** Gives a ready picker a pick request ID and its associated pick locations.
   * 
   * @param picker
   *      The Picker who will be requesting a pick request.
   */
  public void sendPickRequest(Picker picker) {
    if (processQueue.isEmpty()) {
      Simulator.fm.logInfo(2, "No Pick Request Ready");
    } else { // A pick request is available for (picker)!
      PickRequest currentPickRequest = processQueue.remove();// remove a PickRequest in Queue
      picker.setPickRequest(currentPickRequest.getPickRequestId(),
          currentPickRequest.getTraversalLocations());
      warehouse.getMarshalling().addPickRequest(currentPickRequest);
    } // give the whole PickRequest to Marshalling
  }

  /** Sets a Pick Request to first priority in the process queue.
   * 
   * <p>This should only occur when a picking job fails to meet criteria.
   * 
   * @param pickRequest
   *    A priority PickRequest.
   */
  public void priorityQueue(PickRequest pickRequest) {
    processQueue.addFirst(pickRequest);
  }


  /** Get the ProcessQueue of this OrderHandler.
   * 
   * <p>NOTE: for testing purposes only.
   */
  public Deque<PickRequest> getProcessQueue() {
    return processQueue;
  }

  /**
   * Translate the order into SKU items and return the front and back SKUs.
   * 
   * @param order
   *      An order containing attributes colour and model
   */
  private String[] translateOrder(Order order) {
    String[] currentOrderSkus = new String[2];
    for (int i = 0; i < translationTable.size(); i++) { 
      // Will loop until not more elements in translationTable
      if (translationTable.get(i)[0].equals(order.getColour())) {
        if (translationTable.get(i)[1].equals(order.getModel())) {
          currentOrderSkus[0] = translationTable.get(i)[2];
          currentOrderSkus[1] = translationTable.get(i)[3];
        }
      }
    }
    return currentOrderSkus;
  }

  /** Create PickRequest by combining 8 SKUs (4 orders) This PickRequest will be queued into
   * ProcessQueue.
   * 
   * <p>For traversal locations, if there is a generic traversal program, SKUs will be sent
   * to it for traverse. Otherwise, traversePickLocations() will be used for traversal.
   */
  private void queuePickRequest() {
    List<String> pickRequestList = new ArrayList<String>();
    ArrayList<Order> fourOrders = new ArrayList<Order>();
    for (int i = 0; i < processCapacity * 2; i++) { 
      pickRequestList.add(skuQueue.remove());
    }
    for (int i = 0; i < processCapacity; i++) {  // Unloads 4 orders from orderQueue
      fourOrders.add(orderQueue.remove());
    }
    List<String> skuPackage = organizeSku(pickRequestList);  // Organize SKU in the correct sequence
    //See organizeSku method for details
    List<String> traversalLocations = Optimizer.optimize(pickRequestList, warehouse.getStockRoom());
    PickRequest currentPickRequest = new PickRequest(traversalLocations, skuPackage, 
        fourOrders, pickRequestCount);
    pickRequestCount++;
    processQueue.add(currentPickRequest);
  }

  /** Organize SKUs into a integer array of 8 elements.
   * 
   * <p>The format of the elements is [F4, F3, F2, F1, R4, R3, R2, R1],
   * F represent front SKUs and R represent rear SKUs.
   * The numbers represent the order number relevant to this pick request.
   * 
   * @param pickRequest
   *      The SKUs to be organized.
   * @return
   *      The organized SKUs.
   */
  private List<String> organizeSku(List<String> pickRequest) {
    List<String> organizedRequest = new ArrayList<String>();
    Simulator.fm.logInfo(3, "Order Handler: Organizing Pick Request...");
    organizedRequest.add(0, pickRequest.get(6));
    organizedRequest.add(1, pickRequest.get(4));
    organizedRequest.add(2, pickRequest.get(2));
    organizedRequest.add(3, pickRequest.get(0));
    organizedRequest.add(4, pickRequest.get(7));
    organizedRequest.add(5, pickRequest.get(5));
    organizedRequest.add(6, pickRequest.get(3));
    organizedRequest.add(7, pickRequest.get(1));
    Simulator.fm.logInfo(3, "Order Handler: Pick Request successfully organized.");
    return organizedRequest;
  }


  
}



