import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


public class FileManagerTest {
  public FileManager fm;
  
  public ArrayList<String> out = new ArrayList<String> ();
  
  @Before
  public void SetUp() throws IOException {
    fm = new FileManager("Testlog.txt");
    out.add("a b c");
  }
  
  @Test
  public void TestFileNotFound() {
    assertTrue(fm.readFromFile("Nothing").equals(null));
  }
  
  @Test
  public void TestFormatter() {
    ArrayList<String[]> actual = fm.formatInfo(out);
    ArrayList<String[]> expected = new ArrayList<String[]> ();
    String[] content = new String[3];
    content[0] = "a";
    content[1] = "a";
    content[2] = "a";
    expected.add(content);
    assertEquals(actual, expected);
  }
  
}
