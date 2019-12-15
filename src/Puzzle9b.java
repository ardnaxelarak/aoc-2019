import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle9b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    long[] memory = Stream.of(pieces).mapToLong(Long::parseLong).toArray();

    Intcode computer = new Intcode(memory);
    ArrayIO io = new ArrayIO(true, 2);
    computer.execute(io);
    System.out.println(io.getLastOutput());
  }
}
