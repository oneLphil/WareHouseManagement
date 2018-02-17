import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class SimulatorTest {

  //TODO: Will update once IO moved to a separate class, just getting syntax down for testing
  //exceptions.
  /**
   * Tests the file not found exception.
   * @throws FileNotFoundException 
   */
  @Test (expected = FileNotFoundException.class)
  public void testReadFile() throws FileNotFoundException {
    Simulator s = new Simulator();
    Simulator.fm.readFromFile("");
    s = null;
  }
  
  @Test
  public void testmaindefault() throws IOException {
    Simulator.main(null);
    assertTrue(Simulator.readfrom.equals("default"));
  }


}
