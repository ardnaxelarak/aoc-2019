import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle2b {
  public static void main(String[] args) {
    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();

    for (int noun = 0; noun < 100; noun++) {
      for (int verb = 0; verb < 100; verb++) {
        memory[1] = noun;
        memory[2] = verb;
        Intcode computer = new Intcode(memory);
        Optional<int[]> output = computer.execute();
        if (output.isPresent() && output.get()[0] == 19690720) {
          System.out.println(100 * noun + verb);
          System.exit(0);
        }
      }
    }
    System.err.println("No solution found :(");
  }
}
