import java.util.ArrayList;

/** An employee in a Warehouse in charge of loading Trucks with Pallet contents.
 * 
 * @author Jack (Editor: Tyson)
 *
 */
public class Loader extends Worker {

  /** Initializes a Loader.
   * 
   * @param name
   *    The Loader's name.
   * @param warehouse
   *    The Warehouse in which the Loader works.
   */
  public Loader(String name, Warehouse warehouse) {
    super(name, warehouse);
  }

  /** A method used to indicate that a Loader is ready to load Pallets onto a Truck, 
   * if available. 
   */
  public void ready() {
    if (!busy) { // Loader is ready to handle a Loading request.
      resetScan();
      Simulator.fm.logInfo(3, "Loader " + name + ": Ready! Checking for jobs at Marshalling...");
      setPickRequest(this.myWarehouse.getMarshalling().receiveLoader());
      if (getPickRequest() != null) {
        busy = true;
        ArrayList<String> expected = (ArrayList<String>) getPickRequest().getSkuPackage();
        setExpected(expected);
        Simulator.fm.logInfo(3, "Loader " + name + ": Request recieved! Proceed with verification.");
      } else {
        Simulator.fm.logInfo(2, "Loader " + name + ": No jobs available at Marshalling."
            + " Ready request ignored.");
      }
    } else { 
        Simulator.fm.logInfo(2, "Loader " + name + ": Loader is currently preoccupied."
          + " Ready request ignored.");
    } 
  }
  
  /** After scanning is complete, load pallets onto truck. */
  public void complete() {
//    if (myPickRequest == null) {
//      Simulator.fm.logInfo(3, "Loader " + name + ": No job available for loading.");
//      return;
//    }
    if (position == capacity) {
      PickRequest completeRequest = myPickRequest.clone();
      setPickRequest(null);
      resetScan();
      busy = false;
      // find the first non-full truck.
      for (Truck truck : myWarehouse.getMarshalling().getTruckList()) {
        if (!truck.isFull()) {
          truck.loadPallet(completeRequest.getOrders());
          Simulator.fm.logInfo(3, "Loader " + name + ": Pallets have been loaded onto the Truck.");
        }
        return;
      }
      // no trucks available, request a new one. 
      Truck newTruck = new Truck();
      newTruck.loadPallet(completeRequest.getOrders()); //removes pickRequest
      myWarehouse.getMarshalling().getTruckList().add(newTruck);
      Simulator.fm.logInfo(3, "Loader " + name + ": New Truck initialized. Pallets have been loaded.");

    } else {
      Simulator.fm.logInfo(2, "Loader " + name + ": Please verify all SKUs before "
          + "attempting to load Truck.");
    }
  }
}
