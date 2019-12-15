import java.util.Scanner;
import java.util.stream.Stream;

public class Puzzle2b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();

    for (int noun = 0; noun < 100; noun++) {
      for (int verb = 0; verb < 100; verb++) {
        memory[1] = noun;
        memory[2] = verb;
        Intcode computer = new Intcode(memory);
        long[] output = computer.execute();
        if (output[0] == 19690720) {
          System.out.println(100 * noun + verb);
          System.exit(0);
        }
      }
    }
    System.err.println("No solution found :(");
  }
}
