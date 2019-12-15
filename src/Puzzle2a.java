import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle2a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();
    memory[1] = 12;
    memory[2] = 2;
    Intcode computer = new Intcode(memory);
    Optional<int[]> output = computer.execute();
    if (output.isPresent()) {
      memory = output.get();

      String finalState = 
          IntStream.of(memory)
              .collect(
                  StringBuilder::new,
                  (collect, value) -> collect.append(",").append(value),
                  (left, right) -> left.append(",").append(right))
              .toString();
      System.err.println(finalState.substring(1));

      System.out.println(memory[0]);
    }
  }
}
