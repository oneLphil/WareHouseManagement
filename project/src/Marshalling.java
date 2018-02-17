import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/** The Marshaling station in the warehouse simulation.
 * 
 * <p>Receives orders from OrderHandler, dispatches 'ready' Pickers, and manages fascia received 
 * from Pickers to be inserted into Trucks.
 * 
 * @author Jack (Editor: Tyson)
 */
public class Marshalling {
  /** Stores the next expected pickRequest to be sequenced. */
  private Deque<PickRequest> sequencerQueue = new LinkedList<PickRequest>();
  
  /** Stores the next expected pickRequest to be loaded. */
  private Queue<PickRequest> loaderQueue;
  
  /** Tracks all the pickRequestIDs dropped off by the pickers ready to be sequenced. */
  private ArrayList<Integer> receivedList;
  
  /** A list of trucks in the marshalling area. */
  private ArrayList<Truck> truckList;  
 
  /** Constructs a new Marshalling class. */
  public Marshalling() {
    sequencerQueue = new LinkedList<PickRequest>();
    loaderQueue = new LinkedList<PickRequest>();
    receivedList = new ArrayList<>();
    truckList = new ArrayList<>();
  }
  

  /** Adds the next pickRequest to be handled by the Marshalling area. 
   * If the pickRequest is already present, do nothing.
   * 
   * @param pickRequest 
   *      The next pickRequest to be handled.
   */
  public void addPickRequest(PickRequest pickRequest) {
    if (!sequencerQueue.contains(pickRequest)) {
      sequencerQueue.add(pickRequest);
    }
  }
  
  /** Adds a failed pickRequest back to the front of the sequencerQueue. 
   * If the pickRequest is already present, do nothing.
   * 
   * @param pickRequest 
   *      The next pickRequest to be handled.
   */
  public void redoPickRequest(PickRequest pickRequest) {
    if (!sequencerQueue.contains(pickRequest)) {
      sequencerQueue.addFirst(pickRequest);
    }
  }
  
  /** Removes the next PickRequest from the pickRequest queue.
   * @return 
   *    The PickRequest that was successfully loaded onto the truck or removed.
   */
  public PickRequest removePickRequest() {
    return sequencerQueue.poll();
  }
  
  /** Enqueue pick request to loaderQueue. */
  public void addLoaderRequest(PickRequest pickRequest) {

    loaderQueue.add(pickRequest);
  }
  
  /** Dequeue pickRequest from the loaderQueue. */
  public PickRequest removeLoaderRequest() {
    return loaderQueue.poll();
  }
  
  /** Receives the pickID of the stock collected by a Picker.
   * 
   * @param pickID 
   *      The pickID of the pickRequest dropped off by Picker.
   */
  public void receivePicker(int pickId) {
    receivedList.add(pickId);
    Simulator.fm.logInfo(3, "Marshalling: received pick request with id: " + pickId);
  }

  /** Sequencer receives the next expected PickRequestID to be sequenced or -1 if it is unavailable.
   * @return next pickRequest
   */
  public PickRequest receiveSequencer() {
    if (receivedList.isEmpty()) {
      Simulator.fm.logInfo(2, "Marshalling: no loads ready for sequencing.");
      return null;
    }
    // find the next load to be sequenced among all the loads in marshalling
    int nextId = -1;
    if (!sequencerQueue.isEmpty()) {
      nextId = sequencerQueue.peek().getPickRequestId();
    }
    for (Integer id: receivedList) {
      if (id.equals(nextId)) {
        Simulator.fm.logInfo(3, "Marshalling: Sequencer has registered to sequence pickID:"
            + " " + nextId);
        Simulator.fm.logInfo(3, "Marshalling: Sequencer expected to confirm: "
            + sequencerQueue.peek().getSkuPackage());
        receivedList.remove(id); // found next load to be sequenced, remove
        PickRequest outRequest = sequencerQueue.poll().clone();
        return outRequest;
      }      
    }
    Simulator.fm.logInfo(2, "Marshalling: next load not ready for sequencing.");
    return null;
  }
  
  /** Loader receives the expected SKUs on the next two pallets to be loaded onto the truck.
   *  @return next pickRequest
  */
  public PickRequest receiveLoader() {
    if (loaderQueue == null || loaderQueue.isEmpty()) {
      System.out.println("Marshalling: Docking Area: no loads ready for loading.");
      return null;
    }
    System.out.println("Marshalling: Docking Area: delivery loads undergoing scans: " 
        + loaderQueue.peek().getSkuPackage());
    PickRequest outRequest = loaderQueue.poll().clone();
    return outRequest;
  }

  /** Returns a list of Trucks loaded at this Marshalling station.
   * @return
   *      A list of all Trucks loaded at this Marshalling station.
   */
  public ArrayList<Truck> getTruckList() {
    return truckList;
  }
}

