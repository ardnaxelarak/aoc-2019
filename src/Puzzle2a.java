import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle2a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();
    memory[1] = 12;
    memory[2] = 2;
    Intcode computer = new Intcode(memory);
    int[] output = computer.execute();
    String finalState = 
        IntStream.of(output)
            .collect(
                StringBuilder::new,
                (collect, value) -> collect.append(",").append(value),
                (left, right) -> left.append(",").append(right))
            .toString();
    System.err.println(finalState.substring(1));

    System.out.println(output[0]);
  }
}
