/**
 * A class represensts the replenisher in the stockroom 
 *
 */
public class Replenisher extends Worker {

  public Replenisher(String name, Warehouse warehouse) {
    super(name, warehouse);
  }
  
  
  /**
   * Replenisher scans the sku number at the stockroom
   */
  @Override
  public void scanSku(String skuNum) {
    return;
  }
  
  /**
   * Make the replenish ready for work
   */
  @Override
  public void ready() {
    return;
  }
  
  /**
   * Replenisher completes his/her task
   */
  @Override
  public void complete() {
    return;
  }

}
