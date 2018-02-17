import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Warehouse in the Warehouse Simulation.
 * 
 * @author Yuesheng (Editor: Tyson)
 *
 */
public class Warehouse {

  /** The phrases to be read from an event in eventSequence. */ 
  private ArrayList<String> events =
      new ArrayList<>(Arrays.asList("Order", "Picker", "Sequencer", "Loader", "Replenisher"));
  /** Tracks the current event number being processed. */
  private int eventCounter = 0;
  /** A sequence of events the Simulator can recognize and process from the text file. */ 
  private ArrayList<String> eventSequence = new ArrayList<>(); 
  /** A factory-object for producing Worker classes. */
  private WorkerFactory factory = new WorkerFactory();
  /** The OrderHandler inside this Warehouse. */
  private OrderHandler orderHandler;
  /** The Marshalling station inside this Warehouse. */
  private Marshalling marshalling;
  /** The StockRoom inside this Warehouse. */
  private StockRoom stockRoom;
  /** The Pickers employed in the Warehouse. */
  private ArrayList<Worker> workers = new ArrayList<>();
  /** Initializes a Warehouse within the simulation.
   * 
   * @param event 
   *        The events that will be run by this Warehouse to be performed on the classes
   *        inside the warehouse.
   * @param init 
   *        The initial state of the StockRoom's stock.
   * @param traversal 
   *        The traversal table used by OrderHandler's optimize code and to setup the
   *        layout of the StockRoom.
   * @param translation 
   *        The translation table used by OrderHandler to decode orders.
   */
  public Warehouse(ArrayList<String> event, ArrayList<String> init,
      ArrayList<String> traversal, ArrayList<String> translation) {
    this.eventSequence = event;
    translation.remove(0);
    this.stockRoom = new StockRoom(traversal, init);
    this.orderHandler = new OrderHandler(translation, this);
    this.marshalling = new Marshalling();
  }

  /**
   * Runs events in the Warehouse simulation for this particular Warehouse's elements.
   * 
   * <p>See help.txt for a list of commands and their details.
   * 
   * @throws IOException 
   *      Indicates that an I/O exception has occurred.
   */
  public void runWarehouse() throws IOException {
    Simulator.fm.logInfo(3, "#####################################");
    Simulator.fm.logInfo(3, "######### Running Warehouse #########");
    Simulator.fm.logInfo(3, "#####################################");
    for (String eventString : eventSequence) {
      String[] event = eventString.split(" ");
      Simulator.fm.logInfo(3, "## Warehouse: Handling Event #" + eventCounter + ": " + eventString + " ##");
  
      ///////////////// EVENT HANDLING //////////////////
      if (!(events.contains(event[0]))) { // Event not recognized
        Simulator.fm.logInfo(2, "Simulator: system does not understand the event: " + event[0]);
        Simulator.fm.logInfo(3, "Simulator: skipping the event: " + event[0]); 
      }
      if (event[0].equals("Order")) { // Event: Order (model) (colour)
        Simulator.fm.logInfo(3, event[1] + " " + event[2]);
        Order order = new Order(event[1], event[2]);
        orderHandler.addOrder(order);
      } else if (event[2].equals("ready")){ // Event: Worker (name) ready
        if (!checkWorker(event[1])) {
          workers.add(factory.makeWorker(event[1], event[0], this)); // factory generates Worker of given type
          Simulator.fm.logInfo(3, "## Warehouse: Registered worker: " + event[0] + " " + event[1] +" ##");
          getWorker(event[1]).ready();
        } else {
          getWorker(event[1]).ready(); // Worker already exists, just change status to ready
        }
             
      } else if (event[2].equals("scans")) { // Event: Worker (name) scans (SKU)
        Worker worker = getWorker(event[1]);
        worker.scanSku(event[3]);
      } else if (event[2].equals("rescans")) { // Event: Worker (name) rescans (SKU)
        Worker worker = getWorker(event[1]);
        worker.resetScan();
      } else if (event[2].equals("completes")) { // Event: Worker (name) completes ---
        Worker worker = getWorker(event[1]);
        worker.complete();
      } else if (event[2].equals("discards")) { // Event: Worker (name) completes ---
        Worker worker = getWorker(event[1]);
        worker.discard();
      }
      
//      else if (event[0].equals("Picker") && event[2].equals("printout")) { // Event: Picker (name) printout
//          Picker picker = (Picker) getWorker(event[1]); //TODO: generalize this for all workers too
//          picker.printPickInfo();          
//        } 
      
      else if (event[0].equals("Replenisher")) { // Event: Replenisher (name) replenish (locale)
        stockRoom.replenish(event[3] + " " + event[4] + " " + event[5] + " " + event[6]);
      }
//      } else{
//    	  Simulator.fm.logInfo(3, "Simulator: INVALID EVENT!");
//      }
      
      // Move to next line...
      eventCounter++;
      
    } /////////////////EXPORTING///////////////////
    stockRoom.csvExport();  // export stock to final.csv
    for (Truck truck: marshalling.getTruckList()) { // export all orders on trucks to orders.csv
      truck.exportOrders();
    }
  }

  
  /** Gets a Worker employed in this Warehouse by name.
   * 
   * @param name
   *      The name of the Picker.
   * @return
   *      The requested Picker, or null if not found.
   */
  public Worker getWorker(String name) {
    for (Worker p : workers) {
      if (p.getName().equals(name)) {
        return p;
      }
    }
    return null;
  }

  /** Returns this Warehouse's OrderHandler. */
  public OrderHandler getOrderHandler() {
    return orderHandler;
  }

  /** Returns this Warehouse's StockRoom. */
  public StockRoom getStockRoom() {
    return stockRoom;
  }

  /** Returns this Warehouse's Marshalling. */
  public Marshalling getMarshalling() {
    return marshalling;
  }

  /** Verifies that a Worker with name is in this Warehouse.
   * 
   * @param name
   *      The name of the Picker.
   * @return
   *      Is this Picker in the Warehouse?
   */
  private boolean checkWorker(String name) {
    for (Worker i : workers) {
      if (i.getName().equals(name)) {
        return true;
      }
    }
    return false;
  }

}
