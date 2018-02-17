import java.util.ArrayList;
import java.util.List;

/**A mock algorithm for traversal order of the warehouse.
 * This algorithm is used in place of the generic software (WarehousePicking Class).
 */
public class Optimizer {
  
  /** Based on the String SKUs in List 'skus', return List of locations.
   * 
   *<p>Each location is a String with the format "Zone Aisle Rack Level SKU".
   * 
   *<p>This algorithm sorts two ways:
   *If any SKU contain letter(s), the algorithm will sort by character in it's 
   *natural order. (A10 < A9)
   *If SKUs contain numbers only, the algorithm will sort by the value of the entire
   *string from smallest to largest (9 < 10)
   * 
   * @param skus
   * A List of String SKUs.
   * @param stockRoom
   * The StockRoom containing the HashMap of SKUs and Locations.
   * @return the List of locations.
   * 
   */
  public static List<String> optimize(List<String> skus, StockRoom stockRoom) {
    boolean containsNumbersOnly = false;
    List<String> sortedSkus = new ArrayList<String>();
    for (String sku : skus) { //check if SKUs contain only numbers
      if (sku.matches("[0-9]")) {
        containsNumbersOnly = true;
      }
    }
    if (containsNumbersOnly) { //if contains numbers only, convert to Ints and sort
      List<Integer> tempIntSkus = new ArrayList<Integer>();
      for (String sku : skus) { //change skus to Ints
        tempIntSkus.add(Integer.parseInt(sku));
      }
      tempIntSkus.sort(null);//sort skus 
      for (int sku : tempIntSkus) { //change skus back to strings
        sortedSkus.add(Integer.toString(sku));
      }
    } else { // if SKUs contain letters, sort by character 
      skus.sort(null);
      sortedSkus = skus;
    }
    List<String> traversalLocations = new ArrayList<String>();
    for (String sku : sortedSkus) { //translate skus to locations
      traversalLocations.add(stockRoom.getLocation(sku));
    }
    Simulator.fm.logInfo(3, "Optimization routine successfully processed");
    return traversalLocations;
  }

}
