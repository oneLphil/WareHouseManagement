/** A simple factory that constructs a Worker from a given a name and role.*/
public class WorkerFactory {
  
  /**Makes a new Worker object of a given type.
   * 
   * @param name
   *    The worker's name. 
   * @param type
   *    The worker's type or job title. Examples include Picker and Loader.
   * @return
   *    Returns the Worker subclass of the appropriate type.
   */
  public Worker makeWorker(String name, String type, Warehouse warehouse) {
    Worker worker = null;
    switch (type) {
      case "Picker": worker = new Picker(name, warehouse);
        break;
      case "Sequencer": worker = new Sequencer(name, warehouse);
        break;
      case "Loader": worker = new Loader(name, warehouse);
        break;
      case "Replenisher": worker = new Replenisher(name, warehouse);
        break;
      default: Simulator.fm.logInfo(2, "Failed to initialize worker of type " + type + ".");
        return worker; // Type not recognized.
    }
    Simulator.fm.logInfo(3, type + " " + name + " initialized.");
    return worker;
  }
}
