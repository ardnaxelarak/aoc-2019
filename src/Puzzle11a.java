import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle11a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    Intcode computer = new Intcode(memory);
    HPR11a io = new HPR11a();
    computer.execute(io);
    System.out.println(io.getPainted());
  }

  private static class HPR11a implements IntcodeIO {
    private final Table<Integer, Integer, Boolean> hull;
    private int curX, curY;
    private int dir;
    private boolean hasPainted;

    public HPR11a() {
      this.hull = HashBasedTable.create();
      curX = 0;
      curY = 0;
      dir = 0;
      hasPainted = false;
    }

    public long input() {
      if (hull.contains(curX, curY)) {
        return hull.get(curX, curY) ? 1 : 0;
      }

      return 0;
    }

    public void output(long value) {
      if (value != 0 && value != 1) {
        throw new IllegalArgumentException();
      }

      if (!hasPainted) {
        hull.put(curX, curY, value == 1);
        hasPainted = true;
      } else {
        if (value == 0) {
          dir = (dir + 3) % 4;
        } else if (value == 1) {
          dir = (dir + 1) % 4;
        }

        switch (dir) {
          case 0:
            curY--;
            break;
          case 1:
            curX++;
            break;
          case 2:
            curY++;
            break;
          case 3:
            curX--;
            break;
        }

        hasPainted = false;
      }
    }

    public int getPainted() {
      return hull.size();
    }
  }
}
