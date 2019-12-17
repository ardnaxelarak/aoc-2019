import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Puzzle17b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();
    memory[0] = 2;

    IO17b io = new IO17b();
    Intcode computer = new Intcode(memory);
    computer.execute(io);
  }

  private static class IO17b implements IntcodeIO {
    private static final String INPUT = 
        "A,B,B,A,C,A,C,A,C,B\n"
        + "L,6,R,12,R,8\n"
        + "R,8,R,12,L,12\n"
        + "R,12,L,12,L,4,L,4\n"
        + "n\n";

    private int index = 0;

    public IO17b() {}

    public long input() {
      return (int) INPUT.charAt(index++);
    }

    public void output(long value) {
      System.out.println(value);
    }
  }
}
