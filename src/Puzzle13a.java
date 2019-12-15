import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Puzzle13a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    Intcode computer = new Intcode(memory);
    AC13a io = new AC13a();
    computer.execute(io);
    System.out.println(io.getBlocks());
  }

  private static class AC13a implements IntcodeIO {
    private final Table<Long, Long, Integer> screen;
    private long curX, curY;
    private int state;

    public AC13a() {
      this.screen = HashBasedTable.create();
      state = 0;
    }

    public long input() {
      throw new IllegalStateException();
    }

    public void output(long value) {
      if (state == 0) {
        curX = value;
        state = 1;
      } else if (state == 1) {
        curY = value;
        state = 2;
      } else if (state == 2) {
        screen.put(curX, curY, (int) value);
        state = 0;
      } else {
        throw new IllegalStateException();
      }
    }

    public long getBlocks() {
      return screen.cellSet().stream()
          .map(Table.Cell<Long, Long, Integer>::getValue)
          .filter(Predicate.isEqual(2))
          .count();
    }
  }
}
