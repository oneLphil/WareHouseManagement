import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
                SystemTest.class,
                OptimizerTest.class,
                SequencerTest.class,
                StockRoomTest.class,
                FileManagerTest.class,
                OrderHandlerTest.class,
                OptimizerTest.class,
                PickerTest.class,
                PickRequestTest.class,
                SimulatorTest.class, 
                TruckTest.class,
                WarehouseTest.class})

public class AllTests {
  // Stub
  }
