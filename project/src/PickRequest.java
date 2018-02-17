import java.util.ArrayList;
import java.util.List;

/** Stores a pick request containing the SKU numbers of received orders.
 *  
 *  <p>Currently, this class holds 4 minivan orders, or 8 fascia.
 *  
 *  @author Jing (Editor: Tyson)
 */
public class PickRequest {
  
  /** This PickRequests identification number.*/
  private int pickRequestId;
  /** An array of SKU numbers, ordered for sequencing.
   * 
   * <p>Fascia: Organized in the format of: [f4, f3, f2, f1, r4, r3, r2, r1,]. */
  private List<String> skuPackage;
  /** An array of of traversal locations.*/
  private List<String> traversalLocations;
  /** A list of orders contained on this pick request. Used when exporting orders.csv. */ 
  private ArrayList<Order> orders = new ArrayList<Order>();
  
  
  /** Initializes a PickRequest, containing the necessary details to 
   * collect, verify and export a picking job.
   * 
   * @param traversalLocations
   * The traversalLocations of all SKU's in this request.
   * @param skuPackage
   * The SKU's included in this request, in order.
   * @param orders
   * The orders contained in this PickRequest
   */
  public PickRequest(List<String> traversalLocations, List<String> skuPackage, 
      ArrayList<Order> orders, int id) {
    this.traversalLocations = traversalLocations; 
    this.skuPackage = skuPackage;
    this.orders = orders; 
    this.pickRequestId = id;
  } 
  
  /** Returns a clone of this PickRequest that can be passed around. */
  public PickRequest clone() {
    List<String> traversalNew = new ArrayList<String>();
    traversalNew.addAll(this.traversalLocations);
  
    List<String> skusNew = new ArrayList<String>();
    skusNew.addAll(skuPackage);
    
    ArrayList<Order> ordersNew = new ArrayList<Order>();
    ordersNew.addAll(orders);
    
    PickRequest clonedRequest = new PickRequest(traversalNew, skusNew, ordersNew, pickRequestId);
    return clonedRequest;
  }

  /** Get the list of orders from this pick request.*/
  public ArrayList<Order> getOrders() {
    return orders;
  }

  /** Get the ID of this pick request.*/
  public int getPickRequestId() {
    return pickRequestId;
  }

  /** Get the SKU package of this pick request.*/
  public List<String> getSkuPackage() {
    return skuPackage;
  }

  /** Get the Traversal Locations of this pick request.*/
  public List<String> getTraversalLocations() { 
    return traversalLocations;
  }
}
