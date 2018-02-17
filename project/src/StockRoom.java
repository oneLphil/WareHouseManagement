import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** The StockRoom of the warehouse.
 * 
 * <p>Manages events relating to the supply on the warehouse floor, 
 * the 'reserve room' and resupply orders.
 * 
 * <p>Instantiated by OrderHandler with its initial layout and 
 * exports final.csv when all orders have been processed.
 * 
 * @author Jack (Editor: Tyson)
 */
public class StockRoom {

  /** Stores the products currently in the StockRoom in map pair (sku, Product). */
  private Map<String, Product> productMap = new HashMap<String, Product>();
  /** Designates when stock should automatically be replenished. */
  private int replenishValue = 5;
  
  /** Initializes the StockRoom
   * 
   * @param traversal
   *    The layout of the StockRoom floor.
   * @param initial
   *    The inital stock layout of the StockRoom.
   */
  public StockRoom(ArrayList<String> traversal, ArrayList<String> initial) {
    csvImport(traversal); // sets up position and sku
    csvInitalize(initial); // sets up amount of stock
  }

  /** Checks if the SKU is in the StockRoom */
  
  public boolean hasSku(String sku) {
    return productMap.containsKey(sku);
  }

  /** Takes a SKU scanned by picker and, if valid, decreases amount by 1 and returns True.
   * Will trigger a replenish request if necessary. Returns False if the SKU is invalid.
   * 
   * @param sku 
   *      The SKU number scanned by a Picker.
   * @return
   *      Was the product successfully picked / in stock?
   */
  public boolean takeProduct(String sku) {
    if (productMap.containsKey(sku)) {
      Product p1 = productMap.get(sku);
      p1.takeOne();
      Simulator.fm.logInfo(3, "Stock Room: SKU#" + sku + " scan detected.");
      if (p1.stock == replenishValue) {
        triggerReplenishRequest(p1);
      }
      return true;
    } else { // Invalid SKU
      return false;
    }
  }
  
  /** Handles replenisher events.
   * 
   * @param location 
   *      The String location where the replenish took place in form:
   *       "(zone) (aisle) (rack) (level)".
   */
  public void replenish(String location) {
    String[] values = location.split(" ");
    String zone = values[0];
    int aisle = Integer.parseInt(values[1]); // convert string of ints to ints for array index
    int rack = Integer.parseInt(values[2]);
    int level = Integer.parseInt(values[3]);
    for (Product p : productMap.values()) { // find Product with position
      if (p.getZone().equals(zone) && p.getAisle() == aisle && p.getRack() == rack
          && p.getLevel() == level) {
        if (p.getStock() >= 25) {
          Simulator.fm.logInfo(2, "Stock Room: replenish at " + location + " not required."
              + " Please return stock to reserve room.");
        }
        p.stock += 25;
        Simulator.fm.logInfo(3, "Stock Room: replenish at " + location + " complete.");
      }
    }
  }

  /** Takes an sku number and returns a String that contains its location and sku number 
   * 
   * The zone character (in the range ['A'..'B']), the aisle number (an integer
   * in the range [0..1]), the rack number (an integer in the range ([0..2]),
   * and the level on the rack (an integer in the range [0..3]), and the SKU
   * number.*/
  public String getLocation(String sku) {
    Product p1 = productMap.get(sku);
    return p1.getZone() +" "+ p1.getAisle() +" "+ p1.getRack() +" "+ p1.getLevel() +" "+ p1.getSku();
  }
  
  /** Returns a Map of locations to SKUs.
   * 
   * @return
   *      A mapping of locations to SKUs.
   */
  public Map<String, Product> getMap() {
    return productMap;
  }

  /** Exports an array containing the final amounts of Product the StockRoom.
   * 
   * @throws IOException 
   *      Indicates that an I/O exception has occurred.
   */
  public void csvExport() throws IOException {  
    ArrayList<String> outArray = new ArrayList<>();
    for (Product p : productMap.values()) {     // [zone][aisle][rack][level]
      outArray.add(p.getZone() + "," + p.getAisle() + "," + p.getRack() 
            + "," + p.getLevel() + "," + p.getStock()); 
    }
    outArray.sort(null);
    Simulator.writeFile(outArray, "final.csv");
  }

  /** Informs system supply is running low and requests a replenish. 
   * 
   * @param p 
   *      The Product that requires replenishing.
   */
  private void triggerReplenishRequest(Product p) {
    Simulator.fm.logInfo(3, "Running out of " + p.getSku() + ". Please Replenish.");
  }

  /** Reads formatted String array and constructs initial state of StockRoom.
   * 
   * @param array
   *      A .csv file in array form, with each line stored as an element.
   */
  private void csvImport(ArrayList<String> array) {
    Simulator.fm.logInfo(3, "#####################################");
    Simulator.fm.logInfo(3, "####### Initializing StockRoom ######");
    Simulator.fm.logInfo(3, "#####################################");
    for (int i = 0; i < array.size(); i++) {
      String[] values = array.get(i).split(",");
      int aisle = Integer.parseInt(values[1]); // convert string of ints to ints for array index
      int rack = Integer.parseInt(values[2]);
      int level = Integer.parseInt(values[3]);
      Simulator.fm.logInfo(3, 
          "Initializing shelf: " + values[0] + " " + aisle + " " + rack + " " + level + " " + values[4]);
      Product item = new Product(values[4], 30, values[0], aisle, rack, level);
      productMap.put(values[4], item);
    }
  }

  /** Initialize the stock from the initial.csv file. 
   * 
   * @param array
   *      The initial stock layout in the StockRoom.
   */
  private void csvInitalize(ArrayList<String> array) {
    for (int i = 0; i < array.size(); i++) {
      String[] values = array.get(i).split(",");
      int aisle = Integer.parseInt(values[1]); // convert string of ints to ints for array index
      int rack = Integer.parseInt(values[2]);
      int level = Integer.parseInt(values[3]);
      int amount = Integer.parseInt(values[4]);
      for (Product p : productMap.values()) {
        if (p.getZone().equals(values[0]) && p.getAisle() == aisle && p.getRack() == rack
            && p.getLevel() == level) {
          p.stock = amount;
        }
      }
    }
  }

  /** The private Product class stores information about a particular product stored in StockRoom. 
   * This includes its current stock, its position, and its SKU number.
   */
  public class Product {
    /** The Product's SKU number. */
    private String sku;
    /** The amount of product in the StockRoom. */
    private int stock;
    /* The current location of the product in the StockRoom. 
     * [zone][aisle][rack][level] */
    /** The zone in which the Product is located, within the stock room. */
    private String zone;  
    /** The aisle in a particular zone. */
    private int aisle;
    /** The rack in a particular aisle. */
    private int rack;
    /** The level on a particular rack. */
    private int level;

    /** Initialize a Product object.
     * 
     * @param skuNumber
     *      The SKU of the product.
     * @param amount
     *      The amount of product in stock.
     * @param initialZone
     *      A product's zone.
     * @param initialAisle
     *      A product's aisle.
     * @param initialRack
     *      A product's rack.
     * @param initialLevel
     *      A product's level.
     * */
    private Product(String skuNumber, int amount, 
        String initialZone, int initialAisle, int initialRack, int initialLevel) {
      sku = skuNumber;
      stock = amount;
      zone = initialZone;
      aisle = initialAisle;
      rack = initialRack;
      level = initialLevel;
    }

    /** Remove one product from stock. */
    public void takeOne() {
      if (stock > 0) {
        stock--;
      } else {
        Simulator.fm.logInfo(2, "Stock Room: SKU#" + this.getSku() + " out of stock.");
      }
    }

    /** Add 25 more product. */
    public void replenish() {
      stock += 25;
    }
    
    public String getSku() {
      return sku;
    }

    public int getStock() {
      return stock;
    }

    public String getZone() {
      return zone;
    }

    public int getAisle() {
      return aisle;
    }

    public int getRack() {
      return rack;
    }

    public int getLevel() {
      return level;
    }

  }
}
