import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle2a {
  public static void main(String[] args) {
    Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
    String program = sc.nextLine();
    String[] pieces = program.split(",");
    int[] memory = Stream.of(pieces).mapToInt(Integer::parseInt).toArray();
    int index = 0;
    int reg1, reg2;
    while (memory[index] != 99) {
      if (memory[index] == 1) {
        reg1 = memory[memory[index + 1]];
        reg2 = memory[memory[index + 2]];
        memory[memory[index + 3]] = reg1 + reg2;
        index += 4;
      } else if (memory[index] == 2) {
        reg1 = memory[memory[index + 1]];
        reg2 = memory[memory[index + 2]];
        memory[memory[index + 3]] = reg1 * reg2;
        index += 4;
      } else {
        System.err.printf("Uh oh. Unexpected instruction: %d\n", memory[index]);
        System.exit(1);
      }
    }
    String finalState = 
        IntStream.of(memory)
            .collect(
                StringBuilder::new,
                (collect, value) -> collect.append(",").append(value),
                (left, right) -> left.append(",").append(right))
            .toString();
    System.out.println(finalState.substring(1));
  }
}
