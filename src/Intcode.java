import java.util.Arrays;
import java.util.Optional;

public class Intcode {
  private int[] memory;

  public Intcode(int[] memory) {
    this.memory = Arrays.copyOf(memory, memory.length);
  }

  public Optional<int[]> execute() {
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
        return Optional.empty();
      }
    }
    return Optional.of(Arrays.copyOf(memory, memory.length));
  }
}
