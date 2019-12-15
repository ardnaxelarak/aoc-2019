import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle5b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();
    Intcode computer = new Intcode(memory);

    ArrayIO io = new ArrayIO(true, 5);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
