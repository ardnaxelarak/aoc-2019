import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle5b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();
    Intcode computer = new Intcode(memory);

    P5bIO io = new P5bIO();
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }

  private static class P5bIO implements IntcodeIO {
    private boolean inputTaken = false;
    private int lastRead = 0;

    public int input() {
      if (inputTaken) {
        throw new IllegalStateException("No more input!");
      } else {
        return 5;
      }
    }

    public void output(int value) {
      System.err.printf("Output: %d\n", value);
      lastRead = value;
    }

    public int getLastOutput() {
      return lastRead;
    }
  }
}
