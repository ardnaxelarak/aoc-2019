import java.util.HashMap;
import java.util.Map;

public class Puzzle19b {
  public static void main(String[] args) {
    Intcode computer = Intcode.fromStdIn();

    int x = 0;
    int startX = 0;
    int y = 0;
    Map<Integer, Integer> starts = new HashMap<>();
    Map<Integer, Integer> length = new HashMap<>();
    while (true) {
      y++;
      x = startX;
      boolean found = true;
      while (!getBeam(computer, x, y)) {
        if (x > startX + 100) {
          found = false;
          System.err.printf("Line %d empty\n", y);
          break;
        }
        x++;
      }
      if (!found) {
        continue;
      }
      startX = x;
      int len = 0;
      while (getBeam(computer, x, y)) {
        x++;
        len++;
      }

      if (len >= 100) {
        starts.put(y, startX);
        length.put(y, len);
        if (starts.containsKey(y - 99)) {
          if (startX + 99 <= starts.get(y - 99) + length.get(y - 99) - 1) {
            System.out.println(startX * 10000 + (y - 99));
            break;
          }
        }
      }
    }
  }

  private static boolean getBeam(Intcode computer, int x, int y) {
    ArrayIO io = new ArrayIO(false, x, y);
    computer.execute(io);
    return io.getLastOutput() == 1;
  }
}
