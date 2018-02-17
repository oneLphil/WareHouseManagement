import java.util.ArrayList;

public class Sequencer extends Worker {
  
  /** Generate a new sequencer. */
  public Sequencer(String name, Warehouse warehouse) {
    super(name, warehouse);
  }
  
  /** Sequencer is ready to sequence the next drop off if one is available. */
  public void ready() {
    if (busy) {
      return; //TODO: Ready when busy
    }
    resetScan();
    setPickRequest(this.myWarehouse.getMarshalling().receiveSequencer());
    if (getPickRequest() != null) {
      ArrayList<String> expected = (ArrayList<String>) getPickRequest().getSkuPackage();
      setExpected(expected);
      busy = true;
    }    
  }  
  
  /** After scanning is complete, add pallets ready for loading. */
  public void complete() {
    resetScan();
    busy = false;
    // from sequencerQueue to loaderQueue
    this.myWarehouse.getMarshalling().addLoaderRequest(getPickRequest().clone());
    System.out.println("Sequencer: load sequenced and passed scanning, passing to loader.");
    System.out.println("Sequencer: passing SKUs: " + getPickRequest().getSkuPackage());
    setPickRequest(null);
  }
}
